# AppTestersHub Android SDK

[![](https://jitpack.io/v/mabualzait/apptestershub-android-sdk-.svg)](https://jitpack.io/#mabualzait/apptestershub-android-sdk-)
![Platform](https://img.shields.io/badge/platform-Android-green.svg)
![Min SDK](https://img.shields.io/badge/minSdk-21-blue.svg)
![Target SDK](https://img.shields.io/badge/targetSdk-34-brightgreen.svg)
![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)

Lightweight, secure Android SDK for **[AppTestersHub](https://app-testers.com)** to automate Google Play 14-day closed testing compliance. It tracks authentic 1-minute daily testing sessions, verifies tester identity, securely handles cryptographic HMAC request signing, and automatically captures verification screenshots.

---

## 📦 Distribution & Installation Methods

### Method 1: Gradle via JitPack (Recommended)

#### Step 1.1: Add JitPack Repository

In your **`settings.gradle`** (Gradle 7.0+):

```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

*Or in Kotlin DSL (`settings.gradle.kts`):*

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

*(For older projects using root `build.gradle`):*

```groovy
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

#### Step 1.2: Add the SDK Dependency

In your **`app/build.gradle`**:

```groovy
dependencies {
    implementation 'com.github.mabualzait:apptestershub-android-sdk-:v1.0.0'
}
```

*Or in Kotlin DSL (`app/build.gradle.kts`):*

```kotlin
dependencies {
    implementation("com.github.mabualzait:apptestershub-android-sdk-:v1.0.0")
}
```

---

### Method 2: Direct AAR Download (Offline / Manual)

1. Download **`apptestershub-sdk-release.aar`** from the [GitHub Releases](https://github.com/mabualzait/apptestershub-android-sdk-/releases) or directly from [AppTestersHub SDK Docs](https://app-testers.com/sdk-docs).
2. Copy the file into your app module's `libs/` folder (e.g. `app/libs/apptestershub-sdk-release.aar`).
3. In your `app/build.gradle`:

```groovy
dependencies {
    implementation files('libs/apptestershub-sdk-release.aar')

    // Required transitive dependencies
    implementation 'com.squareup.okhttp3:okhttp:4.12.0'
    implementation 'com.google.code.gson:gson:2.10.1'
    implementation 'androidx.security:security-crypto:1.1.0-alpha06'
    implementation 'com.google.android.material:material:1.11.0'
}
```

---

### Method 3: Build from Source

```bash
git clone https://github.com/mabualzait/apptestershub-android-sdk-.git
cd apptestershub-android-sdk-
./gradlew assembleRelease
```
The compiled library will be at `build/outputs/aar/apptestershub-android-sdk--release.aar`.

---

## 🚀 Quick Start Integration

### 1. Initialize in Launcher Activity or Application Class

#### Java
```java
package com.yourcompany.app;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.apptestershub.sdk.AppTestersHubSDK;

public class MainActivity extends AppCompatActivity {

    private static final String APP_ID = "YOUR_APP_ID";
    private static final String API_KEY = "YOUR_SDK_API_KEY";
    private static final String API_SECRET = "YOUR_SDK_API_SECRET";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize AppTestersHub SDK
        AppTestersHubSDK.initialize(this, APP_ID, API_KEY, API_SECRET);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppTestersHubSDK.onActivityResumed(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppTestersHubSDK.onActivityPaused(this);
    }
}
```

#### Kotlin
```kotlin
package com.yourcompany.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.apptestershub.sdk.AppTestersHubSDK

class MainActivity : AppCompatActivity() {

    companion object {
        private const val APP_ID = "YOUR_APP_ID"
        private const val API_KEY = "YOUR_SDK_API_KEY"
        private const val API_SECRET = "YOUR_SDK_API_SECRET"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize AppTestersHub SDK
        AppTestersHubSDK.initialize(this, APP_ID, API_KEY, API_SECRET)
    }

    override fun onResume() {
        super.onResume()
        AppTestersHubSDK.onActivityResumed(this)
    }

    override fun onPause() {
        super.onPause()
        AppTestersHubSDK.onActivityPaused(this)
    }
}
```

---

## 🛡️ ProGuard / R8 Rules

If ProGuard / R8 minification is enabled in your release builds, append these rules to your `proguard-rules.pro`:

```proguard
-keep class com.apptestershub.sdk.** { *; }
-keepclassmembers class com.apptestershub.sdk.models.** { *; }
-dontwarn okhttp3.**
-dontwarn com.google.gson.**
```

---

## ⚙️ How It Works

1. **Email Prompt**: On the tester's first launch, a polished branded modal asks for their registered AppTestersHub email.
2. **Encrypted Storage**: The verified email and device auth tokens are stored encrypted using Android KeyStore (`EncryptedSharedPreferences`).
3. **Session Verification**: A non-intrusive floating timer overlay counts the tester's active session up to 60 seconds.
4. **Heartbeat & Proof**: Heartbeats are sent every 15 seconds. At 60 seconds of verified foreground testing, a proof screenshot is automatically taken and signed via HMAC-SHA256, crediting the tester's daily test cycle.
5. **Remote Kill Switch**: When testing finishes or if disabled by the developer in the AppTestersHub dashboard, the SDK silently deactivates without affecting app performance.

---

## 📄 License

```
Copyright 2026 AppTestersHub

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
```
