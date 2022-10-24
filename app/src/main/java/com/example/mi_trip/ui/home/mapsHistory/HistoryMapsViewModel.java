package com.example.mi_trip.ui.home.mapsHistory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HistoryMapsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public HistoryMapsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}