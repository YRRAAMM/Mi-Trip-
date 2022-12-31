package com.example.mi_trip.ui.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mi_trip.R;
import com.example.mi_trip.databinding.ActivityUpcomingTripsBinding;
import com.example.mi_trip.repo.remote.network.FirebaseDB;
import com.example.mi_trip.ui.home.addButtonActivity.AddButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class UpcomingTripsActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityUpcomingTripsBinding binding;
    android.app.AlertDialog alert;
    FirebaseDB fbdb;
    private ActivityResultLauncher<Intent> mStartActivityIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUpcomingTripsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarUpcomingTrips.toolbar);

        Intent i = getIntent();

        // Overlay Permission
//        checkPermission();

        fbdb = FirebaseDB.getInstance();

        mStartActivityIntent = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Add same code that you want to add in onActivityResult method
                    if (result.getResultCode() == RESULT_OK) {
                        if (checkPermission()) {

                        } else {
                            reqPermission();
                        }
                    }
                });

        binding.appBarUpcomingTrips.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                startActivity(new Intent(UpcomingTripsActivity.this, AddButton.class));
//                mStartActivityIntent.launch(i);
            }
        });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_history, R.id.nav_maps_history)
                .setOpenableLayout(drawer)
                .build();


        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_upcoming_trips);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        
//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                if (item.getItemId() == R.id.nav_logout) {
//                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//                    DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
//                    connectedRef.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            boolean connected = snapshot.getValue(Boolean.class);
//
//                            if (connected) {
//                                Toast.makeText(UpcomingTripsActivity.this, "You are Connected and Data Updated", Toast.LENGTH_SHORT).show();
//
//                            } else {
//                                Toast.makeText(UpcomingTripsActivity.this, "Please check your connection", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//                        }
//                    });
//
//                    return true;
//                } else if (item.getItemId() == R.id.nav_home) {
//
//                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//                    return true;
//                }else if (item.getItemId() == R.id.nav_history) {
//
//                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//                    return true;
//                }else if (item.getItemId() == R.id.nav_maps_history) {
//
//                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//                    return true;
//                }
//
//
//                return true;
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        binding.appBarUpcomingTrips.fab.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.upcoming_trips, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_upcoming_trips);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                reqPermission();
                return false;
            }
            else {
                return true;
            }
        }else{
            return true;
        }

    }

    private void reqPermission(){
        final android.app.AlertDialog.Builder alertBuilder = new android.app.AlertDialog.Builder(this);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Screen overlay detected");
        alertBuilder.setMessage("Enable 'Draw over other apps' in your system setting.");
        alertBuilder.setPositiveButton("OPEN SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                mStartActivityIntent.launch(intent);
            }
        });
        alert = alertBuilder.create();
        alert.show();
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case (55): {
//                if (resultCode == Activity.RESULT_OK) {
//                    TripModel newtrip = (TripModel) data.getSerializableExtra("NEWTRIP");
//                    if (newtrip != null) {
//                        fbdb.saveTripToDatabase(newtrip);
//                    } else {
//                        //Toast.makeText(this, "Something wend wrong", Toast.LENGTH_SHORT).show();
//                    }
//                }
//                break;
//            }
//            case  RESULT_OK: {
//                if (checkPermission()) {
//
//                } else {
//                    reqPermission();
//                }
//            }
//        }
//    }
}