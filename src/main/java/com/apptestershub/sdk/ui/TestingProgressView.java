package com.apptestershub.sdk.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TestingProgressView {
    private Context context;
    private WindowManager windowManager;
    private View progressView;
    private TextView timerText;
    private TextView dayText;
    private ProgressBar progressBar;
    
    public TestingProgressView(Context context) {
        this.context = context;
        this.windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }
    
    public void show() {
        if (progressView != null) return;
        
        int layoutId = context.getResources().getIdentifier("apptestershub_testing_progress", "layout", context.getPackageName());
        if (layoutId == 0) return;
        
        progressView = LayoutInflater.from(context).inflate(layoutId, null);
        
        int timerId = context.getResources().getIdentifier("ath_progress_timer", "id", context.getPackageName());
        int dayId = context.getResources().getIdentifier("ath_progress_day", "id", context.getPackageName());
        int progressId = context.getResources().getIdentifier("ath_progress_circular", "id", context.getPackageName());
        
        timerText = progressView.findViewById(timerId);
        dayText = progressView.findViewById(dayId);
        progressBar = progressView.findViewById(progressId);
        
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_PANEL,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.BOTTOM | Gravity.END;
        params.x = 32;
        params.y = 32;
        
        windowManager.addView(progressView, params);
    }
    
    public void updateTimer(int seconds) {
        if (timerText != null) {
            String time = String.format("Recording: %d:%02d", seconds / 60, seconds % 60);
            timerText.setText(time);
        }
    }
    
    public void showCompletion() {
        if (timerText != null) {
            timerText.setText("Session complete! ✓");
        }
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        
        new Handler(Looper.getMainLooper()).postDelayed(this::hide, 3000);
    }
    
    public void hide() {
        if (progressView != null && progressView.getParent() != null) {
            windowManager.removeView(progressView);
            progressView = null;
        }
    }
}
