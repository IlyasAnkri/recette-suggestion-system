# Technology Stack

## 2. Technology Stack

### 2.1 Backend Technologies

| Component | Technology | Version | Justification |
|-----------|------------|---------|---------------|
| **Runtime** | Java | 21 LTS | Long-term support, virtual threads, modern features |
| **Framework** | Spring Boot | 3.2.x | Microservices support, Spring AI integration, mature ecosystem |
| **AI/ML** | Spring AI | 1.0.x | Native Spring integration, multiple LLM provider support |
| **API Gateway** | Spring Cloud Gateway | 4.1.x | Reactive, Spring ecosystem integration |
| **Service Discovery** | Spring Cloud Config | 4.1.x | Centralized configuration management |
| **Messaging** | Apache Kafka | 3.6.x | Event-driven architecture, high throughput |
| **Security** | Spring Security | 6.2.x | OAuth2, JWT, comprehensive security features |

### 2.2 Frontend Technologies

| Component | Technology | Version | Justification |
|-----------|------------|---------|---------------|
| **Framework** | Angular | 20.x | Signals, SSR improvements, strong typing |
| **State Management** | NgRx | 17.x | Redux pattern, predictable state |
| **UI Components** | Angular Material | 17.x | Material Design, accessibility built-in |
| **Offline Storage** | Dexie.js | 4.x | IndexedDB wrapper, TypeScript support |
| **PWA** | @angular/pwa | 17.x | Service workers, offline caching |
| **HTTP Client** | Angular HttpClient | 20.x | Interceptors, typed responses |

### 2.3 Database Technologies

| Database | Use Case | Free Tier Limits |
|----------|----------|------------------|
| **PostgreSQL** (Render) | User profiles, preferences, recipe metadata | 90 days free, then Supabase |
| **MongoDB Atlas** | Recipe documents, ingredient data | 512 MB storage, shared cluster |
| **Redis** (Upstash) | Caching, session storage | 10,000 commands/day |

### 2.4 Infrastructure & DevOps

| Component | Technology | Justification |
|-----------|------------|---------------|
| **Container Runtime** | Docker | Local development consistency |
| **CI/CD** | GitHub Actions | Free for public repos, integrated |
| **Backend Hosting** | Render.com | Free tier, Docker support |
| **Frontend Hosting** | Netlify | Free tier, automatic HTTPS, CDN |
| **Message Broker** | Confluent Cloud | Free tier: 1 cluster, 1 GB storage |
| **Monitoring** | Prometheus + Grafana Cloud | Free tier available |
