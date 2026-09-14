package com.apptestershub.sdk.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.apptestershub.sdk.AppTestersHubSDK;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Storage manager for encrypted local storage
 */
public class StorageManager {
    private static final String PREFS_NAME = "apptestershub_sdk_prefs";
    private static final String KEY_TESTER_EMAIL = "tester_email";
    private static final String KEY_SESSION_TOKEN = "session_token";
    private static final String KEY_SESSION_ID = "session_id";
    private static final String KEY_LAST_STATUS_CHECK = "last_status_check";
    private static final String KEY_SDK_ENABLED = "sdk_enabled";
    private static final String KEY_LAST_CAPTURE_DATE = "last_capture_date";
    
    private SharedPreferences encryptedPrefs;
    private SharedPreferences regularPrefs;
    
    public StorageManager(Context context) {
        try {
            // Create master key for encryption
            MasterKey masterKey = new MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();
            
            // Create encrypted shared preferences
            encryptedPrefs = EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            AppTestersHubSDK.logError("Failed to create encrypted preferences: " + e.getMessage());
            // Fallback to regular preferences
            regularPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }
    }
    
    /**
     * Get tester email
     */
    public String getTesterEmail() {
        if (encryptedPrefs != null) {
            return encryptedPrefs.getString(KEY_TESTER_EMAIL, null);
        } else if (regularPrefs != null) {
            return regularPrefs.getString(KEY_TESTER_EMAIL, null);
        }
        return null;
    }
    
    /**
     * Save tester email
     */
    public void saveTesterEmail(String email) {
        if (encryptedPrefs != null) {
            encryptedPrefs.edit().putString(KEY_TESTER_EMAIL, email).apply();
        } else if (regularPrefs != null) {
            regularPrefs.edit().putString(KEY_TESTER_EMAIL, email).apply();
        }
    }
    
    /**
     * Get session token
     */
    public String getSessionToken() {
        if (encryptedPrefs != null) {
            return encryptedPrefs.getString(KEY_SESSION_TOKEN, null);
        } else if (regularPrefs != null) {
            return regularPrefs.getString(KEY_SESSION_TOKEN, null);
        }
        return null;
    }
    
    /**
     * Save session token
     */
    public void saveSessionToken(String token) {
        if (encryptedPrefs != null) {
            encryptedPrefs.edit().putString(KEY_SESSION_TOKEN, token).apply();
        } else if (regularPrefs != null) {
            regularPrefs.edit().putString(KEY_SESSION_TOKEN, token).apply();
        }
    }
    
    /**
     * Get session ID
     */
    public int getSessionId() {
        if (encryptedPrefs != null) {
            return encryptedPrefs.getInt(KEY_SESSION_ID, -1);
        } else if (regularPrefs != null) {
            return regularPrefs.getInt(KEY_SESSION_ID, -1);
        }
        return -1;
    }
    
    /**
     * Save session ID
     */
    public void saveSessionId(int sessionId) {
        if (encryptedPrefs != null) {
            encryptedPrefs.edit().putInt(KEY_SESSION_ID, sessionId).apply();
        } else if (regularPrefs != null) {
            regularPrefs.edit().putInt(KEY_SESSION_ID, sessionId).apply();
        }
    }
    
    /**
     * Get last status check time
     */
    public long getLastStatusCheck() {
        if (encryptedPrefs != null) {
            return encryptedPrefs.getLong(KEY_LAST_STATUS_CHECK, 0);
        } else if (regularPrefs != null) {
            return regularPrefs.getLong(KEY_LAST_STATUS_CHECK, 0);
        }
        return 0;
    }
    
    /**
     * Save last status check time
     */
    public void saveLastStatusCheck(long timestamp) {
        if (encryptedPrefs != null) {
            encryptedPrefs.edit().putLong(KEY_LAST_STATUS_CHECK, timestamp).apply();
        } else if (regularPrefs != null) {
            regularPrefs.edit().putLong(KEY_LAST_STATUS_CHECK, timestamp).apply();
        }
    }
    
    /**
     * Get SDK enabled status (cached)
     */
    public boolean isSDKEnabled() {
        if (encryptedPrefs != null) {
            return encryptedPrefs.getBoolean(KEY_SDK_ENABLED, false);
        } else if (regularPrefs != null) {
            return regularPrefs.getBoolean(KEY_SDK_ENABLED, false);
        }
        return false;
    }
    
    /**
     * Save SDK enabled status (cached)
     */
    public void saveSDKEnabled(boolean enabled) {
        if (encryptedPrefs != null) {
            encryptedPrefs.edit().putBoolean(KEY_SDK_ENABLED, enabled).apply();
        } else if (regularPrefs != null) {
            regularPrefs.edit().putBoolean(KEY_SDK_ENABLED, enabled).apply();
        }
    }
    
    /**
     * Get last capture date
     */
    public String getLastCaptureDate() {
        if (encryptedPrefs != null) {
            return encryptedPrefs.getString(KEY_LAST_CAPTURE_DATE, null);
        } else if (regularPrefs != null) {
            return regularPrefs.getString(KEY_LAST_CAPTURE_DATE, null);
        }
        return null;
    }
    
    /**
     * Save last capture date
     */
    public void saveLastCaptureDate(String date) {
        if (encryptedPrefs != null) {
            encryptedPrefs.edit().putString(KEY_LAST_CAPTURE_DATE, date).apply();
        } else if (regularPrefs != null) {
            regularPrefs.edit().putString(KEY_LAST_CAPTURE_DATE, date).apply();
        }
    }
    
    /**
     * Clear all stored data
     */
    public void clear() {
        if (encryptedPrefs != null) {
            encryptedPrefs.edit().clear().apply();
        } else if (regularPrefs != null) {
            regularPrefs.edit().clear().apply();
        }
    }
}

