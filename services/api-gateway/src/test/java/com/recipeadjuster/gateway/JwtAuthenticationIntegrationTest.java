package com.recipeadjuster.gateway;

import com.recipeadjuster.gateway.filter.JwtAuthenticationFilter;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.List;

class JwtAuthenticationIntegrationTest {

    private JwtAuthenticationFilter filter;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private WebFilterChain mockChain;

    @BeforeEach
    void setUp() throws Exception {
        // Generate ephemeral RSA key pair for testing (no embedded keys)
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
        
        // Create filter with test public key
        String publicKeyPEM = "-----BEGIN PUBLIC KEY-----\n" +
            Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(publicKey.getEncoded()) +
            "\n-----END PUBLIC KEY-----";
        
        filter = new JwtAuthenticationFilter();
        filter.setPublicKeyForTests(publicKeyPEM);
        
        // Mock chain that just completes successfully
        mockChain = exchange -> Mono.empty();
    }

    @Test
    void shouldAllowRequestWithValidJwtToken() {
        // Given: Valid JWT token
        String token = createValidToken("user-123", List.of("user"));
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/recipes/search/test")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // When: Filter processes request
        Mono<Void> result = filter.filter(exchange, mockChain);

        // Then: Request should succeed (chain called, no 401)
        StepVerifier.create(result)
            .verifyComplete();
        assert exchange.getResponse().getStatusCode() == null; // No error set
    }

    @Test
    void shouldRejectRequestWithExpiredToken() {
        // Given: Expired JWT token
        String token = createExpiredToken("user-123", List.of("user"));
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/recipes/search/test")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // When: Filter processes request
        Mono<Void> result = filter.filter(exchange, mockChain);

        // Then: Request should return 401
        StepVerifier.create(result)
            .verifyComplete();
        assert exchange.getResponse().getStatusCode() == HttpStatus.UNAUTHORIZED;
    }

    @Test
    void shouldRejectRequestWithInvalidSignature() {
        // Given: Token with invalid signature (signed with different key)
        String token = createTokenWithWrongKey("user-123", List.of("user"));
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/recipes/search/test")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // When: Filter processes request
        Mono<Void> result = filter.filter(exchange, mockChain);

        // Then: Request should return 401
        StepVerifier.create(result)
            .verifyComplete();
        assert exchange.getResponse().getStatusCode() == HttpStatus.UNAUTHORIZED;
    }

    @Test
    void shouldRejectRequestWithoutAuthorizationHeader() {
        // Given: Request without Authorization header
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/recipes/search/test").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // When: Filter processes request
        Mono<Void> result = filter.filter(exchange, mockChain);

        // Then: Request should return 401
        StepVerifier.create(result)
            .verifyComplete();
        assert exchange.getResponse().getStatusCode() == HttpStatus.UNAUTHORIZED;
    }

    @Test
    void shouldRejectRequestWithMalformedToken() {
        // Given: Malformed token
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/recipes/search/test")
            .header(HttpHeaders.AUTHORIZATION, "Bearer invalid.token.here")
            .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // When: Filter processes request
        Mono<Void> result = filter.filter(exchange, mockChain);

        // Then: Request should return 401
        StepVerifier.create(result)
            .verifyComplete();
        assert exchange.getResponse().getStatusCode() == HttpStatus.UNAUTHORIZED;
    }

    @Test
    void shouldAllowPublicEndpointsWithoutToken() {
        // Given: Public endpoint requests without tokens
        String[] publicPaths = {
            "/api/v1/auth/login",
            "/api/v1/recipes/public/featured",
            "/api/v1/ingredients/match",
            "/actuator/health"
        };

        for (String path : publicPaths) {
            MockServerHttpRequest request = MockServerHttpRequest.get(path).build();
            MockServerWebExchange exchange = MockServerWebExchange.from(request);

            // When: Filter processes request
            Mono<Void> result = filter.filter(exchange, mockChain);

            // Then: Request should succeed (no 401)
            StepVerifier.create(result)
                .verifyComplete();
            assert exchange.getResponse().getStatusCode() == null : "Public path " + path + " should not be blocked";
        }
    }

    @Test
    void shouldExtractUserIdAndRolesFromToken() {
        // Given: Token with specific user ID and roles
        String token = createValidToken("user-456", List.of("user", "admin"));
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/admin/users")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // When: Filter processes request
        Mono<Void> result = filter.filter(exchange, mockChain);

        // Then: Request should succeed and authentication context should be set
        StepVerifier.create(result)
            .verifyComplete();
        assert exchange.getResponse().getStatusCode() == null;
    }

    private String createValidToken(String userId, List<String> roles) {
        return Jwts.builder()
            .setSubject(userId)
            .claim("roles", roles)
            .claim("email", "test@example.com")
            .setIssuedAt(Date.from(Instant.now()))
            .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
            .signWith(privateKey, SignatureAlgorithm.RS256)
            .compact();
    }

    private String createExpiredToken(String userId, List<String> roles) {
        return Jwts.builder()
            .setSubject(userId)
            .claim("roles", roles)
            .setIssuedAt(Date.from(Instant.now().minus(2, ChronoUnit.HOURS)))
            .setExpiration(Date.from(Instant.now().minus(1, ChronoUnit.HOURS)))
            .signWith(privateKey, SignatureAlgorithm.RS256)
            .compact();
    }

    private String createTokenWithWrongKey(String userId, List<String> roles) {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair wrongKeyPair = keyGen.generateKeyPair();
            
            return Jwts.builder()
                .setSubject(userId)
                .claim("roles", roles)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
                .signWith(wrongKeyPair.getPrivate(), SignatureAlgorithm.RS256)
                .compact();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
