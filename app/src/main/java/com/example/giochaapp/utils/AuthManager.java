package com.example.giochaapp.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.giochaapp.config.ApiConfig;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AuthManager {
    private static final String TAG = "AuthManager";
    private static final String ME_ENDPOINT = "/api/auth/me";

    private static AuthManager instance;
    private SharedPrefsManager prefs;
    private Context context;

    private AuthManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = new SharedPrefsManager(context);
    }

    public static AuthManager getInstance(Context context) {
        if (instance == null) {
            instance = new AuthManager(context);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return prefs.isLoggedIn() && !prefs.getToken().isEmpty();
    }

    public void login(String email, String token) {
        prefs.setLoggedIn(true);
        prefs.setUserEmail(email);
        prefs.setToken(token);
    }

    public void logout() {
        prefs.clearUserData();
    }


    public void fetchCurrentUser(Callback callback) {
        new Thread(() -> {
            try {
                URL url = new URL(ApiConfig.BASE_URL + ME_ENDPOINT);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + prefs.getToken());

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    reader.close();

                    JSONObject userJson = new JSONObject(result.toString());
                    prefs.setUserObject(userJson);
                    String phone = userJson.optString("phoneNumber", "");
                    prefs.setUserPhone(phone);

                    String name = userJson.optString("userName", "");
                    prefs.setUserName(name);
                    callback.onSuccess(userJson);
                } else {
                    callback.onError("Lỗi xác thực: " + responseCode);
                }
            } catch (Exception e) {
                Log.e(TAG, "fetchCurrentUser: ", e);
                callback.onError("Lỗi kết nối server");
            }
        }).start();
    }

    public interface Callback {
        void onSuccess(JSONObject userData);
        void onError(String errorMessage);
    }
}
