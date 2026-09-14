#!/bin/bash
set -e

cd "$(dirname "$0")"

echo "Setting up Gradle wrapper..."

# Make gradlew executable
chmod +x gradlew

# Download gradle-wrapper.jar if it doesn't exist
if [ ! -f "gradle/wrapper/gradle-wrapper.jar" ]; then
    echo "Downloading gradle-wrapper.jar..."
    curl -L "https://github.com/gradle/gradle/raw/v8.0.0/gradle/wrapper/gradle-wrapper.jar" \
        -o gradle/wrapper/gradle-wrapper.jar
    
    if [ ! -f "gradle/wrapper/gradle-wrapper.jar" ]; then
        echo "Failed to download gradle-wrapper.jar"
        echo "Trying alternative method..."
        
        # Try using gradle command if available
        if command -v gradle >/dev/null 2>&1; then
            echo "Using gradle command to initialize wrapper..."
            gradle wrapper --gradle-version 8.0
        else
            echo "Error: Could not download gradle-wrapper.jar"
            echo "Please download it manually from:"
            echo "https://github.com/gradle/gradle/raw/v8.0.0/gradle/wrapper/gradle-wrapper.jar"
            echo "And save it to: gradle/wrapper/gradle-wrapper.jar"
            exit 1
        fi
    else
        echo "✓ gradle-wrapper.jar downloaded successfully"
    fi
else
    echo "✓ gradle-wrapper.jar already exists"
fi

# Verify wrapper JAR exists
if [ ! -f "gradle/wrapper/gradle-wrapper.jar" ]; then
    echo "Error: gradle-wrapper.jar is still missing"
    exit 1
fi

echo ""
echo "Building SDK..."
./gradlew clean assembleRelease

echo ""
echo "Checking for AAR file..."
if [ -f "build/outputs/aar/apptestershub-sdk-release.aar" ]; then
    echo "✓ AAR file created successfully!"
    ls -lh build/outputs/aar/apptestershub-sdk-release.aar
else
    echo "✗ AAR file not found. Check build output above for errors."
    exit 1
fi

