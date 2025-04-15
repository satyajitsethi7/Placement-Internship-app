
package com.example.placementproject;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AdminActivity extends AppCompatActivity {

    ArrayList<DataClass> jobList = new ArrayList<>();
    RecyclyerDoListAdapter adapter;
    RecyclerView recyclerView;
    FloatingActionButton add_btn;
    ImageView emptytaskimg;

    EditText edtJobPreference, edtCompanyName, edtLink;
    AppCompatButton btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        recyclerView = findViewById(R.id.textlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        emptytaskimg = findViewById(R.id.emptytasks);
        add_btn = findViewById(R.id.btnAddDialog);

        adapter = new RecyclyerDoListAdapter(this, jobList);
        recyclerView.setAdapter(adapter);

        fetchJobsFromFirebase();

        add_btn.setOnClickListener(v -> showAddDialog());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void fetchJobsFromFirebase() {
        FirebaseDatabase.getInstance().getReference("Jobs and Internships")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        jobList.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            DataClass data = snap.getValue(DataClass.class);
                            jobList.add(data);
                        }
                        adapter.notifyDataSetChanged();
                        toggleEmptyImage();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(AdminActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void toggleEmptyImage() {
        emptytaskimg.setVisibility(jobList.isEmpty() ? View.VISIBLE : View.INVISIBLE);
    }

    private void showAddDialog() {
        Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.add_dialog_box);

        edtJobPreference = dialog.findViewById(R.id.edtJobPreference);
        edtCompanyName = dialog.findViewById(R.id.edtCompanyName);
        edtLink = dialog.findViewById(R.id.edtLink);
        btnAdd = dialog.findViewById(R.id.btnAdd);

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

        dialog.show();
    }

    private void uploadJobToFirebase(String job, String company, String link) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        DataClass data = new DataClass(job, company, link, currentDate);

        FirebaseDatabase.getInstance().getReference("Jobs and Internships")
                .child(job)
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
