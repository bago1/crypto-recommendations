#!/bin/bash

set -e

 echo "Running Maven clean and package..."
mvn clean package

 echo "Building Docker image..."
docker build -t bago1/crypto:latest .

 echo "Running Docker container..."
docker run -p 8080:8080 bago1/crypto:latest


