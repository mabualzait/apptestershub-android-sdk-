#!/bin/bash
set -e

cd "$(dirname "$0")"

echo "Fixing Gradle compatibility issues..."

# 1. Clean Gradle cache and build directory
echo "Cleaning Gradle cache and build directory..."
rm -rf .gradle build

# 2. Stop all Gradle daemons
echo "Stopping Gradle daemons..."
if [ -f gradlew ]; then
    ./gradlew --stop 2>/dev/null || true
fi

# 3. Download correct Gradle wrapper JAR (for Gradle 8.2)
echo "Downloading Gradle wrapper JAR for version 8.2..."
curl -L "https://github.com/gradle/gradle/raw/v8.2.0/gradle/wrapper/gradle-wrapper.jar" \
    -o gradle/wrapper/gradle-wrapper.jar

# 4. Verify wrapper JAR exists
if [ ! -f "gradle/wrapper/gradle-wrapper.jar" ]; then
    echo "Error: Failed to download gradle-wrapper.jar"
    exit 1
fi

echo "✓ Gradle wrapper JAR downloaded"

# 5. Make gradlew executable
chmod +x gradlew

echo ""
echo "✓ Gradle compatibility fixed!"
echo ""
echo "Next steps in Android Studio:"
echo "1. File → Invalidate Caches / Restart → Invalidate and Restart"
echo "2. Wait for Gradle sync to complete"
echo "3. Build → Make Project"
echo ""
echo "Or run from terminal:"
echo "  ./gradlew clean assembleRelease"

