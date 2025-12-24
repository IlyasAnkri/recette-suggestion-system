# Technology & Innovation Research Prompt
## Recipe Adjuster App - Zero-Budget Technical Validation

---

## Research Objective

Validate the technical feasibility and optimal implementation approach for a recipe adjuster application built with **Spring Boot microservices, Angular 20, Spring AI, and Kafka** under a **strict zero-budget constraint**. This research will inform critical architectural decisions, identify free-tier infrastructure limits, and establish a viable technical roadmap for MVP development.

---

## Background Context

**Product Concept:**
- Recipe adjuster app where users input available ingredients
- System suggests recipes with quantity adjustments and intelligent substitutions
- Target: All users (no specific niche), global accessibility

**Technical Requirements (Non-Negotiable):**
- Backend: Java Spring Boot with microservices architecture
- Frontend: Angular 20
- AI: Spring AI for substitution intelligence and semantic search
- Messaging: Apache Kafka for event-driven communication
- Budget: $0 (must use free-tier services exclusively)

**Proposed Architecture (from brainstorming):**
- 8 microservices: Ingredient Matching, Recipe Search, Substitution Engine, Quantity Adjustment, User Profile, Recipe Database, Notification, Analytics
- Kafka event bus for service communication
- PWA with offline-first design (IndexedDB)
- Community-contributed recipe database

**Key Uncertainties:**
1. Can Spring AI run cost-effectively on free-tier infrastructure?
2. What are exact limits of free Kafka offerings (Confluent Cloud, Redpanda, etc.)?
3. Which free-tier databases support the required data models and query patterns?
4. How to handle microservices deployment without container orchestration costs?
5. What's the performance impact of local LLMs (Ollama) vs. cloud APIs?

---

## Research Questions

### Primary Questions (Must Answer)

1. **Spring AI Model Selection & Costs**
   - Which LLM providers offer free tiers compatible with Spring AI? (OpenAI, Hugging Face, Ollama, others)
   - What are exact token limits, rate limits, and usage caps for each free tier?
   - Can Ollama run efficiently on free-tier compute (Render, Railway, Fly.io)?
   - What's the latency difference between local models (Llama 3.2, Mistral) vs. cloud APIs for substitution queries?
   - Which embedding models work best for semantic ingredient search at zero cost? (all-MiniLM-L6-v2, instructor-xl, etc.)

2. **Kafka Free-Tier Evaluation**
   - Confluent Cloud free tier: exact message throughput limits, retention period, cluster count, topic limits
   - Redpanda Cloud free tier: comparable limits and Spring Boot integration quality
   - Upstash Kafka: pricing model, free tier caps, latency characteristics
   - Self-hosted Kafka on free compute: feasibility on Render/Railway (memory requirements, persistence)
   - Alternative event streaming: Can Redis Pub/Sub or RabbitMQ free tiers substitute for Kafka?

3. **Database Strategy for Zero Budget**
   - PostgreSQL free tiers: Supabase (500 MB), Render (90 days then expires), Neon (3 GB), ElephantSQL (20 MB)
   - MongoDB Atlas free tier: 512 MB storage, connection limits, query performance
   - Which database fits which microservice? (relational for recipes vs. document for user profiles)
   - How to handle database migrations when free tiers expire? (data export/import strategies)
   - Can IndexedDB in Angular PWA reduce backend database load significantly?

4. **Microservices Deployment Without Orchestration**
   - Free-tier container hosting: Render (750 hours/month), Railway ($5 credit), Fly.io (3 VMs), Koyeb
   - Can 8 microservices fit within free-tier limits? (need resource allocation matrix)
   - Monorepo vs. separate repos for microservices deployment?
   - Service discovery without Eureka/Consul costs: Spring Cloud Gateway + hardcoded URLs viable?
   - Health checks, logging, monitoring on zero budget: which tools? (UptimeRobot, Sentry free tier, LogTail)

5. **Angular 20 PWA Performance & Limits**
   - IndexedDB storage limits per browser (Chrome, Safari, Firefox)
   - Service worker caching strategies for recipe data (how many recipes can be cached offline?)
   - Background sync reliability for ingredient updates when connectivity restored
   - PWA installation rates: what % of users actually "Add to Home Screen"?
   - Free CDN options for Angular assets: Netlify, Vercel, Cloudflare Pages (bandwidth limits)

6. **Recipe Data Sourcing & Storage**
   - Legal recipe scraping: which sites allow it? (Allrecipes, Food Network, BBC Good Food terms of service)
   - Public domain recipe databases: USDA, Project Gutenberg cookbooks, government nutrition sites
   - Recipe schema: how much storage per recipe? (ingredients, instructions, images, metadata)
   - Image hosting on zero budget: Cloudinary free tier (25 GB), ImgBB, Imgur API limits
   - Community contribution moderation: can it be automated with Spring AI to reduce manual effort?

7. **Spring Boot Microservices Communication Patterns**
   - Synchronous REST vs. asynchronous Kafka: when to use each for recipe matching workflow?
   - Circuit breaker patterns without Hystrix (deprecated): Resilience4j on free tier?
   - API Gateway: Spring Cloud Gateway resource requirements, can it run on free tier alongside other services?
   - Distributed tracing on zero budget: Zipkin self-hosted vs. Jaeger vs. skip for MVP?
   - Configuration management: Spring Cloud Config Server vs. environment variables?

8. **Performance Benchmarks & Scalability**
   - Expected response time for ingredient matching query with 1000 recipes in database?
   - Spring AI substitution query latency: local Ollama vs. OpenAI API vs. Hugging Face Inference API?
   - Kafka message throughput needed for 100 concurrent users submitting ingredient lists?
   - Can free-tier PostgreSQL handle recipe search queries with full-text search + filtering?
   - At what user count does the free-tier architecture break? (10 users? 100? 1000?)

### Secondary Questions (Nice to Have)

9. **Development & Testing Tools**
   - Local development environment: Docker Compose for all 8 microservices + Kafka + databases feasible on typical dev machine?
   - CI/CD on zero budget: GitHub Actions free tier (2000 minutes/month) sufficient for microservices builds?
   - Automated testing: Testcontainers for integration tests vs. mocked services?
   - API documentation: Swagger/OpenAPI hosting options (Swagger UI self-hosted, Redoc)?

10. **Security & Compliance**
    - Free SSL certificates: Let's Encrypt auto-renewal on Render/Netlify?
    - OAuth 2.0 / JWT authentication: Spring Security setup, where to store secrets? (environment variables, HashiCorp Vault free tier)
    - GDPR compliance for user data: what's required for EU users? Cookie consent, data export, right to deletion
    - Rate limiting without paid API gateway: Spring Boot built-in rate limiting (Bucket4j)?

11. **Migration & Scaling Path**
    - When free tiers are exhausted, what's the cheapest paid upgrade path?
    - Can architecture migrate to AWS Free Tier (EC2 t2.micro, RDS free tier) after 1 year?
    - Which components should be optimized first when hitting limits? (database, Kafka, compute)
    - Estimated cost at 1000 active users, 10,000 users, 100,000 users?

12. **Alternative Tech Stack Validation**
    - Could serverless (AWS Lambda free tier, Cloudflare Workers) replace some microservices?
    - Is GraphQL federation viable for microservices API aggregation on free tier?
    - Would gRPC reduce bandwidth costs vs. REST for inter-service communication?
    - Can WebAssembly (Wasm) run Spring AI models client-side in Angular to eliminate backend AI costs?

---

## Research Methodology

### Information Sources

**Primary Sources (Prioritize):**
1. **Official Documentation:**
   - Spring AI documentation (model providers, embeddings, prompt engineering)
   - Confluent Cloud, Redpanda, Upstash pricing and limits pages
   - Supabase, Neon, Render, Railway, Fly.io free tier specifications
   - Angular PWA documentation (service workers, IndexedDB, background sync)

2. **Technical Benchmarks:**
   - Ollama model performance benchmarks (tokens/sec, memory usage)
   - Spring Boot microservices resource usage studies
   - Kafka throughput benchmarks on constrained resources
   - IndexedDB storage limits and performance tests

3. **Community Resources:**
   - GitHub repos: Spring AI examples, zero-cost microservices deployments
   - Stack Overflow: common pitfalls with free-tier infrastructure
   - Reddit (r/java, r/angular, r/selfhosted): real-world experiences
   - Dev.to / Medium articles: "Building microservices on $0 budget" case studies

**Secondary Sources:**
4. **Comparison Sites:**
   - Free-for.dev (comprehensive free tier catalog)
   - StackShare (tech stack comparisons)
   - DB-Engines (database feature comparisons)

5. **Legal & Compliance:**
   - Recipe website Terms of Service (scraping permissions)
   - GDPR compliance checklists for web apps
   - Open-source license compatibility (Apache 2.0, MIT, GPL)

### Analysis Frameworks

1. **Cost-Benefit Matrix:**
   - For each technology option, map: free tier limits, performance, migration cost, lock-in risk
   - Score on: ease of setup, documentation quality, community support, upgrade path

2. **Technical Feasibility Assessment:**
   - Prototype critical paths: Spring AI substitution query, Kafka event flow, offline PWA sync
   - Measure: latency, resource usage, error rates, user experience impact
   - Decision criteria: <2 sec response time, <500 MB RAM per service, 99% uptime

3. **Risk Analysis:**
   - Identify single points of failure (free tier expiration, rate limit exceeded, service shutdown)
   - Mitigation strategies: data export automation, fallback services, graceful degradation
   - Probability × Impact scoring for each risk

4. **Scalability Modeling:**
   - Calculate resource usage per user (database storage, Kafka messages, API calls)
   - Project when free tiers will be exhausted based on user growth scenarios
   - Identify optimization opportunities (caching, batching, compression)

### Data Requirements

- **Recency:** Prioritize information from 2024-2025 (free tiers change frequently)
- **Credibility:** Official docs > verified benchmarks > community anecdotes
- **Specificity:** Exact numbers (GB, requests/min, $/month) over vague claims
- **Reproducibility:** Prefer sources with code examples or step-by-step guides

---

## Expected Deliverables

### Executive Summary (2-3 pages)

**Key Findings:**
- Recommended Spring AI model and provider for zero-budget deployment
- Optimal Kafka solution (Confluent Cloud vs. Redpanda vs. self-hosted)
- Database strategy per microservice with migration plan
- Deployment architecture fitting within free-tier constraints
- Critical risks and mitigation strategies

**Go/No-Go Recommendation:**
- Is the proposed tech stack viable at zero budget?
- If not, what's the minimum monthly cost to make it work?
- What features must be cut to stay within free tiers?

**Immediate Next Steps:**
- Top 3 technical validations to prototype first
- Infrastructure setup sequence
- Estimated timeline to working MVP

### Detailed Analysis

#### 1. Spring AI Implementation Guide
- Model selection matrix (Ollama Llama 3.2 vs. OpenAI vs. Hugging Face)
- Code examples: substitution query, semantic search, explanation generation
- Performance benchmarks: latency, accuracy, resource usage
- Cost projections at 10/100/1000 users per day

#### 2. Kafka Architecture Decision
- Feature comparison table: Confluent Cloud vs. Redpanda vs. Upstash vs. self-hosted
- Event schema design for recipe matching workflow
- Throughput calculations and free-tier headroom
- Fallback strategy if Kafka limits exceeded

#### 3. Database Strategy Per Microservice
- Service-to-database mapping with rationale
- Schema designs for recipe, ingredient, user profile data
- Query patterns and indexing strategies
- Free-tier expiration timeline and migration plan

#### 4. Microservices Deployment Plan
- Service-to-platform mapping (which services on Render vs. Railway vs. Fly.io)
- Resource allocation matrix (RAM, CPU, storage per service)
- Service discovery and API gateway configuration
- Monitoring and logging setup (free tools)

#### 5. Angular PWA Offline Strategy
- Caching strategy: which recipes to cache, cache invalidation rules
- IndexedDB schema and storage limits per browser
- Background sync implementation for ingredient updates
- Fallback UX when offline and cache empty

#### 6. Recipe Data Sourcing Plan
- Legal scraping targets with terms of service analysis
- Public domain recipe sources and data quality assessment
- Community contribution workflow and moderation automation
- Image hosting strategy and CDN selection

#### 7. Risk Register & Mitigation
- Technical risks: free tier limits, service deprecation, performance bottlenecks
- Business risks: recipe copyright, user adoption, data quality
- Mitigation strategies with implementation effort estimates
- Monitoring triggers to detect risks early

### Supporting Materials

**Data Tables:**
- Free-tier comparison matrix (15+ services across compute, database, messaging, hosting)
- Resource usage projections (per user, per day, per month)
- Cost escalation timeline (when free tiers exhausted at different growth rates)

**Comparison Matrices:**
- Spring AI model comparison (10+ models on accuracy, speed, cost, ease of use)
- Database options per microservice (PostgreSQL vs. MongoDB vs. Redis)
- Deployment platforms (Render vs. Railway vs. Fly.io vs. Koyeb)

**Architecture Diagrams:**
- Microservices communication flow (synchronous REST + asynchronous Kafka)
- Data flow: user ingredient input → recipe suggestions → substitutions
- Deployment topology: which services on which platforms

**Code Snippets:**
- Spring AI substitution query example
- Kafka producer/consumer for ingredient matching event
- Angular service worker caching strategy
- IndexedDB CRUD operations for offline recipes

**Source Documentation:**
- Links to all official documentation referenced
- GitHub repos with working examples
- Benchmark sources with methodology details
- Terms of service excerpts for recipe scraping

---

## Success Criteria

This research will be considered successful if it provides:

1. ✅ **Clear Go/No-Go Decision:** Definitive answer on whether zero-budget tech stack is viable
2. ✅ **Actionable Architecture:** Specific services, platforms, and configurations to use
3. ✅ **Risk Mitigation Plan:** Identified top 5 risks with concrete mitigation strategies
4. ✅ **Prototype Roadmap:** Sequenced list of technical validations to build (with effort estimates)
5. ✅ **Cost Projection Model:** Calculator showing when free tiers exhausted based on user growth
6. ✅ **Migration Strategy:** Clear path from free tier to paid tier when needed

**Validation Tests:**
- Can a developer follow this research to set up the infrastructure in <1 week?
- Are all free-tier limits documented with exact numbers and sources?
- Is there a fallback plan for every critical dependency?
- Can the architecture handle 100 concurrent users within free-tier constraints?

---

## Timeline and Priority

**Phase 1: Critical Path Validation (Week 1)**
- Spring AI model selection and cost analysis
- Kafka free-tier evaluation and event schema design
- Database strategy and free-tier mapping

**Phase 2: Deployment & Infrastructure (Week 2)**
- Microservices deployment platform selection
- Resource allocation and service discovery
- Monitoring and logging setup

**Phase 3: Frontend & Data (Week 3)**
- Angular PWA offline strategy validation
- Recipe data sourcing legal analysis
- Image hosting and CDN selection

**Phase 4: Risk & Optimization (Week 4)**
- Performance benchmarking and bottleneck identification
- Cost projection modeling
- Migration strategy documentation

**Priority Ranking:**
1. **CRITICAL:** Spring AI costs (determines if AI features viable)
2. **CRITICAL:** Kafka free-tier limits (determines if event-driven architecture viable)
3. **HIGH:** Database strategy (determines data model and scalability)
4. **HIGH:** Microservices deployment (determines if 8 services fit in free tier)
5. **MEDIUM:** PWA offline strategy (nice-to-have, not MVP blocker)
6. **MEDIUM:** Recipe data sourcing (can start with small manual dataset)
7. **LOW:** Advanced monitoring (can use basic logging initially)

---

## Execution Guidance

**Recommended Approach:** Hybrid AI + Human Research

1. **Use AI Research Assistant for:**
   - Aggregating free-tier limits from official documentation
   - Comparing technical specifications across platforms
   - Generating code examples for Spring AI, Kafka, Angular PWA
   - Summarizing community experiences from forums/blogs

2. **Require Human Validation for:**
   - Legal analysis of recipe scraping terms of service
   - Performance benchmarking (must run actual tests)
   - Cost projections (verify AI calculations)
   - Architecture decisions (human judgment on trade-offs)

3. **Prototype Before Finalizing:**
   - Build minimal Spring AI substitution query (1 day)
   - Deploy 2 microservices to free tier (1 day)
   - Test Kafka event flow with free-tier limits (1 day)
   - Validate IndexedDB storage in Angular PWA (1 day)

**Integration with Project:**
- Research findings feed directly into **Architecture Document** creation
- Database strategy informs **Data Model Design**
- Deployment plan becomes **DevOps Setup Guide**
- Risk register becomes **Project Risk Log**

**Review Checkpoints:**
- After Phase 1: Go/No-Go decision on zero-budget viability
- After Phase 2: Deployment architecture finalized
- After Phase 3: Data strategy confirmed
- After Phase 4: Complete technical validation document ready

---

**Next Step:** Execute this research prompt using AI research tools (Claude, ChatGPT, Perplexity) or assign to technical team member. Estimated completion: 2-4 weeks depending on depth of prototyping.
