package com.example.unizulucardsapplicationsystem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;

public class VirtualCardActivity extends AppCompatActivity {
    TextView Surname, Tittle, Identity, Initials, Studentno, Res, Year, DegreeType;
    private String surname, tittle, identity, initials, studentno,passport,employeeno, res, year, degreetype,isStudent,isEmployee;
    ImageView imageView, VirtualPhoto;

    FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_card);
        Tittle = findViewById(R.id.display_tittle);
        Surname = findViewById(R.id.display_surname);
        Identity = findViewById(R.id.display_identity);
        Initials = findViewById(R.id.display_initials);
        Studentno = findViewById(R.id.display_studentno);
        Res = findViewById(R.id.display_res);
        DegreeType = findViewById(R.id.display_degreetype);
        Year = findViewById(R.id.display_year);
        imageView = findViewById(R.id.virtual_card_image);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(VirtualCardActivity.this, "User details are not available!", Toast.LENGTH_SHORT).show();
        } else {
            showProfileActivity(firebaseUser);
        }
    }

    private void showProfileActivity(FirebaseUser firebaseUser) {
        String userId = firebaseUser.getUid();
        firestore.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {

                    surname = document.getString("surname");
                    tittle = document.getString("tittle");
                    identity = document.getString("Identity Number");
                    initials = document.getString("initials");
                    studentno = document.getString("studentno");
                    passport = document.getString("Passport Number");
                    employeeno = document.getString("employeeno");
                    res = document.getString("res");
                    degreetype = document.getString("DegreeType");
                    isStudent = document.getString("isStudent");
                    isEmployee = document.getString("isEmployee");
                    year = document.getString("year");
                    String base64Image = document.getString("profilePhoto");


                    if (identity != null && identity.equals(identity)) {
                        Identity.setText("ID NO: " + identity);

                    } else if (Identity != null && passport.equals(passport)) {
                        Identity.setText("Passport NO: " + passport);
                    }


                    if (isStudent != null && isStudent.equals("isStudent")) {
                        Studentno.setText("Student No: " + studentno);
                        Initials.setText(initials);
                        Surname.setText(surname);
                        Tittle.setText(tittle);
                        Year.setText("Student " + year);

                        if (degreetype.equals("Post Grad")) {
                            DegreeType.setText(degreetype);
                        }

                        if (!"Off_campus".equals(res)) {
                            Res.setText(res);
                        }



                    } else if (isEmployee != null && isEmployee.equals("isEmployee")) {
                        Studentno.setText("Employee No: " + employeeno);
                        Initials.setText(initials);
                        Surname.setText(surname);
                        Tittle.setText(tittle);
                        Year.setText("Employee " + year);
                    }

                    if (base64Image != null) {
                        byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        imageView.setImageBitmap(bitmap);

                    }


                }
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(VirtualCardActivity.this, VirtualCardBackActivity.class);
                        startActivity(intent);
                    }
                });

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.virtualcardmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.cardprogress) {
            Intent intent = new Intent(VirtualCardActivity.this, CardApplicationProgressActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.lostcard) {
            Intent intent = new Intent(VirtualCardActivity.this, LostCardActivity.class);
            startActivity(intent);
            return true;
        }else if (id == R.id.cardapplication) {
            Intent intent = new Intent(VirtualCardActivity.this, CardApplicationActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.logout) {
            auth.signOut();
            Toast.makeText(VirtualCardActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(VirtualCardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        } else {
            Toast.makeText(VirtualCardActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}

