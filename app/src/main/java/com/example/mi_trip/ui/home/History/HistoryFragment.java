package com.example.mi_trip.ui.home.History;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mi_trip.databinding.HistoryBinding;
import com.example.mi_trip.models.TripModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {
    // I need to populate the data inside the adapter
    private HistoryBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mTripReference;
    private List<TripModel> tripDetails = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HistoryViewModel historyViewModel =
                new ViewModelProvider(this).get(HistoryViewModel.class);

        binding = HistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mTripReference = FirebaseDatabase.getInstance().getReference().child("trip-mi").child(currentUser.getUid()).child("historytrips");



        RecyclerView rev = binding.historyRV;
        HistoryAdaptor adaptor = new HistoryAdaptor(tripDetails, getActivity());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        // I have no Idea what those are for.
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rev.setLayoutManager(linearLayoutManager);

        rev.setAdapter(adaptor);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Get Post object and use the values to update the UI
                Log.i("DataSnapshot Loop" ,"##" );
                tripDetails.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    tripDetails.add(ds.getValue(TripModel.class));
                }
                adaptor.notifyDataSetChanged();
                // ...
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Getting Post failed, log a message
                Log.w("Database", "loadPost:onCancelled", error.toException());
                // ...
            }
        };

        mTripReference.addValueEventListener(postListener);
//        final TextView textView = binding.textSlideshow;
//        historyViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}