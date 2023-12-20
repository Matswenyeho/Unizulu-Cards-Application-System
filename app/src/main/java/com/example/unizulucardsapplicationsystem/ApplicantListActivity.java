package com.example.unizulucardsapplicationsystem;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ApplicantListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<String> userIds = new ArrayList();

    FirebaseAuth auth;
    TextView email;
    ApplicantsListAdapter applicantsListAdapter;

    FirebaseFirestore firestore;
    Button print;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicant_list);

        recyclerView = findViewById(R.id.recycleview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        applicantsListAdapter = new ApplicantsListAdapter(this, userIds);
        recyclerView.setAdapter(applicantsListAdapter);

        EventChangeListener();
    }

    private void EventChangeListener() {
        firestore.collection("Applicant").whereEqualTo("userId", auth.getCurrentUser().getUid())
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("Error occurred", error.getMessage());
                        return;
                    }

                    Log.d("Firestore", "Snapshot received with " + value.getDocumentChanges().size() + " changes.");

                    for (DocumentChange dc : value.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            userIds.add(dc.getDocument().getId());
                        }
                    }

                    applicantsListAdapter.notifyDataSetChanged();
                });
    }
}