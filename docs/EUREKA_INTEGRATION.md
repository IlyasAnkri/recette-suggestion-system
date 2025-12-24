# Eureka Discovery Integration

## Overview

Eureka Discovery (Netflix Eureka) has been successfully integrated into the Recipe Adjuster microservices architecture. This enables automatic service registration and discovery, allowing services to find and communicate with each other dynamically.

## Architecture

### Eureka Server
- **Location:** `services/eureka-server/`
- **Port:** 8761
- **URL:** http://localhost:8761/eureka/
- **Dashboard:** http://localhost:8761

### Registered Services

All microservices are configured as Eureka clients and will automatically register with the Eureka server:

1. **API Gateway** (port 8080)
2. **Ingredient Matching Service** (port 8081)
3. **Recipe Search Service** (port 8082)
4. **Substitution Engine Service** (port 8083)
5. **Recipe Database Service** (port 8085)
6. **User Profile Service** (port 8086)
7. **Analytics Service** (port 8086)

## Implementation Details

### 1. Eureka Server Configuration

**Dependencies (`eureka-server/pom.xml`):**
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

**Main Class:**
```java
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

**Configuration (`application.yml`):**
```yaml
server:
  port: 8761

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
  server:
    enable-self-preservation: false
```

### 2. Eureka Client Configuration (All Microservices)

**Dependencies (added to all service `pom.xml`):**
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

**Annotations (added to all main application classes):**
```java
@SpringBootApplication
@EnableDiscoveryClient
public class ServiceApplication {
    // ...
}
```

**Configuration (added to all `application.yml`):**
```yaml
eureka:
  client:
    service-url:
      defaultZone: ${EUREKA_SERVER_URL:http://localhost:8761/eureka/}
    register-with-eureka: true
    fetch-registry: true
  instance:
    prefer-ip-address: true
    instance-id: ${spring.application.name}:${random.value}
```

## Usage

### Starting the Services

1. **Start Eureka Server first:**
   ```bash
   cd services/eureka-server
   mvn spring-boot:run
   ```

2. **Start other microservices** (in any order):
   ```bash
   cd services/api-gateway
   mvn spring-boot:run
   
   cd services/ingredient-matching-service
   mvn spring-boot:run
   
   # ... etc
   ```

3. **Access Eureka Dashboard:**
   Open http://localhost:8761 in your browser to see all registered services.

### Service Discovery in Code

Services can now discover each other using the service name instead of hardcoded URLs:

**Example (API Gateway routes):**
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: ingredient-matching
          uri: lb://ingredient-matching-service  # Load-balanced via Eureka
          predicates:
            - Path=/api/v1/ingredients/**
```

**Example (RestTemplate/WebClient):**
```java
@Bean
@LoadBalanced
public RestTemplate restTemplate() {
    return new RestTemplate();
}

// Usage
String url = "http://ingredient-matching-service/api/v1/ingredients";
ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
```

## Environment Variables

You can override the Eureka server URL using environment variables:

```bash
export EUREKA_SERVER_URL=http://eureka-server:8761/eureka/
```

## Benefits

1. **Dynamic Service Discovery:** Services automatically find each other without hardcoded URLs
2. **Load Balancing:** Built-in client-side load balancing with Ribbon
3. **Health Monitoring:** Eureka tracks service health and removes unhealthy instances
4. **Scalability:** Easy to scale services horizontally
5. **Fault Tolerance:** Automatic failover to healthy instances

## Monitoring

### Eureka Dashboard
- Access: http://localhost:8761
- Shows all registered instances
- Displays health status
- Shows last heartbeat time

### Service Health Checks
Each service sends heartbeats to Eureka every 30 seconds (default). If a service fails to send heartbeats, it will be removed from the registry after 90 seconds.

## Troubleshooting

### Service not appearing in Eureka Dashboard
1. Check if Eureka Server is running on port 8761
2. Verify `eureka.client.service-url.defaultZone` in service configuration
3. Check service logs for connection errors
4. Ensure `@EnableDiscoveryClient` annotation is present

### Service shows as DOWN
1. Check service health endpoint: `http://localhost:<port>/actuator/health`
2. Verify network connectivity between service and Eureka
3. Check for port conflicts

### Load balancing not working
1. Ensure `@LoadBalanced` annotation on RestTemplate/WebClient bean
2. Use service name (not IP/hostname) in URLs
3. Verify multiple instances are registered in Eureka

## Production Considerations

1. **High Availability:** Deploy multiple Eureka servers in production
2. **Security:** Enable authentication for Eureka dashboard
3. **Network Configuration:** Use proper DNS or service mesh
4. **Monitoring:** Integrate with Prometheus/Grafana for metrics
5. **Self-Preservation:** Configure appropriately for production workloads

## Next Steps

- Configure Eureka Server clustering for high availability
- Add Spring Cloud Config Server integration
- Implement circuit breakers with Resilience4j
- Add distributed tracing with Zipkin/Jaeger
- Configure security for Eureka endpoints

## References

- [Spring Cloud Netflix Eureka Documentation](https://cloud.spring.io/spring-cloud-netflix/reference/html/)
- [Eureka GitHub Repository](https://github.com/Netflix/eureka)
- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
