package com.example.giochaapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.giochaapp.R;
import com.example.giochaapp.config.ApiConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity {

    private ImageButton backButton;
    private EditText fullnameInput;
    private EditText emailInput;
    private EditText phoneInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private ImageButton togglePassword;
    private ImageButton toggleConfirmPassword;
    private Button registerButton;
    private TextView loginLink;

    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        backButton = findViewById(R.id.back_button);
        fullnameInput = findViewById(R.id.fullname_input);
        emailInput = findViewById(R.id.email_input);
        phoneInput = findViewById(R.id.phone_input);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        togglePassword = findViewById(R.id.toggle_password);
        toggleConfirmPassword = findViewById(R.id.toggle_confirm_password);
        registerButton = findViewById(R.id.register_button);
        loginLink = findViewById(R.id.login_link);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
        togglePassword.setOnClickListener(v -> togglePasswordVisibility());
        toggleConfirmPassword.setOnClickListener(v -> toggleConfirmPasswordVisibility());
        registerButton.setOnClickListener(v -> performRegister());
        loginLink.setOnClickListener(v -> navigateToLogin());
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

    private void toggleConfirmPasswordVisibility() {
        if (isConfirmPasswordVisible) {
            confirmPasswordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            toggleConfirmPassword.setImageResource(R.drawable.ic_eye);
        } else {
            confirmPasswordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                    android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            toggleConfirmPassword.setImageResource(R.drawable.ic_eye_off);
        }
        isConfirmPasswordVisible = !isConfirmPasswordVisible;
        confirmPasswordInput.setSelection(confirmPasswordInput.getText().length());
    }

    private void performRegister() {
        String fullname = fullnameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        if (TextUtils.isEmpty(fullname) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(phone) || TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, R.string.error_empty_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, R.string.error_invalid_email, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!phone.matches("^0[0-9]{9}$")) {
            Toast.makeText(this, "Số điện thoại không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }


        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, R.string.error_password_mismatch, Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }
        registerButton.setEnabled(false);
        registerButton.setText("Đang đăng ký...");
        new RegisterTask(fullname, email, phone, password).execute();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
    private class RegisterTask extends android.os.AsyncTask<Void, Void, String> {
        String fullName, email, phone, passWord;

        public RegisterTask(String fullName, String email, String phone, String password) {
            this.fullName = fullName;
            this.email = email;
            this.phone = phone;
            this.passWord = password;
        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(ApiConfig.BASE_URL + "/api/auth/register");
                conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                // Tạo JSON object để gửi
                org.json.JSONObject jsonParam = new org.json.JSONObject();
                jsonParam.put("userName", fullName);
                jsonParam.put("email", email);
                jsonParam.put("phoneNumber", phone);
                jsonParam.put("passWord", passWord);

                // Gửi JSON
                java.io.OutputStream os = conn.getOutputStream();
                os.write(jsonParam.toString().getBytes("UTF-8"));
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                InputStream is = (responseCode >= 200 && responseCode < 300)
                        ? conn.getInputStream()
                        : conn.getErrorStream();

                BufferedReader in = new BufferedReader(new InputStreamReader(is));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();
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
            registerButton.setEnabled(true);
            registerButton.setText("Đăng ký");

            if (result != null) {
                try {
                    JSONObject json = new JSONObject(result);
                    int statusCode = json.getInt("status");
                    JSONObject body = json.getJSONObject("body");
                    String message = body.optString("message", "Đăng ký thành công!");

                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();

                    if (statusCode >= 200 && statusCode < 300) {
                        navigateToLogin();
                    }
                } catch (JSONException e) {
                    Toast.makeText(RegisterActivity.this, "Lỗi phản hồi từ server!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(RegisterActivity.this, "Đăng ký thất bại! Kiểm tra kết nối hoặc API.", Toast.LENGTH_LONG).show();
            }
        }
    }


}


