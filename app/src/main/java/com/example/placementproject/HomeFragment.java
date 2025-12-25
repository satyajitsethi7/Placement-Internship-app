package com.example.placementproject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.statuslist);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        arrayListoftracker = new ArrayList<>();
        trackerRecyclerAdapter = new TrackerRecyclerAdapter(requireContext(), arrayListoftracker);
        recyclerView.setAdapter(trackerRecyclerAdapter);

        // Firebase Reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Jobs and Internships");

        fetchDataFromFirebase();

        return view;
    }

    private void fetchDataFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayListoftracker.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // Log the raw data to see what Firebase returns
                    Log.d("FirebaseData", "Value: " + dataSnapshot.getValue());
                    
                    DataClass data = dataSnapshot.getValue(DataClass.class);

                    if (data != null) {
                        String edtlink = data.getEdtLink();
                        String edtDate = data.getEdtDate(); // Get the date from Firebase
                        
                        Log.d("FirebaseData", "Fetched Date: " + edtDate);

                        // Format: (company, job, appliedtime, edtlink)
                        list_of_tracker tracker = new list_of_tracker(
                                data.getEdtCompanyName(),
                                data.getEdtJobPreference(),
                                edtDate != null ? edtDate : "", 
                                edtlink
                        );
                        arrayListoftracker.add(tracker);
                    }
                }
                trackerRecyclerAdapter.setTask(arrayListoftracker);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseData", "Error: " + error.getMessage());
            }
        });
    }
}
