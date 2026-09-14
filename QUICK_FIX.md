# Quick Fix for Gradle Wrapper JAR

The `gradle-wrapper.jar` file is missing. Here's how to fix it:

## Option 1: Download Manually (Recommended)

1. Open your browser and go to:
   ```
   https://github.com/gradle/gradle/raw/v8.0.0/gradle/wrapper/gradle-wrapper.jar
   ```

2. Save the file to:
   ```
   android-sdk/apptestershub-sdk/gradle/wrapper/gradle-wrapper.jar
   ```

## Option 2: Use curl Command

Run this in your terminal:

```bash
cd android-sdk/apptestershub-sdk
curl -L "https://github.com/gradle/gradle/raw/v8.0.0/gradle/wrapper/gradle-wrapper.jar" \
  -o gradle/wrapper/gradle-wrapper.jar
```

## Option 3: Use Gradle Command (if Gradle is installed)

```bash
cd android-sdk/apptestershub-sdk
gradle wrapper --gradle-version 8.0
```

## After Downloading

1. Verify the file exists:
   ```bash
   ls -lh gradle/wrapper/gradle-wrapper.jar
   ```

2. Make gradlew executable:
   ```bash
   chmod +x gradlew
   ```

3. Test the wrapper:
   ```bash
   ./gradlew --version
   ```

4. Build the SDK:
   ```bash
   ./gradlew assembleRelease
   ```

5. Check for the AAR file:
   ```bash
   ls -lh build/outputs/aar/apptestershub-sdk-release.aar
   ```

## Expected File Size

The `gradle-wrapper.jar` should be approximately **57 KB**.

