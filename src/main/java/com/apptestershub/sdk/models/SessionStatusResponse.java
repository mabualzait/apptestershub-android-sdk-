package com.apptestershub.sdk.models;

/**
 * Response model for session status
 */
public class SessionStatusResponse {
    private int session_id;
    private int test_id;
    private int verified_days;
    private String last_screenshot_date;
    private String status;
    
    public int getSessionId() {
        return session_id;
    }
    
    public int getTestId() {
        return test_id;
    }
    
    public int getVerifiedDays() {
        return verified_days;
    }
    
    public String getLastScreenshotDate() {
        return last_screenshot_date;
    }
    
    public String getStatus() {
        return status;
    }
}

