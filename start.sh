#!/bin/bash

# Deployment Stack Console Startup Script

set -e

echo "========================================="
echo "  Deployment Stack Console"
echo "========================================="
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check prerequisites
echo -e "${YELLOW}Checking prerequisites...${NC}"

# Check Java
if ! command -v java &> /dev/null; then
    echo -e "${RED}Error: Java is not installed${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Java found: $(java -version 2>&1 | head -1)${NC}"

# Check Maven
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}Error: Maven is not installed${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Maven found: $(mvn -v | head -1)${NC}"

# Check kubectl
if ! command -v kubectl &> /dev/null; then
    echo -e "${YELLOW}⚠ Warning: kubectl is not installed. Kubernetes access may not work.${NC}"
else
    echo -e "${GREEN}✓ kubectl found${NC}"
fi

echo ""

# Check if kubeconfig exists
if [ ! -f ~/.kube/config ]; then
    echo -e "${YELLOW}⚠ Warning: ~/.kube/config not found. Kubernetes access requires kubeconfig.${NC}"
    echo "   Please configure your kubeconfig with your cluster credentials."
    echo ""
fi

# Build option
BUILD_ONLY=false
DOCKER_BUILD=false

while [[ $# -gt 0 ]]; do
    case $1 in
        --build-only)
            BUILD_ONLY=true
            shift
            ;;
        --docker)
            DOCKER_BUILD=true
            shift
            ;;
        --help)
            echo "Usage: $0 [OPTIONS]"
            echo ""
            echo "Options:"
            echo "  --build-only    Build the project without running"
            echo "  --docker        Build Docker image instead of running locally"
            echo "  --help          Show this help message"
            echo ""
            exit 0
            ;;
        *)
            echo "Unknown option: $1"
            exit 1
            ;;
    esac
done

# Build Phase
echo -e "${YELLOW}Building project...${NC}"
mvn clean package -q

if [ $? -ne 0 ]; then
    echo -e "${RED}Build failed!${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Build successful${NC}"
echo ""

if [ "$BUILD_ONLY" = true ]; then
    echo -e "${GREEN}Build completed. Exiting...${NC}"
    exit 0
fi

# Docker Build
if [ "$DOCKER_BUILD" = true ]; then
    echo -e "${YELLOW}Building Docker image...${NC}"
    docker build -t deployment-stack-console:1.0.0 .
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Docker image built successfully${NC}"
        echo ""
        echo -e "${YELLOW}To run the Docker container:${NC}"
        echo "docker run -p 8080:8080 -v ~/.kube:/root/.kube deployment-stack-console:1.0.0"
        echo ""
    else
        echo -e "${RED}Docker build failed!${NC}"
        exit 1
    fi
    exit 0
fi

# Run Phase
echo -e "${YELLOW}Starting Deployment Stack Console...${NC}"
echo ""

# Run the application
mvn spring-boot:run

# If script reaches here, application has stopped
echo ""
echo -e "${YELLOW}Application stopped${NC}"
