# Coding Standards

## CI/CD Pipeline (Frontend lint/test/build commands)

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
```

## Testing Strategy

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
