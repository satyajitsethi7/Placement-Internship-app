package com.example.placementproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
                // flag=false means logged in, flag=true means logged out
                boolean isLoggedOut = pref.getBoolean("flag", true);
                String role = pref.getString("role", "");

                Intent intent;
                if (isLoggedOut) {
                    // Not logged in, go to Login screen
                    intent = new Intent(SplashActivity.this, LoginAndSignup.class);
                } else {
                    // Logged in, check role
                    if ("admin".equalsIgnoreCase(role)) {
                        intent = new Intent(SplashActivity.this, AdminActivity.class);
                    } else {
                        intent = new Intent(SplashActivity.this, MainActivity.class);
                    }
                }
                
                startActivity(intent);
                finish();
            }
        }, 2000);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
