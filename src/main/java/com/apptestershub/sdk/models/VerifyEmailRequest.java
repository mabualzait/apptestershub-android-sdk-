package com.apptestershub.sdk.models;

/**
 * Request model for email verification
 */
public class VerifyEmailRequest {
    private String email;
    private int app_id;
    private String device_id;
    
    public VerifyEmailRequest(String email, int appId, String deviceId) {
        this.email = email;
        this.app_id = appId;
        this.device_id = deviceId;
    }
    
    public String getEmail() {
        return email;
    }
    
    public int getAppId() {
        return app_id;
    }
    
    public String getDeviceId() {
        return device_id;
    }
}

