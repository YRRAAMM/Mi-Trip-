package com.example.mi_trip.utils.datePicker;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment{
    Calendar cal = Calendar.getInstance();
    int year = cal.get(Calendar.YEAR);
    int month = cal.get(Calendar.MONTH);
    int day = cal.get(Calendar.DAY_OF_MONTH);
    DatePickerDialog.OnDateSetListener listener;

    public DatePickerFragment (DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        DatePickerDialog dialog = new DatePickerDialog(getActivity(), listener,
                year,month,day);

        dialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());

        try {
            return dialog;
        } catch (ClassCastException cce) {
            throw new RuntimeException("The fragment that uses the DatePickerFragment must implement DatePickerDialog.OnDateSetListener.");
        }
//        return new DatePickerDialog(getActivity(),(DatePickerDialog.OnDateSetListener) getActivity(),year,month,day);
    }

//    @Override
//    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//        Calendar c = Calendar.getInstance();
//        c.set(Calendar.YEAR, year);
//        c.set(Calendar.MONTH, month);
//        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
//        Log.i("Date Time Picker", currentDateString);
//    }
}
