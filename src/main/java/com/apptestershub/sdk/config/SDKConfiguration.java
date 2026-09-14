package com.apptestershub.sdk.config;

/**
 * SDK Configuration class
 */
public class SDKConfiguration {
    private String baseUrl;
    private boolean debugMode;
    private int uploadRetryCount;
    private int statusCheckIntervalHours;
    private String apiKey;
    private String apiSecret;
    
    private SDKConfiguration(Builder builder) {
        this.baseUrl = builder.baseUrl;
        this.debugMode = builder.debugMode;
        this.uploadRetryCount = builder.uploadRetryCount;
        this.statusCheckIntervalHours = builder.statusCheckIntervalHours;
        this.apiKey = builder.apiKey;
        this.apiSecret = builder.apiSecret;
    }
    
    /**
     * Create default configuration
     */
    public static SDKConfiguration defaultConfig() {
        return new Builder()
            .setBaseUrl("https://apptestershub.com")
            .setDebugMode(false)
            .setUploadRetryCount(3)
            .setStatusCheckIntervalHours(24)
            .build();
    }
    
    /**
     * Create configuration builder
     */
    public static Builder builder() {
        return new Builder();
    }
    
    public String getBaseUrl() {
        return baseUrl;
    }
    
    public boolean isDebugMode() {
        return debugMode;
    }
    
    public int getUploadRetryCount() {
        return uploadRetryCount;
    }
    
    public int getStatusCheckIntervalHours() {
        return statusCheckIntervalHours;
    }
    
    public String getApiKey() {
        return apiKey;
    }
    
    public String getApiSecret() {
        return apiSecret;
    }
    
    public static class Builder {
        private String baseUrl = "https://apptestershub.com";
        private boolean debugMode = false;
        private int uploadRetryCount = 3;
        private int statusCheckIntervalHours = 24;
        private String apiKey;
        private String apiSecret;
        
        public Builder setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }
        
        public Builder setDebugMode(boolean debugMode) {
            this.debugMode = debugMode;
            return this;
        }
        
        public Builder setUploadRetryCount(int uploadRetryCount) {
            this.uploadRetryCount = uploadRetryCount;
            return this;
        }
        
        public Builder setStatusCheckIntervalHours(int statusCheckIntervalHours) {
            this.statusCheckIntervalHours = statusCheckIntervalHours;
            return this;
        }
        
        public Builder setApiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }
        
        public Builder setApiSecret(String apiSecret) {
            this.apiSecret = apiSecret;
            return this;
        }
        
        public SDKConfiguration build() {
            return new SDKConfiguration(this);
        }
    }
}

