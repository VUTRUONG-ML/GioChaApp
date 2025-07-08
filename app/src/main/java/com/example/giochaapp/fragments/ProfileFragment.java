package com.example.giochaapp.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.giochaapp.activities.LoginActivity;
import com.example.giochaapp.R;
import com.example.giochaapp.models.User;
import com.example.giochaapp.utils.AuthManager;
import com.example.giochaapp.utils.SharedPrefsManager;

public class ProfileFragment extends Fragment {

    private ImageView userAvatar;
    private TextView userName;
    private TextView memberInfo;
    private TextView userEmail;
    private TextView userPhone;
    private TextView totalOrders;
    private TextView userRating;
    private LinearLayout editProfileOption;
    private LinearLayout orderHistoryOption;
    private LinearLayout notificationsOption;
    private LinearLayout settingsOption;
    private LinearLayout logoutOption;

    private SharedPrefsManager prefsManager;
    private User currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        prefsManager = new SharedPrefsManager(getContext());

        initViews(view);
        loadUserData();
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        userAvatar = view.findViewById(R.id.user_avatar);
        userName = view.findViewById(R.id.user_name);
        memberInfo = view.findViewById(R.id.member_info);
        userEmail = view.findViewById(R.id.user_email);
        userPhone = view.findViewById(R.id.user_phone);
        totalOrders = view.findViewById(R.id.total_orders);
        userRating = view.findViewById(R.id.user_rating);
        editProfileOption = view.findViewById(R.id.edit_profile_option);
        orderHistoryOption = view.findViewById(R.id.order_history_option);
        notificationsOption = view.findViewById(R.id.notifications_option);
        settingsOption = view.findViewById(R.id.settings_option);
        logoutOption = view.findViewById(R.id.logout_button);
    }

    private void loadUserData() {
        // Load user data from preferences or database
        currentUser = createSampleUser();
        displayUserData();
    }

    private User createSampleUser() {
        // Sample user data - replace with actual data loading
        return new User(
                "1",
                "Nguyễn Văn An",
                prefsManager.getUserEmail(),
                "+84 123 456 789",
                "https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg",
                "2023",
                24,
                4.8f
        );
    }

    private void displayUserData() {
        userName.setText(currentUser.getName());
        memberInfo.setText("Thành viên từ " + currentUser.getMemberSince());
        userEmail.setText(currentUser.getEmail());
        userPhone.setText(currentUser.getPhone());
        totalOrders.setText(String.valueOf(currentUser.getTotalOrders()));
        userRating.setText(String.valueOf(currentUser.getRating()));

        // Load avatar
        Glide.with(this)
                .load(currentUser.getAvatarUrl())
                .placeholder(R.drawable.avatar_placeholder)
                .circleCrop()
                .into(userAvatar);
    }

    private void setupClickListeners() {
        editProfileOption.setOnClickListener(v -> handleEditProfile());
        orderHistoryOption.setOnClickListener(v -> handleOrderHistory());
        notificationsOption.setOnClickListener(v -> handleNotifications());
        settingsOption.setOnClickListener(v -> handleSettings());
        logoutOption.setOnClickListener(v -> handleLogout());
    }

    private void handleEditProfile() {
        Toast.makeText(getContext(), "Tính năng chỉnh sửa hồ sơ sẽ sớm ra mắt!", Toast.LENGTH_SHORT).show();
    }

    private void handleOrderHistory() {
        Fragment fragment = new OrderHistoryFragment();
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null) // để có thể bấm back quay lại
                .commit();
    }

    private void handleNotifications() {
        Toast.makeText(getContext(), "Tính năng thông báo sẽ sớm ra mắt!", Toast.LENGTH_SHORT).show();
    }

    private void handleSettings() {
        Toast.makeText(getContext(), "Tính năng cài đặt sẽ sớm ra mắt!", Toast.LENGTH_SHORT).show();
    }

    private void handleLogout() {
        new AlertDialog.Builder(getContext())
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    AuthManager.getInstance(requireContext()).logout();

                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}