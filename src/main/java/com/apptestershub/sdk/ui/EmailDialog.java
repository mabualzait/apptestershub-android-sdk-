package com.apptestershub.sdk.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.apptestershub.sdk.AppTestersHubSDK;

public class EmailDialog {
    private EmailDialogCallback callback;
    private Activity activity;
    private AlertDialog dialog;
    
    public EmailDialog(Activity activity, EmailDialogCallback callback) {
        this.activity = activity;
        this.callback = callback;
    }
    
    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        
        int layoutId = activity.getResources().getIdentifier("apptestershub_dialog_email", "layout", activity.getPackageName());
        if (layoutId == 0) {
            return;
        }
        
        View view = LayoutInflater.from(activity).inflate(layoutId, null);
        builder.setView(view);
        
        int inputId = activity.getResources().getIdentifier("ath_email_input", "id", activity.getPackageName());
        int layoutInputId = activity.getResources().getIdentifier("ath_email_layout", "id", activity.getPackageName());
        int btnStartId = activity.getResources().getIdentifier("ath_btn_start", "id", activity.getPackageName());
        int btnCancelId = activity.getResources().getIdentifier("ath_btn_cancel", "id", activity.getPackageName());
        int overlayId = activity.getResources().getIdentifier("ath_loading_overlay", "id", activity.getPackageName());
        
        TextInputEditText emailInput = view.findViewById(inputId);
        TextInputLayout emailLayout = view.findViewById(layoutInputId);
        Button btnStart = view.findViewById(btnStartId);
        Button btnCancel = view.findViewById(btnCancelId);
        View overlay = view.findViewById(overlayId);
        
        btnStart.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            if (isValidEmail(email)) {
                emailLayout.setErrorEnabled(false);
                overlay.setVisibility(View.VISIBLE);
                if (callback != null) {
                    callback.onEmailEntered(email);
                }
            } else {
                int errorStrId = activity.getResources().getIdentifier("ath_invalid_email", "string", activity.getPackageName());
                emailLayout.setError(activity.getString(errorStrId));
            }
        });
        
        btnCancel.setOnClickListener(v -> {
            dismiss();
            if (callback != null) {
                callback.onCancelled();
            }
        });
        
        builder.setCancelable(false);
        dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();
    }
    
    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
    
    public void showError(String error) {
        if (dialog != null && dialog.isShowing()) {
            int overlayId = activity.getResources().getIdentifier("ath_loading_overlay", "id", activity.getPackageName());
            int layoutInputId = activity.getResources().getIdentifier("ath_email_layout", "id", activity.getPackageName());
            View overlay = dialog.findViewById(overlayId);
            TextInputLayout emailLayout = dialog.findViewById(layoutInputId);
            if (overlay != null) overlay.setVisibility(View.GONE);
            if (emailLayout != null) {
                emailLayout.setError(error);
            }
        }
    }
    
    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    
    public interface EmailDialogCallback {
        void onEmailEntered(String email);
        void onCancelled();
    }
}
