# AppTestersHub Android SDK - Usage Example

## Basic Integration

### Step 1: Add Dependency

Add to your app's `build.gradle`:

```gradle
dependencies {
    implementation 'com.github.apptestershub:sdk:1.0.0'
}
```

### Step 2: Initialize SDK

In your `Application` class:

```java
import com.apptestershub.sdk.AppTestersHubSDK;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize SDK
        AppTestersHubSDK.initialize(this, "YOUR_APP_ID");
    }
}
```

Or in your main activity:

```java
import com.apptestershub.sdk.AppTestersHubSDK;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize SDK
        AppTestersHubSDK.initialize(this, "YOUR_APP_ID");
        
        setContentView(R.layout.activity_main);
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

### Step 3: Handle Activity Lifecycle

In each activity:

```java
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
```

## Advanced Configuration

### Custom Base URL

```java
AppTestersHubSDK.initialize(this, "YOUR_APP_ID", "https://your-domain.com");
```

### Enable Debug Logging

```java
AppTestersHubSDK.setDebugMode(true);
```

### Custom Configuration

```java
import com.apptestershub.sdk.config.SDKConfiguration;

SDKConfiguration config = SDKConfiguration.builder()
    .setBaseUrl("https://your-domain.com")
    .setDebugMode(true)
    .setUploadRetryCount(5)
    .setStatusCheckIntervalHours(12)
    .build();

AppTestersHubSDK.initialize(this, "YOUR_APP_ID", config);
```

## Testing

### Test SDK Integration

1. Build and install your app
2. Open the app
3. If SDK is enabled, you should see email dialog on first launch
4. Enter your tester email
5. Confirm testing start
6. Close and reopen app
7. Screenshot should be captured automatically

### Verify Screenshot Upload

1. Log in to AppTestersHub
2. Navigate to your app's testing dashboard
3. Check for uploaded screenshots
4. Verify screenshot appears in testing progress

## Troubleshooting

### SDK Not Working

1. Check if SDK is enabled for your app in AppTestersHub
2. Verify App ID is correct
3. Check network connectivity
4. Enable debug logging to see SDK activity

### Email Dialog Not Showing

1. Check if SDK is enabled for your app
2. Verify App ID is correct
3. Check if email is already stored (clear app data to reset)

### Screenshots Not Uploading

1. Check network connectivity
2. Verify API endpoints are accessible
3. Check device logs for errors
4. Verify date is correct (must be today)

