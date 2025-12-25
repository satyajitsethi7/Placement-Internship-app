package com.example.placementproject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TrackerRecyclerAdapter extends RecyclerView.Adapter<TrackerRecyclerAdapter.ViewHolder> {

    private Context context;
    private ArrayList<list_of_tracker> arrayList;

    public TrackerRecyclerAdapter(Context context, ArrayList<list_of_tracker> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    public void setTask(ArrayList<list_of_tracker> arrayList) {
        this.arrayList = arrayList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.application_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        list_of_tracker item = arrayList.get(position);

        holder.companyname.setText(item.getCompanyname());
        holder.jobstext.setText(item.getJobstext());
        
        // Change the prefix from "Applied on: " to "Listed on: " or similar if needed
        if (item.getAppliedtime() != null && !item.getAppliedtime().isEmpty()) {
            holder.appliedtime.setText("Listed on: " + item.getAppliedtime());
        } else {
            holder.appliedtime.setText("");
        }

        holder.jobstext.setOnClickListener(v -> {
            Log.d("TrackerRecyclerAdapter", "Job Preference clicked at position: " + position);
            String link = item.getEdtlink();

            if (link != null && !link.trim().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                context.startActivity(intent);
            } else {
                Log.d("TrackerRecyclerAdapter", "No link available to apply for the job.");
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView companyname, jobstext, appliedtime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            companyname = itemView.findViewById(R.id.companyname);
            jobstext = itemView.findViewById(R.id.jobstext);
            appliedtime = itemView.findViewById(R.id.appliedtime);
        }
    }
}
