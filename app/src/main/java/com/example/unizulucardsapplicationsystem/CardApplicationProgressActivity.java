package com.example.unizulucardsapplicationsystem;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CardApplicationProgressActivity extends AppCompatActivity {
    private int CurrentProgress = 0;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private Handler handler = new Handler();
    private boolean confirmButtonClicked = false;

    @SuppressLint("MissingInflatedId")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_application_progress);

        progressBar = findViewById(R.id.progressBar);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();


        if (firebaseUser == null) {
            Toast.makeText(CardApplicationProgressActivity.this, "User details are not available!", Toast.LENGTH_SHORT).show();
        } else {
            showProfileActivity(firebaseUser);
        }

        Intent intent = getIntent();
        if (intent != null && "UPDATE_PROGRESS".equals(intent.getAction())) {
            CurrentProgress = 85;
            progressBar.setProgress(CurrentProgress);
        }
    }

    private void showProfileActivity(FirebaseUser firebaseUser) {
        String userId = firebaseUser.getUid();
        firestore.collection("Applicant").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    runOnUiThread(() -> {
                        CurrentProgress = CurrentProgress + 50;
                        progressBar.setProgress(CurrentProgress);
                        Log.d("ProgressUpdate", "Progress updated");
                    });
                }
            }
        });
    }
}