package com.apptestershub.sdk.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.apptestershub.sdk.AppTestersHubSDK;

/**
 * Activity lifecycle helper for automatic SDK integration
 * 
 * Usage:
 * <pre>
 * public class MyApplication extends Application {
 *     @Override
 *     public void onCreate() {
 *         super.onCreate();
 *         AppTestersHubSDK.initialize(this, "YOUR_APP_ID");
 *         registerActivityLifecycleCallbacks(new ActivityLifecycleHelper());
 *     }
 * }
 * </pre>
 */
public class ActivityLifecycleHelper implements Application.ActivityLifecycleCallbacks {
    
    @Override
    public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
        // No action needed
    }
    
    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        // No action needed
    }
    
    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        AppTestersHubSDK.onActivityResumed(activity);
    }
    
    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        AppTestersHubSDK.onActivityPaused(activity);
    }
    
    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        // No action needed
    }
    
    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        // No action needed
    }
    
    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        // No action needed
    }
}

