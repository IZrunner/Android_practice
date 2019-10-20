package com.example.notes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class AuthenticationActivity extends AppCompatActivity {
    private Button mSignInButton, mSignOutButton;
    private String mEmail, mPassword;
    private static final String TAG = "AuthenticationActivity";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        mAuth = FirebaseAuth.getInstance();

        mSignInButton = findViewById(R.id.signInButton);
        mSignOutButton = findViewById(R.id.signOutButton);
        mEmail = findViewById(R.id.emailText).toString();
        mPassword = findViewById(R.id.passwordText).toString();

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mEmail.equals("") && !mPassword.equals("")) {
                    mAuth.signInWithEmailAndPassword(mEmail, mPassword);
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        updateUI(user);
                        Toast.makeText(AuthenticationActivity.this, "User verified, " + user.getUid(),
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                else {
                    Toast.makeText(AuthenticationActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    updateUI(null);
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
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        Toast.makeText(AuthenticationActivity.this, "User is already logged in, " + currentUser.getUid(),
                Toast.LENGTH_SHORT).show();
        finish();
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