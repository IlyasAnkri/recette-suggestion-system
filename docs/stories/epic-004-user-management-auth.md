# Epic 004: User Management & Authentication

## Epic Goal

Implement secure user authentication and profile management with support for guest mode, OAuth2 social login, and user preferences to personalize recipe recommendations.

## Epic Description

**Existing System Context:**
- Infrastructure from Epic 001 (Kafka, API Gateway, PostgreSQL)
- Technology stack: Spring Boot 3.2.x, Spring Security 6.2.x, PostgreSQL (Render free tier 90 days)
- Integration points: API Gateway authentication filter, Kafka `user.preference.updated` events

**Enhancement Details:**

**What's being built:**
- **User Profile Service**: User registration, login, profile management, preferences (dietary restrictions, allergies, cuisine preferences, skill level)
- **Authentication**: JWT tokens (RS256, 1-hour expiry), refresh tokens (HttpOnly cookie, 7-day expiry)
- **OAuth2 Social Login**: Google and GitHub authentication
- **Guest Mode**: Allow users to try app without account creation, store data in browser (IndexedDB)
- **User Preferences**: Dietary restrictions (vegetarian, vegan, gluten-free, keto, halal, kosher), allergies, household size, measurement system

**How it integrates:**
- API Gateway validates JWT tokens for all protected endpoints
- User Profile Service stores user data in PostgreSQL (Render free tier)
- OAuth2 providers (Google, GitHub) redirect to callback endpoint
- Guest users bypass authentication but have limited features (no saved recipes, no preferences sync)
- Preference changes publish `user.preference.updated` events to Kafka for other services

**Success criteria:**
- User can register with email/password and receive JWT token
- User can login with Google or GitHub OAuth2
- Guest users can use app without account (data stored in browser)
- User preferences persist and affect recipe recommendations
- JWT tokens expire after 1 hour, refresh tokens rotate on use
- Password hashing uses BCrypt with cost factor 12

## Stories

1. **Story 4.1:** Set up PostgreSQL database and User/UserPreferences schemas
   - Create PostgreSQL database on Render free tier (90 days)
   - Define User table (id, email, passwordHash, displayName, avatarUrl, createdAt, updatedAt, lastLoginAt, isGuest, authProvider)
   - Define UserPreferences table (userId, dietaryRestrictions, allergies, cuisinePreferences, skillLevel, householdSize, measurementSystem, defaultServings)
   - Create indexes on email (unique) and userId (foreign key)
   - Plan migration to Supabase free tier after 90 days

2. **Story 4.2:** Implement User Profile Service with registration and login
   - Create Spring Boot microservice from base template
   - Implement `/api/v1/auth/register` POST endpoint (email, password, displayName)
   - Implement `/api/v1/auth/login` POST endpoint (email, password) returning JWT + refresh token
   - Hash passwords with BCrypt (cost factor 12)
   - Generate JWT tokens with RS256 algorithm (1-hour expiry)
   - Set refresh token as HttpOnly cookie (7-day expiry, SameSite=Strict)

3. **Story 4.3:** Implement JWT authentication filter in API Gateway
   - Add JWT validation filter to Spring Cloud Gateway
   - Verify token signature using public key
   - Extract user ID and roles from JWT claims
   - Reject requests with expired or invalid tokens (401 Unauthorized)
   - Allow unauthenticated access to public endpoints (/auth/*, /recipes/public/*, /ingredients/match)

4. **Story 4.4:** Implement OAuth2 social login (Google and GitHub)
   - Add Spring Security OAuth2 Client dependency
   - Configure Google OAuth2 (client ID, secret, redirect URI)
   - Configure GitHub OAuth2 (client ID, secret, redirect URI)
   - Implement callback endpoint `/api/v1/auth/oauth2/callback/{provider}`
   - Create or link user account based on OAuth2 email
   - Generate JWT token after successful OAuth2 authentication

5. **Story 4.5:** Implement user preferences management
   - Implement `/api/v1/users/me` GET endpoint (returns user profile + preferences)
   - Implement `/api/v1/users/me/preferences` PUT endpoint (updates preferences)
   - Validate dietary restrictions enum (vegetarian, vegan, gluten-free, dairy-free, keto, halal, kosher)
   - Publish `user.preference.updated` event to Kafka when preferences change
   - Add default preferences for new users (skillLevel=beginner, measurementSystem=metric, defaultServings=4)

6. **Story 4.6:** Implement guest mode and refresh token rotation
   - Add `isGuest=true` flag for users who skip registration
   - Guest users get limited JWT token (no userId, role=GUEST)
   - Implement `/api/v1/auth/refresh` POST endpoint (rotates refresh token)
   - Invalidate old refresh token after issuing new one (prevent reuse)
   - Store refresh tokens in PostgreSQL with expiry timestamp
   - Clean up expired tokens daily (scheduled job)

## Compatibility Requirements

- [x] PostgreSQL Render free tier: 90 days, then migrate to Supabase
- [x] JWT tokens follow RFC 7519 standard
- [x] OAuth2 follows OpenID Connect specification
- [x] Password hashing uses BCrypt (cost factor 12, industry standard)
- [x] Refresh tokens stored securely (HttpOnly, SameSite=Strict)

## Risk Mitigation

**Primary Risk:** PostgreSQL Render free tier expires after 90 days
**Mitigation:**
- Set calendar reminder for day 80 to migrate to Supabase
- Document migration steps in README
- Export database schema and data weekly (backup to GitHub)
- Test Supabase migration in staging environment before production

**Secondary Risk:** OAuth2 provider outages may prevent login
**Mitigation:**
- Always support email/password login as fallback
- Display clear error message if OAuth2 provider unavailable
- Cache OAuth2 user info to allow temporary access during outage
- Monitor OAuth2 provider status pages

**Rollback Plan:**
- Disable OAuth2 login and use email/password only
- Revert JWT validation filter if causing issues (allow all requests temporarily)
- Restore PostgreSQL database from latest backup if data corruption occurs

## Definition of Done

- [x] User Profile Service deployed and accessible via API Gateway
- [x] User can register with email/password and login successfully
- [x] Google OAuth2 login works (tested with real Google account)
- [x] GitHub OAuth2 login works (tested with real GitHub account)
- [x] Guest mode allows app usage without registration
- [x] JWT tokens expire after 1 hour, refresh tokens rotate correctly
- [x] User preferences persist in PostgreSQL and affect recipe recommendations
- [x] Kafka `user.preference.updated` event published when preferences change
- [x] API Gateway rejects requests with invalid/expired tokens (401 Unauthorized)
- [x] Integration tests cover registration, login, OAuth2, preferences, token refresh
- [x] OpenAPI documentation generated

## Dependencies

- **Epic 001**: Infrastructure Foundation (Kafka, API Gateway, PostgreSQL)

## Timeline Estimate

**2-3 weeks** (can start in parallel with Epic 002/003)

## Technical Notes

- JWT secret key stored in Spring Cloud Config (encrypted)
- OAuth2 redirect URI: `https://recipe-adjuster.netlify.app/auth/callback/{provider}`
- PostgreSQL connection string stored in Spring Cloud Config
- User Profile Service port: 8084
- JWT claims: `sub` (user ID), `email`, `roles` (array), `iat` (issued at), `exp` (expiration)
- Refresh token length: 64 characters (random alphanumeric)

## Acceptance Criteria

1. User registers with email "test@example.com" and password, receives JWT token
2. User logs in with Google account, JWT token contains Google email
3. Guest user accesses app without registration, JWT token has `role=GUEST`
4. User updates preferences (dietaryRestrictions=[vegan]), Kafka event published
5. JWT token expires after 1 hour, refresh token endpoint issues new token
6. API Gateway rejects request with expired token (401 Unauthorized)
7. Password "password123" hashed with BCrypt, hash starts with `$2a$12$`
8. OAuth2 callback redirects to frontend with JWT token in URL fragment
