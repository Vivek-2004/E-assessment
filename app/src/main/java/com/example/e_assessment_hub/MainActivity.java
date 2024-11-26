package com.example.e_assessment_hub;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Check if a user is already logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is logged in, navigate to ExamActivity
            startActivity(new Intent(MainActivity.this, ExamActivity.class));
        } else {
            // No user is logged in, navigate to SignupActivity
            startActivity(new Intent(MainActivity.this, SignupActivity.class));
        }
        // Finish the MainActivity so the user cannot navigate back to it
        finish();
    }
}
