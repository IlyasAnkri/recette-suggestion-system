# Epic 006: Recipe Database & Community Contributions

## Epic Goal

Build the Recipe Database Service with CRUD operations, community recipe submission workflow, and moderation system to enable organic growth of the recipe collection while maintaining quality.

## Epic Description

**Existing System Context:**
- Infrastructure from Epic 001 (Kafka, API Gateway, MongoDB)
- Recipe data models from Epic 002 (MongoDB Recipe collection)
- User authentication from Epic 004 (JWT tokens, user roles)
- Technology stack: Spring Boot 3.2.x, MongoDB Atlas (free tier 512MB), Spring Security
- Integration points: API Gateway routes, Kafka `recipe.created` and `recipe.updated` events

**Enhancement Details:**

**What's being built:**
- **Recipe Database Service**: CRUD operations for recipes (create, read, update, delete)
- **Community Submission**: Authenticated users can submit recipes with ingredients, instructions, images
- **Moderation Workflow**: Admin/moderator approval queue for community-submitted recipes
- **Recipe Validation**: Schema validation, ingredient verification, duplicate detection
- **Recipe Ratings**: Users can rate recipes (1-5 stars), average rating calculation
- **Admin Panel Endpoints**: Approve/reject recipes, edit community submissions, manage recipe database

**How it integrates:**
- API Gateway routes `/api/v1/recipes` (CRUD) to Recipe Database Service
- Service stores recipes in MongoDB Atlas (free tier 512MB)
- User authentication required for recipe submission (JWT token)
- Moderator role required for approval/rejection (RBAC)
- Publishes `recipe.created` and `recipe.updated` events to Kafka
- Recipe Search Service (Epic 002) consumes approved recipes only

**Success criteria:**
- Authenticated user can submit recipe with ingredients, instructions, and image URL
- Submitted recipes enter moderation queue with `isApproved=false`
- Moderators can view pending recipes and approve/reject with feedback
- Approved recipes appear in search results immediately
- Users can rate recipes (1-5 stars), average rating updates in real-time
- Duplicate recipe detection prevents identical submissions

## Stories

1. **Story 6.1:** Implement Recipe Database Service with CRUD endpoints
   - Create Spring Boot microservice from base template
   - Implement POST `/api/v1/recipes` (create recipe, requires authentication)
   - Implement GET `/api/v1/recipes/{id}` (read recipe, public access)
   - Implement PUT `/api/v1/recipes/{id}` (update recipe, owner or moderator only)
   - Implement DELETE `/api/v1/recipes/{id}` (delete recipe, owner or admin only)
   - Add authorization checks (users can only edit their own recipes)

2. **Story 6.2:** Implement recipe validation and duplicate detection
   - Validate recipe schema (title, ingredients, instructions required)
   - Verify ingredient IDs exist in Ingredient collection
   - Check for duplicate recipes (fuzzy match on title + ingredients)
   - Validate image URLs (must be HTTPS, valid image format)
   - Validate prep/cook times (must be positive integers)
   - Return detailed validation errors (400 Bad Request with error list)

3. **Story 6.3:** Build moderation workflow and admin endpoints
   - Add `isApproved` boolean field to Recipe model (default false)
   - Implement GET `/api/v1/admin/recipes/pending` (list pending recipes, moderator role required)
   - Implement POST `/api/v1/admin/recipes/{id}/approve` (approve recipe, moderator role)
   - Implement POST `/api/v1/admin/recipes/{id}/reject` (reject recipe with feedback, moderator role)
   - Send email notification to recipe author on approval/rejection
   - Publish `recipe.created` event to Kafka only when approved

4. **Story 6.4:** Implement recipe rating system
   - Add `ratings` field to Recipe model (average, count)
   - Implement POST `/api/v1/recipes/{id}/rate` (submit rating 1-5 stars, authenticated users)
   - Store individual ratings in separate collection (userId, recipeId, rating, timestamp)
   - Calculate average rating and update Recipe document
   - Prevent users from rating their own recipes
   - Allow users to update their rating (replace previous rating)

5. **Story 6.5:** Add recipe image upload and storage
   - Integrate Cloudinary free tier (25GB storage, 25GB bandwidth)
   - Implement POST `/api/v1/recipes/upload-image` (upload image, returns URL)
   - Validate image format (JPEG, PNG, WebP only)
   - Resize images to standard dimensions (800x600 thumbnail, 1920x1440 full)
   - Store image URLs in Recipe document (thumbnail, full-size)
   - Add image moderation (flag inappropriate images for review)

6. **Story 6.6:** Implement recipe search indexing and Kafka events
   - Publish `recipe.created` event when recipe approved
   - Publish `recipe.updated` event when recipe edited
   - Include recipe metadata in events (id, title, cuisine, difficulty, author)
   - Recipe Search Service (Epic 002) consumes events to update search index
   - Add correlation ID for tracing recipe creation flow
   - Implement event schema validation

## Compatibility Requirements

- [x] MongoDB Atlas free tier: 512MB storage (monitor usage)
- [x] Recipe schema matches Epic 002 data models
- [x] Cloudinary free tier: 25GB storage, 25GB bandwidth
- [x] Kafka event schemas match Epic 001 definitions
- [x] RBAC roles: USER (submit recipes), MODERATOR (approve/reject), ADMIN (delete any recipe)

## Risk Mitigation

**Primary Risk:** MongoDB free tier storage (512MB) fills quickly with community submissions
**Mitigation:**
- Limit recipe database to 1,000 recipes initially
- Implement recipe archival (move low-rated recipes to archive collection)
- Compress recipe images via Cloudinary (reduce storage footprint)
- Monitor storage usage via MongoDB Atlas dashboard
- Plan upgrade to paid tier ($9/month for 2GB) when approaching 400MB

**Secondary Risk:** Spam or inappropriate recipe submissions
**Mitigation:**
- Require email verification before allowing recipe submission
- Implement rate limiting (max 5 recipe submissions per user per day)
- Add CAPTCHA to recipe submission form (hCaptcha free tier)
- Moderators review all submissions before approval
- Flag users with multiple rejected submissions for review

**Rollback Plan:**
- Disable community submissions and use pre-curated recipes only
- Remove moderation workflow and auto-approve recipes (risky, only if moderation backlog too large)
- Delete recipes with <2 star rating to reclaim storage

## Definition of Done

- [x] Recipe Database Service deployed and accessible via API Gateway
- [x] User can submit recipe with ingredients, instructions, and image
- [x] Submitted recipes appear in moderation queue (`isApproved=false`)
- [x] Moderator can approve/reject recipes via admin endpoints
- [x] Approved recipes appear in Recipe Search results
- [x] Users can rate recipes (1-5 stars), average rating updates correctly
- [x] Duplicate recipe detection prevents identical submissions
- [x] Kafka `recipe.created` and `recipe.updated` events published
- [x] Image upload to Cloudinary works (returns HTTPS URL)
- [x] Integration tests cover CRUD, moderation, rating workflows
- [x] OpenAPI documentation generated

## Dependencies

- **Epic 001**: Infrastructure Foundation (Kafka, API Gateway, MongoDB)
- **Epic 002**: Ingredient Matching & Recipe Search (Recipe data models, search indexing)
- **Epic 004**: User Management & Authentication (JWT tokens, user roles)

## Timeline Estimate

**3-4 weeks** (can start after Epic 004 completes)

## Technical Notes

- Recipe Database Service port: 8085
- Cloudinary upload preset: `recipe_images` (unsigned upload)
- Image dimensions: 800x600 (thumbnail), 1920x1440 (full-size)
- Rating scale: 1-5 stars (integer)
- Duplicate detection: Levenshtein distance <3 on title, ingredient overlap >80%
- Moderation queue sorted by submission date (oldest first)

## Acceptance Criteria

1. User submits recipe "Chicken Alfredo" with 5 ingredients and 8 instructions, receives 201 Created
2. Recipe appears in moderation queue with `isApproved=false`
3. Moderator approves recipe, `isApproved` changes to `true`, Kafka event published
4. Recipe appears in Recipe Search results within 5 seconds
5. User rates recipe 5 stars, average rating updates to 5.0 (count=1)
6. Second user rates recipe 3 stars, average rating updates to 4.0 (count=2)
7. User uploads image to Cloudinary, receives HTTPS URL (e.g., `https://res.cloudinary.com/...`)
8. Duplicate submission of "Chicken Alfredo" rejected with 409 Conflict error
9. User tries to rate their own recipe, receives 403 Forbidden
10. Admin deletes low-rated recipe (<2 stars), recipe removed from MongoDB and search index
