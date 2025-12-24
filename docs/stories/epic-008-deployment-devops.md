# Epic 008: Deployment & DevOps Pipeline

## Epic Goal

Establish automated CI/CD pipelines, zero-budget production infrastructure, and deployment workflows to enable continuous delivery of backend microservices and frontend PWA.

## Epic Description

**Existing System Context:**
- All microservices from Epics 001-007 (8 services total)
- Angular PWA from Epic 005
- Technology stack: GitHub Actions (CI/CD), Render.com (backend), Netlify (frontend), Docker
- Zero-budget constraint: GitHub Actions free tier, Render free tier (750 hrs/month per service), Netlify free tier (100GB bandwidth/month)

**Enhancement Details:**

**What's being built:**
- **CI/CD Pipelines**: GitHub Actions workflows for automated testing, building, and deployment
- **Backend Deployment**: Render.com free tier for 8 microservices (Docker containers)
- **Frontend Deployment**: Netlify free tier for Angular PWA (automatic HTTPS, CDN)
- **Database Deployment**: MongoDB Atlas free tier (512MB), PostgreSQL on Render (90 days → Supabase)
- **Kafka Deployment**: Confluent Cloud free tier (1 cluster, 1GB storage)
- **Environment Management**: Dev, staging, prod environments with separate configurations
- **Deployment Monitoring**: Health checks, rollback procedures, deployment notifications

**How it integrates:**
- GitHub Actions triggered on push to `main` branch (production) or `develop` branch (staging)
- Backend services built as Docker images, pushed to Render.com
- Frontend built and deployed to Netlify with automatic HTTPS
- Environment variables stored in GitHub Secrets (API keys, database URLs)
- Deployment notifications sent to Slack on success/failure
- Health checks verify services are running after deployment

**Success criteria:**
- Push to `main` branch triggers automated deployment to production
- All tests pass before deployment (unit, integration, E2E)
- Backend services deploy to Render.com without manual intervention
- Frontend deploys to Netlify with automatic HTTPS and CDN
- Rollback procedure documented and tested
- Deployment time <10 minutes from push to production

## Stories

1. **Story 8.1:** Set up GitHub Actions CI pipeline for backend services
   - Create `.github/workflows/ci-backend.yml`
   - Run on push to `main`, `develop`, and pull requests
   - Steps: Checkout code, Set up Java 21, Cache Maven dependencies, Run tests (`mvn clean verify`), Upload test reports
   - Run tests for all 8 microservices in parallel (matrix strategy)
   - Fail pipeline if any test fails or code coverage <80%
   - Add status badge to README

2. **Story 8.2:** Set up GitHub Actions CI pipeline for frontend
   - Create `.github/workflows/ci-frontend.yml`
   - Run on push to `main`, `develop`, and pull requests
   - Steps: Checkout code, Set up Node 20, Cache npm dependencies, Install dependencies (`npm ci`), Run linter (`npm run lint`), Run tests (`npm run test:ci`), Build production (`npm run build:prod`)
   - Fail pipeline if linter errors, test failures, or build errors
   - Upload build artifacts for deployment

3. **Story 8.3:** Set up Render.com deployment for backend services
   - Create Render account and project
   - Configure 8 web services (one per microservice)
   - Set Docker build context and Dockerfile path
   - Configure environment variables (Kafka URL, MongoDB URL, PostgreSQL URL, JWT secret)
   - Set health check endpoint (`/actuator/health`)
   - Configure auto-deploy on push to `main` branch (via Render GitHub integration)
   - Document Render free tier limits (750 hrs/month per service, spins down after 15 min inactivity)

4. **Story 8.4:** Set up Netlify deployment for frontend PWA
   - Create Netlify account and site
   - Connect GitHub repository (recipe-adjuster-web)
   - Configure build settings (build command: `npm run build:prod`, publish directory: `dist/recipe-adjuster-web`)
   - Set environment variables (API base URL: `https://api.recipe-adjuster.com`)
   - Enable automatic HTTPS (Let's Encrypt)
   - Configure custom domain (optional: `recipe-adjuster.com`)
   - Add `_redirects` file for Angular routing (SPA fallback)

5. **Story 8.5:** Create GitHub Actions CD pipeline for production deployment
   - Create `.github/workflows/deploy-production.yml`
   - Trigger on push to `main` branch (after CI passes)
   - Backend deployment: Trigger Render.com deploy via API (POST to deploy hook URL)
   - Frontend deployment: Trigger Netlify deploy via API (POST to build hook URL)
   - Wait for deployments to complete (poll status endpoints)
   - Run smoke tests after deployment (health checks, critical API endpoints)
   - Send Slack notification on success/failure

6. **Story 8.6:** Set up staging environment and deployment workflow
   - Create staging environment on Render.com (separate services)
   - Create staging site on Netlify (subdomain: `staging.recipe-adjuster.com`)
   - Create `.github/workflows/deploy-staging.yml`
   - Trigger on push to `develop` branch
   - Deploy to staging environment (same process as production)
   - Run E2E tests against staging environment (Playwright)
   - Require manual approval before promoting to production

7. **Story 8.7:** Configure database and Kafka deployments
   - Set up MongoDB Atlas free tier (512MB, shared cluster)
   - Set up PostgreSQL on Render (90 days free, then migrate to Supabase)
   - Set up Confluent Cloud Kafka (free tier: 1 cluster, 1GB storage)
   - Store connection strings in GitHub Secrets
   - Document database migration procedures (Render → Supabase after 90 days)
   - Create database backup scripts (weekly exports to GitHub)

8. **Story 8.8:** Implement rollback procedures and deployment monitoring
   - Document rollback procedure (revert Git commit, redeploy previous version)
   - Add rollback script (`scripts/rollback.sh`) that reverts to last known good deployment
   - Configure Render.com to keep last 3 deployments for quick rollback
   - Set up deployment monitoring (health checks every 5 minutes)
   - Create runbook for common deployment issues (service won't start, database connection failure, Kafka unavailable)
   - Add deployment history tracking (log all deployments to PostgreSQL)

9. **Story 8.9:** Implement contract testing with Pact
   - Set up Pact Broker (free tier: pactflow.io or self-hosted)
   - Add Pact provider tests to all 8 microservices (verify API contracts)
   - Add Pact consumer tests to API Gateway (define expected contracts)
   - Implement Kafka event schema validation with Confluent Schema Registry
   - Add contract tests to CI pipeline (fail build on contract violations)
   - Document contract testing workflow and versioning strategy
   - Test OAuth2 provider contracts (Google, GitHub) with mock responses

10. **Story 8.10:** Implement automated security testing
   - Add OWASP ZAP automated scanning to CI pipeline (baseline and full scans)
   - Enable Dependabot for dependency vulnerability scanning (GitHub native)
   - Add Snyk for container image scanning (Docker images)
   - Implement JWT token security tests (expiry validation, signature verification, token refresh)
   - Add SQL/NoSQL injection tests to integration test suite
   - Implement XSS/CSRF tests for frontend endpoints
   - Configure security scan thresholds (fail build on high-severity vulnerabilities)
   - Create security incident response runbook

## Compatibility Requirements

- [x] GitHub Actions free tier: Unlimited minutes for public repos
- [x] Render.com free tier: 750 hrs/month per service (8 services = 6000 hrs/month total)
- [x] Netlify free tier: 100GB bandwidth/month, 300 build minutes/month
- [x] Docker images optimized for fast builds (<5 minutes per service)
- [x] Health checks return 200 OK within 30 seconds of deployment

## Risk Mitigation

**Primary Risk:** Render.com free tier services spin down after 15 minutes of inactivity (cold start delay)
**Mitigation:**
- Implement health check pings every 10 minutes to keep services warm
- Use external uptime monitoring (UptimeRobot free tier: 50 monitors)
- Display "Waking up service..." message in frontend if cold start detected
- Plan upgrade to paid tier ($7/month per service) for always-on services

**Secondary Risk:** Netlify free tier bandwidth (100GB/month) may be exceeded
**Mitigation:**
- Monitor bandwidth usage via Netlify dashboard
- Optimize bundle size (tree shaking, code splitting, image compression)
- Use external CDN for large assets (Cloudinary for images)
- Plan upgrade to paid tier ($19/month for 400GB) if needed

**Rollback Plan:**
- Revert Git commit and redeploy previous version
- Use Render.com "Rollback to previous deploy" button
- Use Netlify "Rollback to previous deploy" button
- Restore database from latest backup if schema migration failed

## Definition of Done

- [x] GitHub Actions CI pipeline runs on every push and pull request
- [x] All tests pass before deployment (unit, integration, E2E, contract, security)
- [x] Backend services deployed to Render.com automatically on push to `main`
- [x] Frontend deployed to Netlify automatically on push to `main`
- [x] Staging environment deployed on push to `develop` branch
- [x] Health checks pass for all services after deployment
- [x] Rollback procedure documented and tested
- [x] Deployment notifications sent to Slack
- [x] Database connection strings stored in GitHub Secrets
- [x] Deployment time <10 minutes from push to production
- [x] Contract tests verify all API and event schemas
- [x] Security scans pass with no high-severity vulnerabilities

## Dependencies

- **All Epics 001-007**: All microservices and frontend must be complete

## Timeline Estimate

**3-4 weeks** (final epic, starts after Epic 007, extended for contract and security testing)

## Technical Notes

- GitHub Actions workflow files: `.github/workflows/`
- Render.com deploy hook URL: `https://api.render.com/deploy/srv-xxx?key=xxx`
- Netlify build hook URL: `https://api.netlify.com/build_hooks/xxx`
- Health check endpoint: `/actuator/health` (returns `{"status":"UP"}`)
- Smoke test endpoints: `/api/v1/recipes`, `/api/v1/ingredients/match`, `/api/v1/substitutions/suggest`
- Deployment notification: Slack webhook URL stored in GitHub Secrets

## Acceptance Criteria

1. Push to `main` branch triggers CI pipeline, all tests pass, deployment starts
2. Backend services deploy to Render.com, health checks return 200 OK
3. Frontend deploys to Netlify, accessible at `https://recipe-adjuster.netlify.app`
4. Staging environment deploys on push to `develop` branch
5. E2E tests run against staging environment, all pass
6. Rollback script reverts to previous deployment successfully
7. Slack notification sent: "Deployment to production successful ✅"
8. Health check pings keep Render services warm (no cold starts during business hours)
9. Deployment time from push to production: 8 minutes (measured)
10. Database backup script runs weekly, exports to GitHub repository
11. Contract tests verify API Gateway routes to all 8 microservices
12. Pact Broker shows all contract versions and verification status
13. OWASP ZAP scan completes with no high-severity vulnerabilities
14. Dependabot creates pull requests for vulnerable dependencies
15. JWT security tests verify token expiry and signature validation
