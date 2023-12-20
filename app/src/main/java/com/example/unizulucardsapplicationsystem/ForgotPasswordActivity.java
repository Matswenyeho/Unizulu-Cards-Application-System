package com.example.unizulucardsapplicationsystem;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ForgotPasswordActivity extends AppCompatActivity {

    Button resetpswdbtn;
    EditText editTextemail;
    FirebaseAuth auth;
    ProgressBar progressBar;
    FirebaseFirestore fstore;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        resetpswdbtn = findViewById(R.id.resetpswdbtn);
        editTextemail = findViewById(R.id.editTextemail);
        progressBar=findViewById(R.id.progressBar);
        fstore = FirebaseFirestore.getInstance();

        resetpswdbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email = editTextemail.getText().toString();
                if (Email.isEmpty()) {
                    editTextemail.setError("Enter an email");
                    editTextemail.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
                    Toast.makeText(ForgotPasswordActivity.this, "re_enter your Email_address ", Toast.LENGTH_SHORT).show();
                    editTextemail.setError("Enter the valid email address");
                    editTextemail.requestFocus();
                } else {

                    progressBar.setVisibility(View.GONE);
                    resetPassword(Email);
                }

            }

            private void resetPassword(String Email) {
                auth = FirebaseAuth.getInstance();
                auth.sendPasswordResetEmail(Email).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this, "reset your password with the link sent to your email!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ForgotPasswordActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                    } else {

                        Toast.makeText(ForgotPasswordActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();


                    }
                    progressBar.setVisibility(View.GONE);
                });
            }
        });


    }
}