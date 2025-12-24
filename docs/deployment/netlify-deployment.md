# Netlify Deployment Guide

## Overview
Deploy the Recipe Adjuster Angular PWA to Netlify with automatic HTTPS, CDN, and continuous deployment.

## Prerequisites
- Netlify account (free tier available)
- GitHub repository connected
- Angular application built and tested

## Quick Start

### 1. Connect Repository
1. Go to https://app.netlify.com
2. Click "Add new site" → "Import an existing project"
3. Choose GitHub and authorize
4. Select `suggestionrecette` repository
5. Configure build settings:
   - **Base directory**: `frontend`
   - **Build command**: `npm run build`
   - **Publish directory**: `dist/recipe-adjuster-web/browser`
   - **Node version**: 20

### 2. Environment Variables
Add in Netlify dashboard under Site settings → Environment variables:

```bash
API_BASE_URL=https://recipe-adjuster-api-gateway.onrender.com
NODE_VERSION=20
NPM_VERSION=10
```

### 3. Deploy
Click "Deploy site" - Netlify will:
1. Clone repository
2. Install dependencies (`npm ci`)
3. Build application (`npm run build`)
4. Deploy to CDN
5. Provision SSL certificate

## Configuration Files

### netlify.toml
Located at `frontend/netlify.toml` - defines build settings, headers, redirects, and plugins.

Key features:
- **Build configuration**: Node 20, npm build command
- **Context-specific environments**: Production vs staging
- **Security headers**: CSP, X-Frame-Options, etc.
- **Asset caching**: Aggressive caching for static files
- **SPA routing**: Redirect all routes to index.html
- **Lighthouse plugin**: Automated performance audits

### _redirects
Located at `frontend/_redirects` - handles SPA routing and API proxying.

```
/api/*  https://recipe-adjuster-api-gateway.onrender.com/:splat  200
/*      /index.html  200
```

## Custom Domain Setup

### Add Custom Domain
1. Go to Site settings → Domain management
2. Click "Add custom domain"
3. Enter domain: `recipe-adjuster.com`
4. Follow DNS configuration instructions

### DNS Configuration
Add these records to your DNS provider:

```
Type    Name    Value
A       @       75.2.60.5
CNAME   www     recipe-adjuster.netlify.app
```

### SSL Certificate
Netlify automatically provisions Let's Encrypt SSL certificates:
- Issued within minutes
- Auto-renewal every 90 days
- Forced HTTPS enabled by default

## Branch Deploys

### Production (main branch)
- URL: `https://recipe-adjuster.netlify.app`
- Auto-deploy on push to `main`
- Environment: `production`

### Staging (develop branch)
- URL: `https://develop--recipe-adjuster.netlify.app`
- Auto-deploy on push to `develop`
- Environment: `deploy-preview`

### Pull Request Previews
- URL: `https://deploy-preview-{PR#}--recipe-adjuster.netlify.app`
- Created automatically for each PR
- Deleted when PR is closed

## Build Configuration

### Build Command
```bash
npm run build
```

This runs:
```json
{
  "scripts": {
    "build": "ng build --configuration production"
  }
}
```

### Build Settings
```toml
[build]
  base = "frontend"
  command = "npm run build"
  publish = "dist/recipe-adjuster-web/browser"
  
[build.environment]
  NODE_VERSION = "20"
  NPM_VERSION = "10"
```

### Build Optimization
- **Tree shaking**: Remove unused code
- **Minification**: Compress JS/CSS
- **Code splitting**: Lazy load routes
- **Asset optimization**: Compress images
- **Service Worker**: Cache assets for offline

## Performance Optimization

### Lighthouse Scores Target
- Performance: ≥ 90
- Accessibility: ≥ 90
- Best Practices: ≥ 90
- SEO: ≥ 90

### Optimization Techniques
1. **Prerendering**: Angular Universal SSR
2. **Image optimization**: WebP format, lazy loading
3. **Font optimization**: Subset fonts, preload
4. **Bundle size**: Code splitting, tree shaking
5. **Caching**: Aggressive cache headers

### CDN Configuration
Netlify's global CDN automatically:
- Serves from nearest edge location
- Compresses responses (gzip/brotli)
- Handles DDoS protection
- Provides instant cache invalidation

## Monitoring

### Build Logs
1. Go to Deploys tab
2. Click on a deploy
3. View build logs
4. Download logs if needed

### Analytics
Enable Netlify Analytics (paid):
- Page views
- Unique visitors
- Top pages
- Traffic sources
- Bandwidth usage

### Performance Monitoring
Use Lighthouse plugin results:
1. Go to Deploy details
2. View Lighthouse report
3. Check performance metrics
4. Identify optimization opportunities

## Troubleshooting

### Build Failures

**Node version mismatch**
```bash
# Solution: Set NODE_VERSION in netlify.toml
[build.environment]
  NODE_VERSION = "20"
```

**Dependency installation fails**
```bash
# Solution: Delete package-lock.json and regenerate
npm install
git add package-lock.json
git commit -m "Update package-lock.json"
```

**Build command not found**
```bash
# Solution: Verify build command in netlify.toml
[build]
  command = "npm run build"
```

### Routing Issues

**404 on page refresh**
```bash
# Solution: Ensure _redirects file exists
/*  /index.html  200
```

**API calls fail (CORS)**
```bash
# Solution: Configure API proxy in _redirects
/api/*  https://api-backend.com/:splat  200
```

### Performance Issues

**Large bundle size**
- Enable code splitting
- Lazy load routes
- Optimize images
- Remove unused dependencies

**Slow initial load**
- Enable prerendering (SSR)
- Optimize critical CSS
- Defer non-critical JS
- Use service worker caching

## Free Tier Limits
- **100 GB bandwidth/month**
- **300 build minutes/month**
- **Unlimited sites**
- **Automatic HTTPS**
- **Deploy previews**
- **Form submissions**: 100/month

## Deployment Checklist
- [ ] Repository connected to Netlify
- [ ] Build settings configured
- [ ] Environment variables set
- [ ] netlify.toml file created
- [ ] _redirects file created
- [ ] Custom domain configured (optional)
- [ ] SSL certificate provisioned
- [ ] Branch deploys enabled
- [ ] Lighthouse plugin configured
- [ ] Build successful
- [ ] Site accessible via HTTPS
- [ ] SPA routing works
- [ ] API calls successful

## CI/CD Integration

### GitHub Actions
Netlify automatically deploys on:
- Push to `main` → Production
- Push to `develop` → Staging
- Pull requests → Deploy previews

### Manual Deploy
```bash
# Install Netlify CLI
npm install -g netlify-cli

# Login
netlify login

# Deploy
netlify deploy --prod
```

## Security Best Practices
1. **HTTPS only**: Force HTTPS redirect
2. **Security headers**: CSP, X-Frame-Options
3. **Environment variables**: Never commit secrets
4. **Access control**: Restrict admin access
5. **Audit logs**: Monitor deploy activity

## Next Steps
- Configure custom domain
- Set up monitoring alerts
- Enable Netlify Analytics
- Implement A/B testing
- Configure form handling
- Set up serverless functions (if needed)
