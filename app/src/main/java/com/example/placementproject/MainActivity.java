package com.example.placementproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {
    FrameLayout container;
    ImageView news,profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check login flag from SharedPreferences
        SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
        boolean isLoggedOut = pref.getBoolean("flag", true); // true = not logged in

        if (isLoggedOut) {
            // Redirect to Login screen
            Intent intent = new Intent(MainActivity.this, LoginAndSignup.class);
            startActivity(intent);
            finish(); // Prevent coming back to this screen
            return;
        }

        // Continue with main screen if user is logged in
        setContentView(R.layout.activity_main);

        news = findViewById(R.id.newsImg);
//        logout = findViewById(R.id.logout);
        profile=findViewById(R.id.profile);
        container = findViewById(R.id.frameContainer);

        // Load HomeFragment by default
        HomeFragment fragment = new HomeFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.frameContainer, fragment);
        ft.commit();

        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inews = new Intent(MainActivity.this, NewsActivity.class);
                startActivity(inews);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inews = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(inews);
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
