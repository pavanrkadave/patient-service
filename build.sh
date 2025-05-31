#!/bin/bash

# Docker build script for patient-service

set -e  # Exit on any error

# Configuration
IMAGE_NAME="pavanrkadave/patient-service"
TAG="${1:-latest}"
FULL_IMAGE_NAME="$IMAGE_NAME:$TAG"

echo "🏗️  Building Docker image: $FULL_IMAGE_NAME"

# Build the Docker image
docker build -t "$FULL_IMAGE_NAME" .

echo "✅ Successfully built: $FULL_IMAGE_NAME"

# Show image details
echo "📊 Image details:"
docker images | grep "$IMAGE_NAME" | head -1

# Optional: Tag for different environments
if [ "$TAG" != "latest" ]; then
    docker tag "$FULL_IMAGE_NAME" "$IMAGE_NAME:latest"
    echo "🏷️  Also tagged as: $IMAGE_NAME:latest"
fi

echo "🎉 Build completed!"
echo ""
echo "To run the container:"
echo "  docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=local $FULL_IMAGE_NAME"
echo ""
echo "To run with docker-compose:"
echo "  docker-compose up -d"