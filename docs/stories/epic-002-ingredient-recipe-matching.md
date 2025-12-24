# Epic 002: Ingredient Matching & Recipe Search Services

## Epic Goal

Build the core recipe discovery functionality by implementing Ingredient Matching and Recipe Search microservices that enable users to find recipes based on available ingredients with semantic search capabilities.

## Epic Description

**Existing System Context:**
- Infrastructure foundation from Epic 001 (Kafka, API Gateway, Docker Compose)
- Technology stack: Spring Boot 3.2.x, MongoDB Atlas (free tier 512MB), Spring AI for embeddings
- Integration points: API Gateway routes, Kafka event topics, MongoDB recipe/ingredient collections

**Enhancement Details:**

**What's being built:**
- **Ingredient Matching Service**: Accepts user's available ingredients, queries MongoDB for recipe matches, calculates match percentages, publishes `ingredient.submitted` and `recipe.matched` events
- **Recipe Search Service**: Full-text search, filtering by cuisine/dietary/difficulty, ranking by rating/match percentage, pagination support
- **MongoDB Data Models**: Recipe and Ingredient collections with embedding vectors for semantic search
- **Semantic Search**: Spring AI integration for ingredient embeddings to enable fuzzy matching ("greens" matches spinach, kale, arugula)

**How it integrates:**
- Both services connect to MongoDB Atlas free tier (shared cluster)
- API Gateway routes `/api/v1/ingredients/match` to Ingredient Matching Service
- API Gateway routes `/api/v1/recipes` to Recipe Search Service
- Services publish events to Kafka for Analytics and Notification services
- Embeddings generated via Spring AI (Hugging Face all-MiniLM-L6-v2 or OpenAI)

**Success criteria:**
- User can submit 3+ ingredients and receive ranked recipe matches with >50% match percentage
- Semantic search correctly matches ingredient aliases (e.g., "cilantro" = "coriander")
- Recipe search returns results in <500ms for typical queries
- Match percentage calculation is accurate (matched ingredients / total required ingredients)
- Kafka events published successfully for all ingredient submissions

## Stories

1. **Story 2.1:** Create MongoDB data models and seed initial recipe/ingredient data
   - Define Recipe and Ingredient schemas with embedding fields
   - Create MongoDB indexes (text search on recipe title/description, embedding vector index)
   - Seed 50 initial recipes covering 5 cuisines (Italian, Mexican, Asian, American, Mediterranean)
   - Seed 200 common ingredients with aliases and categories
   - Generate embeddings for all seeded data using Spring AI

2. **Story 2.2:** Implement Ingredient Matching Service with match algorithm
   - Create Spring Boot microservice from base template
   - Implement `/api/v1/ingredients/match` POST endpoint
   - Build match algorithm: calculate percentage, rank by match score
   - Support filters (cuisine, maxCookTime, difficulty, dietary restrictions)
   - Return matched/missing ingredients for each recipe
   - Publish `ingredient.submitted` event to Kafka

3. **Story 2.3:** Implement semantic ingredient matching with Spring AI embeddings
   - Integrate Spring AI with Hugging Face embedding model (all-MiniLM-L6-v2)
   - Generate embeddings for user-submitted ingredients
   - Perform vector similarity search against ingredient collection
   - Map similar ingredients (cosine similarity >0.8) to canonical ingredient names
   - Handle ingredient aliases and common misspellings

4. **Story 2.4:** Implement Recipe Search Service with filtering and pagination
   - Create Spring Boot microservice from base template
   - Implement `/api/v1/recipes` GET endpoint with query parameters
   - Support full-text search on title/description (MongoDB text index)
   - Implement filters: cuisine, dietary, difficulty, maxPrepTime, maxCookTime
   - Add pagination (default 20 results per page)
   - Support sorting: rating, newest, cookTime

5. **Story 2.5:** Optimize MongoDB queries and add caching layer
   - Analyze slow queries with MongoDB profiler
   - Add compound indexes for common filter combinations
   - Implement Redis caching for popular recipes (TTL 1 hour)
   - Cache ingredient embeddings to avoid regeneration
   - Add query result caching for identical searches (TTL 15 minutes)

## Compatibility Requirements

- [x] MongoDB Atlas free tier limits: 512MB storage, shared cluster
- [x] API responses follow OpenAPI 3.0 specification
- [x] All endpoints return JSON with consistent error format
- [x] Kafka event schemas match definitions in Epic 001
- [x] Recipe match percentage calculation is deterministic and testable

## Risk Mitigation

**Primary Risk:** MongoDB free tier storage (512MB) may fill quickly with embeddings
**Mitigation:**
- Monitor storage usage via MongoDB Atlas dashboard
- Limit initial recipe database to 500 recipes (~100MB with embeddings)
- Implement data archival strategy for low-rated recipes
- Plan migration to paid tier ($9/month for 2GB) if needed

**Secondary Risk:** Embedding generation latency may slow ingredient matching
**Mitigation:**
- Pre-generate embeddings for all ingredients in database
- Cache user ingredient embeddings for session duration
- Use batch embedding generation for multiple ingredients
- Consider local embedding model (all-MiniLM-L6-v2) vs. OpenAI API for cost/speed

**Rollback Plan:**
- Disable semantic search and fall back to exact string matching
- Remove embedding fields from MongoDB to reclaim storage
- Revert to keyword-based search if full-text search causes issues

## Definition of Done

- [x] Ingredient Matching Service deployed and accessible via API Gateway
- [x] Recipe Search Service deployed and accessible via API Gateway
- [x] MongoDB collections created with 50+ recipes and 200+ ingredients
- [x] Semantic search correctly matches ingredient aliases (tested with 20 examples)
- [x] Match percentage calculation verified with unit tests
- [x] Kafka events published for all ingredient submissions
- [x] API response time <500ms for 95th percentile
- [x] Integration tests pass for both services
- [x] OpenAPI documentation generated and accessible at `/swagger-ui`

## Dependencies

- **Epic 001**: Infrastructure Foundation (Kafka, API Gateway, MongoDB setup)

## Timeline Estimate

**3-4 weeks** (parallel development with Epic 003)

## Technical Notes

- MongoDB connection string stored in Spring Cloud Config
- Embedding model: `sentence-transformers/all-MiniLM-L6-v2` (384 dimensions)
- Cosine similarity threshold for ingredient matching: 0.8
- Match percentage formula: `(matched_ingredients / total_required_ingredients) * 100`
- Ingredient Matching Service port: 8081
- Recipe Search Service port: 8082

## Acceptance Criteria

1. User submits ["chicken", "garlic", "olive oil"] and receives 5+ recipe matches ranked by percentage
2. Semantic search matches "cilantro" to recipes containing "coriander"
3. Recipe search with filters (cuisine=Italian, difficulty=easy) returns only matching recipes
4. Pagination works correctly (page 1 shows recipes 1-20, page 2 shows 21-40)
5. Kafka `ingredient.submitted` event contains userId, ingredients array, and timestamp
6. MongoDB queries execute in <100ms (measured via Spring Boot Actuator metrics)
7. Redis cache hit rate >60% for popular recipes
