package com.example.unizulucardsapplicationsystem;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ApplicantsListAdapter extends RecyclerView.Adapter<ApplicantsListAdapter.ViewHolder> {
    private final Context context;
    private final List<String> userIds;

    public ApplicantsListAdapter(Context context, List<String> userIds) {
        this.context = context;
        this.userIds = userIds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.applicantlistdesign, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String userId = userIds.get(position);

        holder.userIdText.setText(userId);

        holder.viewButton.setOnClickListener(v -> showCardApplicantList(userId));
    }

    @Override
    public int getItemCount() {
        return userIds.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userIdText;
        Button viewButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userIdText = itemView.findViewById(R.id.userId);
            viewButton = itemView.findViewById(R.id.view);
        }
    }

    private void showCardApplicantList(String userId) {
        Intent intent = new Intent(context, CardDataActivity.class);
        intent.putExtra("userId", userId);
        context.startActivity(intent);
    }

}
