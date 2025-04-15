
package com.example.placementproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RecyclyerDoListAdapter extends RecyclerView.Adapter<RecyclyerDoListAdapter.viewHolder> {

    Context context;
    ArrayList<DataClass> arrayList;

    public RecyclyerDoListAdapter(Context context, ArrayList<DataClass> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.do_list, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        DataClass model = arrayList.get(position);
        holder.jobpreference.setText(model.getEdtJobPreference());
        holder.companyname.setText(model.getEdtCompanyName());
        holder.link.setText(model.getEdtLink());

        holder.llRow.setOnClickListener(v -> showUpdateDialog(model, position));
        holder.llRow.setOnLongClickListener(v -> {
            showDeleteConfirmation(model, position);
            return true;
        });
    }

    private void showUpdateDialog(DataClass model, int position) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.add_dialog_box);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText edtJobPreference = dialog.findViewById(R.id.edtJobPreference);
        EditText edtCompanyName = dialog.findViewById(R.id.edtCompanyName);
        EditText edtLink = dialog.findViewById(R.id.edtLink);
        AppCompatButton btnAdd = dialog.findViewById(R.id.btnAdd);
        TextView updatetext = dialog.findViewById(R.id.addtext);

        edtJobPreference.setText(model.getEdtJobPreference());
        edtCompanyName.setText(model.getEdtCompanyName());
        edtLink.setText(model.getEdtLink());
        updatetext.setText("Update Task");
        btnAdd.setText("Update");
        btnAdd.setOnClickListener(v -> {
            String job = edtJobPreference.getText().toString().trim();
            String company = edtCompanyName.getText().toString().trim();
            String link = edtLink.getText().toString().trim();

            if (job.isEmpty() || company.isEmpty() || link.isEmpty()) {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get current date
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            String currentDate = sdf.format(new Date());

            DataClass updatedData = new DataClass(job, company, link, currentDate);

//            FirebaseDatabase.getInstance().getReference("Jobs and Internships")
//                    .child(model.getEdtJobPreference()) // Remove old data
//                    .removeValue();
            String jobKey = FirebaseDatabase.getInstance().getReference("Jobs and Internships").push().getKey();
            FirebaseDatabase.getInstance().getReference("Jobs and Internships")
                    .child(jobKey) // Use the generated unique key
                    .setValue(updatedData);


            FirebaseDatabase.getInstance().getReference("Jobs and Internships")
                    .child(job) // Add updated data
                    .setValue(updatedData)
                    .addOnSuccessListener(unused -> {
                        arrayList.set(position, updatedData);
                        notifyItemChanged(position);
                        Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(context, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });


        dialog.show();
    }

    private void showDeleteConfirmation(DataClass model, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this?")
                .setIcon(R.drawable.baseline_delete_24)
                .setPositiveButton("No", null)
                .setNegativeButton("Yes", (dialog, which) -> {
                    FirebaseDatabase.getInstance().getReference("Jobs and Internships")
                            .child(model.getEdtJobPreference())
                            .removeValue()
                            .addOnSuccessListener(unused -> {
                                arrayList.remove(position);
                                notifyItemRemoved(position);
                                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(context, "Delete failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .show();
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        TextView jobpreference, companyname, link;
        LinearLayout llRow;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            jobpreference = itemView.findViewById(R.id.jobpreference);
            companyname = itemView.findViewById(R.id.companyname);
            link = itemView.findViewById(R.id.link);
            llRow = itemView.findViewById(R.id.llRow);
        }
    }
}

