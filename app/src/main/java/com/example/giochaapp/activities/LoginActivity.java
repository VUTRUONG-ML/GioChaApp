package com.example.giochaapp.activities;

import android.content.Intent;
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

    private class LoginTask extends android.os.AsyncTask<Void, Void, String> {
        private String email, password;

        public LoginTask(String email, String password) {
            this.email = email;
            this.password = password;
        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(ApiConfig.BASE_URL + "/api/auth/login");
                conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                // Gửi JSON
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("email", email);
                jsonParam.put("passWord", password);

                OutputStream os = conn.getOutputStream();
                os.write(jsonParam.toString().getBytes("UTF-8"));
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                InputStream inputStream;

                if (responseCode >= 200 && responseCode < 300) {
                    inputStream = conn.getInputStream(); // Thành công
                } else {
                    inputStream = conn.getErrorStream(); // Lỗi (400, 404, 500...)
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                JSONObject wrappedResult = new JSONObject();
                wrappedResult.put("status", responseCode);
                wrappedResult.put("body", new JSONObject(response.toString()));
                return wrappedResult.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                if (conn != null) conn.disconnect();
            }
        }


        @Override
        protected void onPostExecute(String result) {
            loginButton.setEnabled(true);
            loginButton.setText("Đăng nhập");

            if (result != null) {
                try {
                    JSONObject json = new JSONObject(result);
                    int statusCode = json.getInt("status");
                    JSONObject body = json.getJSONObject("body");
                    String message = body.optString("message", "Đăng nhập thành công!");

                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();

                    if (statusCode >= 200 && statusCode < 300) {
                        String token = body.optString("token", "");
                        if (!token.isEmpty()) {
                            AuthManager.getInstance(LoginActivity.this).login(email, token);

                            // Gọi API /me để lấy thông tin người dùng và lưu lại
                            AuthManager.getInstance(LoginActivity.this).fetchCurrentUser(new AuthManager.Callback() {
                                @Override
                                public void onSuccess(JSONObject userData) {
                                    // Điều hướng sang màn chính
                                    runOnUiThread(() -> navigateToMain());
                                }

                                @Override
                                public void onError(String errorMessage) {
                                    runOnUiThread(() -> {
                                        Toast.makeText(LoginActivity.this, "Lỗi khi lấy thông tin user: " + errorMessage, Toast.LENGTH_SHORT).show();
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(LoginActivity.this, "Không nhận được token!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    Toast.makeText(LoginActivity.this, "Lỗi phản hồi từ server!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Đăng nhập thất bại! Kiểm tra thông tin hoặc API.", Toast.LENGTH_LONG).show();
            }
        }
    }

}