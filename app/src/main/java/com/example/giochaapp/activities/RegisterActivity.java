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

import com.example.giochaapp.R;

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

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, R.string.error_password_mismatch, Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        // Simulate registration process
        registerButton.setEnabled(false);
        registerButton.setText("Đang đăng ký...");

        // Simulate network delay
        new android.os.Handler().postDelayed(() -> {
            Toast.makeText(this, R.string.success_register, Toast.LENGTH_SHORT).show();
            navigateToLogin();
        }, 1500);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}