package com.example.mi_trip.ui.user;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mi_trip.R;
import com.example.mi_trip.databinding.SignUpBinding;
import com.example.mi_trip.pojo.UserModel;
import com.example.mi_trip.ui.MainActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.HashMap;

public class SignUp extends Fragment {

    private SignUpBinding binding;

    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private StorageReference mStorageReferencePicsRef;

    private Uri imageUri;
    private String myUri = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = SignUpBinding.inflate(inflater, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User");
        mStorageReferencePicsRef = FirebaseStorage.getInstance().getReference().child("Profile Pic");

        hideProgressBar();

        binding.profileImageRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        binding.signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.nameRegisterEdt.getText().toString().equals(""))
                    binding.nameRegisterEdt.setError("Please enter your name");
                if (binding.emailRegisterEdt.getText().toString().equals(""))
                    binding.emailRegisterEdt.setError("Please enter your email");
                if (binding.phoneRegisterEdt.getText().toString().equals(""))
                    binding.phoneRegisterEdt.setError("Please enter your Phone number");
                if (binding.passwordRegisterEdt.getText().toString().equals(""))
                    binding.passwordRegisterEdt.setError("Please enter your password");
                if (binding.rePasswordRegisterEdt.getText().toString().equals(""))
                    binding.rePasswordRegisterEdt.setError("Please enter your password again");
                else {
                    if (binding.passwordRegisterEdt.getText().toString().equals(binding.rePasswordRegisterEdt.getText().toString())) {
                        CreateUserAccount(
                                binding.emailRegisterEdt.getText().toString(),
                                binding.passwordRegisterEdt.getText().toString());
                    }
                    else {
                        binding.rePasswordRegisterEdt.setError("passowrd doesn't match");
                    }
                }
            }
        });

        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SignUp.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void CreateUserAccount(String email, String password) {
        showProgressBar();
        // this method create user account with specific email and password
//        mProgressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            showMessage("Account created");
                            addUserData();
                        }
                        else
                        {
                            hideProgressBar();
                            showMessage("account creation failed" + task.getException().getMessage());
                        }
                    }
                });
    }


    private void addUserData() {
        UserModel user = new UserModel(
                binding.nameRegisterEdt.getText().toString(),
                binding.emailRegisterEdt.getText().toString(),
                binding.phoneRegisterEdt.getText().toString());


        mDatabaseReference.child(mAuth.getCurrentUser().getUid())
                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            showMessage("User has been registered successfully!");
                            if (imageUri != null) {
                                uploadProfileImage();
                            } else {

                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            }
//                            mProgressBar.setVisibility(View.GONE);
                        } else {
                            showMessage("Failed to register! try again!");
//                            mProgressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        hideProgressBar();
        if (user != null) {

            Intent mainIntent = new Intent(getContext(), MainActivity.class);
//            mainIntent.putExtra("username", nameField.getText().toString());
            startActivity(mainIntent);
            getActivity().finish();

        } else {
            binding.textViewStatus.setText("Invalid Credentials");
        }
    }

    private void uploadProfileImage() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Set your profile");
        progressDialog.setMessage("Please wait, while we are setting your data ");
        progressDialog.show();

        if (imageUri != null) {
            final StorageReference fileRef = mStorageReferencePicsRef
                    .child("Users")
                    .child(mAuth.getCurrentUser().getUid()+".jpg");
            StorageTask uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUrl = task.getResult();
                        myUri = downloadUrl.toString();

                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("image", myUri);

                        mDatabaseReference
                                .child(mAuth.getCurrentUser().getUid())
                                .updateChildren(userMap);

                        progressDialog.dismiss();

                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    }
                }
            });
        } else {
            progressDialog.dismiss();
            showMessage("Image not selected");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1 && data != null && data.getData() != null) {

            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            imageUri = data.getData();
            binding.profileImageRegister.setImageURI(imageUri);
        } else {
            showMessage("Error, Try again");
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(galleryIntent, 1);
    }

    private void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void hideProgressBar(){
        binding.determinateBar.setVisibility(View.INVISIBLE);

    }
    private void showProgressBar(){
        binding.determinateBar.setVisibility(View.VISIBLE);

    }
}