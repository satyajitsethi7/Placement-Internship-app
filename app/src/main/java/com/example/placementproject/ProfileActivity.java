package com.example.placementproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_RESUME_REQUEST = 1;
    private static final int PICK_PROFILE_IMAGE_REQUEST = 2;

    private Button logout, uploadResumeButton;
    private TextView atsScore, userName, selectedFileName;
    private ImageView profileImage;
    private String apiKey = "K88841546488957";

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private StorageReference storageRef;

    private final String[] keywords = {
            "Java", "C++", "C#", "HTML", "CSS", "JavaScript",
            "React", "Firebase", "Android", "OOP", "SQL", "ASP.NET",
            "Python", "Kotlin", "Git", "REST API", "Agile", "Problem Solving"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginAndSignup.class));
            finish();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        storageRef = FirebaseStorage.getInstance().getReference("ProfileImages").child(userId + ".jpg");

        logout = findViewById(R.id.logout);
        uploadResumeButton = findViewById(R.id.uploadResumeButton);
        atsScore = findViewById(R.id.atsScore);
        userName = findViewById(R.id.userName);
        selectedFileName = findViewById(R.id.selectedFileName);
        profileImage = findViewById(R.id.profileImage);

        SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
        String name = pref.getString("username", "User Name");
        userName.setText(name);

        loadProfileImage();

        profileImage.setOnClickListener(v -> openProfileImagePicker());

        logout.setOnClickListener(v -> {
            mAuth.signOut();
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("flag", true);
            editor.putString("username", "");
            editor.apply();
            startActivity(new Intent(ProfileActivity.this, LoginAndSignup.class));
            finish();
        });

        uploadResumeButton.setOnClickListener(v -> openResumePicker());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void openResumePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_RESUME_REQUEST);
    }

    private void openProfileImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_PROFILE_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == PICK_RESUME_REQUEST) {
                Uri resumeUri = data.getData();
                String path = getFileNameFromUri(resumeUri);
                selectedFileName.setText("Selected: " + path);
                runOCRAndScore(resumeUri);
            } else if (requestCode == PICK_PROFILE_IMAGE_REQUEST) {
                Uri profileUri = data.getData();
                uploadProfileImage(profileUri);
            }
        }
    }

    private void uploadProfileImage(Uri uri) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading profile picture...");
        dialog.setCancelable(false);
        dialog.show();

        storageRef.putFile(uri).addOnSuccessListener(taskSnapshot -> {
            storageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                String imageUrl = downloadUri.toString();
                userRef.child("profileImageUrl").setValue(imageUrl).addOnCompleteListener(task -> {
                    dialog.dismiss();
                    if (task.isSuccessful()) {
                        Glide.with(ProfileActivity.this).load(imageUrl).into(profileImage);
                        Toast.makeText(ProfileActivity.this, "Profile picture updated!", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(ProfileActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void loadProfileImage() {
        userRef.child("profileImageUrl").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String url = snapshot.getValue(String.class);
                    if (url != null && !url.isEmpty() && !isFinishing()) {
                        Glide.with(ProfileActivity.this).load(url).into(profileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private String getFileNameFromUri(Uri uri) {
        String result = "Unknown File";
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                }
            }
        }
        if (result == null || result.isEmpty()) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) result = result.substring(cut + 1);
        }
        return result;
    }

    private void runOCRAndScore(Uri uri) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Analyzing resume...");
        dialog.setCancelable(false);
        dialog.show();

        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            // Using NO_WRAP to avoid newlines in the Base64 string
            String base64Image = Base64.encodeToString(byteBuffer.toByteArray(), Base64.NO_WRAP);

            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://api.ocr.space/parse/image";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    response -> {
                        dialog.dismiss();
                        Log.d("OCR_RESPONSE", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("ParsedResults")) {
                                JSONArray results = jsonObject.getJSONArray("ParsedResults");
                                if (results.length() > 0) {
                                    String parsedText = results.getJSONObject(0).getString("ParsedText");
                                    if (parsedText == null || parsedText.trim().isEmpty()) {
                                        atsScore.setText("ATS Compatibility Score: 0%\n(No readable text found)");
                                    } else {
                                        int score = calculateATSScore(parsedText);
                                        atsScore.setText("ATS Compatibility Score: " + score + "%");
                                    }
                                } else {
                                    atsScore.setText("OCR failed: No text found.");
                                }
                            } else if (jsonObject.has("ErrorMessage")) {
                                JSONArray errorMsgs = jsonObject.getJSONArray("ErrorMessage");
                                atsScore.setText("Error: " + errorMsgs.getString(0));
                            } else {
                                atsScore.setText("Analysis failed: Unknown response.");
                            }
                        } catch (Exception e) {
                            Log.e("OCR", "Error parsing response", e);
                            atsScore.setText("Error parsing result.");
                        }
                    },
                    error -> {
                        dialog.dismiss();
                        Log.e("OCR", "Volley Error: " + error.toString());
                        String message = "Network error. Please try again.";
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            message = new String(error.networkResponse.data);
                        }
                        atsScore.setText(message);
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("apikey", apiKey);
                    params.put("base64Image", "data:image/jpeg;base64," + base64Image);
                    params.put("language", "eng");
                    params.put("isOverlayRequired", "false");
                    params.put("filetype", "JPG");
                    return params;
                }
            };

            // OCR.space can be slow, so we increase the timeout to 30 seconds
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(stringRequest);

        } catch (Exception e) {
            dialog.dismiss();
            Log.e("OCR", "Setup Error", e);
            atsScore.setText("Error setting up analysis.");
        }
    }

    private int calculateATSScore(String text) {
        if (text == null || text.isEmpty()) return 0;
        int matches = 0;
        String lowerText = text.toLowerCase();
        for (String keyword : keywords) {
            if (lowerText.contains(keyword.toLowerCase())) {
                matches++;
            }
        }
        return (int) (((double) matches / keywords.length) * 100);
    }
}
