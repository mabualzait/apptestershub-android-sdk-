package com.apptestershub.sdk.models;

import java.util.Map;

/**
 * Request model for session initialization
 */
public class InitSessionRequest {
    private String email;
    private int app_id;
    private String device_id;
    private String session_token;
    private Map<String, Object> device_info;
    
    public InitSessionRequest(String email, int appId, String deviceId, String sessionToken, Map<String, Object> deviceInfo) {
        this.email = email;
        this.app_id = appId;
        this.device_id = deviceId;
        this.session_token = sessionToken;
        this.device_info = deviceInfo;
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
    
    public String getSessionToken() {
        return session_token;
    }
    
    public Map<String, Object> getDeviceInfo() {
        return device_info;
    }
}

