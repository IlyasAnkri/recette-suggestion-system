# Project Brief: Recipe Adjuster

## Executive Summary

Recipe Adjuster is an AI-powered web application that helps home cooks maximize their available ingredients by suggesting recipes with intelligent quantity adjustments and substitutions. Users input what they have in their kitchen, and the app recommends recipes they can make right now, explains why ingredient substitutions work, and adjusts quantities automatically. Built with a zero-budget constraint using Spring Boot microservices, Angular 20, Spring AI, and Kafka, the platform targets all home cooks globally—from busy parents to college students to health-conscious individuals—who want to reduce food waste, save money, and cook with confidence.

**Primary Problem:** Home cooks struggle to use available ingredients efficiently, leading to food waste, unnecessary grocery spending, and decision fatigue about what to cook.

**Target Market:** Global home cooks across all demographics, with initial focus on budget-conscious users, sustainability-minded individuals, and cooking beginners.

**Key Value Proposition:** Unlike basic recipe search apps, Recipe Adjuster provides intelligent AI-powered substitution explanations, semantic ingredient matching, offline-first PWA functionality, and cooking education—all at zero cost to users.

---

## Problem Statement

### Current State and Pain Points

Home cooks face a recurring challenge: they open their refrigerator or pantry, see a collection of ingredients, and struggle to determine what they can cook. This leads to several interconnected problems:

1. **Food Waste:** According to USDA data, households waste 30-40% of food supply. Much of this occurs because people don't know how to use ingredients before they expire.

2. **Decision Fatigue:** The average person makes 226 food-related decisions daily. Figuring out "what's for dinner" with available ingredients adds cognitive burden to already busy lives.

3. **Unnecessary Spending:** Without knowing what they can make with existing ingredients, people over-purchase groceries or resort to expensive takeout/delivery.

4. **Cooking Confidence Gap:** Beginner cooks lack the knowledge to substitute ingredients confidently, leading them to abandon recipes when they're missing one item.

5. **Recipe Discovery Friction:** Existing recipe apps require users to search by recipe name or cuisine type, not by available ingredients. Those that do ingredient-based search provide poor matches and no substitution guidance.

### Impact of the Problem

- **Financial:** Average household wastes $1,500-2,000 annually on unused food
- **Environmental:** Food waste contributes 8-10% of global greenhouse gas emissions
- **Time:** 30-45 minutes spent daily on meal planning and grocery shopping decisions
- **Psychological:** Cooking anxiety and guilt about wasted food reduce overall well-being

### Why Existing Solutions Fall Short

Current recipe apps (Yummly, Tasty, Allrecipes) focus on recipe discovery by cuisine or dietary preference, not ingredient optimization. Apps like Supercook and MyFridgeFood offer ingredient-based search but lack:

- **Intelligent substitutions:** They show recipes requiring exact ingredients, not creative alternatives
- **Educational value:** No explanation of WHY substitutions work (flavor chemistry, texture science)
- **Offline functionality:** Require constant internet connectivity in kitchens with poor WiFi
- **Quantity adjustment:** Don't automatically scale recipes based on available ingredient amounts
- **AI-powered semantic search:** Can't understand "leafy greens" should match spinach, kale, arugula

### Urgency and Importance

The convergence of rising food costs (20% increase 2020-2024), growing sustainability awareness (Gen Z prioritizes eco-friendly choices), and AI technology maturation (Spring AI, local LLMs) creates a unique opportunity. Home cooking surged during COVID-19 and remains elevated, with 82% of meals now prepared at home. The market is primed for a solution that combines ingredient optimization with AI-powered cooking assistance.

---

## Proposed Solution

### Core Concept and Approach

Recipe Adjuster is an intelligent cooking assistant that bridges the gap between "what you have" and "what you can make." Users input their available ingredients through a simple interface (typing, voice, or future: image recognition), and the system:

1. **Matches recipes** using semantic search (understands "greens" = spinach/kale/arugula)
2. **Suggests substitutions** with AI-generated explanations of why they work
3. **Adjusts quantities** automatically based on available amounts vs. recipe requirements
4. **Educates users** on cooking principles, not just instructions
5. **Works offline** as a Progressive Web App with cached recipes and local storage

The technical foundation leverages a microservices architecture (Spring Boot + Kafka) for scalability, Spring AI for intelligent substitution reasoning, and Angular 20 PWA for cross-platform offline-first experience—all deployed on free-tier infrastructure.

### Key Differentiators

**1. AI-Powered Substitution Intelligence**
- Unlike 1:1 substitution databases, Spring AI explains WHY replacements work (flavor profiles, cooking chemistry, nutritional similarity)
- Context-aware suggestions based on recipe type (baking vs. sautéing requires different substitution rules)
- Confidence scoring for substitutions (Classic, Creative Twist, Experimental)

**2. Cooking Education, Not Just Recipes**
- Teach principles: why we sear meat, why pasta water is salted, how emulsions work
- Build user confidence through understanding, not rote instruction-following
- Troubleshooting guides for common mistakes

**3. Zero-Budget, Community-Driven**
- Free-tier infrastructure enables zero cost to users (no ads, no subscriptions)
- Community-contributed recipes grow database organically
- Open-source ethos with transparent AI explanations

**4. Offline-First PWA**
- Works in kitchens with poor WiFi connectivity
- No app store downloads or updates required
- Instant load times with cached recipes

**5. Semantic Ingredient Understanding**
- Fuzzy matching: "I have tomatoes" matches cherry tomatoes, Roma tomatoes, canned tomatoes
- Ingredient categories: "greens," "proteins," "starches" understood contextually
- Handles typos and informal names ("zucchini" = "courgette")

### Why This Solution Will Succeed

**Market Timing:** AI technology (Spring AI, local LLMs) is now mature enough for zero-cost deployment, making intelligent features accessible without venture funding.

**Behavioral Shift:** Post-pandemic home cooking habits persist; users are comfortable with AI assistants (ChatGPT, voice assistants).

**Sustainability Tailwind:** Gen Z and Millennials prioritize eco-friendly solutions; food waste reduction aligns with values.

**Technical Moat:** Microservices architecture allows rapid feature iteration; Spring AI integration creates defensible differentiation vs. static recipe databases.

**Zero-Budget Viability:** Free-tier infrastructure (Confluent Kafka, Supabase, Render, Netlify) proven viable for MVP scale (100-1000 users).

### High-Level Vision

Recipe Adjuster evolves from a simple ingredient matcher to a comprehensive kitchen management platform:

- **Phase 1 (MVP):** Ingredient matching, substitution suggestions, quantity adjustment
- **Phase 2:** Meal planning calendar, shopping list generation, expiration tracking
- **Phase 3:** AI cooking coach with voice guidance, dietary goal optimization, nutritional analysis
- **Long-term:** Zero-waste kitchen optimizer, smart appliance integration, B2B licensing to nutritionists/meal kit services

---

## Target Users

### Primary User Segment: Budget-Conscious Home Cooks

**Demographic Profile:**
- Age: 25-45 years old
- Income: $30,000-$70,000 household income
- Education: High school to bachelor's degree
- Location: Urban and suburban areas globally
- Household: Singles, couples, young families (1-4 people)

**Current Behaviors and Workflows:**
- Shop weekly at discount grocers (Aldi, Lidl, Walmart)
- Cook 5-7 meals per week at home to save money
- Use Pinterest, YouTube, or Google for recipe inspiration
- Often abandon recipes when missing one ingredient (can't afford extra trip to store)
- Track grocery spending through budgeting apps (Mint, YNAB)

**Specific Needs and Pain Points:**
- Need to maximize every ingredient purchased to stay within budget
- Frustrated by food waste when ingredients expire before use
- Anxious about cooking failures (wasted ingredients = wasted money)
- Want to learn cooking skills but can't afford classes or expensive cookbooks
- Struggle with meal variety within budget constraints

**Goals They're Trying to Achieve:**
- Reduce monthly grocery spending by 15-25%
- Eliminate food waste to stretch budget further
- Build cooking confidence to avoid expensive takeout
- Feed family nutritious meals without premium ingredients
- Learn ingredient substitution skills for long-term savings

### Secondary User Segment: Sustainability-Minded Individuals

**Demographic Profile:**
- Age: 22-40 years old (Gen Z and Millennials)
- Income: $40,000-$100,000 household income
- Education: Bachelor's degree or higher
- Location: Urban areas, college towns
- Values: Environmental consciousness, ethical consumption, minimalism

**Current Behaviors and Workflows:**
- Shop at farmers markets, co-ops, or zero-waste stores
- Compost food scraps and track waste reduction
- Follow sustainability influencers and eco-friendly blogs
- Use reusable containers, buy in bulk, avoid single-use packaging
- Participate in meal prep Sundays to reduce weekday waste

**Specific Needs and Pain Points:**
- Guilt about food waste contradicts environmental values
- Difficulty using "ugly" produce or bulk ingredients creatively
- Want to support local/seasonal eating but need recipe guidance
- Frustrated by recipes requiring single-use specialty ingredients
- Seek tools that align with zero-waste lifestyle

**Goals They're Trying to Achieve:**
- Achieve zero food waste household
- Cook seasonally with local ingredients
- Reduce carbon footprint through efficient ingredient use
- Share sustainable cooking practices with community
- Prove eco-friendly living is practical and delicious

---

## Goals & Success Metrics

### Business Objectives

- **User Acquisition:** Reach 1,000 active users within 3 months of MVP launch, 10,000 within 6 months
- **Engagement:** Achieve 40% weekly active user rate (users returning weekly to input ingredients)
- **Retention:** Maintain 60% 30-day retention rate (users still active after 1 month)
- **Community Growth:** Generate 500 community-contributed recipes within 6 months
- **Zero-Budget Validation:** Operate within free-tier infrastructure limits for first 6 months (prove viability)
- **Technical Stability:** Maintain 99% uptime for core ingredient matching and substitution services

### User Success Metrics

- **Food Waste Reduction:** Users report 25% reduction in food waste within 30 days of use
- **Cost Savings:** Users save average $50-100/month on groceries by using available ingredients
- **Cooking Confidence:** 70% of users report increased confidence in ingredient substitution after 10 uses
- **Time Savings:** Users spend 50% less time on "what's for dinner" decisions (15 min → 7 min)
- **Recipe Completion:** 80% of suggested recipes are successfully cooked (not abandoned mid-process)
- **Offline Usage:** 30% of recipe views occur offline (validates PWA offline-first strategy)

### Key Performance Indicators (KPIs)

- **Ingredient Match Rate:** % of ingredient inputs that return at least 3 viable recipe matches (Target: >85%)
- **Substitution Acceptance:** % of AI-suggested substitutions that users mark as "helpful" (Target: >70%)
- **Recipe Success Rate:** % of started recipes marked as "completed successfully" (Target: >75%)
- **Community Contribution Rate:** # of recipes submitted per 100 active users per month (Target: 5 recipes)
- **PWA Installation Rate:** % of users who "Add to Home Screen" (Target: >20%)
- **Average Session Duration:** Time spent per visit (Target: 8-12 minutes)
- **Spring AI Query Latency:** Average response time for substitution suggestions (Target: <2 seconds)
- **Free-Tier Headroom:** % of infrastructure limits remaining (Target: >30% buffer)

---

## MVP Scope

### Core Features (Must Have)

- **Ingredient Input Interface:** Simple text-based ingredient entry with autocomplete (leverages semantic search to handle typos and variations). Users can add/remove ingredients from their "virtual pantry."

- **Recipe Matching Engine:** Microservice that queries recipe database for partial and full matches, ranks by % ingredient match, and displays top 10 results with missing ingredients clearly marked.

- **AI-Powered Substitution Suggestions:** Spring AI integration that generates 2-3 substitution options per missing ingredient with explanations (e.g., "Greek yogurt works here because it provides similar tanginess and protein content to sour cream").

- **Quantity Adjustment Calculator:** Automatically scales recipe quantities based on available ingredient amounts (e.g., "You have 2 cups flour, recipe needs 3 cups → suggests making 2/3 batch or lists what else you need").

- **Offline-First PWA:** Angular service workers cache 50 most popular recipes and user's ingredient list in IndexedDB, enabling full functionality without internet connection.

- **User Profile & Preferences:** Basic account creation (optional—guest mode available) to save ingredient inventory, dietary restrictions (vegetarian, vegan, gluten-free, allergies), and cooking skill level.

- **Recipe Detail View:** Displays ingredients, step-by-step instructions, cooking time, difficulty rating, and user ratings/reviews.

- **Community Recipe Submission:** Simple form for users to contribute recipes (title, ingredients, instructions, tags) with moderation queue to ensure quality.

### Out of Scope for MVP

- Image recognition for ingredient input (future Phase 2)
- Meal planning calendar and weekly menu generation
- Shopping list generation and price comparison
- Nutritional analysis and calorie tracking
- Video tutorials for cooking techniques
- Voice input and hands-free cooking mode
- Social sharing and recipe collections
- Barcode scanning for pantry inventory
- Integration with smart appliances or grocery delivery APIs
- Advanced analytics dashboard for users
- Mobile native apps (PWA covers mobile use cases)

### MVP Success Criteria

The MVP will be considered successful if:

1. **Technical Validation:** All 4 core microservices (Ingredient Matching, Recipe Search, Substitution Engine, User Profile) deployed and operational on free-tier infrastructure within 6 weeks
2. **User Validation:** 100 beta users successfully find and cook at least 1 recipe using the app within first month
3. **AI Validation:** Spring AI substitution suggestions rated "helpful" by >60% of users (proves AI adds value over static database)
4. **Offline Validation:** PWA works offline for >90% of users who test offline mode (proves offline-first architecture)
5. **Community Validation:** At least 50 community-contributed recipes submitted and approved within 3 months (proves content growth model)
6. **Cost Validation:** Infrastructure costs remain $0/month for first 3 months at <500 users (proves zero-budget viability)

---

## Post-MVP Vision

### Phase 2 Features

**Meal Planning & Shopping Integration (3-4 months post-MVP)**
- Weekly meal calendar with drag-and-drop recipe scheduling
- Automatic shopping list generation for missing ingredients
- Price comparison using free grocery APIs (where available)
- Leftover tracking with expiration date alerts

**Enhanced AI Capabilities**
- Recipe remixing: Combine elements from multiple recipes based on available ingredients
- Dietary conversion: Make any recipe vegan/gluten-free/keto using substitution engine
- Cooking style suggestions: Transform ingredients into Italian-style, Asian-fusion, etc.

**Social & Community Features**
- Recipe collections and favorites
- Social sharing to Instagram, Pinterest, Facebook
- User-to-user recipe recommendations
- Cooking challenges and achievements (gamification)

### Long-Term Vision (1-2 Years)

**AI Cooking Coach**
- Voice-activated assistant for hands-free cooking guidance
- Real-time troubleshooting ("My sauce is too thin—what do I do?")
- Personalized cooking skill progression tracking
- Video integration with step-by-step visual guides

**Zero-Waste Kitchen Optimizer**
- Predictive food waste alerts based on purchase history
- Smart pantry integration with barcode scanning
- Carbon footprint tracking for ingredient choices
- Composting and preservation guides

**Health & Nutrition Platform**
- Integration with fitness trackers for dietary goal alignment
- Nutritional analysis with macro/micronutrient tracking
- Meal plans optimized for weight loss, muscle gain, diabetes management
- B2B licensing to nutritionists, dietitians, and healthcare providers

### Expansion Opportunities

**Geographic Expansion**
- Localization for regional ingredients and cuisines (European, Asian, Latin American markets)
- Multi-language support (Spanish, French, German, Mandarin)
- Partnership with local grocery chains for region-specific ingredient databases

**B2B Opportunities**
- Licensing to meal kit services (HelloFresh, Blue Apron) for substitution intelligence
- White-label solution for grocery store apps
- Integration with smart appliance manufacturers (Samsung, LG smart fridges)
- Corporate wellness programs for employee meal planning

**Monetization Paths (Post Free-Tier Scaling)**
- Freemium model: Basic features free, advanced features (meal planning, nutritional analysis) $4.99/month
- Affiliate revenue: Ingredient purchase links to grocery delivery services (Instacart, Amazon Fresh)
- B2B licensing: $500-2000/month per enterprise customer
- Premium content: Celebrity chef recipes, cooking courses ($9.99-29.99 one-time)

---

## Technical Considerations

### Platform Requirements

- **Target Platforms:** Web (primary), mobile web (responsive), Progressive Web App (installable on iOS/Android)
- **Browser/OS Support:** 
  - Modern browsers: Chrome 90+, Firefox 88+, Safari 14+, Edge 90+
  - Mobile: iOS 14+, Android 8+
  - Offline support via service workers (requires HTTPS)
- **Performance Requirements:**
  - Initial page load: <3 seconds on 3G connection
  - Ingredient search autocomplete: <200ms response time
  - Spring AI substitution query: <2 seconds end-to-end
  - Recipe list rendering: <1 second for 50 recipes
  - Offline mode: Instant load from cache

### Technology Preferences

**Frontend:**
- **Framework:** Angular 20 (required)
- **State Management:** NgRx for reactive state management
- **UI Library:** Angular Material or Tailwind CSS + shadcn/ui components
- **Icons:** Lucide icons
- **PWA:** Angular service workers, IndexedDB via Dexie.js
- **Build Tool:** Angular CLI with production optimizations

**Backend:**
- **Framework:** Spring Boot 3.x (required)
- **Architecture:** Microservices with domain-driven design
- **AI Integration:** Spring AI with Ollama (local LLM) or OpenAI API (free tier)
- **Messaging:** Apache Kafka via Confluent Cloud free tier
- **API Gateway:** Spring Cloud Gateway
- **Authentication:** Spring Security with JWT tokens
- **Documentation:** Swagger/OpenAPI (Springdoc)

**Database:**
- **Relational:** PostgreSQL (Supabase free tier 500MB or Neon 3GB) for recipe data, user profiles
- **Document:** MongoDB Atlas free tier (512MB) for flexible recipe schema if needed
- **Caching:** Redis (Upstash free tier) for frequently accessed recipes
- **Client-Side:** IndexedDB for offline recipe storage (Angular PWA)

**Hosting/Infrastructure:**
- **Frontend:** Netlify or Vercel free tier (100GB bandwidth/month)
- **Backend Services:** Render.com free tier (750 hours/month) or Railway ($5 credit)
- **Kafka:** Confluent Cloud free tier (1 cluster, 1GB storage)
- **Database:** Supabase (PostgreSQL), MongoDB Atlas, Upstash (Redis)
- **CDN:** Cloudflare Pages or built-in Netlify/Vercel CDN
- **SSL:** Let's Encrypt (auto-provisioned by hosting platforms)

### Architecture Considerations

**Repository Structure:**
- Monorepo approach using Nx or Turborepo for microservices + frontend
- Shared libraries for common DTOs, utilities, Kafka event schemas
- Separate deployment pipelines per service

**Service Architecture:**
- **Microservices (MVP - 4 core services):**
  1. Ingredient Matching Service: Semantic search, fuzzy matching
  2. Recipe Search Service: Query, ranking, filtering
  3. Substitution Engine Service: Spring AI integration, explanation generation
  4. User Profile Service: Authentication, preferences, saved recipes
- **Post-MVP Services:**
  5. Recipe Database Service: CRUD operations, community contributions
  6. Notification Service: Expiration alerts, new recipe matches
  7. Analytics Service: Usage tracking, recommendation improvement
  8. Quantity Adjustment Service: Recipe scaling calculations

**Integration Requirements:**
- **Kafka Event Bus:** Asynchronous communication between services (IngredientSubmitted, RecipeMatched, SubstitutionRequested events)
- **REST APIs:** Synchronous communication for real-time queries (ingredient autocomplete, recipe detail fetch)
- **Spring Cloud Gateway:** Single entry point for Angular frontend, routing to microservices
- **Service Discovery:** Hardcoded URLs in free tier (Eureka/Consul too expensive), environment variables for service endpoints

**Security/Compliance:**
- **Authentication:** OAuth 2.0 with JWT tokens (Spring Security)
- **Authorization:** Role-based access control (user, moderator, admin)
- **Data Privacy:** GDPR-compliant (user data export, right to deletion, cookie consent)
- **API Security:** Rate limiting (Bucket4j), CORS configuration, input validation
- **Secret Management:** Environment variables (Render/Netlify), avoid hardcoded credentials
- **SSL/TLS:** HTTPS enforced (required for PWA service workers)

---

## Constraints & Assumptions

### Constraints

**Budget:**
- **Hard Constraint:** $0/month for infrastructure (must use free tiers exclusively)
- **Implication:** Limited to ~500-1000 users before free-tier limits exhausted
- **Mitigation:** Optimize resource usage, implement caching, monitor free-tier headroom closely

**Timeline:**
- **MVP Development:** 6-8 weeks (part-time development)
- **Beta Testing:** 2-4 weeks with 50-100 users
- **Public Launch:** 3 months from project start
- **Constraint:** Solo developer or small team (2-3 people max)

**Resources:**
- **Development Team:** 1-2 developers (full-stack or frontend + backend split)
- **Design:** Self-service using Tailwind CSS + shadcn/ui (no dedicated designer)
- **Content:** Community-contributed recipes (no budget for licensed recipe database)
- **Marketing:** Organic growth via Reddit, Product Hunt, social media (no paid ads)

**Technical:**
- **Mandatory Stack:** Spring Boot microservices, Angular 20, Spring AI, Kafka (non-negotiable per project requirements)
- **Free-Tier Limits:**
  - Confluent Kafka: 1 cluster, 1GB storage, limited throughput
  - Supabase PostgreSQL: 500MB storage, 2GB bandwidth/month
  - Render: 750 hours/month (≈1 service running 24/7)
  - Netlify: 100GB bandwidth/month, 300 build minutes/month
- **Performance:** Must work on 3G mobile connections (global accessibility)
- **Browser Support:** Modern browsers only (no IE11 support)

### Key Assumptions

- **User Behavior:** Users are willing to manually input ingredients (image recognition not required for MVP adoption)
- **Recipe Quality:** Community-contributed recipes will be sufficient quality with basic moderation (don't need professional chef-curated content initially)
- **AI Accuracy:** Spring AI substitution suggestions will be "good enough" at 70% helpfulness rate (doesn't need to be perfect)
- **Offline Usage:** 30% of users will use offline mode regularly (validates PWA investment)
- **Free-Tier Stability:** Hosting providers won't drastically change free-tier offerings in first 6 months
- **Scalability:** Microservices architecture will allow smooth transition from free tier to paid tier when needed
- **Market Demand:** Budget-conscious and sustainability-minded users exist in sufficient numbers to achieve 1000 users organically
- **Technical Feasibility:** Ollama local LLM can run on free-tier compute with acceptable latency (<2 sec)
- **Legal:** Recipe scraping from public sources is legally permissible with proper attribution (requires validation)
- **Competition:** Existing recipe apps won't add AI-powered substitution features in next 6 months (window of opportunity)

---

## Risks & Open Questions

### Key Risks

- **Free-Tier Exhaustion Risk:** Infrastructure limits exceeded faster than expected due to viral growth or inefficient resource usage. *Impact: High. Mitigation: Implement aggressive caching, monitor usage daily, have paid tier migration plan ready.*

- **Spring AI Cost Overrun Risk:** If using cloud APIs (OpenAI), free tier consumed rapidly by substitution queries. *Impact: High. Mitigation: Use local Ollama LLM as primary, cloud API as fallback; implement query caching; rate-limit per user.*

- **Recipe Data Quality Risk:** Community-contributed recipes are low quality, incomplete, or plagiarized. *Impact: Medium. Mitigation: Implement moderation queue, use Spring AI to validate recipe completeness, require user ratings before recipes go public.*

- **User Adoption Risk:** Target users don't find value in AI substitutions or prefer traditional recipe search. *Impact: High. Mitigation: Beta test with 50 users before public launch, gather feedback on substitution helpfulness, pivot features if needed.*

- **Technical Complexity Risk:** Microservices architecture too complex for small team, slows development velocity. *Impact: Medium. Mitigation: Start with 3 services (merge some MVP services), add more post-launch; use Spring Boot starter templates to accelerate setup.*

- **Offline Sync Conflict Risk:** Users modify ingredient lists offline on multiple devices, causing data conflicts when syncing. *Impact: Low. Mitigation: Implement last-write-wins strategy for MVP, add conflict resolution UI in Phase 2.*

- **Legal/Copyright Risk:** Recipe scraping violates terms of service, leading to cease-and-desist. *Impact: Medium. Mitigation: Research legal precedent (recipes not copyrightable, only specific expression), use public domain sources, rely on community contributions.*

- **Performance Degradation Risk:** Spring AI latency exceeds 2-second target on free-tier compute. *Impact: Medium. Mitigation: Benchmark Ollama models early (week 1), optimize prompts, implement timeout fallbacks to cached substitutions.*

### Open Questions

- How do we handle recipe copyright and attribution for community-contributed content? (Need legal research or consultation)
- What's the minimum viable recipe database size to launch? (100 recipes? 500? 1000?)
- Should we prioritize breadth (many cuisines) or depth (perfect substitutions for one cuisine like Italian or American) initially?
- How do we measure substitution success? User ratings? Recipe completion rates? Follow-up surveys?
- What's the fallback strategy if Spring AI costs exceed free tier during beta testing? (Switch to rule-based substitutions? Limit queries per user?)
- Can we legally scrape recipes from Allrecipes, Food Network, BBC Good Food? (Terms of service analysis needed)
- Which Ollama model provides best balance of accuracy and speed for substitution queries? (Llama 3.2 vs. Mistral vs. Phi-3?)
- How do we prevent spam or malicious recipe submissions? (CAPTCHA? Email verification? Reputation system?)
- Should guest mode have feature limitations to incentivize account creation? (Or keep fully open to maximize trial conversion?)
- What's the optimal number of substitution suggestions per ingredient? (2? 3? 5? Trade-off between choice and overwhelm)

### Areas Needing Further Research

- **Free-Tier Infrastructure Limits:** Exact throughput, storage, and compute limits for Confluent Kafka, Render, Supabase, Netlify (documented in technical research prompt)
- **Spring AI Model Selection:** Benchmark Ollama Llama 3.2, Mistral, Phi-3 for substitution query accuracy and latency
- **Recipe Scraping Legality:** Legal analysis of terms of service for major recipe sites, precedent for recipe aggregation
- **Semantic Search Implementation:** Best embedding model for ingredient matching (all-MiniLM-L6-v2 vs. instructor-xl vs. OpenAI embeddings)
- **PWA Adoption Rates:** Industry benchmarks for "Add to Home Screen" conversion rates to set realistic KPI targets
- **Competitive Analysis:** Deep-dive into Supercook, MyFridgeFood, Yummly feature sets and user reviews to identify gaps
- **User Research:** Interviews with 10-20 target users to validate problem statement and solution approach
- **Microservices Communication Patterns:** Best practices for REST vs. Kafka event-driven communication in recipe matching workflow

---

## Appendices

### A. Research Summary

**Brainstorming Session (December 19, 2025):**
- Generated 47 ideas across First Principles Thinking, SCAMPER Method, and Assumption Reversal techniques
- Identified 8 core microservices architecture aligned with domain boundaries
- Validated zero-budget constraint as feature (forces creative solutions like community contributions, PWA)
- Key insight: Spring AI differentiation through substitution explanations, not just 1:1 mappings
- Prioritized 3 immediate opportunities: Core microservices MVP, intelligent substitution engine, offline-first PWA

**Technical Research Prompt (December 19, 2025):**
- Comprehensive Technology & Innovation Research prompt created to validate zero-budget tech stack
- 8 primary research questions covering Spring AI costs, Kafka free tiers, database strategy, deployment platforms
- 4-week phased research plan with Go/No-Go decision criteria
- Deliverables include architecture decision matrices, cost projection models, risk mitigation strategies
- Research execution pending (to be completed before development starts)

### B. Stakeholder Input

**Project Stakeholder:** Solo developer/founder
- **Primary Goal:** Build viable product at zero cost to validate market demand before seeking funding
- **Technical Preferences:** Strongly prefers Spring Boot microservices, Angular 20, Spring AI, Kafka (non-negotiable)
- **Timeline Pressure:** Wants MVP in 6-8 weeks to capitalize on market opportunity window
- **Risk Tolerance:** Willing to accept 70% AI accuracy and limited initial recipe database to launch quickly
- **Success Definition:** 1000 active users within 6 months proves concept worth scaling

### C. References

**Brainstorming Session Results:**
- File: `docs/brainstorming-session-results.md`
- 47 ideas categorized into Immediate Opportunities, Future Innovations, Moonshots, Insights & Learnings

**Technical Research Prompt:**
- File: `docs/research-prompt-tech-validation.md`
- Comprehensive research framework for validating zero-budget tech stack feasibility

**Technology Documentation:**
- Spring AI: https://docs.spring.io/spring-ai/reference/
- Angular 20: https://angular.dev/
- Confluent Kafka Free Tier: https://www.confluent.io/confluent-cloud/
- Supabase: https://supabase.com/pricing
- Render: https://render.com/pricing
- Netlify: https://www.netlify.com/pricing/

**Market Research (To Be Conducted):**
- USDA Food Waste Statistics
- Competitor analysis: Supercook, MyFridgeFood, Yummly, Tasty
- User interviews with budget-conscious and sustainability-minded home cooks

---

## Next Steps

### Immediate Actions

1. **Execute Technical Research (Week 1-2):** Complete the Technology & Innovation Research prompt to validate free-tier infrastructure limits, Spring AI model selection, and database strategy. Deliverable: Go/No-Go decision document.

2. **Prototype Critical Paths (Week 2):** Build minimal proofs-of-concept for (a) Spring AI substitution query, (b) Kafka event flow between 2 services, (c) Angular PWA offline mode with IndexedDB. Validate <2 sec latency targets.

3. **Set Up Development Environment (Week 2):** Configure local Docker Compose with Kafka, PostgreSQL, Redis; scaffold 4 core microservices with Spring Boot; initialize Angular 20 project with PWA support.

4. **Bootstrap Recipe Database (Week 2-3):** Manually curate 100 starter recipes from public domain sources (USDA, Project Gutenberg cookbooks); design recipe schema; load into PostgreSQL.

5. **Develop MVP Core Features (Week 3-6):** Implement ingredient input, recipe matching, substitution engine, user profile services; build Angular frontend with offline caching; integrate Spring AI.

6. **Deploy to Free-Tier Infrastructure (Week 6):** Set up Confluent Kafka, Supabase PostgreSQL, Render backend services, Netlify frontend; configure CI/CD with GitHub Actions.

7. **Beta Testing (Week 7-8):** Recruit 50 beta users from Reddit (r/Cooking, r/EatCheapAndHealthy), Product Hunt; gather feedback on substitution helpfulness, offline mode, recipe quality.

8. **Iterate Based on Feedback (Week 9-10):** Refine AI prompts, improve recipe ranking algorithm, fix bugs, optimize performance.

9. **Public Launch (Week 11-12):** Soft launch on Product Hunt, Reddit, Hacker News; monitor infrastructure usage; prepare migration plan if free tiers exhausted.

10. **Post-Launch Monitoring (Ongoing):** Track KPIs (ingredient match rate, substitution acceptance, recipe success rate); monitor free-tier headroom; gather user feedback for Phase 2 prioritization.

---

### PM Handoff

This Project Brief provides the full context for **Recipe Adjuster**. The next phase is to create a detailed Product Requirements Document (PRD) that translates this vision into specific, actionable requirements for development.

**Recommended Next Steps for PM:**
1. Review this brief thoroughly and ask clarifying questions
2. Execute the Technical Research prompt (or assign to technical team member) to validate infrastructure decisions
3. Begin PRD creation in collaboration with development team, working section by section as the PRD template indicates
4. Prioritize user stories for MVP Sprint 1 (ingredient input, basic recipe matching)
5. Define acceptance criteria and testing strategies for each core feature

**Key Areas Requiring PM Attention:**
- Finalizing MVP feature scope (ensure 6-8 week timeline is realistic)
- Defining user onboarding flow and first-time experience
- Establishing moderation workflow for community-contributed recipes
- Creating measurement plan for KPIs (instrumentation, analytics tools)
- Coordinating beta testing recruitment and feedback collection

Please start in **PRD Generation Mode**, review the brief thoroughly, and work with the user to create the PRD section by section as the template indicates, asking for any necessary clarification or suggesting improvements.
