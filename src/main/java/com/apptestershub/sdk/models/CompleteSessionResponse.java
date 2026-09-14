package com.apptestershub.sdk.models;

public class CompleteSessionResponse {
    private boolean success;
    private boolean sessionComplete;
    private int coinsEarned;
    private int verifiedDays;
    private boolean testComplete;
    private String message;
    
    public boolean isSuccess() { return success; }
    public boolean isSessionComplete() { return sessionComplete; }
    public int getCoinsEarned() { return coinsEarned; }
    public int getVerifiedDays() { return verifiedDays; }
    public boolean isTestComplete() { return testComplete; }
    public String getMessage() { return message; }
}
