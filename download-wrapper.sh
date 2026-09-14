#!/bin/bash
cd "$(dirname "$0")"

echo "Downloading gradle-wrapper.jar..."

# Try multiple download methods
if curl -L "https://github.com/gradle/gradle/raw/v8.0.0/gradle/wrapper/gradle-wrapper.jar" -o gradle/wrapper/gradle-wrapper.jar 2>&1; then
    echo "✓ Downloaded using curl"
elif wget -O gradle/wrapper/gradle-wrapper.jar "https://github.com/gradle/gradle/raw/v8.0.0/gradle/wrapper/gradle-wrapper.jar" 2>&1; then
    echo "✓ Downloaded using wget"
elif python3 -c "import urllib.request; urllib.request.urlretrieve('https://github.com/gradle/gradle/raw/v8.0.0/gradle/wrapper/gradle-wrapper.jar', 'gradle/wrapper/gradle-wrapper.jar')" 2>&1; then
    echo "✓ Downloaded using python3"
else
    echo "✗ Failed to download. Please download manually:"
    echo "https://github.com/gradle/gradle/raw/v8.0.0/gradle/wrapper/gradle-wrapper.jar"
    echo "Save to: gradle/wrapper/gradle-wrapper.jar"
    exit 1
fi

# Verify file was downloaded
if [ -f "gradle/wrapper/gradle-wrapper.jar" ]; then
    echo "✓ File verified: $(ls -lh gradle/wrapper/gradle-wrapper.jar | awk '{print $5, $9}')"
    exit 0
else
    echo "✗ File not found after download"
    exit 1
fi

