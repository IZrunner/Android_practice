package com.example.notes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class AuthenticationActivity extends AppCompatActivity {
    private Button mSignInButton, mSignOutButton;
    private EditText mEmail, mPassword;
    private static final String TAG = "AuthenticationActivity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        mSignInButton = findViewById(R.id.signInButton);
        mSignOutButton = findViewById(R.id.signOutButton);
        mEmail = findViewById(R.id.emailText);
        mPassword = findViewById(R.id.passwordText);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Toast.makeText(AuthenticationActivity.this, "Successfully signed in with: " + user.getEmail(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Toast.makeText(AuthenticationActivity.this, "Successfully signed out.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();
                String pass = mPassword.getText().toString();
                if (!email.equals("") && !pass.equals("")) {
                    mAuth.signInWithEmailAndPassword(email, pass);
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        Intent k = new Intent(AuthenticationActivity.this, MainActivity.class);
                        startActivity(k);
                        Toast.makeText(AuthenticationActivity.this, "User verified, " + user.getUid(),
                                Toast.LENGTH_SHORT).show();
                        //finish();
                    }
                }
                else {
                    Toast.makeText(AuthenticationActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    //updateUI(null);
                }
            }
        });

        mSignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Toast.makeText(AuthenticationActivity.this, "User is already logged in, " + currentUser.getUid(),
                    Toast.LENGTH_SHORT).show();
            updateUI(currentUser);
        }
        //finish();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent k = new Intent(AuthenticationActivity.this, MainActivity.class);
            startActivity(k);
            finish();
        } else {
            Log.d(TAG, "Task failed successfully");
        }
    }
}