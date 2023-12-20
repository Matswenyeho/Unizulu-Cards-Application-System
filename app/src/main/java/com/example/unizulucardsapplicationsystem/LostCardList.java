package com.example.unizulucardsapplicationsystem;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class LostCardList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private final List<String> userIds = new ArrayList<>();
    private LostCardListAdapter lostCardListAdapter;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_card_list);
        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        lostCardListAdapter = new LostCardListAdapter(this, userIds);  // Pass the list of user IDs
        recyclerView.setAdapter(lostCardListAdapter);

        eventChangeListener();
    }

    private void eventChangeListener() {
        firestore.collection("lostcards").addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("Error occurred", error.getMessage());
                if (auth.getCurrentUser() != null) {
                    Log.d("User UID", auth.getCurrentUser().getUid());
                }
                return;
            }

            Log.d("Firestore", "Snapshot received with " + value.getDocumentChanges().size() + " changes.");

            for (DocumentChange dc : value.getDocumentChanges()) {
                if (dc.getType() == DocumentChange.Type.ADDED) {
                    String userId = dc.getDocument().getId();
                    userIds.add(userId);
                }
            }

            lostCardListAdapter.notifyDataSetChanged();
        });
    }
}
