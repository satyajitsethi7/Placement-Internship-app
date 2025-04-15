package com.example.placementproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Button logout, uploadResumeButton;
    private TextView atsScore, userName;
    private Uri imageUri;
    private String apiKey = "your_ocr_space_api_key"; // Replace with real key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        logout = findViewById(R.id.logout);
        uploadResumeButton = findViewById(R.id.uploadResumeButton);
        atsScore = findViewById(R.id.atsScore);
        userName = findViewById(R.id.userName);

        // Update user name dynamically from SharedPreferences
        SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
        String name = pref.getString("username", "User Name");
        userName.setText(name);

        logout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("flag", true);
            editor.apply();

            startActivity(new Intent(ProfileActivity.this, LoginAndSignup.class));
            finish();
        });

        uploadResumeButton.setOnClickListener(v -> openImagePicker());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
}