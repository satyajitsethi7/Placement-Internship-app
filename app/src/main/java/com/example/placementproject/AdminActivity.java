
package com.example.placementproject;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AdminActivity extends AppCompatActivity {

    private ArrayList<DataClass> jobList = new ArrayList<>();
    private RecyclyerDoListAdapter adapter;
    private RecyclerView recyclerView;
    private FloatingActionButton add_btn;
    private ImageView emptytaskimg, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initialize UI components
        recyclerView = findViewById(R.id.textlist);
        emptytaskimg = findViewById(R.id.emptytasks);
        add_btn = findViewById(R.id.btnAddDialog);
        btnLogout = findViewById(R.id.btnLogout);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclyerDoListAdapter(this, jobList);
        recyclerView.setAdapter(adapter);

        // Fetch data
        fetchJobsFromFirebase();

        // Listeners
        if (add_btn != null) {
            add_btn.setOnClickListener(v -> showAddDialog());
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut();
                SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("flag", true); // Logged out
                editor.putString("role", "");
                editor.putString("username", "");
                editor.apply();

                Intent intent = new Intent(AdminActivity.this, LoginAndSignup.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }

        // Handle window insets
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
    }

    private void fetchJobsFromFirebase() {
        FirebaseDatabase.getInstance().getReference("Jobs and Internships")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        jobList.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            try {
                                DataClass data = snap.getValue(DataClass.class);
                                if (data != null) {
                                    data.setKey(snap.getKey()); // Critical for identifying the record
                                    jobList.add(data);
                                }
                            } catch (Exception e) {
                                // Skip malformed data from previous versions
                                e.printStackTrace();
                            }
                        }
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                        toggleEmptyImage();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AdminActivity.this, "Failed to fetch data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void toggleEmptyImage() {
        if (emptytaskimg != null) {
            emptytaskimg.setVisibility(jobList.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    private void showAddDialog() {
        Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.add_dialog_box);

        EditText edtJobPreference = dialog.findViewById(R.id.edtJobPreference);
        EditText edtCompanyName = dialog.findViewById(R.id.edtCompanyName);
        EditText edtLink = dialog.findViewById(R.id.edtLink);
        AppCompatButton btnAdd = dialog.findViewById(R.id.btnAdd);

        if (btnAdd != null) {
            btnAdd.setOnClickListener(v -> {
                String job = edtJobPreference.getText().toString().trim();
                String company = edtCompanyName.getText().toString().trim();
                String link = edtLink.getText().toString().trim();

                if (job.isEmpty() || company.isEmpty() || link.isEmpty()) {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                dialog.dismiss();
                uploadJobToFirebase(job, company, link);
            });
        }

        dialog.show();
    }

    private void uploadJobToFirebase(String job, String company, String link) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        DataClass data = new DataClass(job, company, link, currentDate);

        FirebaseDatabase.getInstance().getReference("Jobs and Internships")
                .push() // Ensures unique keys
                .setValue(data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Job Added", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
