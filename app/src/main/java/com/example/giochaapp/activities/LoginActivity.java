package com.example.giochaapp.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.giochaapp.MainActivity;
import com.example.giochaapp.R;
import com.example.giochaapp.config.ApiConfig;
import com.example.giochaapp.utils.AuthManager;
import com.example.giochaapp.utils.HttpHelper;
import com.example.giochaapp.utils.SharedPrefsManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput;
    private EditText passwordInput;
    private ImageButton togglePassword;
    private Button loginButton;
    private TextView forgotPassword;
    private TextView registerLink;

    private boolean isPasswordVisible = false;
    private SharedPrefsManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefsManager = new SharedPrefsManager(this);

        // Check if user is already logged in
        if (prefsManager.isLoggedIn()) {
            navigateToMain();
            return;
        }

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        togglePassword = findViewById(R.id.toggle_password);
        loginButton = findViewById(R.id.login_button);
        forgotPassword = findViewById(R.id.forgot_password);
        registerLink = findViewById(R.id.register_link);
    }

    private void setupClickListeners() {
        togglePassword.setOnClickListener(v -> togglePasswordVisibility());
        loginButton.setOnClickListener(v -> performLogin());
        forgotPassword.setOnClickListener(v -> handleForgotPassword());
        registerLink.setOnClickListener(v -> navigateToRegister());
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            togglePassword.setImageResource(R.drawable.ic_eye);
        } else {
            passwordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                    android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            togglePassword.setImageResource(R.drawable.ic_eye_off);
        }
        isPasswordVisible = !isPasswordVisible;
        passwordInput.setSelection(passwordInput.getText().length());
    }

    private void performLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.error_empty_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, R.string.error_invalid_email, Toast.LENGTH_SHORT).show();
            return;
        }

        // Simulate login process
        loginButton.setEnabled(false);
        loginButton.setText("Đang đăng nhập...");

        new LoginTask(email, password).execute();
    }

    private void handleForgotPassword() {
        Toast.makeText(this, "Tính năng quên mật khẩu sẽ sớm ra mắt!", Toast.LENGTH_SHORT).show();
    }

    private void navigateToRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private class LoginTask extends AsyncTask<Void, Void, JSONObject> {
        private String email, password;

        public LoginTask(String email, String password) {
            this.email = email;
            this.password = password;
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {
                JSONObject loginBody = new JSONObject();
                loginBody.put("email", email);
                loginBody.put("passWord", password);

                return HttpHelper.postJson(ApiConfig.BASE_URL + "/api/auth/login", loginBody, null);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            loginButton.setEnabled(true);
            loginButton.setText("Đăng nhập");

            if (result != null) {
                int statusCode = result.optInt("status", 0);
                JSONObject body = result.optJSONObject("body");
                String message = body != null ? body.optString("message", "Đăng nhập thành công!") : "Lỗi không xác định";

                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();

                if (statusCode >= 200 && statusCode < 300 && body != null) {
                    String token = body.optString("token", "");
                    if (!token.isEmpty()) {
                        AuthManager.getInstance(LoginActivity.this).login(email, token);

                        AuthManager.getInstance(LoginActivity.this).fetchCurrentUser(new AuthManager.Callback() {
                            @Override
                            public void onSuccess(JSONObject userData) {
                                runOnUiThread(() -> navigateToMain());
                            }

                            @Override
                            public void onError(String errorMessage) {
                                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Lỗi khi lấy thông tin user: " + errorMessage, Toast.LENGTH_SHORT).show());
                            }
                        });
                    } else {
                        Toast.makeText(LoginActivity.this, "Không nhận được token!", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(LoginActivity.this, "Đăng nhập thất bại! Kiểm tra kết nối hoặc server.", Toast.LENGTH_LONG).show();
            }
        }
    }


}