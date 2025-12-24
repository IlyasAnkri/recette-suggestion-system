# Epic 005: Angular PWA Frontend with Offline Support

## Epic Goal

Build a modern, responsive Angular 20 Progressive Web App with offline-first capabilities, NgRx state management, and seamless integration with backend microservices.

## Epic Description

**Existing System Context:**
- Backend microservices from Epics 002-004 (Ingredient Matching, Recipe Search, Substitution Engine, User Profile)
- API Gateway at `https://api.recipe-adjuster.com` (or localhost:8080 for dev)
- Technology stack: Angular 20.x, NgRx 17.x, Angular Material 17.x, Dexie.js 4.x, @angular/pwa
- Zero-budget constraint: Netlify free tier (100GB bandwidth/month)

**Enhancement Details:**

**What's being built:**
- **Angular 20 Application**: Responsive web app with signals, standalone components, and SSR
- **PWA Features**: Service workers, offline caching, "Add to Home Screen" support, background sync
- **NgRx State Management**: Centralized state for recipes, user, substitutions with effects for API calls
- **Offline-First Architecture**: IndexedDB storage (Dexie.js) for recipes, ingredients, user preferences
- **UI Components**: Ingredient input, recipe cards, substitution panel, user profile, loading states
- **Routing**: Home, recipe search, recipe detail, substitutions, user profile, auth (login/register)

**How it integrates:**
- Angular HttpClient calls API Gateway endpoints with JWT token in Authorization header
- Service workers cache API responses for offline access (freshness strategy for recipes, performance strategy for ingredients)
- IndexedDB stores cached recipes, user preferences, pending sync operations
- Background sync queues user actions (save recipe, update preferences) when offline
- NgRx effects handle API calls, error handling, and Kafka event simulation (optimistic updates)

**Success criteria:**
- App loads in <3 seconds on 3G connection
- Offline mode allows browsing cached recipes without internet
- "Add to Home Screen" prompt appears on mobile devices
- State management prevents duplicate API calls (NgRx selectors)
- Responsive design works on desktop (1920px), tablet (768px), mobile (375px)
- Lighthouse PWA score >90

## Stories

1. **Story 5.1:** Set up Angular 20 project with PWA and NgRx
   - Create Angular 20 project with standalone components
   - Add @angular/pwa package (service worker, manifest.webmanifest)
   - Add NgRx store, effects, and entity packages
   - Configure Angular Material with custom theme
   - Set up routing with lazy-loaded feature modules
   - Configure environment files (dev, staging, prod API URLs)

2. **Story 5.2:** Implement core layout and navigation
   - Create app shell with header, sidebar, main content area
   - Add navigation menu (Home, Search, Saved Recipes, Profile)
   - Implement responsive design (mobile hamburger menu, desktop sidebar)
   - Add loading spinner component (global and inline variants)
   - Create error message component with retry button
   - Add footer with links (About, Privacy, Terms)

3. **Story 5.3:** Build ingredient input feature with NgRx state
   - Create ingredient input component (chip input with autocomplete)
   - Implement NgRx state: `IngredientState` (submitted ingredients, suggestions)
   - Add effects for fetching ingredient suggestions from API
   - Implement "Find Recipes" button that dispatches search action
   - Add validation (min 1 ingredient, max 20 ingredients)
   - Store submitted ingredients in IndexedDB for offline access

4. **Story 5.4:** Build recipe results and detail views
   - Create recipe card component (thumbnail, title, match percentage, missing ingredients)
   - Implement recipe results list with infinite scroll
   - Add filters (cuisine, dietary, difficulty) with NgRx state
   - Create recipe detail component (full recipe, ingredients, instructions, nutrition)
   - Implement "Show Substitutions" button that fetches from Substitution Engine
   - Cache recipe details in IndexedDB (Dexie.js)

5. **Story 5.5:** Implement substitution panel with AI explanations
   - Create substitution panel component (slides in from right)
   - Display missing ingredients with suggested substitutions
   - Show AI-generated explanations (flavor impact, texture impact, conversion ratio)
   - Add "Use Substitution" button that updates recipe ingredient list
   - Implement dietary filter for substitutions (vegan, gluten-free, etc.)
   - Store substitution preferences in IndexedDB

6. **Story 5.6:** Build user authentication and profile management
   - Create login/register forms with validation
   - Implement OAuth2 social login buttons (Google, GitHub)
   - Add JWT token storage in localStorage (with expiry check)
   - Create HTTP interceptor to attach Authorization header
   - Implement user profile page (display name, avatar, preferences)
   - Add preferences form (dietary restrictions, skill level, household size)
   - Implement "Continue as Guest" button for guest mode

7. **Story 5.7:** Implement offline-first architecture with IndexedDB
   - Set up Dexie.js database with tables (recipes, ingredients, userPreferences, pendingSync)
   - Implement offline storage service (cache recipes, get cached recipes, queue for sync)
   - Add background sync for pending operations (save recipe, update preferences)
   - Implement conflict resolution (last-write-wins for preferences)
   - Show offline indicator in UI when no internet connection
   - Test offline functionality (disconnect network, browse cached recipes)

8. **Story 5.8:** Configure service worker for PWA caching
   - Configure ngsw-config.json (asset groups, data groups)
   - Set freshness strategy for recipe API (cache-first with 1-day TTL)
   - Set performance strategy for ingredient API (cache-first with 7-day TTL)
   - Add "Add to Home Screen" prompt (detect iOS Safari, Android Chrome)
   - Implement service worker update notification (new version available)
   - Test PWA features (offline mode, install prompt, push notifications placeholder)

9. **Story 5.9:** Optimize performance and Lighthouse score
   - Implement lazy loading for feature modules (recipe detail, profile)
   - Add image lazy loading with placeholder
   - Optimize bundle size (tree shaking, code splitting)
   - Add preloading strategy for critical routes
   - Implement virtual scrolling for long recipe lists
   - Run Lighthouse audit, fix issues to achieve >90 PWA score

## Compatibility Requirements

- [x] Angular 20.x with standalone components (no NgModules)
- [x] Responsive design: mobile (375px), tablet (768px), desktop (1920px)
- [x] Browser support: Chrome 90+, Firefox 88+, Safari 14+, Edge 90+
- [x] PWA features work on iOS Safari and Android Chrome
- [x] Netlify free tier: 100GB bandwidth/month, 300 build minutes/month

## Risk Mitigation

**Primary Risk:** Netlify free tier bandwidth (100GB/month) may be insufficient for images
**Mitigation:**
- Use external image CDN (Cloudinary free tier: 25GB storage, 25GB bandwidth)
- Compress images to WebP format (smaller file size)
- Implement lazy loading for images (only load visible images)
- Monitor bandwidth usage via Netlify dashboard
- Plan upgrade to paid tier ($19/month for 400GB) if needed

**Secondary Risk:** IndexedDB storage quota may be exceeded on mobile devices
**Mitigation:**
- Limit cached recipes to 100 most recent (estimate 10MB total)
- Implement LRU eviction policy (remove least recently used recipes)
- Request persistent storage permission (prevents browser from clearing data)
- Show storage usage indicator in settings (e.g., "Using 8MB of 50MB")

**Rollback Plan:**
- Disable service worker if caching causes issues (users always fetch fresh data)
- Remove IndexedDB storage and rely on API calls only
- Revert to server-side rendering if client-side performance is poor

## Definition of Done

- [x] Angular PWA deployed to Netlify and accessible at `https://recipe-adjuster.netlify.app`
- [x] User can search recipes by ingredients and view results
- [x] Offline mode works (cached recipes browsable without internet)
- [x] "Add to Home Screen" prompt appears on mobile devices
- [x] User can login with email/password or OAuth2 (Google, GitHub)
- [x] Substitution panel shows AI-generated explanations
- [x] Responsive design tested on mobile, tablet, desktop
- [x] Lighthouse PWA score >90
- [x] NgRx DevTools show state changes correctly
- [x] Service worker caches API responses (verified in DevTools)
- [x] E2E tests pass (Playwright) for critical flows

## Dependencies

- **Epic 001**: Infrastructure Foundation (API Gateway)
- **Epic 002**: Ingredient Matching & Recipe Search (API endpoints)
- **Epic 003**: AI-Powered Substitution Engine (API endpoints)
- **Epic 004**: User Management & Authentication (API endpoints)

## Timeline Estimate

**4-5 weeks** (as per brainstorming Priority #3)

## Technical Notes

- Angular version: 20.x (signals, standalone components)
- NgRx version: 17.x (signal store, functional effects)
- Angular Material version: 17.x (Material Design 3)
- Dexie.js version: 4.x (IndexedDB wrapper)
- Service worker: @angular/pwa (ngsw)
- API base URL: `https://api.recipe-adjuster.com` (prod), `http://localhost:8080` (dev)
- JWT token stored in localStorage with key `auth_token`
- IndexedDB database name: `RecipeAdjusterDB`

## Acceptance Criteria

1. User visits app, sees ingredient input, types "chicken", "garlic", clicks "Find Recipes", sees 5+ results
2. User clicks recipe card, sees full recipe detail with ingredients and instructions
3. User clicks "Show Substitutions", sees 3 alternatives for missing ingredient with AI explanation
4. User disconnects internet, browses cached recipes, sees offline indicator
5. User clicks "Add to Home Screen" on mobile, app installs as PWA
6. User logs in with Google, sees profile page with display name and avatar
7. Lighthouse audit shows PWA score >90, Performance >85
8. NgRx DevTools show state tree with recipes, user, substitutions slices
9. Service worker caches `/api/v1/recipes` responses (verified in DevTools Application tab)
10. E2E test: User searches recipes, views detail, requests substitutions (all pass)
