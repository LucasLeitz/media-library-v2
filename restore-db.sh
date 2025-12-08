#!/bin/bash

# Check if backup file is provided
if [ -z "$1" ]; then
  # No file provided - find the most recent backup
  BACKUP_FILE=$(ls -t backups/*.sql 2>/dev/null | head -1)

  if [ -z "${BACKUP_FILE}" ]; then
    echo "❌ Error: No backups found in backups/"
    echo "💡 Run ./backup-db.sh to create a backup first"
    exit 1
  fi

  echo "📁 No backup file specified, using most recent: ${BACKUP_FILE}"
  echo ""
else
  BACKUP_FILE="$1"

  # Check if file exists
  if [ ! -f "${BACKUP_FILE}" ]; then
    echo "❌ Error: Backup file '${BACKUP_FILE}' not found"
    echo ""
    echo "Available backups:"
    ls -lh backups/*.sql 2>/dev/null || echo "  No backups found in backups/"
    exit 1
  fi
fi

# Load environment variables
set -a
source .env
set +a

echo "🔄 Restoring database from ${BACKUP_FILE}..."
echo ""
echo "⚠️  WARNING: This will replace ALL current data in the database!"
read -p "Are you sure you want to continue? (yes/no): " CONFIRM

if [ "${CONFIRM}" != "yes" ]; then
  echo "❌ Restore cancelled"
  exit 0
fi

# Restore the database
docker exec -i media_mysql mysql \
  -uapp_user \
  -p${MYSQL_PASSWORD} \
  media_library < "${BACKUP_FILE}"

if [ $? -eq 0 ]; then
  echo ""
  echo "✅ Database restored successfully!"
  echo "🔄 Restarting containers to apply changes..."
  docker compose restart api web
  echo "✅ Done!"
else
  echo "❌ Restore failed!"
  exit 1
fi
