package com.apptestershub.sdk.analytics;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.apptestershub.sdk.AppTestersHubSDK;
import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Google Analytics helper for SDK tracking (using Firebase Analytics for GA4)
 */
public class AnalyticsHelper {
    private static final String MEASUREMENT_ID = "G-48LY31BY7Q";
    private static FirebaseAnalytics firebaseAnalytics;
    private static boolean initialized = false;
    
    /**
     * Initialize Google Analytics (Firebase Analytics)
     */
    public static void initialize(Context context) {
        if (initialized) {
            return;
        }
        
        try {
            firebaseAnalytics = FirebaseAnalytics.getInstance(context);
            initialized = true;
            AppTestersHubSDK.logDebug("Google Analytics (Firebase) initialized");
        } catch (Exception e) {
            AppTestersHubSDK.logError("Failed to initialize Google Analytics: " + e.getMessage());
            // Firebase Analytics might not be available if Firebase is not configured
            // This is okay, we'll just skip tracking
        }
    }
    
    /**
     * Track a screen view
     */
    public static void trackScreenView(String screenName) {
        if (firebaseAnalytics == null) {
            return;
        }
        
        try {
            Bundle params = new Bundle();
            params.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params);
            AppTestersHubSDK.logDebug("Tracked screen view: " + screenName);
        } catch (Exception e) {
            AppTestersHubSDK.logError("Failed to track screen view: " + e.getMessage());
        }
    }
    
    /**
     * Track an event
     */
    public static void trackEvent(String eventName, Bundle params) {
        if (firebaseAnalytics == null) {
            return;
        }
        
        try {
            if (params == null) {
                params = new Bundle();
            }
            firebaseAnalytics.logEvent(eventName, params);
            AppTestersHubSDK.logDebug("Tracked event: " + eventName);
        } catch (Exception e) {
            AppTestersHubSDK.logError("Failed to track event: " + e.getMessage());
        }
    }
    
    /**
     * Track an event with category, action, and label (for compatibility)
     */
    public static void trackEvent(String category, String action, String label) {
        if (firebaseAnalytics == null) {
            return;
        }
        
        try {
            Bundle params = new Bundle();
            params.putString("category", category);
            params.putString("action", action);
            params.putString("label", label);
            firebaseAnalytics.logEvent("custom_event", params);
            AppTestersHubSDK.logDebug("Tracked event: " + category + "/" + action + "/" + label);
        } catch (Exception e) {
            AppTestersHubSDK.logError("Failed to track event: " + e.getMessage());
        }
    }
    
    /**
     * Track SDK initialization
     */
    public static void trackSDKInitialized(String appId) {
        trackEvent("sdk", "initialized", "app_id_" + appId);
    }
    
    /**
     * Track email verification
     */
    public static void trackEmailVerified(String appId) {
        trackEvent("sdk", "email_verified", "app_id_" + appId);
    }
    
    /**
     * Track session initialized
     */
    public static void trackSessionInitialized(String appId, int sessionId) {
        trackEvent("sdk", "session_initialized", "app_id_" + appId + "_session_" + sessionId);
    }
    
    /**
     * Track screenshot captured
     */
    public static void trackScreenshotCaptured(String appId, int sessionId) {
        trackEvent("sdk", "screenshot_captured", "app_id_" + appId + "_session_" + sessionId);
    }
    
    /**
     * Track screenshot uploaded
     */
    public static void trackScreenshotUploaded(String appId, int sessionId, boolean success) {
        trackEvent("sdk", success ? "screenshot_uploaded" : "screenshot_upload_failed", 
            "app_id_" + appId + "_session_" + sessionId);
    }
    
    /**
     * Track SDK status checked
     */
    public static void trackSDKStatusChecked(String appId, boolean enabled) {
        trackEvent("sdk", "status_checked", "app_id_" + appId + "_enabled_" + enabled);
    }
    
    /**
     * Track SDK error
     */
    public static void trackSDKError(String appId, String error) {
        trackEvent("sdk", "error", "app_id_" + appId + "_error_" + error);
    }
    
    /**
     * Get Firebase Analytics instance
     */
    public static FirebaseAnalytics getFirebaseAnalytics() {
        return firebaseAnalytics;
    }
    
    /**
     * Check if analytics is initialized
     */
    public static boolean isInitialized() {
        return initialized && firebaseAnalytics != null;
    }
}

