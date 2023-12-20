package com.example.unizulucardsapplicationsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class VirtualCardBackActivity extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_card_back);
        imageView = findViewById(R.id.backcard);


        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(VirtualCardBackActivity.this, VirtualCardActivity.class);
            startActivity(intent);

        });
    }
}
