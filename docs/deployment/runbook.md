# Deployment Runbook

## Common Issues and Resolutions

### 1. Service Won't Start

**Symptoms:**
- Service shows "Failed" status in Render
- Health check endpoint returns 503
- Logs show startup errors

**Diagnosis:**
```bash
# Check Render logs
# Go to service → Logs tab

# Common errors:
# - Port binding issues
# - Environment variable missing
# - Database connection failure
```

**Resolution:**
1. Verify environment variables are set
2. Check database connection strings
3. Ensure port matches Dockerfile EXPOSE
4. Review startup logs for specific errors
5. Restart service manually if needed

### 2. Database Connection Failure

**Symptoms:**
- "Connection refused" errors
- "Authentication failed" errors
- Timeout errors

**Diagnosis:**
```bash
# Test MongoDB connection
mongosh "$MONGODB_URI"

# Test PostgreSQL connection
psql "$POSTGRES_URL"
```

**Resolution:**

**MongoDB:**
1. Check IP whitelist (0.0.0.0/0 for Render)
2. Verify username/password
3. Ensure cluster is running
4. Check connection string format

**PostgreSQL:**
1. Verify Render PostgreSQL is running
2. Check connection string
3. Ensure database exists
4. Test from local environment

### 3. Kafka Unavailable

**Symptoms:**
- "Connection timeout" to Kafka
- "Authentication failed" errors
- Messages not being produced/consumed

**Diagnosis:**
```bash
# Check Kafka cluster status in Confluent Cloud
# Verify API keys are valid
```

**Resolution:**
1. Verify KAFKA_BOOTSTRAP_SERVERS
2. Check KAFKA_API_KEY and KAFKA_API_SECRET
3. Ensure topics exist
4. Verify SASL configuration
5. Check Confluent Cloud cluster status

### 4. High Error Rate

**Symptoms:**
- 500 errors in logs
- Increased response times
- Memory/CPU spikes

**Diagnosis:**
```bash
# Check service metrics in Render
# Review error logs
# Check database performance
```

**Resolution:**
1. Scale up instance (if on paid tier)
2. Optimize database queries
3. Add caching
4. Review recent code changes
5. Consider rollback if issue started after deployment

### 5. Frontend Not Loading

**Symptoms:**
- Blank page
- 404 errors on routes
- API calls failing

**Diagnosis:**
```bash
# Check browser console
# Verify Netlify deployment status
# Test API endpoints directly
```

**Resolution:**
1. Clear browser cache
2. Check _redirects file
3. Verify API_BASE_URL environment variable
4. Test API Gateway health endpoint
5. Review Netlify build logs

### 6. SSL Certificate Issues

**Symptoms:**
- "Not secure" warning
- Certificate expired errors
- Mixed content warnings

**Resolution:**
1. Netlify: Auto-renews, check domain settings
2. Render: Verify custom domain configuration
3. Ensure HTTPS redirect is enabled
4. Check for mixed content (http in https page)

## Rollback Procedures

### Quick Rollback (Revert Last Commit)
```bash
cd /path/to/suggestionrecette
./scripts/rollback.sh
```

### Manual Rollback Steps
```bash
# 1. Revert commit
git revert HEAD --no-edit
git push origin main

# 2. Wait for auto-deploy or trigger manually
curl -X POST "$RENDER_DEPLOY_HOOK_URL"
curl -X POST "$NETLIFY_BUILD_HOOK_URL"

# 3. Monitor deployment
# Check Render dashboard
# Check Netlify dashboard
```

### Rollback to Specific Version
```bash
# Find commit hash
git log --oneline

# Revert to specific commit
git revert <commit-hash> --no-edit
git push origin main
```

### Render One-Click Rollback
1. Go to service in Render dashboard
2. Click "Deploys" tab
3. Find previous successful deployment
4. Click "Redeploy"

## Monitoring and Alerts

### Health Check Endpoints
```bash
# API Gateway
curl https://recipe-adjuster-api-gateway.onrender.com/actuator/health

# Recipe Database Service
curl https://recipe-adjuster-recipe-db.onrender.com/actuator/health

# User Profile Service
curl https://recipe-adjuster-user-profile.onrender.com/actuator/health

# Frontend
curl https://recipe-adjuster.netlify.app
```

### Set Up UptimeRobot
1. Go to https://uptimerobot.com
2. Create monitors for each service
3. Set check interval: 5 minutes
4. Configure alerts (email/Slack)

### Render Monitoring
- CPU usage
- Memory usage
- Request count
- Response times
- Error rates

### Log Aggregation
```bash
# View logs in real-time
# Render: Service → Logs tab
# Netlify: Site → Deploys → View logs

# Download logs for analysis
# Render: Click "Download logs"
```

## Emergency Contacts

### On-Call Rotation
- Primary: [Name] - [Contact]
- Secondary: [Name] - [Contact]

### Escalation Path
1. Check runbook for known issues
2. Attempt rollback if recent deployment
3. Contact on-call engineer
4. Escalate to team lead if unresolved in 30 min

## Deployment Checklist

### Pre-Deployment
- [ ] All tests passing in CI
- [ ] Code reviewed and approved
- [ ] Database migrations tested
- [ ] Environment variables updated
- [ ] Backup completed

### During Deployment
- [ ] Monitor deployment logs
- [ ] Check health endpoints
- [ ] Verify database connections
- [ ] Test critical user flows

### Post-Deployment
- [ ] Run smoke tests
- [ ] Monitor error rates
- [ ] Check performance metrics
- [ ] Update deployment log
- [ ] Notify team in Slack

## Performance Optimization

### Backend Services
1. Enable JVM optimizations: `-Xmx400m -Xms200m`
2. Use connection pooling
3. Implement caching (Redis)
4. Add database indexes
5. Optimize queries

### Frontend
1. Enable code splitting
2. Lazy load routes
3. Optimize images (WebP)
4. Use service worker caching
5. Minimize bundle size

## Security Checklist
- [ ] Secrets stored in environment variables
- [ ] HTTPS enabled everywhere
- [ ] Security headers configured
- [ ] Dependencies updated
- [ ] Vulnerability scans passing
- [ ] API authentication working
- [ ] CORS configured correctly
