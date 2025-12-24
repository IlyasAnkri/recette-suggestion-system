# Recipe Adjuster Application - Full-Stack Architecture

## Document Information
- **Version:** 1.0
- **Last Updated:** December 19, 2025
- **Status:** Draft

---

## 1. Executive Summary

The Recipe Adjuster Application is a microservices-based platform that helps users discover recipes based on available ingredients, with intelligent substitution suggestions and quantity adjustments. Built with a zero-budget constraint, it leverages free-tier cloud services, Spring AI for intelligent features, and Angular 20 PWA for cross-platform access.

### Key Architectural Decisions
- **Microservices Architecture**: 8 domain-driven services for scalability and independent deployment
- **Event-Driven Communication**: Apache Kafka for asynchronous, loosely-coupled service interaction
- **PWA-First Frontend**: Angular 20 with offline-first capabilities, eliminating native mobile app costs
- **AI-Powered Features**: Spring AI for semantic ingredient matching and intelligent substitutions
- **Zero-Budget Infrastructure**: Free-tier cloud services (Render, Confluent, Netlify, MongoDB Atlas)

---

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

---

## 3. High-Level Architecture

### 3.1 System Context Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              EXTERNAL SYSTEMS                                │
├─────────────────────────────────────────────────────────────────────────────┤
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐    │
│  │   OpenAI     │  │  Hugging     │  │   OAuth      │  │   Recipe     │    │
│  │   API        │  │  Face        │  │   Providers  │  │   Sources    │    │
│  │  (Spring AI) │  │  (Embeddings)│  │  (Google)    │  │  (Community) │    │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘    │
└─────────┼─────────────────┼─────────────────┼─────────────────┼────────────┘
          │                 │                 │                 │
          ▼                 ▼                 ▼                 ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                         RECIPE ADJUSTER PLATFORM                             │
├─────────────────────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                      API GATEWAY (Spring Cloud Gateway)              │   │
│  │                    - Authentication/Authorization                    │   │
│  │                    - Rate Limiting                                   │   │
│  │                    - Request Routing                                 │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                     │                                       │
│  ┌──────────────────────────────────┼───────────────────────────────────┐  │
│  │                    MICROSERVICES LAYER                                │  │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐    │  │
│  │  │ Ingredient  │ │   Recipe    │ │Substitution │ │  Quantity   │    │  │
│  │  │  Matching   │ │   Search    │ │   Engine    │ │ Adjustment  │    │  │
│  │  │  Service    │ │   Service   │ │   Service   │ │   Service   │    │  │
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘    │  │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐    │  │
│  │  │    User     │ │   Recipe    │ │Notification │ │  Analytics  │    │  │
│  │  │   Profile   │ │  Database   │ │   Service   │ │   Service   │    │  │
│  │  │   Service   │ │   Service   │ │             │ │             │    │  │
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘    │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
│                                     │                                       │
│  ┌──────────────────────────────────┼───────────────────────────────────┐  │
│  │                    EVENT BUS (Apache Kafka)                           │  │
│  │  Topics: ingredient.submitted | recipe.matched | substitution.request │  │
│  │          user.preference.updated | analytics.event                    │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
│                                     │                                       │
│  ┌──────────────────────────────────┼───────────────────────────────────┐  │
│  │                    DATA LAYER                                         │  │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐                     │  │
│  │  │ PostgreSQL  │ │  MongoDB    │ │   Redis     │                     │  │
│  │  │ (Users,     │ │  (Recipes,  │ │  (Cache,    │                     │  │
│  │  │  Prefs)     │ │  Ingredients│ │   Sessions) │                     │  │
│  │  └─────────────┘ └─────────────┘ └─────────────┘                     │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
          ▲
          │
┌─────────┴─────────────────────────────────────────────────────────────────┐
│                         CLIENT APPLICATIONS                                │
├───────────────────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────────────────────┐  │
│  │                    Angular 20 PWA                                    │  │
│  │   - Responsive Web App (Desktop, Tablet, Mobile)                    │  │
│  │   - Offline-First with IndexedDB                                    │  │
│  │   - Service Workers for Caching                                     │  │
│  │   - Add to Home Screen Support                                      │  │
│  └─────────────────────────────────────────────────────────────────────┘  │
└───────────────────────────────────────────────────────────────────────────┘
```

### 3.2 Microservices Overview

| Service | Responsibility | Database | Key Events |
|---------|---------------|----------|------------|
| **Ingredient Matching** | Match user ingredients to recipes | MongoDB (read) | `ingredient.submitted`, `ingredient.matched` |
| **Recipe Search** | Search, rank, and filter recipes | MongoDB | `recipe.search.completed`, `recipe.matched` |
| **Substitution Engine** | AI-powered ingredient substitutions | MongoDB + Redis | `substitution.requested`, `substitution.completed` |
| **Quantity Adjustment** | Scale recipe quantities | - (stateless) | `quantity.adjusted` |
| **User Profile** | User preferences, dietary restrictions | PostgreSQL | `user.preference.updated`, `user.created` |
| **Recipe Database** | CRUD for recipes, moderation | MongoDB | `recipe.created`, `recipe.updated` |
| **Notification** | Alerts, reminders, suggestions | Redis (queues) | Consumes all events |
| **Analytics** | Usage tracking, insights | PostgreSQL | Consumes all events |

### 3.3 Service Communication Patterns

```
┌─────────────────────────────────────────────────────────────────┐
│                   COMMUNICATION PATTERNS                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  SYNCHRONOUS (REST/HTTP)                                        │
│  ─────────────────────────                                      │
│  Client ──HTTP──► API Gateway ──HTTP──► Microservice            │
│                                                                  │
│  Used for:                                                       │
│  • User authentication                                           │
│  • Recipe search queries (immediate response needed)             │
│  • Real-time ingredient matching                                 │
│  • User profile CRUD operations                                  │
│                                                                  │
│  ASYNCHRONOUS (Kafka Events)                                    │
│  ────────────────────────────                                   │
│  Service A ──publish──► Kafka Topic ──subscribe──► Service B    │
│                                                                  │
│  Used for:                                                       │
│  • Substitution suggestions (can be computed async)              │
│  • Analytics event collection                                    │
│  • Notification triggers                                         │
│  • User preference propagation                                   │
│  • Recipe indexing updates                                       │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 4. Data Models

### 4.1 Core Domain Entities

#### User (PostgreSQL)
```typescript
interface User {
  id: UUID;
  email: string;
  passwordHash: string;
  displayName: string;
  avatarUrl?: string;
  createdAt: DateTime;
  updatedAt: DateTime;
  lastLoginAt?: DateTime;
  isGuest: boolean;
  authProvider: 'local' | 'google' | 'github';
}
```

#### UserPreferences (PostgreSQL)
```typescript
interface UserPreferences {
  userId: UUID;
  dietaryRestrictions: DietaryRestriction[];
  allergies: string[];
  cuisinePreferences: string[];
  skillLevel: 'beginner' | 'intermediate' | 'advanced';
  householdSize: number;
  measurementSystem: 'metric' | 'imperial';
  defaultServings: number;
}

enum DietaryRestriction {
  VEGETARIAN = 'vegetarian',
  VEGAN = 'vegan',
  GLUTEN_FREE = 'gluten_free',
  DAIRY_FREE = 'dairy_free',
  KETO = 'keto',
  HALAL = 'halal',
  KOSHER = 'kosher'
}
```

#### Recipe (MongoDB)
```typescript
interface Recipe {
  _id: ObjectId;
  title: string;
  slug: string;
  description: string;
  ingredients: RecipeIngredient[];
  instructions: Instruction[];
  prepTime: number; // minutes
  cookTime: number; // minutes
  servings: number;
  difficulty: 'easy' | 'medium' | 'hard';
  cuisine: string;
  categories: string[];
  tags: string[];
  nutritionalInfo?: NutritionalInfo;
  imageUrl?: string;
  videoUrl?: string;
  author: {
    userId?: UUID;
    name: string;
  };
  ratings: {
    average: number;
    count: number;
  };
  isApproved: boolean;
  createdAt: DateTime;
  updatedAt: DateTime;
  embedding?: number[]; // For semantic search
}

interface RecipeIngredient {
  ingredientId: ObjectId;
  name: string;
  quantity: number;
  unit: string;
  notes?: string;
  isOptional: boolean;
  substitutes?: string[];
}

interface Instruction {
  stepNumber: number;
  text: string;
  duration?: number; // minutes
  tips?: string[];
}

interface NutritionalInfo {
  calories: number;
  protein: number;
  carbohydrates: number;
  fat: number;
  fiber: number;
  sodium: number;
  perServing: boolean;
}
```

#### Ingredient (MongoDB)
```typescript
interface Ingredient {
  _id: ObjectId;
  name: string;
  aliases: string[];
  category: IngredientCategory;
  nutritionalInfoPer100g?: NutritionalInfo;
  commonUnits: string[];
  shelfLife?: {
    pantry?: number; // days
    refrigerated?: number;
    frozen?: number;
  };
  flavorProfile?: string[];
  embedding?: number[]; // For semantic matching
}

enum IngredientCategory {
  PROTEIN = 'protein',
  VEGETABLE = 'vegetable',
  FRUIT = 'fruit',
  DAIRY = 'dairy',
  GRAIN = 'grain',
  SPICE = 'spice',
  CONDIMENT = 'condiment',
  OIL = 'oil',
  SWEETENER = 'sweetener',
  BEVERAGE = 'beverage',
  OTHER = 'other'
}
```

#### Substitution (MongoDB)
```typescript
interface Substitution {
  _id: ObjectId;
  originalIngredientId: ObjectId;
  substituteIngredientId: ObjectId;
  conversionRatio: number;
  compatibilityScore: number; // 0-1
  flavorImpact: 'minimal' | 'moderate' | 'significant';
  textureImpact: 'minimal' | 'moderate' | 'significant';
  dietaryTags: string[];
  explanation: string;
  aiGenerated: boolean;
  approvedAt?: DateTime;
}
```

### 4.2 Event Schemas (Kafka)

```typescript
// Base event structure
interface BaseEvent {
  eventId: UUID;
  eventType: string;
  timestamp: DateTime;
  source: string;
  correlationId?: UUID;
}

// Ingredient Events
interface IngredientSubmittedEvent extends BaseEvent {
  eventType: 'ingredient.submitted';
  payload: {
    userId?: UUID;
    sessionId: string;
    ingredients: string[];
    preferences?: {
      dietaryRestrictions: string[];
      maxCookTime?: number;
    };
  };
}

// Recipe Events
interface RecipeMatchedEvent extends BaseEvent {
  eventType: 'recipe.matched';
  payload: {
    userId?: UUID;
    sessionId: string;
    recipeIds: ObjectId[];
    matchScores: number[];
    queryIngredients: string[];
  };
}

// Substitution Events
interface SubstitutionRequestedEvent extends BaseEvent {
  eventType: 'substitution.requested';
  payload: {
    userId?: UUID;
    recipeId: ObjectId;
    missingIngredients: string[];
    availableIngredients: string[];
  };
}

// User Events
interface UserPreferenceUpdatedEvent extends BaseEvent {
  eventType: 'user.preference.updated';
  payload: {
    userId: UUID;
    changes: Partial<UserPreferences>;
  };
}

// Analytics Events
interface AnalyticsEvent extends BaseEvent {
  eventType: 'analytics.event';
  payload: {
    userId?: UUID;
    sessionId: string;
    action: string;
    properties: Record<string, any>;
  };
}
```

---

## 5. API Specifications

### 5.1 API Gateway Routes

| Method | Path | Service | Description |
|--------|------|---------|-------------|
| POST | `/api/v1/auth/register` | User Profile | Register new user |
| POST | `/api/v1/auth/login` | User Profile | Authenticate user |
| POST | `/api/v1/auth/refresh` | User Profile | Refresh JWT token |
| GET | `/api/v1/users/me` | User Profile | Get current user profile |
| PUT | `/api/v1/users/me/preferences` | User Profile | Update preferences |
| POST | `/api/v1/ingredients/match` | Ingredient Matching | Find recipes by ingredients |
| GET | `/api/v1/recipes` | Recipe Search | Search recipes |
| GET | `/api/v1/recipes/{id}` | Recipe Database | Get recipe details |
| POST | `/api/v1/recipes` | Recipe Database | Create recipe |
| POST | `/api/v1/substitutions/suggest` | Substitution Engine | Get substitution suggestions |
| POST | `/api/v1/recipes/{id}/adjust` | Quantity Adjustment | Scale recipe |

### 5.2 Core API Endpoints

#### Ingredient Matching Service

```yaml
POST /api/v1/ingredients/match
Description: Find recipes matching available ingredients
Request:
  Content-Type: application/json
  Body:
    ingredients: string[]          # List of available ingredients
    minMatchPercentage?: number    # Minimum ingredient match % (default: 50)
    maxResults?: number            # Max recipes to return (default: 20)
    filters?:
      cuisines?: string[]
      maxCookTime?: number
      difficulty?: string[]
      dietary?: string[]
Response:
  200 OK:
    matches:
      - recipeId: string
        title: string
        matchPercentage: number
        matchedIngredients: string[]
        missingIngredients: string[]
        thumbnail: string
    totalResults: number
  400 Bad Request:
    error: string
    details: string[]
```

#### Recipe Search Service

```yaml
GET /api/v1/recipes
Description: Search and filter recipes
Query Parameters:
  q?: string                       # Full-text search query
  cuisine?: string[]               # Filter by cuisines
  dietary?: string[]               # Dietary restrictions
  difficulty?: string              # easy | medium | hard
  maxPrepTime?: number             # Maximum prep time in minutes
  maxCookTime?: number             # Maximum cook time in minutes
  page?: number                    # Page number (default: 1)
  limit?: number                   # Results per page (default: 20)
  sort?: string                    # rating | newest | cookTime
Response:
  200 OK:
    recipes:
      - id: string
        title: string
        description: string
        thumbnail: string
        prepTime: number
        cookTime: number
        difficulty: string
        rating: number
        ratingCount: number
    pagination:
      page: number
      limit: number
      total: number
      totalPages: number
```

#### Substitution Engine Service

```yaml
POST /api/v1/substitutions/suggest
Description: Get AI-powered substitution suggestions
Request:
  Content-Type: application/json
  Body:
    recipeId: string
    missingIngredients: string[]
    availableIngredients?: string[]
    preferences?:
      dietary?: string[]
      preferBudget?: boolean
Response:
  200 OK:
    substitutions:
      - original: string
        suggestions:
          - substitute: string
            conversionRatio: string
            compatibilityScore: number
            explanation: string
            flavorImpact: string
            dietaryInfo: string[]
    confidence: number
    aiGenerated: boolean
```

#### Quantity Adjustment Service

```yaml
POST /api/v1/recipes/{id}/adjust
Description: Scale recipe to desired servings
Request:
  Content-Type: application/json
  Body:
    targetServings: number
    measurementSystem?: 'metric' | 'imperial'
Response:
  200 OK:
    recipeId: string
    originalServings: number
    targetServings: number
    scaleFactor: number
    adjustedIngredients:
      - name: string
        originalQuantity: string
        adjustedQuantity: string
        unit: string
    adjustedInstructions:
      - stepNumber: number
        text: string
        adjustedDuration?: number
```

### 5.3 Authentication & Authorization

```yaml
# JWT Token Structure
Header:
  alg: RS256
  typ: JWT
Payload:
  sub: string          # User ID
  email: string
  roles: string[]      # ['user', 'admin', 'moderator']
  iat: number          # Issued at
  exp: number          # Expiration (1 hour)
  
# Refresh Token
Stored in: HttpOnly cookie
Expiration: 7 days
Rotation: New refresh token on each use

# API Key (for service-to-service)
Header: X-API-Key
Used by: Internal services, analytics
```

---

## 6. Backend Architecture

### 6.1 Microservice Structure

Each microservice follows a consistent layered architecture:

```
service-name/
├── src/main/java/com/recipeadjuster/servicename/
│   ├── ServiceNameApplication.java
│   ├── config/
│   │   ├── KafkaConfig.java
│   │   ├── SecurityConfig.java
│   │   └── OpenApiConfig.java
│   ├── controller/
│   │   └── ServiceController.java
│   ├── service/
│   │   ├── ServiceImpl.java
│   │   └── ServiceInterface.java
│   ├── repository/
│   │   └── EntityRepository.java
│   ├── model/
│   │   ├── entity/
│   │   ├── dto/
│   │   └── event/
│   ├── kafka/
│   │   ├── producer/
│   │   └── consumer/
│   └── exception/
│       ├── GlobalExceptionHandler.java
│       └── CustomExceptions.java
├── src/main/resources/
│   ├── application.yml
│   └── application-{profile}.yml
├── src/test/
├── Dockerfile
└── pom.xml
```

### 6.2 Spring AI Integration

```java
// SubstitutionAiService.java
@Service
public class SubstitutionAiService {
    
    private final ChatClient chatClient;
    private final EmbeddingClient embeddingClient;
    
    public SubstitutionAiService(ChatClient.Builder builder, 
                                  EmbeddingClient embeddingClient) {
        this.chatClient = builder
            .defaultSystem("""
                You are a culinary expert specializing in ingredient substitutions.
                Provide substitutions that maintain flavor profile and texture.
                Always explain WHY the substitution works.
                Consider dietary restrictions when suggesting alternatives.
                """)
            .build();
        this.embeddingClient = embeddingClient;
    }
    
    public SubstitutionResponse suggestSubstitutions(SubstitutionRequest request) {
        String prompt = buildPrompt(request);
        
        return chatClient.prompt()
            .user(prompt)
            .call()
            .entity(SubstitutionResponse.class);
    }
    
    public float[] generateEmbedding(String ingredientName) {
        return embeddingClient.embed(ingredientName);
    }
}
```

### 6.3 Kafka Event Configuration

```java
// KafkaConfig.java
@Configuration
@EnableKafka
public class KafkaConfig {
    
    @Bean
    public NewTopic ingredientSubmittedTopic() {
        return TopicBuilder.name("ingredient.submitted")
            .partitions(3)
            .replicas(1)
            .build();
    }
    
    @Bean
    public NewTopic recipeMatchedTopic() {
        return TopicBuilder.name("recipe.matched")
            .partitions(3)
            .replicas(1)
            .build();
    }
    
    @Bean
    public NewTopic substitutionRequestedTopic() {
        return TopicBuilder.name("substitution.requested")
            .partitions(3)
            .replicas(1)
            .build();
    }
}

// EventProducer.java
@Service
public class EventProducer {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    public void publishIngredientSubmitted(IngredientSubmittedEvent event) {
        kafkaTemplate.send("ingredient.submitted", event.getUserId(), event);
    }
}

// EventConsumer.java
@Service
public class AnalyticsEventConsumer {
    
    @KafkaListener(topics = {"ingredient.submitted", "recipe.matched", 
                             "substitution.requested"}, 
                   groupId = "analytics-service")
    public void consumeEvent(ConsumerRecord<String, BaseEvent> record) {
        analyticsService.processEvent(record.value());
    }
}
```

### 6.4 API Gateway Configuration

```yaml
# application.yml for Spring Cloud Gateway
spring:
  cloud:
    gateway:
      routes:
        - id: ingredient-matching
          uri: lb://ingredient-matching-service
          predicates:
            - Path=/api/v1/ingredients/**
          filters:
            - AuthFilter
            - RateLimiter=10,20
            
        - id: recipe-search
          uri: lb://recipe-search-service
          predicates:
            - Path=/api/v1/recipes/**
          filters:
            - AuthFilter
            
        - id: substitution-engine
          uri: lb://substitution-engine-service
          predicates:
            - Path=/api/v1/substitutions/**
          filters:
            - AuthFilter
            - CircuitBreaker=substitutionCB
            
        - id: user-profile
          uri: lb://user-profile-service
          predicates:
            - Path=/api/v1/users/**, /api/v1/auth/**
          filters:
            - AuthFilter

      default-filters:
        - DedupeResponseHeader=Access-Control-Allow-Origin
        - AddResponseHeader=X-Response-Time, %{response_time}ms
```

---

## 7. Frontend Architecture

### 7.1 Angular Project Structure

```
frontend/
├── src/
│   ├── app/
│   │   ├── core/
│   │   │   ├── auth/
│   │   │   │   ├── auth.guard.ts
│   │   │   │   ├── auth.interceptor.ts
│   │   │   │   └── auth.service.ts
│   │   │   ├── services/
│   │   │   │   ├── api.service.ts
│   │   │   │   ├── offline-storage.service.ts
│   │   │   │   └── sync.service.ts
│   │   │   └── core.module.ts
│   │   ├── shared/
│   │   │   ├── components/
│   │   │   │   ├── ingredient-input/
│   │   │   │   ├── recipe-card/
│   │   │   │   ├── loading-spinner/
│   │   │   │   └── error-message/
│   │   │   ├── directives/
│   │   │   ├── pipes/
│   │   │   └── shared.module.ts
│   │   ├── features/
│   │   │   ├── home/
│   │   │   │   ├── home.component.ts
│   │   │   │   └── home.routes.ts
│   │   │   ├── ingredient-input/
│   │   │   │   ├── ingredient-input.component.ts
│   │   │   │   └── ingredient-input.store.ts
│   │   │   ├── recipe-results/
│   │   │   │   ├── recipe-results.component.ts
│   │   │   │   ├── recipe-detail/
│   │   │   │   └── recipe-results.store.ts
│   │   │   ├── substitutions/
│   │   │   │   ├── substitution-panel.component.ts
│   │   │   │   └── substitution.store.ts
│   │   │   ├── user-profile/
│   │   │   │   ├── preferences/
│   │   │   │   ├── saved-recipes/
│   │   │   │   └── user-profile.routes.ts
│   │   │   └── auth/
│   │   │       ├── login/
│   │   │       ├── register/
│   │   │       └── auth.routes.ts
│   │   ├── state/
│   │   │   ├── app.state.ts
│   │   │   ├── user/
│   │   │   │   ├── user.actions.ts
│   │   │   │   ├── user.reducer.ts
│   │   │   │   ├── user.effects.ts
│   │   │   │   └── user.selectors.ts
│   │   │   └── recipes/
│   │   │       ├── recipes.actions.ts
│   │   │       ├── recipes.reducer.ts
│   │   │       ├── recipes.effects.ts
│   │   │       └── recipes.selectors.ts
│   │   ├── app.component.ts
│   │   ├── app.config.ts
│   │   └── app.routes.ts
│   ├── assets/
│   ├── environments/
│   ├── styles/
│   │   ├── _variables.scss
│   │   ├── _mixins.scss
│   │   └── styles.scss
│   ├── index.html
│   ├── main.ts
│   └── manifest.webmanifest
├── angular.json
├── package.json
├── ngsw-config.json
└── tsconfig.json
```

### 7.2 State Management with NgRx

```typescript
// recipes.actions.ts
export const RecipeActions = createActionGroup({
  source: 'Recipes',
  events: {
    'Search By Ingredients': props<{ ingredients: string[] }>(),
    'Search Success': props<{ matches: RecipeMatch[] }>(),
    'Search Failure': props<{ error: string }>(),
    'Load Recipe Detail': props<{ id: string }>(),
    'Recipe Detail Loaded': props<{ recipe: Recipe }>(),
    'Request Substitutions': props<{ recipeId: string; missing: string[] }>(),
    'Substitutions Received': props<{ substitutions: Substitution[] }>(),
  }
});

// recipes.reducer.ts
export interface RecipesState {
  matches: RecipeMatch[];
  selectedRecipe: Recipe | null;
  substitutions: Substitution[];
  loading: boolean;
  error: string | null;
}

const initialState: RecipesState = {
  matches: [],
  selectedRecipe: null,
  substitutions: [],
  loading: false,
  error: null
};

export const recipesReducer = createReducer(
  initialState,
  on(RecipeActions.searchByIngredients, state => ({
    ...state,
    loading: true,
    error: null
  })),
  on(RecipeActions.searchSuccess, (state, { matches }) => ({
    ...state,
    matches,
    loading: false
  })),
  on(RecipeActions.searchFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  }))
);

// recipes.effects.ts
@Injectable()
export class RecipesEffects {
  
  searchByIngredients$ = createEffect(() =>
    this.actions$.pipe(
      ofType(RecipeActions.searchByIngredients),
      switchMap(({ ingredients }) =>
        this.recipeService.matchByIngredients(ingredients).pipe(
          map(matches => RecipeActions.searchSuccess({ matches })),
          catchError(error => of(RecipeActions.searchFailure({ error: error.message })))
        )
      )
    )
  );
  
  constructor(
    private actions$: Actions,
    private recipeService: RecipeService
  ) {}
}
```

### 7.3 Offline-First with IndexedDB

```typescript
// offline-storage.service.ts
import Dexie, { Table } from 'dexie';

export interface CachedRecipe {
  id: string;
  data: Recipe;
  cachedAt: Date;
}

export interface PendingSync {
  id: string;
  action: 'create' | 'update' | 'delete';
  entity: string;
  data: any;
  createdAt: Date;
}

class RecipeAdjusterDB extends Dexie {
  recipes!: Table<CachedRecipe>;
  ingredients!: Table<Ingredient>;
  userPreferences!: Table<UserPreferences>;
  pendingSync!: Table<PendingSync>;
  
  constructor() {
    super('RecipeAdjusterDB');
    this.version(1).stores({
      recipes: 'id, cachedAt',
      ingredients: 'id, name, *aliases',
      userPreferences: 'userId',
      pendingSync: '++id, entity, createdAt'
    });
  }
}

@Injectable({ providedIn: 'root' })
export class OfflineStorageService {
  private db = new RecipeAdjusterDB();
  
  async cacheRecipe(recipe: Recipe): Promise<void> {
    await this.db.recipes.put({
      id: recipe.id,
      data: recipe,
      cachedAt: new Date()
    });
  }
  
  async getCachedRecipe(id: string): Promise<Recipe | undefined> {
    const cached = await this.db.recipes.get(id);
    return cached?.data;
  }
  
  async queueForSync(action: string, entity: string, data: any): Promise<void> {
    await this.db.pendingSync.add({
      action: action as any,
      entity,
      data,
      createdAt: new Date()
    });
  }
  
  async processPendingSync(): Promise<void> {
    const pending = await this.db.pendingSync.toArray();
    for (const item of pending) {
      try {
        await this.syncItem(item);
        await this.db.pendingSync.delete(item.id);
      } catch (error) {
        console.error('Sync failed for item:', item.id);
      }
    }
  }
}
```

### 7.4 PWA Service Worker Configuration

```json
// ngsw-config.json
{
  "$schema": "./node_modules/@angular/service-worker/config/schema.json",
  "index": "/index.html",
  "assetGroups": [
    {
      "name": "app",
      "installMode": "prefetch",
      "resources": {
        "files": [
          "/favicon.ico",
          "/index.html",
          "/manifest.webmanifest",
          "/*.css",
          "/*.js"
        ]
      }
    },
    {
      "name": "assets",
      "installMode": "lazy",
      "updateMode": "prefetch",
      "resources": {
        "files": [
          "/assets/**",
          "/*.(svg|cur|jpg|jpeg|png|apng|webp|avif|gif|otf|ttf|woff|woff2)"
        ]
      }
    }
  ],
  "dataGroups": [
    {
      "name": "api-recipes",
      "urls": ["/api/v1/recipes/**"],
      "cacheConfig": {
        "strategy": "freshness",
        "maxSize": 100,
        "maxAge": "1d",
        "timeout": "5s"
      }
    },
    {
      "name": "api-ingredients",
      "urls": ["/api/v1/ingredients/**"],
      "cacheConfig": {
        "strategy": "performance",
        "maxSize": 50,
        "maxAge": "7d"
      }
    }
  ]
}
```

---

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

---

## 9. Deployment Architecture

### 9.1 Zero-Budget Infrastructure

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        PRODUCTION ENVIRONMENT                            │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │                         NETLIFY (Frontend)                          │ │
│  │  • Angular PWA hosting                                              │ │
│  │  • Automatic HTTPS                                                  │ │
│  │  • Global CDN                                                       │ │
│  │  • Free tier: 100GB bandwidth/month                                │ │
│  └────────────────────────────────────────────────────────────────────┘ │
│                                    │                                     │
│                                    ▼                                     │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │                       RENDER.COM (Backend)                          │ │
│  │  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐               │ │
│  │  │ API Gateway  │ │  Ingredient  │ │   Recipe     │               │ │
│  │  │   Service    │ │   Matching   │ │   Search     │               │ │
│  │  └──────────────┘ └──────────────┘ └──────────────┘               │ │
│  │  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐               │ │
│  │  │ Substitution │ │    User      │ │   Recipe     │               │ │
│  │  │   Engine     │ │   Profile    │ │   Database   │               │ │
│  │  └──────────────┘ └──────────────┘ └──────────────┘               │ │
│  │  Free tier: 750 hours/month per service (spins down after 15min)  │ │
│  └────────────────────────────────────────────────────────────────────┘ │
│                                    │                                     │
│          ┌─────────────────────────┼─────────────────────────┐          │
│          ▼                         ▼                         ▼          │
│  ┌──────────────┐         ┌──────────────┐         ┌──────────────┐    │
│  │   MongoDB    │         │  PostgreSQL  │         │    Redis     │    │
│  │   Atlas      │         │   (Render)   │         │   (Upstash)  │    │
│  │  Free: 512MB │         │ Free: 90 days│         │ Free: 10K/day│    │
│  └──────────────┘         └──────────────┘         └──────────────┘    │
│                                    │                                     │
│                                    ▼                                     │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │                     CONFLUENT CLOUD (Kafka)                         │ │
│  │  • Free tier: 1 cluster, 1GB storage, 10 partitions                │ │
│  │  • Topics: ingredient.submitted, recipe.matched, etc.              │ │
│  └────────────────────────────────────────────────────────────────────┘ │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### 9.2 CI/CD Pipeline

```yaml
# .github/workflows/ci.yml
name: CI Pipeline

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  test-backend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
      - name: Cache Maven packages
        uses: actions/cache@v3
        with:
          path: ~/.m2
          key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
      - name: Run tests
        run: mvn clean verify

  test-frontend:
    runs-on: ubuntu-latest
    defaults:
      run:
        working-directory: frontend/recipe-adjuster-web
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version: '20'
          cache: 'npm'
          cache-dependency-path: frontend/recipe-adjuster-web/package-lock.json
      - run: npm ci
      - run: npm run lint
      - run: npm run test:ci
      - run: npm run build:prod

  deploy-backend:
    needs: test-backend
    if: github.ref == 'refs/heads/main'
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Deploy to Render
        env:
          RENDER_API_KEY: ${{ secrets.RENDER_API_KEY }}
        run: |
          curl -X POST "https://api.render.com/deploy/srv-xxx?key=$RENDER_API_KEY"

  deploy-frontend:
    needs: test-frontend
    if: github.ref == 'refs/heads/main'
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Deploy to Netlify
        uses: nwtgck/actions-netlify@v2
        with:
          publish-dir: './frontend/recipe-adjuster-web/dist'
          production-deploy: true
        env:
          NETLIFY_AUTH_TOKEN: ${{ secrets.NETLIFY_AUTH_TOKEN }}
          NETLIFY_SITE_ID: ${{ secrets.NETLIFY_SITE_ID }}
```

### 9.3 Docker Configuration

```yaml
# docker-compose.yml (Local Development)
version: '3.8'

services:
  # Databases
  postgres:
    image: postgres:16-alpine
    environment:
      POSTGRES_USER: recipeadj
      POSTGRES_PASSWORD: devpassword
      POSTGRES_DB: recipeadjuster
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  mongodb:
    image: mongo:7
    ports:
      - "27017:27017"
    volumes:
      - mongo_data:/data/db

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"

  # Kafka
  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1

  # Services (development)
  api-gateway:
    build: ./services/api-gateway
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: dev
    depends_on:
      - kafka

volumes:
  postgres_data:
  mongo_data:
```

---

## 10. Security Architecture

### 10.1 Security Layers

```
┌─────────────────────────────────────────────────────────────────┐
│                      SECURITY ARCHITECTURE                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  LAYER 1: NETWORK SECURITY                                      │
│  ──────────────────────────                                     │
│  • HTTPS everywhere (Netlify/Render auto-provision)             │
│  • CORS configuration (whitelist frontend domain)               │
│  • Rate limiting at API Gateway (10 req/sec per IP)             │
│                                                                  │
│  LAYER 2: AUTHENTICATION                                        │
│  ────────────────────────                                       │
│  • JWT tokens (RS256, 1-hour expiry)                            │
│  • Refresh tokens (HttpOnly cookie, 7-day expiry)               │
│  • OAuth2 social login (Google, GitHub)                         │
│  • Guest mode with limited features                             │
│                                                                  │
│  LAYER 3: AUTHORIZATION                                         │
│  ───────────────────────                                        │
│  • Role-based access control (USER, ADMIN, MODERATOR)           │
│  • Resource-level permissions (own recipes, own profile)        │
│  • API key authentication for service-to-service               │
│                                                                  │
│  LAYER 4: DATA PROTECTION                                       │
│  ─────────────────────────                                      │
│  • Password hashing (BCrypt, cost factor 12)                    │
│  • PII encryption at rest (user email, preferences)             │
│  • Input validation and sanitization                            │
│  • SQL/NoSQL injection prevention (parameterized queries)       │
│                                                                  │
│  LAYER 5: APPLICATION SECURITY                                  │
│  ─────────────────────────────                                  │
│  • OWASP Top 10 compliance                                      │
│  • Content Security Policy headers                              │
│  • XSS prevention (Angular sanitization)                        │
│  • CSRF protection (SameSite cookies)                           │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 10.2 Spring Security Configuration

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())  // Stateless API
            .cors(cors -> cors.configurationSource(corsConfigSource()))
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/recipes/public/**").permitAll()
                .requestMatchers("/api/v1/ingredients/match").permitAll()
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/v1/moderation/**").hasAnyRole("ADMIN", "MODERATOR")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
            .build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
            "https://recipe-adjuster.netlify.app",
            "http://localhost:4200"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
```

---

## 11. Testing Strategy

### 11.1 Testing Pyramid

```
                    ┌───────────────┐
                    │   E2E Tests   │  ← 10% (Playwright)
                    │  (Critical    │
                    │   Flows)      │
                    └───────┬───────┘
                            │
              ┌─────────────┴─────────────┐
              │    Integration Tests      │  ← 30% (Spring Boot Test)
              │  (API, Database, Kafka)   │
              └─────────────┬─────────────┘
                            │
        ┌───────────────────┴───────────────────┐
        │           Unit Tests                   │  ← 60% (JUnit, Jest)
        │  (Services, Components, Utilities)     │
        └────────────────────────────────────────┘
```

### 11.2 Backend Testing

```java
// Integration Test Example
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class IngredientMatchingControllerTest {
    
    @Container
    static MongoDBContainer mongodb = new MongoDBContainer("mongo:7");
    
    @Container
    static KafkaContainer kafka = new KafkaContainer(
        DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void shouldMatchRecipesByIngredients() throws Exception {
        // Given
        String request = """
            {
                "ingredients": ["chicken", "garlic", "olive oil"],
                "minMatchPercentage": 60
            }
            """;
        
        // When/Then
        mockMvc.perform(post("/api/v1/ingredients/match")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.matches").isArray())
            .andExpect(jsonPath("$.matches[0].matchPercentage").value(greaterThan(60)));
    }
}
```

### 11.3 Frontend Testing

```typescript
// Component Test (Jest + Angular Testing Library)
describe('IngredientInputComponent', () => {
  it('should add ingredient to list when user types and presses enter', async () => {
    const { getByPlaceholderText, getByText } = await render(IngredientInputComponent);
    
    const input = getByPlaceholderText('Enter ingredient...');
    await userEvent.type(input, 'chicken{enter}');
    
    expect(getByText('chicken')).toBeInTheDocument();
  });
  
  it('should call search when ingredients are submitted', async () => {
    const mockStore = jasmine.createSpyObj('Store', ['dispatch']);
    const { getByRole } = await render(IngredientInputComponent, {
      providers: [{ provide: Store, useValue: mockStore }]
    });
    
    await userEvent.click(getByRole('button', { name: /find recipes/i }));
    
    expect(mockStore.dispatch).toHaveBeenCalledWith(
      RecipeActions.searchByIngredients({ ingredients: jasmine.any(Array) })
    );
  });
});
```

### 11.4 E2E Testing (Playwright)

```typescript
// e2e/ingredient-search.spec.ts
import { test, expect } from '@playwright/test';

test.describe('Recipe Search Flow', () => {
  test('user can search recipes by ingredients', async ({ page }) => {
    await page.goto('/');
    
    // Add ingredients
    await page.fill('[data-testid="ingredient-input"]', 'chicken');
    await page.press('[data-testid="ingredient-input"]', 'Enter');
    await page.fill('[data-testid="ingredient-input"]', 'garlic');
    await page.press('[data-testid="ingredient-input"]', 'Enter');
    
    // Search
    await page.click('[data-testid="search-button"]');
    
    // Verify results
    await expect(page.locator('[data-testid="recipe-card"]')).toHaveCount.greaterThan(0);
    await expect(page.locator('[data-testid="match-percentage"]').first())
      .toContainText('%');
  });
  
  test('user can view substitution suggestions', async ({ page }) => {
    await page.goto('/recipes/sample-recipe-id');
    
    await page.click('[data-testid="show-substitutions"]');
    
    await expect(page.locator('[data-testid="substitution-panel"]')).toBeVisible();
    await expect(page.locator('[data-testid="substitution-item"]')).toHaveCount.greaterThan(0);
  });
});
```

---

## 12. Monitoring & Observability

### 12.1 Observability Stack

```
┌─────────────────────────────────────────────────────────────────┐
│                    OBSERVABILITY ARCHITECTURE                    │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  METRICS (Prometheus + Grafana Cloud Free Tier)                 │
│  ──────────────────────────────────────────────                 │
│  • JVM metrics (heap, GC, threads)                              │
│  • HTTP metrics (request rate, latency, errors)                 │
│  • Kafka metrics (consumer lag, throughput)                     │
│  • Custom business metrics (searches/min, substitutions/min)    │
│                                                                  │
│  LOGGING (Structured JSON + Grafana Loki Free Tier)             │
│  ─────────────────────────────────────────────────              │
│  • Structured JSON logs with correlation IDs                    │
│  • Log levels: ERROR, WARN, INFO, DEBUG                         │
│  • Centralized aggregation across all services                  │
│                                                                  │
│  TRACING (OpenTelemetry + Grafana Tempo Free Tier)              │
│  ────────────────────────────────────────────────               │
│  • Distributed tracing across microservices                     │
│  • Request flow visualization                                   │
│  • Performance bottleneck identification                        │
│                                                                  │
│  ALERTING                                                        │
│  ────────                                                        │
│  • Error rate > 1% for 5 minutes                                │
│  • P99 latency > 2 seconds                                      │
│  • Kafka consumer lag > 1000 messages                           │
│  • Service health check failures                                │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 12.2 Spring Boot Actuator Configuration

```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: health,info,prometheus,metrics
  endpoint:
    health:
      show-details: when_authorized
      probes:
        enabled: true
  metrics:
    tags:
      application: ${spring.application.name}
    export:
      prometheus:
        enabled: true
  tracing:
    sampling:
      probability: 1.0  # 100% for dev, reduce for prod

logging:
  pattern:
    console: '{"timestamp":"%d{ISO8601}","level":"%p","service":"${spring.application.name}","traceId":"%X{traceId}","spanId":"%X{spanId}","message":"%m"}%n'
```

### 12.3 Health Check Endpoints

```java
@Component
public class KafkaHealthIndicator implements HealthIndicator {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    @Override
    public Health health() {
        try {
            kafkaTemplate.getProducerFactory()
                .createProducer()
                .partitionsFor("health-check");
            return Health.up()
                .withDetail("kafka", "Connected")
                .build();
        } catch (Exception e) {
            return Health.down()
                .withDetail("kafka", "Disconnected")
                .withException(e)
                .build();
        }
    }
}
```

---

## 13. Future Considerations

### 13.1 Scalability Roadmap

| Phase | Trigger | Actions |
|-------|---------|---------|
| **Phase 1** | >1,000 DAU | Migrate from Render free tier to paid; add Redis caching layer |
| **Phase 2** | >10,000 DAU | Kubernetes deployment; horizontal pod autoscaling |
| **Phase 3** | >100,000 DAU | Multi-region deployment; read replicas; CDN for recipe images |

### 13.2 Feature Roadmap Integration

| Feature | Architecture Impact | Timeline |
|---------|---------------------|----------|
| Image Recognition | New AI service; vision model integration | Phase 2 |
| Meal Planning | New microservice; calendar integration | Phase 2 |
| Barcode Scanning | Mobile-specific PWA feature; product DB | Phase 2 |
| Voice Input | WebSpeech API integration; no backend changes | Phase 1 |

### 13.3 Technical Debt Considerations

- **Database Migration**: Plan Render PostgreSQL → Supabase before 90-day limit
- **Cold Start Optimization**: Implement GraalVM native images for faster Render spin-up
- **Cost Monitoring**: Set up billing alerts for all free-tier services
- **Recipe Licensing**: Establish community contribution guidelines and moderation workflow

---

## 14. Appendix

### 14.1 Glossary

| Term | Definition |
|------|------------|
| **PWA** | Progressive Web App - web application with native-like capabilities |
| **Spring AI** | Spring framework for integrating AI/ML models |
| **NgRx** | Reactive state management for Angular |
| **IndexedDB** | Browser-based NoSQL database for offline storage |
| **Kafka** | Distributed event streaming platform |

### 14.2 References

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring AI Reference](https://docs.spring.io/spring-ai/reference/)
- [Angular Documentation](https://angular.dev/)
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [Confluent Cloud Free Tier](https://www.confluent.io/confluent-cloud/tryfree/)

### 14.3 Architecture Decision Records (ADR)

| ADR | Decision | Rationale |
|-----|----------|-----------|
| ADR-001 | Use microservices over monolith | Independent scaling, team autonomy, technology flexibility |
| ADR-002 | Choose Kafka over RabbitMQ | Better durability, replay capability, free Confluent tier |
| ADR-003 | Angular PWA over native apps | Zero mobile development cost, single codebase, offline support |
| ADR-004 | Spring AI over direct LLM APIs | Spring ecosystem integration, provider abstraction |
| ADR-005 | MongoDB for recipes, PostgreSQL for users | Document model fits recipes; relational model fits users |

---

*Document generated: December 19, 2025*
*Next review: January 2026*

