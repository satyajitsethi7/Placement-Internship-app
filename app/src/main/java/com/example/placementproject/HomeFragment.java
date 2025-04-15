package com.example.placementproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment{

    private RecyclerView recyclerView;
    private TrackerRecyclerAdapter trackerRecyclerAdapter;
    private ArrayList<list_of_tracker> arrayListoftracker;
    private DatabaseReference databaseReference;
    private Spinner spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.statuslist);
        spinner = view.findViewById(R.id.filter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        arrayListoftracker = new ArrayList<>();
        trackerRecyclerAdapter = new TrackerRecyclerAdapter(requireContext(), arrayListoftracker);
        recyclerView.setAdapter(trackerRecyclerAdapter);

        // Firebase Reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Jobs and Internships");

        fetchDataFromFirebase();

        // Spinner setup
        ArrayList<String> filterid = new ArrayList<>();
        filterid.add("Latest");
        filterid.add("Internships");
        filterid.add("Jobs");

        ArrayAdapter<String> spinneradapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, filterid);
        spinner.setAdapter(spinneradapter);

        return view;
    }

    private void fetchDataFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayListoftracker.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DataClass data = dataSnapshot.getValue(DataClass.class);

                    if (data != null) {
                        // Format: (company, job, category/status, date)
                        String edtlink = data.getEdtLink();
                        list_of_tracker tracker = new list_of_tracker(
                                data.getEdtCompanyName(),
                                data.getEdtJobPreference(),
                                "Latest", // Placeholder for status - update as needed
                                data.getAppliedDate(), // Make sure DataClass has getDate()
                                edtlink // Add edtlink
                        );
                        arrayListoftracker.add(tracker);
                    }
                }
                trackerRecyclerAdapter.setTask(arrayListoftracker);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error if needed
            }
        });
    }
}

