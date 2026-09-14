package com.apptestershub.sdk.upload;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.apptestershub.sdk.AppTestersHubSDK;
import com.apptestershub.sdk.network.ApiClient;
import com.apptestershub.sdk.network.ApiClient.ApiCallback;
import com.apptestershub.sdk.models.UploadScreenshotResponse;
import com.apptestershub.sdk.storage.StorageManager;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Upload manager for screenshot uploads
 */
public class UploadManager {
    private Context context;
    private ApiClient apiClient;
    private StorageManager storageManager;
    private ExecutorService executorService;
    private Handler mainHandler;
    
    public UploadManager(Context context, ApiClient apiClient, StorageManager storageManager) {
        this.context = context;
        this.apiClient = apiClient;
        this.storageManager = storageManager;
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }
    
    /**
     * Upload screenshot
     */
    public void uploadScreenshot(int sessionId, String imagePath, String date, Map<String, Object> deviceInfo, UploadCallback callback) {
        executorService.execute(() -> {
            try {
                File imageFile = new File(imagePath);
                if (!imageFile.exists()) {
                    mainHandler.post(() -> callback.onError("Image file not found"));
                    return;
                }
                
                // Compress image if needed
                File compressedFile = compressImageIfNeeded(imageFile);
                File fileToUpload = compressedFile != null ? compressedFile : imageFile;
                
                // Upload on background thread
                apiClient.uploadScreenshot(sessionId, fileToUpload, date, deviceInfo, new ApiCallback<UploadScreenshotResponse>() {
                    @Override
                    public void onSuccess(UploadScreenshotResponse response) {
                        mainHandler.post(() -> {
                            if (response.isSuccess()) {
                                AppTestersHubSDK.logDebug("Screenshot uploaded: " + response.getScreenshotId());
                                callback.onSuccess();
                            } else {
                                callback.onError(response.getMessage());
                            }
                        });
                    }
                    
                    @Override
                    public void onError(String error) {
                        mainHandler.post(() -> callback.onError(error));
                    }
                });
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }
    
    /**
     * Compress image if needed (max 5MB)
     */
    private File compressImageIfNeeded(File imageFile) {
        long maxSize = 5 * 1024 * 1024; // 5MB
        
        if (imageFile.length() <= maxSize) {
            return null; // No compression needed
        }
        
        // TODO: Implement image compression
        // For now, return null (use original file)
        AppTestersHubSDK.logDebug("Image size: " + imageFile.length() + " bytes, compression may be needed");
        return null;
    }
    
    /**
     * Upload callback interface
     */
    public interface UploadCallback {
        void onSuccess();
        void onError(String error);
    }
}

