package com.example.mi_trip.repo.remote.network;

import android.util.Log;

import com.example.mi_trip.models.NotesModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.example.mi_trip.models.TripModel;

public class FirebaseDB {

    DatabaseReference myRef;
    FirebaseDatabase database;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private static FirebaseDB singleton;


    private FirebaseDB() {

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        //myRef = database.getReference(currentUser.getUid());
        myRef = database.getReference();


    }

    public static FirebaseDB getInstance() {
        if (singleton == null) {
            synchronized (FirebaseDB.class) {
                if (singleton == null) {
                    singleton = new FirebaseDB();
                }
            }
            return singleton;
        } else {
            return singleton;
        }
    }

    public void saveTripToDatabase(TripModel cvm) {

        myRef.child("trip-mi").child(currentUser.getUid()).child("upcomingtrips").child(myRef.push().getKey()).setValue(cvm);
        Log.i("Firebase Database", "I'm fired :)");

    }

    public void saveNotesToDatabase(NotesModel notes) {
        myRef.child("trip-mi").child(currentUser.getUid()).child("upcomingtrips").child(myRef.push().getKey()).setValue(notes);
        Log.i("Firebase Database", "I'm fired :)");
    }

    // Use it when you see the data directly.
    public void removeTripFromHome(TripModel tm){
        Query tripsQuery = myRef.child("trip-mi").child(currentUser.getUid()).child("upcomingtrips").orderByChild("tripname").equalTo(tm.getTripname());

        tripsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                    Log.i("DELETE_FROM_DB","DELETE_FROM_DB");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FBDB", "onCancelled", databaseError.toException());
            }
        });
    }

//    public void saveUserToFirebase(String email, String name) {
//
//        myRef.child("trip-mi").child(currentUser.getUid()).child("userinfo").child("email").setValue(email);
//        myRef.child("trip-mi").child(currentUser.getUid()).child("userinfo").child("name").setValue(name);
//
//    }

    public void removeFromUpcoming(TripModel tm){
        Query tripsQuery = myRef.child("trip-mi").child(currentUser.getUid()).child("upcomingtrips").orderByChild("tripname").equalTo(tm.getTripname());

        tripsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                    Log.i("DELETE_FROM_DB","DELETE_FROM_DB");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FBDB", "onCancelled", databaseError.toException());
            }
        });
    }

    public void addTripToHistory(TripModel tm){
        myRef.child("trip-mi").child(currentUser.getUid()).child("historytrips").child(myRef.push().getKey()).setValue(tm);
    }
    public void removeTripFromHistory(TripModel tm){
        Query tripsQuery = myRef.child("trip-mi").child(currentUser.getUid()).child("historytrips").orderByChild("tripname").equalTo(tm.getTripname());

        tripsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                    Log.i("DELETE_FROM_DB","DELETE_FROM_DB");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FBDB", "onCancelled", databaseError.toException());
            }
        });
    }
}
