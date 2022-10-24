package com.example.mi_trip.repo;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.mi_trip.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class AuthenticationRepository {

    private Application mApplication;
    private MutableLiveData<FirebaseUser> mFirebaseUserMultibleLiveData;
    private MutableLiveData<UserModel> mFirebaseDatabaseMutableLiveData;
    private MutableLiveData<FirebaseStorage> mFirebaseStorageMutableLiveData;
    private MutableLiveData<Boolean> mUserLoggedMutableLiveData;
    private FirebaseAuth mAuth;
    private static AuthenticationRepository singleton;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private StorageReference mStorageReferencePicsRef;
    private FirebaseStorage mFirebaseStorage;

    public MutableLiveData<UserModel> getFirebaseDatabaseMutableLiveData() {
        return mFirebaseDatabaseMutableLiveData;
    }

    public MutableLiveData<FirebaseStorage> getFirebaseStorageMutableLiveData() {
        return mFirebaseStorageMutableLiveData;
    }

    public MutableLiveData<FirebaseUser> getFirebaseUserMutableLiveData() {
        return mFirebaseUserMultibleLiveData;
    }

    public MutableLiveData<Boolean> getUserLoggedMutableLiveData() {
        return mUserLoggedMutableLiveData;
    }

    public AuthenticationRepository(Application application) {
        mApplication = application;
        mFirebaseUserMultibleLiveData = new MutableLiveData<>();
        mUserLoggedMutableLiveData = new MutableLiveData<>();
        mFirebaseDatabaseMutableLiveData = new MutableLiveData<>();
        mFirebaseStorageMutableLiveData = new MutableLiveData<>();

        mAuth = FirebaseAuth.getInstance();
//        myRef = database.getReference().child("User");
//        mStorageReferencePicsRef = mFirebaseStorage.getReference().child("Profile Pic");

        if (mAuth.getCurrentUser() != null) {
            mFirebaseUserMultibleLiveData.postValue(mAuth.getCurrentUser());
        }
    }

    public void register (String email, String pass) {

        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                getFirebaseUserMutableLiveData().postValue(mAuth.getCurrentUser());
            } else {
                Toast.makeText(mApplication, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void login(String email , String pass){
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                mFirebaseUserMultibleLiveData.postValue(mAuth.getCurrentUser());
            }else{
                Toast.makeText(mApplication, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void signOut(){
        mAuth.signOut();
        mUserLoggedMutableLiveData.postValue(true);
    }

    // user rest of the data
    public void addUserData(UserModel user) {
        myRef.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mFirebaseDatabaseMutableLiveData.postValue(user);
                        } else {
                            Toast.makeText(mApplication, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    // the images


}
