package com.apptestershub.sdk.upload;

import android.content.Context;
import android.content.SharedPreferences;

public class RetryQueue {
    private SharedPreferences prefs;

    public RetryQueue(Context context) {
        prefs = context.getSharedPreferences("ath_retry_queue", Context.MODE_PRIVATE);
    }
    
    public void addRetryItem(String key, String data) {
        prefs.edit().putString(key, data).apply();
    }
    
    public String getRetryItem(String key) {
        return prefs.getString(key, null);
    }
    
    public void removeRetryItem(String key) {
        prefs.edit().remove(key).apply();
    }
}
