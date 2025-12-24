# Render.com Deployment Guide

## Overview
This guide covers deploying Recipe Adjuster microservices to Render.com using Docker containers.

## Prerequisites
- Render.com account (free tier available)
- GitHub repository connected to Render
- Docker images built and tested locally

## Service Configuration

### 1. API Gateway (Port 8080)
```yaml
Name: recipe-adjuster-api-gateway
Type: Web Service
Environment: Docker
Build Context: ./services/api-gateway
Dockerfile: Dockerfile
Port: 8080
Health Check Path: /actuator/health
Instance Type: Free (512 MB RAM)
Auto-Deploy: Yes (main branch)
```

**Environment Variables:**
```bash
SPRING_PROFILES_ACTIVE=prod
JWT_PUBLIC_KEY=<from-secrets>
KAFKA_BOOTSTRAP_SERVERS=<confluent-cloud-url>
KAFKA_API_KEY=<from-secrets>
KAFKA_API_SECRET=<from-secrets>
```

### 2. Recipe Database Service (Port 8085)
```yaml
Name: recipe-adjuster-recipe-db
Type: Web Service
Environment: Docker
Build Context: ./services/recipe-database-service
Dockerfile: Dockerfile
Port: 8085
Health Check Path: /actuator/health
```

**Environment Variables:**
```bash
SPRING_PROFILES_ACTIVE=prod
MONGODB_URI=<mongodb-atlas-connection-string>
KAFKA_BOOTSTRAP_SERVERS=<confluent-cloud-url>
```

### 3. User Profile Service (Port 8086)
```yaml
Name: recipe-adjuster-user-profile
Type: Web Service
Environment: Docker
Build Context: ./services/user-profile-service
Dockerfile: Dockerfile
Port: 8086
Health Check Path: /actuator/health
```

**Environment Variables:**
```bash
SPRING_PROFILES_ACTIVE=prod
POSTGRES_URL=<render-postgres-url>
JWT_SECRET=<from-secrets>
OAUTH2_CLIENT_ID=<from-secrets>
OAUTH2_CLIENT_SECRET=<from-secrets>
```

### 4. Ingredient Matching Service (Port 8081)
```yaml
Name: recipe-adjuster-ingredient-matching
Type: Web Service
Environment: Docker
Build Context: ./services/ingredient-matching-service
Dockerfile: Dockerfile
Port: 8081
Health Check Path: /actuator/health
```

### 5. Recipe Search Service (Port 8082)
```yaml
Name: recipe-adjuster-recipe-search
Type: Web Service
Environment: Docker
Build Context: ./services/recipe-search-service
Dockerfile: Dockerfile
Port: 8082
Health Check Path: /actuator/health
```

### 6. Substitution Engine Service (Port 8083)
```yaml
Name: recipe-adjuster-substitution-engine
Type: Web Service
Environment: Docker
Build Context: ./services/substitution-engine-service
Dockerfile: Dockerfile
Port: 8083
Health Check Path: /actuator/health
```

### 7. Analytics Service (Port 8084)
```yaml
Name: recipe-adjuster-analytics
Type: Web Service
Environment: Docker
Build Context: ./services/analytics-service
Dockerfile: Dockerfile
Port: 8084
Health Check Path: /actuator/health
```

### 8. Config Server (Port 8888)
```yaml
Name: recipe-adjuster-config-server
Type: Web Service
Environment: Docker
Build Context: ./services/config-server
Dockerfile: Dockerfile
Port: 8888
Health Check Path: /actuator/health
```

## Deployment Steps

### Step 1: Create Render Account
1. Go to https://render.com
2. Sign up with GitHub
3. Grant repository access

### Step 2: Create Web Services
For each microservice:
1. Click "New +" → "Web Service"
2. Connect GitHub repository
3. Select branch (main)
4. Configure service settings (see above)
5. Add environment variables
6. Click "Create Web Service"

### Step 3: Configure Health Checks
```yaml
Health Check:
  Path: /actuator/health
  Interval: 30s
  Timeout: 10s
  Unhealthy Threshold: 3
  Healthy Threshold: 2
```

### Step 4: Set Up Auto-Deploy
1. Go to service settings
2. Enable "Auto-Deploy"
3. Select branch: main
4. Save changes

## Environment Variables Management

### Using Render Dashboard
1. Navigate to service
2. Go to "Environment" tab
3. Add key-value pairs
4. Click "Save Changes"

### Using render.yaml (Infrastructure as Code)
```yaml
services:
  - type: web
    name: recipe-adjuster-api-gateway
    env: docker
    dockerfilePath: ./services/api-gateway/Dockerfile
    dockerContext: ./services/api-gateway
    envVars:
      - key: SPRING_PROFILES_ACTIVE
        value: prod
      - key: JWT_PUBLIC_KEY
        sync: false  # Secret, set manually
```

## Free Tier Limitations
- **750 hours/month** per service (31.25 days)
- **512 MB RAM** per instance
- **Spins down after 15 minutes** of inactivity
- **Cold start time**: 30-60 seconds
- **Build minutes**: 500/month

## Monitoring

### Health Check Monitoring
```bash
# Check all services
curl https://recipe-adjuster-api-gateway.onrender.com/actuator/health
curl https://recipe-adjuster-recipe-db.onrender.com/actuator/health
curl https://recipe-adjuster-user-profile.onrender.com/actuator/health
```

### Logs
1. Go to service dashboard
2. Click "Logs" tab
3. View real-time logs
4. Download logs for analysis

## Troubleshooting

### Service Won't Start
1. Check logs for errors
2. Verify environment variables
3. Ensure health check endpoint responds
4. Check Docker image builds locally

### Database Connection Failures
1. Verify connection string format
2. Check network access (IP whitelist)
3. Ensure database credentials are correct
4. Test connection from local environment

### High Memory Usage
1. Monitor memory in dashboard
2. Optimize JVM settings: `-Xmx400m -Xms200m`
3. Consider upgrading to paid tier

## Deployment Checklist
- [ ] All services created on Render
- [ ] Environment variables configured
- [ ] Health checks passing
- [ ] Auto-deploy enabled
- [ ] Database connections verified
- [ ] Kafka connections verified
- [ ] API Gateway routing tested
- [ ] Monitoring alerts configured

## Cost Optimization
1. **Use Free Tier Efficiently**: 8 services × 750 hours = 6000 hours/month available
2. **Spin Down Unused Services**: Disable auto-deploy for dev services
3. **Combine Services**: Consider merging low-traffic services
4. **Monitor Usage**: Track hours in Render dashboard

## Next Steps
- Set up custom domain
- Configure SSL certificates
- Implement blue-green deployment
- Set up monitoring alerts
- Create backup procedures
