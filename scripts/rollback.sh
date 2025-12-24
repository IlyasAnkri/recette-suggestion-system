#!/bin/bash

# Rollback Script for Recipe Adjuster
# Reverts last commit and triggers redeployment

set -e

echo "🔄 Starting rollback procedure..."

# Check if we're on main branch
CURRENT_BRANCH=$(git branch --show-current)
if [ "$CURRENT_BRANCH" != "main" ]; then
    echo "❌ Error: Must be on main branch to rollback production"
    exit 1
fi

# Confirm rollback
read -p "Are you sure you want to rollback the last deployment? (yes/no): " CONFIRM
if [ "$CONFIRM" != "yes" ]; then
    echo "Rollback cancelled"
    exit 0
fi

# Revert last commit
echo "Reverting last commit..."
git revert HEAD --no-edit

# Push to trigger redeployment
echo "Pushing revert commit..."
git push origin main

echo "✅ Rollback commit pushed successfully"
echo "⏳ Waiting for CI/CD to redeploy..."

# Trigger deploy hooks (optional, if not using auto-deploy)
if [ -n "$RENDER_DEPLOY_HOOK_URL" ]; then
    echo "Triggering Render deployment..."
    curl -X POST "$RENDER_DEPLOY_HOOK_URL"
fi

if [ -n "$NETLIFY_BUILD_HOOK_URL" ]; then
    echo "Triggering Netlify deployment..."
    curl -X POST "$NETLIFY_BUILD_HOOK_URL"
fi

echo "✅ Rollback procedure completed"
echo "📊 Monitor deployment at:"
echo "  - Render: https://dashboard.render.com"
echo "  - Netlify: https://app.netlify.com"
