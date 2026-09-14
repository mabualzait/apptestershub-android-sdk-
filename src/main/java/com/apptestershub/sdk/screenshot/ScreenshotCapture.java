package com.apptestershub.sdk.screenshot;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.view.View;

import androidx.annotation.NonNull;

import com.apptestershub.sdk.AppTestersHubSDK;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Screenshot capture utility
 */
public class ScreenshotCapture {
    private static final String TAG = "ScreenshotCapture";
    private static final String SCREENSHOT_DIR = "apptestershub_screenshots";
    
    private Context context;
    
    public ScreenshotCapture(Context context) {
        this.context = context;
    }
    
    /**
     * Capture screenshot of activity
     */
    public void captureScreenshot(@NonNull Activity activity, ScreenshotCallback callback) {
        try {
            // Get root view
            View rootView = activity.getWindow().getDecorView().getRootView();
            
            // Create bitmap
            Bitmap bitmap = Bitmap.createBitmap(rootView.getWidth(), rootView.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            rootView.draw(canvas);
            
            // Save bitmap to file
            String imagePath = saveBitmap(bitmap);
            
            if (imagePath != null) {
                AppTestersHubSDK.logDebug("Screenshot captured: " + imagePath);
                callback.onScreenshotCaptured(imagePath);
            } else {
                callback.onError("Failed to save screenshot");
            }
        } catch (Exception e) {
            AppTestersHubSDK.logError("Screenshot capture error: " + e.getMessage());
            callback.onError(e.getMessage());
        }
    }
    
    /**
     * Save bitmap to file
     */
    private String saveBitmap(Bitmap bitmap) {
        FileOutputStream fos = null;
        try {
            // Create screenshot directory
            File screenshotDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), SCREENSHOT_DIR);
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }
            
            // Create file with timestamp
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String filename = "screenshot_" + timestamp + ".jpg";
            File imageFile = new File(screenshotDir, filename);
            
            // Compress and save
            fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos);
            fos.flush();
            
            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            AppTestersHubSDK.logError("Failed to save bitmap: " + e.getMessage());
            return null;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }
    
    /**
     * Screenshot callback interface
     */
    public interface ScreenshotCallback {
        void onScreenshotCaptured(String imagePath);
        void onError(String error);
    }
}

