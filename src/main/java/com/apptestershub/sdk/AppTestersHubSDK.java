package com.apptestershub.sdk;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apptestershub.sdk.analytics.AnalyticsHelper;
import com.apptestershub.sdk.config.SDKConfiguration;
import com.apptestershub.sdk.manager.SDKManager;
import com.apptestershub.sdk.storage.StorageManager;
import com.apptestershub.sdk.utils.DeviceInfo;

/**
 * Main SDK class for AppTestersHub Android SDK
 * 
 * Usage:
 * <pre>
 * AppTestersHubSDK.initialize(this, "YOUR_APP_ID");
 * </pre>
 */
public class AppTestersHubSDK {
    private static final String TAG = "AppTestersHubSDK";
    private static final String PREFS_NAME = "apptestershub_sdk_prefs";
    
    private static AppTestersHubSDK instance;
    private static boolean initialized = false;
    private static boolean debugMode = false;
    
    private Context applicationContext;
    private String appId;
    private String baseUrl;
    private SDKConfiguration configuration;
    private SDKManager sdkManager;
    private StorageManager storageManager;
    
    /**
     * Initialize the SDK with default configuration
     * 
     * @param context Application context
     * @param appId Your app ID from AppTestersHub
     */
    public static void initialize(@NonNull Context context, @NonNull String appId) {
        initialize(context, appId, (String) null);
    }
    
    /**
     * Initialize the SDK with custom base URL
     * 
     * @param context Application context
     * @param appId Your app ID from AppTestersHub
     * @param baseUrl Custom base URL (optional, defaults to https://app-testers.com)
     */
    public static void initialize(@NonNull Context context, @NonNull String appId, @Nullable String baseUrl) {
        if (initialized) {
            logDebug("SDK already initialized");
            return;
        }
        
        Context appContext = context.getApplicationContext();
        if (appContext == null) {
            appContext = context;
        }
        
        instance = new AppTestersHubSDK(appContext, appId, baseUrl);
        initialized = true;
        logDebug("SDK initialized with App ID: " + appId);
    }
    
    public static void initialize(@NonNull Context context, @NonNull String appId, @NonNull String apiKey, @NonNull String apiSecret) {
        initialize(context, appId, apiKey, apiSecret, null);
    }
    
    public static void initialize(@NonNull Context context, @NonNull String appId, @NonNull String apiKey, @NonNull String apiSecret, @Nullable String baseUrl) {
        SDKConfiguration.Builder builder = SDKConfiguration.builder()
            .setApiKey(apiKey)
            .setApiSecret(apiSecret);
        if (baseUrl != null) {
            builder.setBaseUrl(baseUrl);
        }
        initialize(context, appId, builder.build());
    }
    
    /**
     * Initialize the SDK with custom configuration
     * 
     * @param context Application context
     * @param appId Your app ID from AppTestersHub
     * @param configuration SDK configuration
     */
    public static void initialize(@NonNull Context context, @NonNull String appId, @NonNull SDKConfiguration configuration) {
        if (initialized) {
            logDebug("SDK already initialized");
            return;
        }
        
        Context appContext = context.getApplicationContext();
        if (appContext == null) {
            appContext = context;
        }
        
        instance = new AppTestersHubSDK(appContext, appId, configuration);
        initialized = true;
        logDebug("SDK initialized with App ID: " + appId);
    }
    
    private AppTestersHubSDK(Context context, String appId, String baseUrl) {
        this.applicationContext = context;
        this.appId = appId;
        this.baseUrl = baseUrl != null ? baseUrl : "https://app-testers.com";
        this.configuration = SDKConfiguration.defaultConfig();
        
        initializeComponents();
    }
    
    private AppTestersHubSDK(Context context, String appId, SDKConfiguration configuration) {
        this.applicationContext = context;
        this.appId = appId;
        this.baseUrl = configuration.getBaseUrl() != null ? configuration.getBaseUrl() : "https://app-testers.com";
        this.configuration = configuration;
        
        initializeComponents();
    }
    
    private void initializeComponents() {
        // Initialize Google Analytics
        AnalyticsHelper.initialize(applicationContext);
        
        // Initialize storage manager
        storageManager = new StorageManager(applicationContext);
        
        // Initialize SDK manager
        sdkManager = new SDKManager(applicationContext, appId, baseUrl, configuration, storageManager);
        
        // Track SDK initialization
        AnalyticsHelper.trackSDKInitialized(appId);
        
        // Check SDK status on initialization
        sdkManager.checkSDKStatus(new SDKManager.StatusCallback() {
            @Override
            public void onStatusChecked(boolean enabled) {
                if (enabled) {
                    logDebug("SDK is enabled for this app");
                    // Check if email is stored
                    String storedEmail = storageManager.getTesterEmail();
                    if (storedEmail == null || storedEmail.isEmpty()) {
                        // Email not stored, will show dialog on next activity
                        logDebug("Email not stored, will show dialog on next activity");
                    } else {
                        logDebug("Email stored: " + storedEmail);
                    }
                } else {
                    logDebug("SDK is disabled for this app");
                }
            }
            
            @Override
            public void onError(String error) {
                logError("Error checking SDK status: " + error);
            }
        });
    }
    
    /**
     * Handle activity lifecycle - call this from your activities
     * 
     * @param activity Current activity
     */
    public static void onActivityResumed(@NonNull Activity activity) {
        if (!initialized || instance == null) {
            logError("SDK not initialized. Call AppTestersHubSDK.initialize() first.");
            return;
        }
        
        instance.sdkManager.onActivityResumed(activity);
    }
    
    /**
     * Handle activity lifecycle - call this from your activities
     * 
     * @param activity Current activity
     */
    public static void onActivityPaused(@NonNull Activity activity) {
        if (!initialized || instance == null) {
            return;
        }
        
        instance.sdkManager.onActivityPaused(activity);
    }
    
    /**
     * Enable or disable debug logging
     * 
     * @param enabled True to enable debug logging
     */
    public static void setDebugMode(boolean enabled) {
        debugMode = enabled;
        logDebug("Debug mode " + (enabled ? "enabled" : "disabled"));
    }
    
    /**
     * Check if SDK is initialized
     * 
     * @return True if SDK is initialized
     */
    public static boolean isInitialized() {
        return initialized;
    }
    
    /**
     * Get SDK instance (for internal use)
     */
    static AppTestersHubSDK getInstance() {
        return instance;
    }
    
    /**
     * Get application context
     */
    Context getApplicationContext() {
        return applicationContext;
    }
    
    /**
     * Get app ID
     */
    String getAppId() {
        return appId;
    }
    
    /**
     * Get base URL
     */
    String getBaseUrl() {
        return baseUrl;
    }
    
    /**
     * Get SDK configuration
     */
    SDKConfiguration getConfiguration() {
        return configuration;
    }
    
    /**
     * Log debug message
     */
    public static void logDebug(String message) {
        if (debugMode) {
            Log.d(TAG, message);
        }
    }
    
    /**
     * Log error message
     */
    public static void logError(String message) {
        Log.e(TAG, message);
    }
    
    /**
     * Log info message
     */
    static void logInfo(String message) {
        if (debugMode) {
            Log.i(TAG, message);
        }
    }
}

