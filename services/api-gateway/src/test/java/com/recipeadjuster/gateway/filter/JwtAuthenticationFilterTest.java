package com.recipeadjuster.gateway.filter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtAuthenticationFilterTest {

    private JwtAuthenticationFilter filter;
    private KeyPair keyPair;
    private WebFilterChain chain;

    @BeforeEach
    void setUp() throws Exception {
        filter = new JwtAuthenticationFilter();
        keyPair = Keys.keyPairFor(SignatureAlgorithm.RS256);
        
        String publicKeyPem = "-----BEGIN PUBLIC KEY-----\n" +
            Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()) +
            "\n-----END PUBLIC KEY-----";
        
        filter.setPublicKeyForTests(publicKeyPem);
        
        chain = mock(WebFilterChain.class);
        when(chain.filter(any())).thenReturn(Mono.empty());
    }

    @Test
    void shouldAllowPublicEndpoints() {
        MockServerHttpRequest request = MockServerHttpRequest
            .get("/api/v1/auth/login")
            .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(filter.filter(exchange, chain))
            .verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isNull();
    }

    @Test
    void shouldRejectRequestWithoutToken() {
        MockServerHttpRequest request = MockServerHttpRequest
            .get("/api/v1/users/me")
            .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(filter.filter(exchange, chain))
            .verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldAcceptValidToken() {
        String token = generateToken("user-123", "user@example.com", List.of("USER"));
        
        MockServerHttpRequest request = MockServerHttpRequest
            .get("/api/v1/users/me")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(filter.filter(exchange, chain))
            .verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isNull();
    }

    @Test
    void shouldAddUserContextHeaders() {
        String token = generateToken("user-456", "test@example.com", List.of("USER", "ADMIN"));
        
        MockServerHttpRequest request = MockServerHttpRequest
            .get("/api/v1/recipes")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        when(chain.filter(any())).thenAnswer(invocation -> {
            MockServerWebExchange mutatedExchange = invocation.getArgument(0);
            assertThat(mutatedExchange.getRequest().getHeaders().getFirst("X-User-Id"))
                .isEqualTo("user-456");
            assertThat(mutatedExchange.getRequest().getHeaders().getFirst("X-User-Email"))
                .isEqualTo("test@example.com");
            assertThat(mutatedExchange.getRequest().getHeaders().getFirst("X-User-Roles"))
                .isEqualTo("USER,ADMIN");
            return Mono.empty();
        });

        StepVerifier.create(filter.filter(exchange, chain))
            .verifyComplete();
    }

    @Test
    void shouldRejectExpiredToken() {
        String token = generateExpiredToken("user-789", "expired@example.com");
        
        MockServerHttpRequest request = MockServerHttpRequest
            .get("/api/v1/users/me")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(filter.filter(exchange, chain))
            .verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldRejectMalformedToken() {
        MockServerHttpRequest request = MockServerHttpRequest
            .get("/api/v1/users/me")
            .header(HttpHeaders.AUTHORIZATION, "Bearer invalid.token.here")
            .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(filter.filter(exchange, chain))
            .verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private String generateToken(String userId, String email, List<String> roles) {
        Instant now = Instant.now();
        Instant expiry = now.plus(1, ChronoUnit.HOURS);

        return Jwts.builder()
            .setSubject(userId)
            .claim("email", email)
            .claim("roles", roles)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(expiry))
            .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
            .compact();
    }

    private String generateExpiredToken(String userId, String email) {
        Instant past = Instant.now().minus(2, ChronoUnit.HOURS);
        Instant expiry = Instant.now().minus(1, ChronoUnit.HOURS);

        return Jwts.builder()
            .setSubject(userId)
            .claim("email", email)
            .claim("roles", List.of("USER"))
            .setIssuedAt(Date.from(past))
            .setExpiration(Date.from(expiry))
            .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
            .compact();
    }
}
