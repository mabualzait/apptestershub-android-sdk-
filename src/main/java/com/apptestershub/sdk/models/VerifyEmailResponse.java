package com.apptestershub.sdk.models;

/**
 * Response model for email verification
 */
public class VerifyEmailResponse {
    private boolean verified;
    private int test_id;
    private String session_token;
    private String message;
    
    public boolean isVerified() {
        return verified;
    }
    
    public int getTestId() {
        return test_id;
    }
    
    public String getSessionToken() {
        return session_token;
    }
    
    public String getMessage() {
        return message;
    }
}

