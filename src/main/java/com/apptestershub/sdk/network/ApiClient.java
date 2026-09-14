package com.apptestershub.sdk.network;

import android.content.Context;
import android.util.Log;

import com.apptestershub.sdk.AppTestersHubSDK;
import com.apptestershub.sdk.models.VerifyEmailRequest;
import com.apptestershub.sdk.models.VerifyEmailResponse;
import com.apptestershub.sdk.models.InitSessionRequest;
import com.apptestershub.sdk.models.InitSessionResponse;
import com.apptestershub.sdk.models.UploadScreenshotResponse;
import com.apptestershub.sdk.models.SessionStatusResponse;
import com.apptestershub.sdk.models.SDKConfigResponse;
import com.apptestershub.sdk.utils.DeviceInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.apptestershub.sdk.models.HeartbeatResponse;
import com.apptestershub.sdk.models.CompleteSessionResponse;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * API client for AppTestersHub SDK
 */
public class ApiClient {
    private static final String TAG = "ApiClient";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final MediaType IMAGE = MediaType.get("image/*");
    
    private String baseUrl;
    private String apiKey;
    private String apiSecret;
    private OkHttpClient httpClient;
    private Gson gson;
    
    public ApiClient(String baseUrl, String apiKey, String apiSecret) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
        this.gson = new GsonBuilder().create();
    }
    
    /**
     * Verify tester email
     */
    public void verifyEmail(String email, int appId, String deviceId, ApiCallback<VerifyEmailResponse> callback) {
        VerifyEmailRequest request = new VerifyEmailRequest(email, appId, deviceId);
        String json = gson.toJson(request);
        RequestBody body = RequestBody.create(json, JSON);
        
        Request httpRequest = new Request.Builder().header("X-SDK-API-Key", apiKey != null ? apiKey : "")
            .url(baseUrl + "/api/sdk/verify-email").header("X-SDK-Signature", hmacSha256(json, apiSecret))
            .post(body)
            .build();
        
        httpClient.newCall(httpRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                AppTestersHubSDK.logError("Verify email failed: " + e.getMessage());
                callback.onError(e.getMessage());
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    VerifyEmailResponse verifyResponse = gson.fromJson(responseBody, VerifyEmailResponse.class);
                    callback.onSuccess(verifyResponse);
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                    AppTestersHubSDK.logError("Verify email failed: " + response.code() + " - " + errorBody);
                    callback.onError("HTTP " + response.code() + ": " + errorBody);
                }
            }
        });
    }
    
    /**
     * Initialize testing session
     */
    public void initSession(String email, int appId, String deviceId, String sessionToken, Map<String, Object> deviceInfo, ApiCallback<InitSessionResponse> callback) {
        InitSessionRequest request = new InitSessionRequest(email, appId, deviceId, sessionToken, deviceInfo);
        String json = gson.toJson(request);
        RequestBody body = RequestBody.create(json, JSON);
        
        Request httpRequest = new Request.Builder().header("X-SDK-API-Key", apiKey != null ? apiKey : "")
            .url(baseUrl + "/api/sdk/init-session").header("X-SDK-Signature", hmacSha256(json, apiSecret))
            .post(body)
            .build();
        
        httpClient.newCall(httpRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                AppTestersHubSDK.logError("Init session failed: " + e.getMessage());
                callback.onError(e.getMessage());
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    InitSessionResponse initResponse = gson.fromJson(responseBody, InitSessionResponse.class);
                    callback.onSuccess(initResponse);
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                    AppTestersHubSDK.logError("Init session failed: " + response.code() + " - " + errorBody);
                    callback.onError("HTTP " + response.code() + ": " + errorBody);
                }
            }
        });
    }
    
    /**
     * Upload screenshot
     */
    public void uploadScreenshot(int sessionId, File imageFile, String date, Map<String, Object> deviceInfo, ApiCallback<UploadScreenshotResponse> callback) {
        try {
            RequestBody imageBody = RequestBody.create(imageFile, IMAGE);
            
            MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("session_id", String.valueOf(sessionId))
                .addFormDataPart("image", imageFile.getName(), imageBody)
                .addFormDataPart("date", date);
            
            // Add device info as JSON
            if (deviceInfo != null && !deviceInfo.isEmpty()) {
                String deviceInfoJson = gson.toJson(deviceInfo);
                multipartBuilder.addFormDataPart("device_info", deviceInfoJson);
            }
            
            RequestBody requestBody = multipartBuilder.build();
            
            Request httpRequest = new Request.Builder().header("X-SDK-API-Key", apiKey != null ? apiKey : "")
                .url(baseUrl + "/api/sdk/upload-screenshot").header("X-SDK-Signature", hmacSha256(String.valueOf(sessionId) + date, apiSecret))
                .post(requestBody)
                .build();
            
            httpClient.newCall(httpRequest).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    AppTestersHubSDK.logError("Upload screenshot failed: " + e.getMessage());
                    callback.onError(e.getMessage());
                }
                
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        UploadScreenshotResponse uploadResponse = gson.fromJson(responseBody, UploadScreenshotResponse.class);
                        callback.onSuccess(uploadResponse);
                    } else {
                        String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                        AppTestersHubSDK.logError("Upload screenshot failed: " + response.code() + " - " + errorBody);
                        callback.onError("HTTP " + response.code() + ": " + errorBody);
                    }
                }
            });
        } catch (Exception e) {
            AppTestersHubSDK.logError("Upload screenshot error: " + e.getMessage());
            callback.onError(e.getMessage());
        }
    }
    
    /**
     * Get session status
     */
    public void getSessionStatus(int sessionId, ApiCallback<SessionStatusResponse> callback) {
        Request httpRequest = new Request.Builder().header("X-SDK-API-Key", apiKey != null ? apiKey : "")
            .url(baseUrl + "/api/sdk/session/" + sessionId)
            .get()
            .build();
        
        httpClient.newCall(httpRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                AppTestersHubSDK.logError("Get session status failed: " + e.getMessage());
                callback.onError(e.getMessage());
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    SessionStatusResponse statusResponse = gson.fromJson(responseBody, SessionStatusResponse.class);
                    callback.onSuccess(statusResponse);
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                    AppTestersHubSDK.logError("Get session status failed: " + response.code() + " - " + errorBody);
                    callback.onError("HTTP " + response.code() + ": " + errorBody);
                }
            }
        });
    }
    
    /**
     * Get SDK configuration
     */
    public void getSDKConfig(int appId, ApiCallback<SDKConfigResponse> callback) {
        Request httpRequest = new Request.Builder().header("X-SDK-API-Key", apiKey != null ? apiKey : "")
            .url(baseUrl + "/api/sdk/config/" + appId)
            .get()
            .build();
        
        httpClient.newCall(httpRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                AppTestersHubSDK.logError("Get SDK config failed: " + e.getMessage());
                callback.onError(e.getMessage());
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    SDKConfigResponse configResponse = gson.fromJson(responseBody, SDKConfigResponse.class);
                    callback.onSuccess(configResponse);
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                    AppTestersHubSDK.logError("Get SDK config failed: " + response.code() + " - " + errorBody);
                    callback.onError("HTTP " + response.code() + ": " + errorBody);
                }
            }
        });
    }
    
    /**
     * API callback interface
     */
        public void sendHeartbeat(int sessionId, long timestamp, ApiCallback<HeartbeatResponse> callback) {
        String json = "{\"session_id\":" + sessionId + ", \"timestamp\":" + timestamp + "}";
        RequestBody body = RequestBody.create(json, JSON);
        Request httpRequest = new Request.Builder()
            .header("X-SDK-API-Key", apiKey != null ? apiKey : "")
            .header("X-SDK-Signature", hmacSha256(json, apiSecret))
            .url(baseUrl + "/api/sdk/heartbeat")
            .post(body)
            .build();
        httpClient.newCall(httpRequest).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) { callback.onError(e.getMessage()); }
            @Override public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()) {
                    callback.onSuccess(gson.fromJson(response.body().string(), HeartbeatResponse.class));
                } else {
                    callback.onError("HTTP " + response.code());
                }
            }
        });
    }

    public void completeSession(int sessionId, ApiCallback<CompleteSessionResponse> callback) {
        String json = "{\"session_id\":" + sessionId + "}";
        RequestBody body = RequestBody.create(json, JSON);
        Request httpRequest = new Request.Builder()
            .header("X-SDK-API-Key", apiKey != null ? apiKey : "")
            .header("X-SDK-Signature", hmacSha256(json, apiSecret))
            .url(baseUrl + "/api/sdk/complete-session")
            .post(body)
            .build();
        httpClient.newCall(httpRequest).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) { callback.onError(e.getMessage()); }
            @Override public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()) {
                    callback.onSuccess(gson.fromJson(response.body().string(), CompleteSessionResponse.class));
                } else {
                    callback.onError("HTTP " + response.code());
                }
            }
        });
    }

    private String hmacSha256(String data, String key) {
        if (key == null || data == null) return "";
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            AppTestersHubSDK.logError("Error generating HMAC: " + e.getMessage());
            return "";
        }
    }

    public interface ApiCallback<T> {
        void onSuccess(T response);
        void onError(String error);
    }
}

