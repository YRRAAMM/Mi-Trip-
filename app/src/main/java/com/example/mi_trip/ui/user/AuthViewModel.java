package com.example.mi_trip.ui.user;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.mi_trip.models.UserModel;
import com.example.mi_trip.repo.AuthenticationRepository;
import com.google.firebase.auth.FirebaseUser;

public class AuthViewModel extends AndroidViewModel {

    AuthenticationRepository mRepository;
    MutableLiveData<FirebaseUser> mUserData;
    MutableLiveData<Boolean> mLoggedStatus;
    MutableLiveData<UserModel> mUserData_extra;

    public MutableLiveData<UserModel> getUserData_extra() {
        return mUserData_extra;
    }

    public MutableLiveData<FirebaseUser> getUserData() {
        return mUserData;
    }

    public MutableLiveData<Boolean> getLoggedStatus() {
        return mLoggedStatus;
    }

    public AuthViewModel(@NonNull Application application) {
        super(application);
        mRepository = new AuthenticationRepository(application);
        mUserData = mRepository.getFirebaseUserMutableLiveData();
        mLoggedStatus = mRepository.getUserLoggedMutableLiveData();
        mUserData_extra = mRepository.getFirebaseDatabaseMutableLiveData();
    }

    public void register(String email, String pass) {
        mRepository.register(email, pass);
    }

    public void setUserData_extra(UserModel user) {
        mRepository.addUserData(user);
    }

    public void signIn(String email, String pass) {
        mRepository.login(email, pass);
    }
    public void signOut() {
        mRepository.signOut();
    }
}
