package com.example.mi_trip.utils.alarmManagerReciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.mi_trip.models.TripModel;
import com.example.mi_trip.ui.home.addButtonActivity.AddTripFragment;

public class AlarmEventReciever extends BroadcastReceiver {

    public static final String RECEIVED_TRIP = "RECEIVED_TRIP";
    public static final String RECEIVED_TRIP_SEND_SERIAL = "RECEIVED_TRIP_SEND_SERIAL";

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        //Toast.makeText(context,"service music",Toast.LENGTH_LONG).show();
        Log.i("service ", "serviceAlarmHasFired");
//        Toast.makeText(context.getApplicationContext(), "Alarm Manager just ran", Toast.LENGTH_LONG).show();
//        Intent myService = new Intent(context, MyRingingService.class);
//        context.startService(myService);

        Bundle b = intent.getBundleExtra(AddTripFragment.NEW_TRIP_OBJECT);
        TripModel tm = (TripModel) b.getSerializable(AddTripFragment.NEW_TRIP_OBJ_SERIAL);

        if (tm != null) {
            displayAlert(tm);
            Log.i("OnReceive", "Trip name" + tm.getTripname());
        }
    }

    private void displayAlert(TripModel tm) {
        Intent i = new Intent(context, MyDialogActivity.class);
        Bundle b = new Bundle();
        b.putSerializable(RECEIVED_TRIP_SEND_SERIAL, tm);

        i.putExtra(RECEIVED_TRIP, b);
        i.setClass(context, MyDialogActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
