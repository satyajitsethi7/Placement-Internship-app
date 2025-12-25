//package com.example.placementproject;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.provider.OpenableColumns;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import android.util.Base64;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
//import org.json.JSONObject;
//import java.io.InputStream;
//
//public class ProfileActivity extends AppCompatActivity {
//
//    private static final int PICK_IMAGE_REQUEST = 1;
//    private Button logout, uploadResumeButton;
//    private TextView atsScore, userName, selectedFileName;
//    private Uri imageUri;
//    private String apiKey = "K88841546488957"; // Replace with real key
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_profile);
//
//        logout = findViewById(R.id.logout);
//        uploadResumeButton = findViewById(R.id.uploadResumeButton);
//        atsScore = findViewById(R.id.atsScore);
//        userName = findViewById(R.id.userName);
//        selectedFileName = findViewById(R.id.selectedFileName);
//
//        // Load name from SharedPreferences
//        SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
//        String name = pref.getString("username", "User Name");
//        userName.setText(name);
//
//        logout.setOnClickListener(v -> {
//            SharedPreferences.Editor editor = pref.edit();
//            editor.putBoolean("flag", true);
//            editor.apply();
//            startActivity(new Intent(ProfileActivity.this, LoginAndSignup.class));
//            finish();
//        });
//
//        uploadResumeButton.setOnClickListener(v -> openImagePicker());
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//    }
//
//
//
//    private void openImagePicker() {
//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(intent, PICK_IMAGE_REQUEST);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            imageUri = data.getData();
//
//            String fileName = getFileNameFromUri(imageUri);
//            selectedFileName.setText("File Selected: " + fileName);
//            Toast.makeText(this, "Resume selected successfully", Toast.LENGTH_SHORT).show();
//
//            extractTextFromImage(imageUri); // 👈 Start OCR processing
//        }
//    }
//
//
//    private String getFileNameFromUri(Uri uri) {
//        String result = "Unknown";
//        if (uri.getScheme().equals("content")) {
//            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
//                if (cursor != null && cursor.moveToFirst()) {
//                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//                    if (nameIndex != -1) {
//                        result = cursor.getString(nameIndex);
//                    }
//                }
//            }
//        } else if (uri.getScheme().equals("file")) {
//            result = uri.getLastPathSegment();
//        }
//        return result;
//    }
//    private void extractTextFromImage(Uri imageUri) {
//        try {
//            InputStream inputStream = getContentResolver().openInputStream(imageUri);
//            byte[] imageBytes = new byte[inputStream.available()];
//            inputStream.read(imageBytes);
//
//            // Convert image to Base64
//            String encodedImage = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
//
//            RequestQueue queue = Volley.newRequestQueue(this);
//            String url = "https://api.ocr.space/parse/image";
//
//            StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//                    String extractedText = jsonObject.getJSONArray("ParsedResults")
//                            .getJSONObject(0)
//                            .getString("ParsedText");
//
//                    calculateATSScore(extractedText);
//                } catch (Exception e) {
//                    Toast.makeText(this, "Failed to extract text", Toast.LENGTH_SHORT).show();
//                }
//            }, error -> {
//                Toast.makeText(this, "OCR API failed", Toast.LENGTH_SHORT).show();
//            }) {
//                @Override
//                public byte[] getBody() {
//                    String data = "base64Image=data:image/jpeg;base64," + encodedImage +
//                            "&language=eng&apikey=" + apiKey;
//                    return data.getBytes();
//                }
//
//                @Override
//                public String getBodyContentType() {
//                    return "application/x-www-form-urlencoded";
//                }
//            };
//
//            queue.add(request);
//
//        } catch (Exception e) {
//            Toast.makeText(this, "Image error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//    private void calculateATSScore(String text) {
//        int score = 0;
//        int total = 6;
//
//        String[] keywords = {"Java", "C#", "Android", "Firebase", "OOP", "SQL"};
//
//        for (String keyword : keywords) {
//            if (text.toLowerCase().contains(keyword.toLowerCase())) {
//                score++;
//            }
//        }
//
//        int percentage = (score * 100) / total;
//        atsScore.setText("ATS Compatibility Score: " + percentage + "%");
//    }
//
//}
package com.example.placementproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.database.Cursor;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Button logout, uploadResumeButton;
    private TextView atsScore, userName, selectedFileName;
    private Uri imageUri;
    private String apiKey = "K88841546488957"; // Replace with your OCR.Space API key

    private final String[] keywords = {
            "Java", "C++", "C#", "HTML", "CSS", "JavaScript",
            "React", "Firebase", "Android", "OOP", "SQL", "ASP.NET"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        logout = findViewById(R.id.logout);
        uploadResumeButton = findViewById(R.id.uploadResumeButton);
        atsScore = findViewById(R.id.atsScore);
        userName = findViewById(R.id.userName);
        selectedFileName = findViewById(R.id.selectedFileName);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            // Display file name or path
            String path = getFileNameFromUri(imageUri);
            selectedFileName.setText("Selected: " + path);

            // Start OCR and ATS scoring
            runOCRAndScore(imageUri);
        }
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

        new Thread(() -> {
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    byteBuffer.write(buffer, 0, len);
                }

                String base64Image = Base64.encodeToString(byteBuffer.toByteArray(), Base64.NO_WRAP);

                URL url = new URL("https://api.ocr.space/parse/image");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("apikey", apiKey);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String data = "base64Image=data:image/jpeg;base64," + base64Image +
                        "&language=eng&isOverlayRequired=false";

                OutputStream os = conn.getOutputStream();
                os.write(data.getBytes());
                os.flush();
                os.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                String parsedText = jsonResponse
                        .getJSONArray("ParsedResults")
                        .getJSONObject(0)
                        .getString("ParsedText");

                int score = calculateATSScore(parsedText);

                runOnUiThread(() -> {
                    atsScore.setText("ATS Compatibility Score: " + score + "%");
                    dialog.dismiss();
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    atsScore.setText("Error analyzing resume");
                    dialog.dismiss();
                });
            }
        }).start();
    }

    private int calculateATSScore(String text) {
        int matches = 0;
        text = text.toLowerCase();
        for (String keyword : keywords) {
            if (text.contains(keyword.toLowerCase())) {
                matches++;
            }
        }
        return (matches * 100) / keywords.length;
    }
}
