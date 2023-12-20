package com.example.unizulucardsapplicationsystem;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LostCardActivity extends AppCompatActivity {

    private EditText nameEditText, reasonEditText, dateEditText, regnumberEditText;
    private ProgressBar progressBar;
    private Button submitbtn;
    private FirebaseAuth auth;
    private byte[] signatureBytes;
    private FirebaseFirestore firestore;
    private String Name, Reason, Regnumber;
    private Date reportDate;
    private String userId;
    private SignaturePad signaturePad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_card);
        auth = FirebaseAuth.getInstance();

        nameEditText = findViewById(R.id.editTextname);
        reasonEditText = findViewById(R.id.reason);
        dateEditText = findViewById(R.id.date);
        regnumberEditText = findViewById(R.id.regnumber);
        progressBar = findViewById(R.id.progressBar);
        submitbtn = findViewById(R.id.submitbtn);
        signaturePad = findViewById(R.id.signature);

        firestore = FirebaseFirestore.getInstance();



        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                Name = nameEditText.getText().toString().trim();
                Reason = reasonEditText.getText().toString().trim();
                Regnumber = regnumberEditText.getText().toString().trim();
                Log.d("Debug", "Name: " + Name);
                Log.d("Debug", "Reason: " + Reason);
                Log.d("Debug", "Regnumber: " + Regnumber);
                Log.d("Debug", "Signature Bytes Length: " + (signatureBytes != null ? signatureBytes.length : 0));

                Bitmap signatureBitmap = signaturePad.getSignatureBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                signatureBytes = baos.toByteArray();

                if (Name.isEmpty() || Reason.isEmpty() || Regnumber.isEmpty()) {
                    Toast.makeText(LostCardActivity.this, "Please enter all required fields", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                } else {
                    String dateInput = dateEditText.getText().toString().trim();
                    if (dateInput.isEmpty()) {
                        Toast.makeText(LostCardActivity.this, "Please enter a valid date", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    } else {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        try {
                            reportDate = simpleDateFormat.parse(dateInput);
                            submitReport();
                        } catch (ParseException e) {
                            Toast.makeText(LostCardActivity.this, "Please enter a valid date", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
    }

    private void submitReport() {
        String userId = auth.getCurrentUser().getUid();
        CollectionReference collectionReference = firestore.collection("lostcards");

        collectionReference.whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(LostCardActivity.this, "You have already reported a lost card", Toast.LENGTH_SHORT).show();
                        } else {
                            DocumentReference documentReference = collectionReference.document();
                            Map<String, Object> reportData = new HashMap<>();
                            reportData.put("name", Name);
                            reportData.put("reason", Reason);
                            reportData.put("regnumber", Regnumber);
                            reportData.put("signatureImage", Base64.encodeToString(signatureBytes, Base64.DEFAULT));
                            reportData.put("reportDate", reportDate);
                            reportData.put("userId", userId);

                            documentReference.set(reportData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(LostCardActivity.this, "Report submitted successfully", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(LostCardActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
    }
}