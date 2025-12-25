package com.example.placementproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class LoginAndSignup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Session management: check if user is already logged in
        SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
        // flag=false means logged in, flag=true (default) means logged out
        boolean isLoggedIn = !pref.getBoolean("flag", true); 
        String role = pref.getString("role", "").trim();

        if (isLoggedIn && !role.isEmpty()) {
            Intent intent;
            if ("admin".equalsIgnoreCase(role)) {
                intent = new Intent(LoginAndSignup.this, AdminActivity.class);
            } else {
                intent = new Intent(LoginAndSignup.this, MainActivity.class);
            }
            startActivity(intent);
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_and_signup);

        if (savedInstanceState == null) {
            switchFragment(new LoginFragment());
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.loginandsignupmain);
                if (currentFragment instanceof SignupFragment) {
                    switchFragment(new LoginFragment());
                } else {
                    finish();
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginandsignupmain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void switchFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.loginandsignupmain, fragment);
        ft.commit();
    }
}
