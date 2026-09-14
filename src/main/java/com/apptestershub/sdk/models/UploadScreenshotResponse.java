package com.apptestershub.sdk.models;

/**
 * Response model for screenshot upload
 */
public class UploadScreenshotResponse {
    private boolean success;
    private int screenshot_id;
    private boolean verified;
    private String message;
    
    public boolean isSuccess() {
        return success;
    }
    
    public int getScreenshotId() {
        return screenshot_id;
    }
    
    public boolean isVerified() {
        return verified;
    }
    
    public String getMessage() {
        return message;
    }
}

