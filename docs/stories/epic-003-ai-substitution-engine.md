# Epic 003: AI-Powered Substitution Engine

## Epic Goal

Implement the intelligent substitution engine using Spring AI to suggest ingredient alternatives with explanations, enabling users to cook recipes even when missing specific ingredients.

## Epic Description

**Existing System Context:**
- Infrastructure from Epic 001 (Kafka, API Gateway)
- Recipe/Ingredient data models from Epic 002 (MongoDB)
- Technology stack: Spring Boot 3.2.x, Spring AI 1.0.x, MongoDB, Redis (Upstash free tier)
- Integration points: API Gateway route, Kafka `substitution.requested` topic, MongoDB substitution collection

**Enhancement Details:**

**What's being built:**
- **Substitution Engine Service**: AI-powered ingredient substitution suggestions with explanations
- **Spring AI Integration**: ChatClient for generating substitution explanations, EmbeddingClient for semantic matching
- **Substitution Database**: Pre-curated common substitutions as fallback when AI unavailable
- **Explanation Generation**: "Why this substitution works" based on flavor profile, texture, nutritional similarity
- **Dietary Conversion**: Suggest substitutions that meet dietary restrictions (vegan, gluten-free, keto, etc.)

**How it integrates:**
- API Gateway routes `/api/v1/substitutions/suggest` to Substitution Engine Service
- Service queries MongoDB for pre-curated substitutions first (fast path)
- Falls back to Spring AI for novel substitutions (slow path with caching)
- Publishes `substitution.requested` and `substitution.completed` events to Kafka
- Caches AI-generated substitutions in Redis (TTL 7 days) for reuse

**Success criteria:**
- User requests substitution for "butter" and receives 3+ alternatives (oil, margarine, applesauce) with explanations
- AI-generated explanations include flavor impact, texture impact, and conversion ratio
- Dietary filter works (vegan substitution for butter excludes dairy-based alternatives)
- Response time <2 seconds for AI-generated substitutions, <200ms for cached/pre-curated
- Substitution compatibility score (0-1) accurately reflects suitability

## Stories

1. **Story 3.1:** Set up Spring AI with ChatClient and EmbeddingClient
   - Add Spring AI dependencies to microservice
   - Configure ChatClient with system prompt for culinary expertise
   - Set up EmbeddingClient (Hugging Face or OpenAI)
   - Implement prompt templates for substitution requests
   - Add fallback handling for API failures (use pre-curated substitutions)

2. **Story 3.2:** Build pre-curated substitution database in MongoDB
   - Define Substitution schema (originalIngredientId, substituteIngredientId, conversionRatio, compatibilityScore, flavorImpact, textureImpact, dietaryTags, explanation, aiGenerated)
   - Seed 100 common substitutions (butter→oil, milk→almond milk, egg→flax egg, etc.)
   - Create indexes on originalIngredientId and dietaryTags
   - Implement CRUD endpoints for admin to add/approve substitutions
   - Add moderation flag for AI-generated substitutions pending approval

3. **Story 3.3:** Implement Substitution Engine Service with AI generation
   - Create Spring Boot microservice from base template
   - Implement `/api/v1/substitutions/suggest` POST endpoint
   - Query pre-curated substitutions first (fast path)
   - Generate AI substitutions for missing ingredients (slow path)
   - Parse AI response into structured format (substitute, ratio, explanation, impacts)
   - Calculate compatibility score based on flavor/texture similarity

4. **Story 3.4:** Add Redis caching for AI-generated substitutions
   - Integrate Redis (Upstash free tier: 10,000 commands/day)
   - Cache AI responses with key: `substitution:{originalIngredient}:{dietaryTags}`
   - Set TTL to 7 days for AI-generated substitutions
   - Implement cache warming for top 50 most-requested ingredients
   - Add cache hit/miss metrics to Spring Boot Actuator

5. **Story 3.5:** Implement dietary restriction filtering and explanation enhancement
   - Filter substitutions by dietary tags (vegan, gluten-free, keto, halal, kosher)
   - Enhance AI prompt to explain WHY substitution works (flavor chemistry, cooking properties)
   - Add "upgrade" vs. "budget" substitution categories
   - Label substitutions as "Classic version" vs. "Creative twist" vs. "Dietary alternative"
   - Generate nutritional comparison (calories, protein, fat) for substitutions

6. **Story 3.6:** Publish Kafka events and integrate with Analytics
   - Publish `substitution.requested` event when user requests substitutions
   - Publish `substitution.completed` event with results (AI-generated vs. pre-curated)
   - Include metadata: recipeId, missingIngredients, suggestedSubstitutions, responseTime
   - Add correlation ID for tracing requests across services
   - Implement event schema validation

## Compatibility Requirements

- [x] Spring AI version 1.0.x compatible with Spring Boot 3.2.x
- [x] Redis Upstash free tier limits: 10,000 commands/day (monitor usage)
- [x] AI prompt responses parseable into structured JSON format
- [x] Substitution API follows OpenAPI 3.0 specification
- [x] Kafka event schemas match Epic 001 definitions

## Risk Mitigation

**Primary Risk:** Spring AI costs may exceed free tier during beta testing
**Mitigation:**
- Use local LLM (Ollama with Llama 3.2) for development
- Reserve OpenAI API for production (monitor $5 free credit usage)
- Implement request throttling (max 10 AI requests per user per hour)
- Pre-curate top 100 substitutions to reduce AI calls by 80%
- Cache aggressively (7-day TTL) to avoid duplicate AI requests

**Secondary Risk:** AI-generated substitutions may be incorrect or unsafe
**Mitigation:**
- Require admin approval for AI-generated substitutions before showing to users
- Add user feedback mechanism (thumbs up/down) to flag bad substitutions
- Implement safety checks (e.g., never suggest raw meat substitutions)
- Display disclaimer: "AI-generated suggestion, use culinary judgment"
- Maintain human-curated "golden set" of verified substitutions

**Rollback Plan:**
- Disable AI generation and serve only pre-curated substitutions
- Fall back to simple keyword matching if Spring AI unavailable
- Remove Redis caching if Upstash limits exceeded (direct MongoDB queries)

## Definition of Done

- [x] Substitution Engine Service deployed and accessible via API Gateway
- [x] Spring AI ChatClient generates substitution explanations successfully
- [x] MongoDB contains 100+ pre-curated substitutions
- [x] Redis caching reduces AI calls by >70%
- [x] Dietary restriction filtering works correctly (tested with 10 scenarios)
- [x] AI-generated explanations include flavor/texture impact and conversion ratio
- [x] Kafka events published for all substitution requests
- [x] Response time <2s for AI-generated, <200ms for cached/pre-curated
- [x] Integration tests cover AI success, AI failure, and cache hit scenarios
- [x] OpenAPI documentation generated

## Dependencies

- **Epic 001**: Infrastructure Foundation (Kafka, API Gateway, Redis)
- **Epic 002**: Ingredient Matching & Recipe Search (Ingredient data models)

## Timeline Estimate

**3-4 weeks** (parallel development with Epic 002, as per brainstorming Priority #2)

## Technical Notes

- Spring AI system prompt: "You are a culinary expert specializing in ingredient substitutions. Provide substitutions that maintain flavor profile and texture. Always explain WHY the substitution works. Consider dietary restrictions."
- Embedding model: `sentence-transformers/all-MiniLM-L6-v2` (same as Epic 002)
- Compatibility score calculation: `(flavorSimilarity * 0.5) + (textureSimilarity * 0.3) + (nutritionalSimilarity * 0.2)`
- Substitution Engine Service port: 8083
- Redis key pattern: `sub:{ingredient}:{dietary}` (e.g., `sub:butter:vegan`)

## Acceptance Criteria

1. User requests substitution for "butter" in a recipe, receives 3 alternatives with explanations
2. Vegan filter excludes dairy-based substitutions (margarine shown, butter excluded)
3. AI explanation includes: "Olive oil works because it provides similar fat content and moisture, though flavor will be more savory. Use 3/4 cup oil for 1 cup butter."
4. Cache hit rate >70% for top 50 ingredients after 1 week of usage
5. Pre-curated substitutions return in <200ms (95th percentile)
6. AI-generated substitutions return in <2s (95th percentile)
7. Kafka `substitution.completed` event includes all suggested substitutions and metadata
8. Admin can approve/reject AI-generated substitutions via moderation endpoint
