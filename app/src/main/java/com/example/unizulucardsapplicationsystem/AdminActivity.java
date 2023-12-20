package com.example.unizulucardsapplicationsystem;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {

    ImageView ApplicantList,LostCardList;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        ApplicantList=findViewById(R.id.applicantbtn);
        LostCardList=findViewById(R.id.lostcardbtn);

        ApplicantList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, ApplicantListActivity.class);
                startActivity(intent);

            }
        });
        LostCardList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this,LostCardList.class);
                startActivity(intent);
            }
        });
    }
}