#!/bin/bash

# Load environment variables
set -a
source .env
set +a

# Create backups directory if it doesn't exist
mkdir -p backups

# Generate filename with timestamp
TIMESTAMP=$(date +"%Y-%m-%d_%H-%M-%S")
BACKUP_FILE="backups/media_library_${TIMESTAMP}.sql"

echo "🔄 Backing up database to ${BACKUP_FILE}..."

# Run mysqldump inside the MySQL container
docker exec media_mysql mysqldump \
  -uapp_user \
  -p${MYSQL_PASSWORD} \
  --no-tablespaces \
  media_library > "${BACKUP_FILE}" 2>/dev/null

if [ $? -eq 0 ]; then
  FILE_SIZE=$(du -h "${BACKUP_FILE}" | cut -f1)
  echo "✅ Backup complete! (${FILE_SIZE})"
  echo "📁 Location: ${BACKUP_FILE}"
else
  echo "❌ Backup failed!"
  exit 1
fi
