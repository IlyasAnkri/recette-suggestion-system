# Epic 007: Analytics & Monitoring

## Epic Goal

Implement comprehensive analytics tracking and observability infrastructure to understand user behavior, monitor system health, and identify performance bottlenecks across all microservices.

## Epic Description

**Existing System Context:**
- All microservices from Epics 001-006 (8 services total)
- Kafka event bus with events from all services
- Technology stack: Spring Boot Actuator, Prometheus, Grafana Cloud (free tier), Loki, Tempo
- Integration points: Kafka analytics events, Spring Boot Actuator endpoints, Prometheus metrics

**Enhancement Details:**

**What's being built:**
- **Analytics Service**: Consumes Kafka events, aggregates usage data, stores in PostgreSQL
- **Prometheus Metrics**: JVM metrics, HTTP request metrics, Kafka consumer lag, custom business metrics
- **Grafana Dashboards**: System health, user activity, recipe popularity, substitution success rates
- **Distributed Tracing**: OpenTelemetry integration for request flow visualization
- **Structured Logging**: JSON logs with correlation IDs, centralized in Grafana Loki
- **Alerting**: Error rate, latency, consumer lag, service health check failures

**How it integrates:**
- Analytics Service consumes all Kafka events (ingredient.submitted, recipe.matched, substitution.requested, user.preference.updated, recipe.created)
- Spring Boot Actuator exposes `/actuator/prometheus` endpoint for metrics scraping
- Prometheus scrapes metrics from all microservices every 15 seconds
- Grafana Cloud visualizes metrics and logs (free tier: 10K series, 50GB logs)
- OpenTelemetry exports traces to Grafana Tempo (free tier: 50GB traces)
- Alerts sent to email or Slack when thresholds exceeded

**Success criteria:**
- Analytics Service tracks daily active users, recipe searches, substitution requests
- Grafana dashboard shows real-time system health (CPU, memory, request rate, error rate)
- Distributed tracing visualizes request flow across microservices
- Alerts trigger when error rate >1% for 5 minutes
- Logs searchable by correlation ID, user ID, or error message
- Business metrics: top 10 recipes, most-searched ingredients, substitution success rate

## Stories

1. **Story 7.1:** Implement Analytics Service with Kafka event consumption
   - Create Spring Boot microservice from base template
   - Consume all Kafka events (ingredient.submitted, recipe.matched, substitution.requested, user.preference.updated, recipe.created, analytics.event)
   - Store aggregated data in PostgreSQL (daily_active_users, recipe_searches, substitution_requests, popular_recipes, popular_ingredients)
   - Implement scheduled jobs (daily aggregation at midnight UTC)
   - Expose analytics API endpoints (GET `/api/v1/analytics/dashboard`, GET `/api/v1/analytics/recipes/popular`)

2. **Story 7.2:** Configure Spring Boot Actuator and Prometheus metrics
   - Add Spring Boot Actuator dependency to all microservices
   - Enable `/actuator/health`, `/actuator/info`, `/actuator/prometheus` endpoints
   - Configure Micrometer registry for Prometheus
   - Add custom business metrics (searches_per_minute, substitutions_per_minute, recipe_views_total)
   - Tag metrics with service name and environment (dev, staging, prod)
   - Secure actuator endpoints (require admin role or internal network access)

3. **Story 7.3:** Set up Prometheus and Grafana Cloud
   - Create Grafana Cloud account (free tier: 10K series, 50GB logs, 50GB traces)
   - Configure Prometheus to scrape all microservices (scrape interval 15s)
   - Create Grafana dashboards:
     - **System Health**: CPU, memory, JVM heap, GC pauses, thread count
     - **HTTP Metrics**: Request rate, latency (P50, P95, P99), error rate
     - **Kafka Metrics**: Consumer lag, message throughput, partition assignment
     - **Business Metrics**: DAU, recipe searches, substitution requests, top recipes
   - Add dashboard variables (service, environment, time range)

4. **Story 7.4:** Implement distributed tracing with OpenTelemetry
   - Add OpenTelemetry dependencies to all microservices
   - Configure trace sampling (100% for dev, 10% for prod)
   - Export traces to Grafana Tempo (free tier: 50GB traces)
   - Add trace context propagation across HTTP and Kafka
   - Instrument critical paths (ingredient search, recipe detail, substitution generation)
   - Visualize traces in Grafana (request flow, span duration, error traces)

5. **Story 7.5:** Implement structured logging with Grafana Loki
   - Configure Logback to output JSON logs
   - Add correlation ID to all log entries (MDC context)
   - Include metadata: timestamp, level, service, traceId, spanId, userId, message
   - Ship logs to Grafana Loki (free tier: 50GB logs)
   - Create log queries (filter by service, level, userId, error message)
   - Add log panels to Grafana dashboards

6. **Story 7.6:** Set up alerting rules and notifications
   - Create Prometheus alerting rules:
     - Error rate >1% for 5 minutes
     - P99 latency >2 seconds for 5 minutes
     - Kafka consumer lag >1000 messages
     - Service health check failures (3 consecutive failures)
     - JVM heap usage >85%
   - Configure Grafana alerting (send to email or Slack)
   - Test alerts by simulating failures (stop service, inject errors)
   - Document alert response procedures (runbooks)

7. **Story 7.7:** Implement performance testing and benchmarking
   - Set up Gatling for load testing (or JMeter as alternative)
   - Define performance benchmarks:
     - API response time <500ms for 95th percentile
     - Throughput >100 requests/second per service
     - Kafka consumer lag <100 messages under load
     - Database query time <100ms for 95th percentile
   - Create load test scenarios:
     - Normal load: 50 concurrent users
     - Peak load: 100 concurrent users
     - Stress test: 200 concurrent users (find breaking point)
   - Test free-tier limits:
     - Render.com: 750 hrs/month per service
     - Netlify: 100GB bandwidth/month
     - MongoDB Atlas: 512MB storage, connection limits
     - Confluent Cloud Kafka: 1GB storage, throughput limits
   - Create performance regression tests (run on every release)
   - Add performance dashboards to Grafana (response time trends, throughput, error rates)
   - Document performance baselines and optimization recommendations

## Compatibility Requirements

- [x] Grafana Cloud free tier: 10K series, 50GB logs, 50GB traces
- [x] Prometheus scrape interval: 15 seconds (balance freshness vs. load)
- [x] OpenTelemetry trace sampling: 100% dev, 10% prod (reduce overhead)
- [x] Structured logs follow JSON format with consistent fields
- [x] Correlation IDs propagate across HTTP and Kafka boundaries

## Risk Mitigation

**Primary Risk:** Grafana Cloud free tier limits (10K series, 50GB logs) may be exceeded
**Mitigation:**
- Monitor usage via Grafana Cloud dashboard
- Reduce metric cardinality (limit tag values, aggregate low-value metrics)
- Reduce trace sampling rate (10% → 5% if needed)
- Implement log sampling (only log errors and warnings in prod)
- Plan upgrade to paid tier ($49/month for 15K series, 100GB logs) if needed

**Secondary Risk:** High cardinality metrics (e.g., userId tag) may cause performance issues
**Mitigation:**
- Avoid userId in metric tags (use aggregated metrics instead)
- Limit tag values to known set (service, environment, status_code)
- Use exemplars for high-cardinality data (link metrics to traces)
- Monitor Prometheus memory usage (alert if >2GB)

**Rollback Plan:**
- Disable OpenTelemetry tracing if causing performance issues
- Reduce Prometheus scrape frequency (15s → 60s)
- Disable structured logging and use plain text logs

## Definition of Done

- [x] Analytics Service deployed and consuming Kafka events
- [x] Prometheus scraping metrics from all 8 microservices
- [x] Grafana dashboards created (System Health, HTTP Metrics, Kafka Metrics, Business Metrics, Performance)
- [x] Distributed tracing visualizes request flow across services
- [x] Structured logs searchable in Grafana Loki
- [x] Alerting rules configured and tested (error rate, latency, consumer lag)
- [x] Alerts sent to email or Slack when thresholds exceeded
- [x] Analytics API endpoints return aggregated data (DAU, popular recipes)
- [x] Documentation: Grafana dashboard guide, alert response runbooks
- [x] Integration tests verify metrics and traces are exported correctly
- [x] Performance tests validate API response time <500ms (95th percentile)
- [x] Load tests verify system handles 100 concurrent users
- [x] Free-tier limits documented and tested

## Dependencies

- **Epic 001**: Infrastructure Foundation (Kafka, PostgreSQL)
- **All Epics 002-006**: Microservices to monitor

## Timeline Estimate

**3-4 weeks** (can start in parallel with Epic 006, extended for performance testing)

## Technical Notes

- Analytics Service port: 8086
- Prometheus scrape endpoint: `/actuator/prometheus`
- Grafana Cloud URL: `https://recipe-adjuster.grafana.net`
- OpenTelemetry collector: Grafana Agent (installed on each service host)
- Correlation ID header: `X-Correlation-ID` (UUID format)
- Log format: `{"timestamp":"2025-12-19T12:34:56Z","level":"INFO","service":"ingredient-matching","traceId":"abc123","spanId":"def456","message":"Recipe matched"}`

## Acceptance Criteria

1. Grafana dashboard shows real-time CPU usage for all 8 microservices
2. HTTP request rate metric increases when user searches recipes
3. Distributed trace shows request flow: API Gateway → Ingredient Matching → Recipe Search
4. Structured log query `{service="substitution-engine"} |= "error"` returns error logs
5. Alert triggers when error rate >1% for 5 minutes (tested by injecting errors)
6. Analytics API returns top 10 recipes sorted by view count
7. Kafka consumer lag metric shows <100 messages for all consumers
8. Correlation ID propagates from frontend request to all backend services
9. Grafana dashboard shows daily active users (DAU) trend over 7 days
10. Alert sent to Slack when service health check fails 3 times consecutively
11. Gatling load test completes with 100 concurrent users, API response time <500ms (95th percentile)
12. Performance dashboard shows response time trends over 7 days
13. Free-tier limits tested: Render services handle expected load, Kafka throughput within limits
14. Performance regression tests integrated into CI pipeline
