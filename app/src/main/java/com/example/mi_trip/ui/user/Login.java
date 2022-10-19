package com.example.mi_trip.ui.user;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mi_trip.R;
import com.example.mi_trip.databinding.LoginBinding;
import com.example.mi_trip.ui.MainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class Login extends Fragment {


    FirebaseAuth mAuth;
    FirebaseUser currentUser;

//    GoogleSignInClient mGoogleSignInClient;

//    public SignInButton btnGoogle;
//    private static final int RC_SIGN_IN = 9001;

    private LoginBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = LoginBinding.inflate(inflater, container, false);

        hideProgressBar();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null){
            showProgressBar();
            Intent mainIntent = new Intent(getContext(), MainActivity.class);
            startActivity(mainIntent);
            hideProgressBar();
            getActivity().finish();
        }

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.emailRegisterEdt.getText().toString().equals(""))
                    binding.emailRegisterEdt.setError("Enter an email");
                if (binding.passwordLoginEdt.getText().toString().equals(""))
                    binding.passwordLoginEdt.setError("Enter Password");
                if (!binding.emailRegisterEdt.getText().toString().equals("") && !binding.passwordLoginEdt.getText().toString().equals(""))
                    loginUser(binding.emailRegisterEdt.getText().toString(), binding.passwordLoginEdt.getText().toString());
            }
        });

        binding.signupBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(Login.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(Objects.requireNonNull(getActivity()), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateUI(FirebaseUser user) {
        hideProgressBar();
        if (user != null) {

            Intent mainIntent = new Intent(getContext(), MainActivity.class);
            startActivity(mainIntent);
            getActivity().finish();

        } else {
            binding.textViewStatus.setText("Invalid Credentials");
        }
    }

    private void hideProgressBar(){
        binding.determinateBar.setVisibility(View.INVISIBLE);

    }
    private void showProgressBar(){
        binding.determinateBar.setVisibility(View.VISIBLE);

    }
}