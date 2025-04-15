
package com.example.placementproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
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
        holder.status.setText(item.getStatus());
        holder.appliedtime.setText("Applied on: "+item.getAppliedtime()); // Binds date


        // Color for status background
        GradientDrawable background = (GradientDrawable) holder.status.getBackground();
        switch (item.getStatus()) {
            case "Latest":
                background.setColor(ContextCompat.getColor(context, R.color.interview));
                break;
            case "Internship":
                background.setColor(ContextCompat.getColor(context, R.color.offer));
                break;
            default:
                background.setColor(ContextCompat.getColor(context, R.color.ourblue));
                break;
        }
//        holder.itemView.setTag(item);

        // Set the link click handler on the text view

        holder.jobstext.setOnClickListener(v -> {
            Log.d("TrackerRecyclerAdapter", "Job Preference clicked at position: " + position);
            String link = item.getEdtlink();  // Get the link from the item

            if (link != null && !link.trim().isEmpty()) {
                // Open the link in the browser
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
        TextView companyname, jobstext, status, appliedtime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            companyname = itemView.findViewById(R.id.companyname);
            jobstext = itemView.findViewById(R.id.jobstext);
            status = itemView.findViewById(R.id.status);
            appliedtime = itemView.findViewById(R.id.appliedtime);

        }
        }
    }

