package com.apptestershub.sdk.models;

import java.util.Map;

/**
 * Response model for SDK configuration
 */
public class SDKConfigResponse {
    private int app_id;
    private boolean sdk_enabled;
    private String testing_url;
    private Map<String, Object> requirements;
    
    public int getAppId() {
        return app_id;
    }
    
    public boolean isSdkEnabled() {
        return sdk_enabled;
    }
    
    public String getTestingUrl() {
        return testing_url;
    }
    
    public Map<String, Object> getRequirements() {
        return requirements;
    }
}

