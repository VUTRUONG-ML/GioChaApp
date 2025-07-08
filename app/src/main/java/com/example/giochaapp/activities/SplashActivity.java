package com.example.giochaapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.giochaapp.MainActivity;
import com.example.giochaapp.R;
import com.example.giochaapp.utils.AuthManager;

import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash); // layout loading/logo

        AuthManager authManager = AuthManager.getInstance(this);

        if (!authManager.isLoggedIn()) {
            navigateToLogin();
            return;
        }

        // Nếu đã lưu token → xác thực lại bằng API /me
        authManager.fetchCurrentUser(new AuthManager.Callback() {
            @Override
            public void onSuccess(JSONObject userData) {
                runOnUiThread(() -> {
                    navigateToMain();
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    Toast.makeText(SplashActivity.this, "Phiên đăng nhập hết hạn", Toast.LENGTH_SHORT).show();
                    authManager.logout();
                    navigateToLogin();
                });
            }
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
