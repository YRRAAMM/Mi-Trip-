package com.example.mi_trip.utils.timePicker;
//
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.mi_trip.ui.home.addButtonActivity.AddTripFragment;

import java.util.Calendar;

public  class TimePickerFragment extends DialogFragment {

    Calendar c =Calendar.getInstance();
    int hour = c.get(Calendar.HOUR_OF_DAY);
    int min = c.get(Calendar.MINUTE);
    TimePickerDialog.OnTimeSetListener mListener;

    public TimePickerFragment(AddTripFragment listener) {
        this.mListener = listener;
    }

    @NonNull
   @Override
   public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
       //default current time shown when appearing
       try {

           return new TimePickerDialog(getActivity(), mListener,hour,min, DateFormat.is24HourFormat(getActivity()));
       } catch (ClassCastException cce) {
           throw new RuntimeException("The fragment that uses the TimePickerFragment must implement TimePickerDialog.OnTimeSetListener.");
       }
   }
}
