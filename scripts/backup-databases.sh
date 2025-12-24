#!/bin/bash

# Database Backup Script for Recipe Adjuster
# Runs weekly via GitHub Actions

set -e

TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="./backups/$TIMESTAMP"
mkdir -p "$BACKUP_DIR"

echo "Starting database backups at $TIMESTAMP..."

# MongoDB Backup
echo "Backing up MongoDB..."
mongodump --uri="$MONGODB_URI" --out="$BACKUP_DIR/mongodb" --gzip
echo "✅ MongoDB backup complete"

# PostgreSQL Backup
echo "Backing up PostgreSQL..."
pg_dump "$POSTGRES_URL" --format=custom --file="$BACKUP_DIR/postgres.dump"
echo "✅ PostgreSQL backup complete"

# Compress backups
echo "Compressing backups..."
tar -czf "backup_$TIMESTAMP.tar.gz" -C "$BACKUP_DIR" .
echo "✅ Compression complete"

# Upload to cloud storage (optional)
if [ -n "$AWS_S3_BUCKET" ]; then
    echo "Uploading to S3..."
    aws s3 cp "backup_$TIMESTAMP.tar.gz" "s3://$AWS_S3_BUCKET/backups/"
    echo "✅ Upload complete"
fi

# Cleanup old backups (keep last 7)
echo "Cleaning up old backups..."
ls -t backup_*.tar.gz | tail -n +8 | xargs -r rm
echo "✅ Cleanup complete"

echo "Backup completed successfully at $(date)"
