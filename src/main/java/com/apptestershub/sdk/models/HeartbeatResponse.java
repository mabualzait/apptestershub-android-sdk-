package com.apptestershub.sdk.models;

public class HeartbeatResponse {
    private boolean success;
    private int heartbeatCount;
    private int durationSeconds;
    private boolean sessionComplete;
    
    public boolean isSuccess() { return success; }
    public int getHeartbeatCount() { return heartbeatCount; }
    public int getDurationSeconds() { return durationSeconds; }
    public boolean isSessionComplete() { return sessionComplete; }
}
