# Database and Kafka Deployment Guide

## MongoDB Atlas Setup

### 1. Create Account and Cluster
1. Go to https://www.mongodb.com/cloud/atlas
2. Sign up for free tier
3. Create new cluster:
   - **Tier:** M0 (Free, 512MB)
   - **Region:** Closest to your users
   - **Cluster Name:** recipe-adjuster-prod

### 2. Configure Network Access
```bash
# Allow access from anywhere (for Render services)
IP Address: 0.0.0.0/0
Description: Allow all (Render services)
```

### 3. Create Database User
```bash
Username: recipeadjuster
Password: <generate-strong-password>
Role: readWrite on recipeadjuster database
```

### 4. Get Connection String
```bash
mongodb+srv://recipeadjuster:<password>@recipe-adjuster-prod.xxxxx.mongodb.net/recipeadjuster?retryWrites=true&w=majority
```

## PostgreSQL Setup (Render)

### 1. Create PostgreSQL Database
1. Go to Render Dashboard
2. Click "New +" → "PostgreSQL"
3. Configure:
   - **Name:** recipe-adjuster-postgres
   - **Database:** recipeadjuster
   - **User:** recipeadj
   - **Region:** Same as services
   - **Plan:** Free (90 days, then migrate to Supabase)

### 2. Get Connection Details
```bash
Internal URL: postgresql://user:pass@host:5432/dbname
External URL: postgresql://user:pass@external-host:5432/dbname
```

### 3. Migration to Supabase (After 90 days)
```bash
# Export from Render
pg_dump -h render-host -U user -d dbname > backup.sql

# Import to Supabase
psql -h supabase-host -U postgres -d postgres < backup.sql
```

## Confluent Cloud Kafka Setup

### 1. Create Account
1. Go to https://confluent.cloud
2. Sign up for free tier
3. Create cluster:
   - **Type:** Basic
   - **Region:** Closest to services
   - **Name:** recipe-adjuster-kafka

### 2. Create Topics
```bash
# Recipe events
recipe.submitted
recipe.matched
recipe.updated

# Ingredient events
ingredient.submitted
ingredient.matched

# User events
user.registered
user.preference.updated

# Substitution events
substitution.requested
substitution.completed

# Analytics events
analytics.event.tracked
```

### 3. Generate API Keys
```bash
# Create API key for services
Key: <KAFKA_API_KEY>
Secret: <KAFKA_API_SECRET>
```

### 4. Get Bootstrap Servers
```bash
pkc-xxxxx.region.provider.confluent.cloud:9092
```

## Environment Variables

### Backend Services
```bash
# MongoDB
MONGODB_URI=mongodb+srv://user:pass@cluster.mongodb.net/recipeadjuster

# PostgreSQL
POSTGRES_URL=postgresql://user:pass@host:5432/dbname
POSTGRES_USER=recipeadj
POSTGRES_PASSWORD=<password>

# Kafka
KAFKA_BOOTSTRAP_SERVERS=pkc-xxxxx.region.provider.confluent.cloud:9092
KAFKA_API_KEY=<key>
KAFKA_API_SECRET=<secret>
KAFKA_SECURITY_PROTOCOL=SASL_SSL
KAFKA_SASL_MECHANISM=PLAIN
```

## Free Tier Limits

### MongoDB Atlas
- **Storage:** 512 MB
- **RAM:** Shared
- **Connections:** 500 concurrent
- **Backups:** Not included (manual only)

### Render PostgreSQL
- **Duration:** 90 days free
- **Storage:** 1 GB
- **Connections:** 97 concurrent
- **After 90 days:** $7/month or migrate to Supabase

### Confluent Cloud Kafka
- **Storage:** 1 GB
- **Throughput:** 250 MB/s
- **Retention:** 1 day
- **Partitions:** Unlimited

## Cost Optimization

### MongoDB
1. Use indexes efficiently
2. Implement data archiving
3. Monitor storage usage
4. Consider upgrading to M10 ($0.08/hr) if needed

### PostgreSQL
1. Migrate to Supabase free tier after 90 days
2. Implement connection pooling
3. Archive old data
4. Use read replicas for analytics

### Kafka
1. Set short retention periods (1 day)
2. Use compact topics where possible
3. Monitor throughput
4. Batch messages efficiently
