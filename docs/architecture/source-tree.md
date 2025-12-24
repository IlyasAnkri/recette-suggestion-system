# Source Tree

## 8. Project Structure

### 8.1 Monorepo Layout

```
recipe-adjuster/
├── .github/
│   └── workflows/
│       ├── ci.yml
│       ├── deploy-backend.yml
│       └── deploy-frontend.yml
├── services/
│   ├── api-gateway/
│   ├── ingredient-matching-service/
│   ├── recipe-search-service/
│   ├── substitution-engine-service/
│   ├── quantity-adjustment-service/
│   ├── user-profile-service/
│   ├── recipe-database-service/
│   ├── notification-service/
│   └── analytics-service/
├── frontend/
│   └── recipe-adjuster-web/
├── shared/
│   ├── common-models/
│   ├── event-schemas/
│   └── testing-utils/
├── infrastructure/
│   ├── docker/
│   │   └── docker-compose.yml
│   ├── k8s/  (future)
│   └── terraform/  (future)
├── docs/
│   ├── architecture.md
│   ├── api-docs/
│   └── adr/  (Architecture Decision Records)
├── scripts/
│   ├── setup-dev.sh
│   └── seed-data.sh
├── pom.xml  (parent POM)
└── README.md
```
