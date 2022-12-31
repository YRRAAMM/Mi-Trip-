package com.example.mi_trip.ui.home.addButtonActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mi_trip.R;
import com.example.mi_trip.databinding.AddTripBinding;
import com.example.mi_trip.models.NotesModel;
import com.example.mi_trip.models.TripModel;
import com.example.mi_trip.repo.remote.network.FirebaseDB;
import com.example.mi_trip.utils.alarmManagerReciever.AlarmEventReciever;
import com.example.mi_trip.utils.datePicker.DatePickerFragment;
import com.example.mi_trip.utils.timePicker.TimePickerFragment;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddTripFragment extends Fragment implements
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener,
        View.OnClickListener {

    public static final String NEW_TRIP_OBJECT = "NEW_TRIP_OBJECT";
    public static final String NEW_TRIP_OBJ_SERIAL = "NEW_TRIP_OBJECT";

    private AddTripBinding binding;

    FirebaseDB fbdb;

    PlacesClient mPlacesClient;
    List<Place.Field> placeField = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);

    int hour;
    int minutes;
    int increasedID = 0;

    ArrayAdapter<CharSequence> adapterTripDirectionSpin;
    ArrayAdapter<CharSequence> adapterTripRepeatSpin;

    List<TextInputLayout> mNotesTextInputLayout = new ArrayList<>();
    List<String> notesList = new ArrayList<>();

    String selectedStartPlace = "";
    String selectedEndPlace = "";

    AlarmManager alarmManager;
    PendingIntent pendingIntent;

    Calendar mCalendar;
    ProgressBar progressBar;
    private EditText mTripNameEdt;
    private EditText mDateTextField;
    private TextInputEditText mTimeTextField;

    private void spinnerInit() {
        //Trip Direction Spinner
        adapterTripDirectionSpin = ArrayAdapter.createFromResource(requireActivity(),
                R.array.trip_direction_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterTripDirectionSpin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        binding.tripWaySpinner.setAdapter(adapterTripDirectionSpin);
        binding.tripWaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                Log.i("Spinner", adapterView.getItemAtPosition(pos).toString() + "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Trip Repeat Spinner
        adapterTripRepeatSpin = ArrayAdapter.createFromResource(requireActivity(),
                R.array.times_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterTripRepeatSpin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        binding.repeatSpinner.setAdapter(adapterTripRepeatSpin);
        binding.repeatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                Log.i("Spinner", adapterView.getItemAtPosition(pos).toString() + "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setUpAutoComplete() {
        AutocompleteSupportFragment placeStartPointAutoComplete;
        AutocompleteSupportFragment placeDestPointAutoComplete;

        if (!Places.isInitialized()) {
            // @TODO Get Places API key
            Places.initialize(requireContext(), "AIzaSyDbQxlvW4q0t1rhRHicRHVDzWbDP8y1Hlc");
        }
        //Init Frags
        placeStartPointAutoComplete = (AutocompleteSupportFragment)
                requireActivity().getSupportFragmentManager().findFragmentById(R.id.start_autoComplete_Frag);
        assert placeStartPointAutoComplete != null;
        placeStartPointAutoComplete.setPlaceFields(placeField);

        placeDestPointAutoComplete = (AutocompleteSupportFragment)
                requireActivity().getSupportFragmentManager().findFragmentById(R.id.dest_autoComplete_Frag);
        assert placeDestPointAutoComplete != null;
        placeDestPointAutoComplete.setPlaceFields(placeField);

        placeStartPointAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                Log.i("Places", "Place: " + place.getAddress() + ", " + place.getId());
                selectedStartPlace = place.getAddress();
            }

            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i("Places", "An error occurred: " + status);
            }
        });
        placeDestPointAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                Log.i("Places", "Place: " + place.getAddress() + ", " + place.getId());
                selectedEndPlace = place.getAddress();
            }

            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i("Places", "An error occurred: " + status);
            }
        });
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = AddTripBinding.inflate(inflater, container, false);



        hideProgressBar();

        //Auto Complete Google
//        setUpAutoComplete();
        // set Location Data
        locationData();

        //Spinner init
        spinnerInit();

        // add first Note to mNotesTextInputLayout !


        return binding.getRoot();


    }

    private void locationData() {
        selectedStartPlace = binding.startAutoCompleteFrag.getText().toString();
        selectedEndPlace = binding.destAutoCompleteFrag.getText().toString();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCalendar = Calendar.getInstance();
        fbdb = FirebaseDB.getInstance();

        binding.buttonFirst.setOnClickListener(view1 -> NavHostFragment.findNavController(AddTripFragment.this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment));

        binding.addTripBtn.setOnClickListener(view1 -> {
            showProgressBar();
            //@TODO Copy this to another place !
//            for (TextInputLayout txtLayout : mNotesTextInputLayout) {
//                Log.i("Notes List", txtLayout.getEditText().getText().toString());
//                notesList.add(txtLayout.getEditText().getText().toString());
//            }
            mTripNameEdt = binding.tripNameEdt;
            mDateTextField = binding.dateTextField;
            mTimeTextField = binding.timeTextField;
            if (mTripNameEdt.getText().toString().equals("")) {
                mTripNameEdt.setError("Cannot be blank!");
            } else {
                if (mDateTextField.getText().toString().equals("")) {
                    mDateTextField.setError("Cannot be blank!");
                } else {
                    if (mTimeTextField.getText().toString().equals("")) {
                        mTimeTextField.setError("Cannot be blank!");
                    } else {
                        Intent notes = new Intent();
                        NotesModel notesModel = notes.getParcelableExtra("notes");
                        Bundle bundle = new Bundle();
                        bundle.getParcelable("notes");
                        TripModel newTrip = new TripModel(selectedStartPlace, selectedEndPlace, mDateTextField.getText().toString(),
                                mTimeTextField.getText().toString(), mTripNameEdt.getText().toString(), null, notesModel.getNotes(), mCalendar.getTime().toString());

                        fbdb.saveTripToDatabase(newTrip);
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("NEWTRIP", newTrip);
                        startAlarm(newTrip);
                        requireActivity().setResult(Activity.RESULT_OK, resultIntent);
                        Toast.makeText(requireActivity(), "Added Successfully", Toast.LENGTH_SHORT).show();
                        requireActivity().finish();
                    }
                }
            }
        });

        binding.dateTextField.setOnClickListener(view1 -> {
            showDatePickerDialog();

        });

        binding.timeTextField.setOnClickListener(view1 -> showTimePickerDialog());
    }

    private void showDatePickerDialog() {
        DialogFragment datepicker = new DatePickerFragment(AddTripFragment.this);

        datepicker.show(requireActivity().getSupportFragmentManager(), "date");
    }

    private void showTimePickerDialog() {
        DialogFragment timepicker = new TimePickerFragment(AddTripFragment.this);
        timepicker.show(requireActivity().getSupportFragmentManager(), "time");
    }

    private void startAlarm(TripModel tripModel) {
        alarmManager = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);
//        Log.i("time", mCalendar.getTime().toString());
//        long alarmTime = mCalendar.getTimeInMillis();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        try {
            cal.setTime(sdf.parse(tripModel.dateTime));// all done
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(requireActivity(), AlarmEventReciever.class);
        intent.putExtra(NEW_TRIP_OBJECT, tripModel);

        Bundle b = new Bundle();
        b.putSerializable(AddTripFragment.NEW_TRIP_OBJ_SERIAL, tripModel);
        intent.putExtra(NEW_TRIP_OBJECT, b);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireActivity(), 0, intent, 0);
        alarmManager.set(AlarmManager.RTC, cal.getTimeInMillis(), pendingIntent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void hideProgressBar(){
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    private void cancelAlarm() {
        alarmManager.cancel(pendingIntent);
        Toast.makeText(requireActivity(), "Alarm Cancelled", Toast.LENGTH_LONG).show();
    }

    private void showProgressBar(){
        binding.progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        Log.i("Date Time Picker", currentDateString);
        binding.dateTextField.setText(currentDateString);
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month); // Month is zero-based
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        hour = hourOfDay;
        minutes = minute;
        String timeSet = "";
        if (hour > 12) {
            hour -= 12;
            timeSet = "PM";
        } else if (hour == 0) {
            hour += 12;
            timeSet = "AM";
        } else if (hour == 12) {
            timeSet = "PM";
        } else {
            timeSet = "AM";
        }

        String min = "";
        if (minutes < 10)
            min = "0" + minutes;
        else
            min = String.valueOf(minutes);

        // Append in a StringBuilder
        String aTime = new StringBuilder().append(hour).append(':')
                .append(min).append(" ").append(timeSet).toString();
        binding.timeTextField.setText(aTime);

        // Set calendat item
        mCalendar = Calendar.getInstance();


        mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        mCalendar.set(Calendar.MINUTE, minute);
        mCalendar.set(Calendar.SECOND, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // write it one after the other ....
        }
    }
}