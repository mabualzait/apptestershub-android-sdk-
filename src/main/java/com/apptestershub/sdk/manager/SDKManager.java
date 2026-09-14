package com.apptestershub.sdk.manager;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.apptestershub.sdk.AppTestersHubSDK;
import com.apptestershub.sdk.analytics.AnalyticsHelper;
import com.apptestershub.sdk.config.SDKConfiguration;
import com.apptestershub.sdk.network.ApiClient;
import com.apptestershub.sdk.network.ApiClient.ApiCallback;
import com.apptestershub.sdk.models.InitSessionResponse;
import com.apptestershub.sdk.models.SDKConfigResponse;
import com.apptestershub.sdk.models.VerifyEmailResponse;
import com.apptestershub.sdk.screenshot.ScreenshotCapture;
import com.apptestershub.sdk.storage.StorageManager;
import com.apptestershub.sdk.ui.EmailDialog;
import com.apptestershub.sdk.models.HeartbeatResponse;
import com.apptestershub.sdk.models.CompleteSessionResponse;
import com.apptestershub.sdk.ui.TestingProgressView;
import com.apptestershub.sdk.upload.UploadManager;
import com.apptestershub.sdk.utils.DeviceInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * SDK Manager - Main SDK logic handler
 */
public class SDKManager {
    private Context context;
    private String appId;
    private String baseUrl;
    private SDKConfiguration configuration;
    private StorageManager storageManager;
    private ApiClient apiClient;
    private ScreenshotCapture screenshotCapture;
    private UploadManager uploadManager;
    private Handler mainHandler;
    
    private Activity currentActivity;
    private boolean sdkEnabled = false;
    private boolean isProcessing = false;
    private Handler heartbeatHandler;
    private Runnable heartbeatRunnable;
    private Runnable timerRunnable;
    private int heartbeatCount = 0;
    private int elapsedSeconds = 0;
    private TestingProgressView progressView;
    
    public SDKManager(Context context, String appId, String baseUrl, SDKConfiguration configuration, StorageManager storageManager) {
        this.context = context;
        this.appId = appId;
        this.baseUrl = baseUrl;
        this.configuration = configuration;
        this.storageManager = storageManager;
        this.apiClient = new ApiClient(baseUrl, configuration.getApiKey(), configuration.getApiSecret());
        this.screenshotCapture = new ScreenshotCapture(context);
        this.uploadManager = new UploadManager(context, apiClient, storageManager);
        this.mainHandler = new Handler(Looper.getMainLooper());
        
        // Check SDK status on initialization
        checkSDKStatus(null);
    }
    
    /**
     * Check SDK status from backend
     */
    public void checkSDKStatus(StatusCallback callback) {
        // Check if we need to check status (based on interval)
        long lastCheck = storageManager.getLastStatusCheck();
        long now = System.currentTimeMillis();
        long intervalMs = configuration.getStatusCheckIntervalHours() * 60 * 60 * 1000;
        
        if (now - lastCheck < intervalMs && lastCheck > 0) {
            // Use cached status
            boolean cachedEnabled = storageManager.isSDKEnabled();
            sdkEnabled = cachedEnabled;
            AppTestersHubSDK.logDebug("Using cached SDK status: " + (cachedEnabled ? "enabled" : "disabled"));
            if (callback != null) {
                callback.onStatusChecked(cachedEnabled);
            }
            return;
        }
        
        // Check status from backend
        try {
            int appIdInt = Integer.parseInt(appId);
            apiClient.getSDKConfig(appIdInt, new ApiCallback<SDKConfigResponse>() {
                @Override
                public void onSuccess(SDKConfigResponse response) {
                    sdkEnabled = response.isSdkEnabled();
                    storageManager.saveSDKEnabled(sdkEnabled);
                    storageManager.saveLastStatusCheck(System.currentTimeMillis());
                    
                    AppTestersHubSDK.logDebug("SDK status checked: " + (sdkEnabled ? "enabled" : "disabled"));
                    
                    // Track status check
                    AnalyticsHelper.trackSDKStatusChecked(appId, sdkEnabled);
                    
                    if (callback != null) {
                        callback.onStatusChecked(sdkEnabled);
                    }
                }
                
                @Override
                public void onError(String error) {
                    AppTestersHubSDK.logError("Failed to check SDK status: " + error);
                    // Use cached status on error
                    boolean cachedEnabled = storageManager.isSDKEnabled();
                    sdkEnabled = cachedEnabled;
                    if (callback != null) {
                        callback.onError(error);
                    }
                }
            });
        } catch (NumberFormatException e) {
            AppTestersHubSDK.logError("Invalid app ID: " + appId);
            if (callback != null) {
                callback.onError("Invalid app ID");
            }
        }
    }
    
    /**
     * Handle activity resumed
     */
    public void onActivityResumed(Activity activity) {
        this.currentActivity = activity;
        
        // Check SDK status first
        checkSDKStatus(new StatusCallback() {
            @Override
            public void onStatusChecked(boolean enabled) {
                if (!enabled) {
                    AppTestersHubSDK.logDebug("SDK is disabled, skipping all operations");
                    return;
                }
                
                // Check if email is stored
                String storedEmail = storageManager.getTesterEmail();
                if (storedEmail == null || storedEmail.isEmpty()) {
                    // Show email dialog
                    showEmailDialog(activity);
                } else {
                    // Email stored, check if session is initialized
                    int sessionId = storageManager.getSessionId();
                    if (sessionId == -1) {
                        // Initialize session
                        initializeSession(storedEmail);
                    } else {
                        // Session exists, capture screenshot if needed
                        captureScreenshotIfNeeded(activity);
                    }
                }
            }
            
            @Override
            public void onError(String error) {
                AppTestersHubSDK.logError("Error checking SDK status: " + error);
            }
        });
    }
    
    /**
     * Handle activity paused
     */
    public void onActivityPaused(Activity activity) {
        // Clean up if needed
    }
    
    /**
     * Show email dialog
     */
    private void showEmailDialog(Activity activity) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        
        EmailDialog dialog = new EmailDialog(activity, new EmailDialog.EmailDialogCallback() {
            @Override
            public void onEmailEntered(String email) {
                verifyEmail(email);
            }
            
            @Override
            public void onCancelled() {
                AppTestersHubSDK.logDebug("Email dialog cancelled");
            }
        });
        
        dialog.show();
    }
    
    /**
     * Verify email with backend
     */
    private void verifyEmail(String email) {
        if (isProcessing) {
            return;
        }
        
        isProcessing = true;
        String deviceId = DeviceInfo.getDeviceId(context);
        
        try {
            int appIdInt = Integer.parseInt(appId);
            apiClient.verifyEmail(email, appIdInt, deviceId, new ApiCallback<VerifyEmailResponse>() {
                @Override
                public void onSuccess(VerifyEmailResponse response) {
                    isProcessing = false;
                    
                    if (response.isVerified()) {
                        // Save email
                        storageManager.saveTesterEmail(email);
                        storageManager.saveSessionToken(response.getSessionToken());
                        
                        // Track email verification
                        AnalyticsHelper.trackEmailVerified(appId);
                        
                        // Initialize session
                        initializeSession(email);
                    } else {
                        AppTestersHubSDK.logError("Email verification failed: " + response.getMessage());
                        // Show error to user if needed
                    }
                }
                
                @Override
                public void onError(String error) {
                    isProcessing = false;
                    AppTestersHubSDK.logError("Email verification error: " + error);
                }
            });
        } catch (NumberFormatException e) {
            isProcessing = false;
            AppTestersHubSDK.logError("Invalid app ID: " + appId);
        }
    }
    
    /**
     * Initialize testing session
     */
    private void initializeSession(String email) {
        if (isProcessing) {
            return;
        }
        
        isProcessing = true;
        String deviceId = DeviceInfo.getDeviceId(context);
        String sessionToken = storageManager.getSessionToken();
        
        if (sessionToken == null || sessionToken.isEmpty()) {
            // Generate session token if not exists
            sessionToken = generateSessionToken();
            storageManager.saveSessionToken(sessionToken);
        }
        
        Map<String, Object> deviceInfo = DeviceInfo.getDeviceInfo(context);
        
        try {
            int appIdInt = Integer.parseInt(appId);
            apiClient.initSession(email, appIdInt, deviceId, sessionToken, deviceInfo, new ApiCallback<InitSessionResponse>() {
                @Override
                public void onSuccess(InitSessionResponse response) {
                    isProcessing = false;
                    
                    if (response.isSuccess()) {
                        // Save session ID
                        storageManager.saveSessionId(response.getSessionId());
                        
                        AppTestersHubSDK.logDebug("Session initialized: " + response.getSessionId());
                        
                        // Track session initialization
                        AnalyticsHelper.trackSessionInitialized(appId, response.getSessionId());
                        
                        // Capture screenshot if needed
                        if (currentActivity != null) {
                            captureScreenshotIfNeeded(currentActivity);
                        }
                        startHeartbeat(response.getSessionId());
                    } else {
                        AppTestersHubSDK.logError("Session initialization failed: " + response.getMessage());
                    }
                }
                
                @Override
                public void onError(String error) {
                    isProcessing = false;
                    AppTestersHubSDK.logError("Session initialization error: " + error);
                }
            });
        } catch (NumberFormatException e) {
            isProcessing = false;
            AppTestersHubSDK.logError("Invalid app ID: " + appId);
        }
    }
    
    /**
     * Capture screenshot if needed
     */
    private void captureScreenshotIfNeeded(Activity activity) {
        if (activity == null || activity.isFinishing() || isProcessing) {
            return;
        }
        
        // Check if screenshot already captured today
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String lastCaptureDate = storageManager.getLastCaptureDate();
        
        if (today.equals(lastCaptureDate)) {
            AppTestersHubSDK.logDebug("Screenshot already captured today");
            return;
        }
        
        // Capture screenshot
        isProcessing = true;
        screenshotCapture.captureScreenshot(activity, new ScreenshotCapture.ScreenshotCallback() {
            @Override
            public void onScreenshotCaptured(String imagePath) {
                isProcessing = false;
                
                if (imagePath != null) {
                    // Track screenshot capture
                    int sessionId = storageManager.getSessionId();
                    if (sessionId != -1) {
                        AnalyticsHelper.trackScreenshotCaptured(appId, sessionId);
                    }
                    
                    // Upload screenshot
                    uploadScreenshot(imagePath, today);
                } else {
                    AppTestersHubSDK.logError("Failed to capture screenshot");
                }
            }
            
            @Override
            public void onError(String error) {
                isProcessing = false;
                AppTestersHubSDK.logError("Screenshot capture error: " + error);
            }
        });
    }
    
    /**
     * Upload screenshot
     */
    private void uploadScreenshot(String imagePath, String date) {
        int sessionId = storageManager.getSessionId();
        if (sessionId == -1) {
            AppTestersHubSDK.logError("Session ID not found");
            return;
        }
        
        Map<String, Object> deviceInfo = DeviceInfo.getDeviceInfo(context);
        uploadManager.uploadScreenshot(sessionId, imagePath, date, deviceInfo, new UploadManager.UploadCallback() {
            @Override
            public void onSuccess() {
                AppTestersHubSDK.logDebug("Screenshot uploaded successfully");
                storageManager.saveLastCaptureDate(date);
                
                // Track successful upload
                AnalyticsHelper.trackScreenshotUploaded(appId, sessionId, true);
            }
            
            @Override
            public void onError(String error) {
                AppTestersHubSDK.logError("Screenshot upload error: " + error);
                
                // Track failed upload
                AnalyticsHelper.trackScreenshotUploaded(appId, sessionId, false);
            }
        });
    }
    
    /**
     * Generate session token
     */
    private String generateSessionToken() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * Status callback interface
     */

    private void startHeartbeat(int sessionId) {
        if (currentActivity == null) return;
        
        mainHandler.post(() -> {
            if (progressView == null) {
                progressView = new TestingProgressView(currentActivity);
            }
            progressView.show();
            elapsedSeconds = 0;
            heartbeatCount = 0;
        });

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                elapsedSeconds++;
                mainHandler.post(() -> {
                    if (progressView != null) {
                        progressView.updateTimer(elapsedSeconds);
                    }
                });
                if (elapsedSeconds < 60) {
                    mainHandler.postDelayed(this, 1000);
                }
            }
        };
        mainHandler.postDelayed(timerRunnable, 1000);

        heartbeatHandler = new Handler(Looper.getMainLooper());
        heartbeatRunnable = new Runnable() {
            @Override
            public void run() {
                long timestamp = System.currentTimeMillis();
                apiClient.sendHeartbeat(sessionId, timestamp, new ApiCallback<HeartbeatResponse>() {
                    @Override
                    public void onSuccess(HeartbeatResponse response) {
                        heartbeatCount++;
                        if (elapsedSeconds >= 60 || heartbeatCount >= 4) {
                            completeSession(sessionId);
                        } else {
                            heartbeatHandler.postDelayed(heartbeatRunnable, 15000);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        AppTestersHubSDK.logError("Heartbeat error: " + error);
                        // Retry later
                        if (elapsedSeconds < 60) {
                            heartbeatHandler.postDelayed(heartbeatRunnable, 15000);
                        }
                    }
                });
            }
        };
        heartbeatHandler.postDelayed(heartbeatRunnable, 15000);
    }

    private void completeSession(int sessionId) {
        if (heartbeatHandler != null && heartbeatRunnable != null) {
            heartbeatHandler.removeCallbacks(heartbeatRunnable);
        }
        apiClient.completeSession(sessionId, new ApiCallback<CompleteSessionResponse>() {
            @Override
            public void onSuccess(CompleteSessionResponse response) {
                mainHandler.post(() -> {
                    if (progressView != null) {
                        progressView.showCompletion();
                    }
                });
                AppTestersHubSDK.logDebug("Session completed successfully");
            }

            @Override
            public void onError(String error) {
                AppTestersHubSDK.logError("Session completion error: " + error);
            }
        });
    }
    public interface StatusCallback {
        void onStatusChecked(boolean enabled);
        void onError(String error);
    }
}

