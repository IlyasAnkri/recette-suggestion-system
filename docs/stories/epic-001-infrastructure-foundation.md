# Epic 001: Core Infrastructure & Microservices Foundation

## Epic Goal

Establish the foundational infrastructure for the Recipe Adjuster platform, including Kafka event bus, API Gateway, service discovery, and local development environment to enable parallel microservice development.

## Epic Description

**Existing System Context:**
- Greenfield project (new development)
- Technology stack: Spring Boot 3.2.x, Apache Kafka 3.6.x, Docker, Spring Cloud Gateway
- Zero-budget constraint: Confluent Cloud free tier, Render.com free tier

**Enhancement Details:**

**What's being built:**
- Apache Kafka cluster setup on Confluent Cloud (free tier)
- Spring Cloud Gateway as API Gateway with routing, authentication, and rate limiting
- Docker Compose environment for local development (PostgreSQL, MongoDB, Redis, Kafka, Zookeeper)
- Spring Cloud Config for centralized configuration management
- Base microservice template with common dependencies and structure
- Kafka topic definitions for core events (ingredient.submitted, recipe.matched, substitution.requested, user.preference.updated, analytics.event)

**How it integrates:**
- API Gateway will route all client requests to appropriate microservices
- Kafka event bus enables asynchronous communication between services
- Docker Compose provides consistent local development environment
- All microservices will follow the established template structure

**Success criteria:**
- API Gateway successfully routes requests to mock services
- Kafka topics created and accessible from local environment
- Docker Compose starts all infrastructure services without errors
- Base microservice template can be cloned and deployed
- Health checks pass for all infrastructure components

## Stories

1. **Story 1.1:** Set up Confluent Cloud Kafka cluster and create core event topics
   - Configure free-tier Kafka cluster
   - Create topics with appropriate partitions and retention
   - Generate API keys and configure local access
   - Document connection strings and configuration

2. **Story 1.2:** Implement Spring Cloud Gateway with routing and security filters
   - Set up Spring Cloud Gateway project
   - Configure routes for all 8 microservices
   - Implement JWT authentication filter
   - Add rate limiting (10 req/sec per IP)
   - Configure CORS for frontend domain

3. **Story 1.3:** Create Docker Compose local development environment
   - Define PostgreSQL, MongoDB, Redis, Kafka, Zookeeper services
   - Configure volume mounts for data persistence
   - Create initialization scripts for databases
   - Document startup and teardown procedures

4. **Story 1.4:** Build base microservice template with common structure
   - Create Spring Boot 3.2.x project template
   - Add common dependencies (Spring Web, Kafka, Actuator, Security)
   - Implement standard package structure (controller, service, repository, model, kafka)
   - Add global exception handler and logging configuration
   - Create Dockerfile for containerization

5. **Story 1.5:** Set up Spring Cloud Config for centralized configuration
   - Create Config Server project
   - Define configuration profiles (dev, staging, prod)
   - Store Kafka connection strings, database URLs
   - Implement encryption for sensitive values
   - Configure microservices to pull from Config Server

## Compatibility Requirements

- [x] All services use Spring Boot 3.2.x for consistency
- [x] Kafka client version matches Confluent Cloud requirements
- [x] Docker Compose version 3.8+ for modern features
- [x] Java 21 LTS for all microservices
- [x] API Gateway supports both HTTP/1.1 and HTTP/2

## Risk Mitigation

**Primary Risk:** Kafka free tier limits (1 cluster, 1GB storage, 10 partitions) may be insufficient for development
**Mitigation:** 
- Use local Kafka in Docker Compose for development
- Reserve Confluent Cloud for staging/production testing
- Monitor partition and storage usage closely
- Plan migration to paid tier or self-hosted Kafka if limits exceeded

**Rollback Plan:** 
- Revert to local Kafka only if Confluent Cloud issues arise
- API Gateway can be temporarily bypassed with direct service calls
- Docker Compose can be stopped and restarted without data loss (volumes persist)

## Definition of Done

- [x] Confluent Cloud Kafka cluster operational with all 5 core topics
- [x] API Gateway routes requests to all 8 microservice endpoints (mocked initially)
- [x] Docker Compose starts all services successfully on fresh clone
- [x] Base microservice template documented and ready for cloning
- [x] Spring Cloud Config serves configuration to all services
- [x] Health checks return 200 OK for all infrastructure components
- [x] README with setup instructions completed
- [x] No hardcoded credentials (all externalized to Config Server)

## Dependencies

- None (foundational epic)

## Timeline Estimate

**4-6 weeks** (as per brainstorming session Priority #1)

## Technical Notes

- Use `confluentinc/cp-kafka:7.5.0` Docker image for local Kafka
- API Gateway port: 8080
- Kafka bootstrap servers: localhost:9092 (local), cloud endpoint (Confluent)
- Config Server port: 8888
- All microservices will use ports 8081-8088

## Acceptance Criteria

1. Developer can clone repo, run `docker-compose up`, and have full local environment running
2. API Gateway successfully authenticates requests with JWT tokens
3. Kafka topics visible in Confluent Cloud console
4. Base microservice template can be instantiated for new service in <15 minutes
5. Configuration changes in Config Server propagate to services without restart (Spring Cloud Bus)
