# Building the Android SDK

## Prerequisites

1. **Java JDK 8 or later** - Required for Gradle
2. **Android SDK** - Required for Android builds
3. **Gradle** (optional) - Will be downloaded automatically by wrapper

## Build Steps

### Step 1: Fix Permissions

Make sure `gradlew` is executable:

```bash
cd android-sdk/apptestershub-sdk
chmod +x gradlew
```

### Step 2: Download Gradle Wrapper JAR (if missing)

If `gradle/wrapper/gradle-wrapper.jar` doesn't exist, download it:

```bash
curl -L -o gradle/wrapper/gradle-wrapper.jar \
  https://raw.githubusercontent.com/gradle/gradle/v8.0.0/gradle/wrapper/gradle-wrapper.jar
```

Or use Gradle to initialize the wrapper:

```bash
gradle wrapper --gradle-version 8.0
```

### Step 3: Build the SDK

Build the release AAR:

```bash
./gradlew assembleRelease
```

Or use the build script:

```bash
bash build-sdk.sh
```

### Step 4: Find the AAR File

The AAR file will be created at:

```
build/outputs/aar/apptestershub-sdk-release.aar
```

## Troubleshooting

### Permission Denied Error

If you get `permission denied: ./gradlew`:

```bash
chmod +x gradlew
```

### Gradle Wrapper JAR Missing

If you get an error about missing `gradle-wrapper.jar`:

```bash
gradle wrapper --gradle-version 8.0
```

Or download it manually:

```bash
curl -L -o gradle/wrapper/gradle-wrapper.jar \
  https://raw.githubusercontent.com/gradle/gradle/v8.0.0/gradle/wrapper/gradle-wrapper.jar
```

### Build Errors

If you encounter build errors:

1. Clean the build:
   ```bash
   ./gradlew clean
   ```

2. Rebuild:
   ```bash
   ./gradlew assembleRelease
   ```

3. Check for compilation errors in the output

## Using Android Studio

1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. Go to **Build → Make Project** (Cmd+F9 / Ctrl+F9)
4. Or **Build → Build Bundle(s) / APK(s) → Build AAR(s)**

The AAR file will be in `build/outputs/aar/` directory.

