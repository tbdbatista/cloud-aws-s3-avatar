#!/usr/bin/env bash
set -e

# Change directory to the script parent to find docker-compose.yml
cd "$(dirname "$0")/.."

echo "Starting LocalStack with S3 enabled..."
docker-compose up -d

echo "Waiting for LocalStack S3 to be ready..."
sleep 5
echo "LocalStack S3 is ready!"
