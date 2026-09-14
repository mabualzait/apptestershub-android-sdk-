package com.apptestershub.sdk.models;

/**
 * Response model for session initialization
 */
public class InitSessionResponse {
    private boolean success;
    private int session_id;
    private int test_id;
    private String status;
    private String message;
    
    public boolean isSuccess() {
        return success;
    }
    
    public int getSessionId() {
        return session_id;
    }
    
    public int getTestId() {
        return test_id;
    }
    
    public String getStatus() {
        return status;
    }
    
    public String getMessage() {
        return message;
    }
}

