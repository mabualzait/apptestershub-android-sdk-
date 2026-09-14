package com.apptestershub.sdk.utils;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import java.util.HashMap;
import java.util.Map;

/**
 * Device information utility class
 */
public class DeviceInfo {
    
    /**
     * Get device ID (Android ID)
     */
    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
    
    /**
     * Get device information as map
     */
    public static Map<String, Object> getDeviceInfo(Context context) {
        Map<String, Object> info = new HashMap<>();
        info.put("model", Build.MODEL);
        info.put("manufacturer", Build.MANUFACTURER);
        info.put("brand", Build.BRAND);
        info.put("device", Build.DEVICE);
        info.put("os_version", Build.VERSION.RELEASE);
        info.put("sdk_version", Build.VERSION.SDK_INT);
        info.put("device_id", getDeviceId(context));
        return info;
    }
    
    /**
     * Get device model
     */
    public static String getModel() {
        return Build.MODEL;
    }
    
    /**
     * Get Android version
     */
    public static String getAndroidVersion() {
        return Build.VERSION.RELEASE;
    }
    
    /**
     * Get SDK version
     */
    public static int getSDKVersion() {
        return Build.VERSION.SDK_INT;
    }
}

