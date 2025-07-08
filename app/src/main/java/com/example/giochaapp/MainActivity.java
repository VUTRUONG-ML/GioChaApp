package com.example.giochaapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.giochaapp.utils.AuthManager;
import com.example.giochaapp.utils.SharedPrefsManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.giochaapp.fragments.HomeFragment;
import com.example.giochaapp.fragments.CartFragment;
import com.example.giochaapp.fragments.ProfileFragment;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupBottomNavigation();

        // Load default fragment
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
        SharedPrefsManager prefs = new SharedPrefsManager(MainActivity.this);
        JSONObject user = prefs.getUserObject();
        if (user != null) {
            String name = user.optString("userName", "Unknown");
            Toast.makeText(this, "Xin chào " + name, Toast.LENGTH_LONG).show();
        }

        // Load default fragment
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fragmentManager = getSupportFragmentManager();
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(menuItem -> {
            Fragment selectedFragment = null;

            if (menuItem.getItemId() == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (menuItem.getItemId() == R.id.nav_cart) {
                selectedFragment = new CartFragment();
            } else if (menuItem.getItemId() == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }

            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();

        // Ẩn/hiện header + search tùy fragment
        if (fragment instanceof HomeFragment) {
            showHeaderSearch(true);
        } else if (fragment instanceof CartFragment || fragment instanceof ProfileFragment) {
            showHeaderSearch(false);
        } else {
            showHeaderSearch(true); // Mặc định hiện
        }
    }


    public void showHeaderSearch(boolean show) {
        View header = findViewById(R.id.header_layout);
        View search = findViewById(R.id.search_card);
        if (header != null && search != null) {
            header.setVisibility(show ? View.VISIBLE : View.GONE);
            search.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

}