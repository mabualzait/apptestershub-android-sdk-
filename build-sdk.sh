#!/bin/bash
cd "$(dirname "$0")"
chmod +x gradlew
./gradlew clean assembleRelease 2>&1 | tee build-output.log
echo ""
echo "Build completed. Checking for AAR file..."
if [ -f "build/outputs/aar/apptestershub-sdk-release.aar" ]; then
    echo "✓ AAR file created successfully!"
    ls -lh build/outputs/aar/apptestershub-sdk-release.aar
else
    echo "✗ AAR file not found. Check build-output.log for errors."
fi

