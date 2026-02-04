package com.nsoft.nchat;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.MyViewHolder> {
    private Activity activity;
    private List<UserModelClass> usersList;

    public UsersListAdapter(Activity activity, List<UserModelClass> usersList) {
        this.activity = activity;
        this.usersList = usersList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView profileTextView, userNameTextView, userBioTextView, statusTextView;
        View statusView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profileTextView = itemView.findViewById(R.id.profileTextView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            userBioTextView = itemView.findViewById(R.id.userBioTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            statusView = itemView.findViewById(R.id.statusView);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View myView = activity.getLayoutInflater().inflate(R.layout.user_list_layout, parent, false);
        return new MyViewHolder(myView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        UserModelClass person = usersList.get(position);

        holder.profileTextView.setText("" + (position + 1));
        holder.userNameTextView.setText(person.getName());
        holder.userBioTextView.setText(person.getBio());
        holder.statusTextView.setText(person.getOnline_status());

        if (person.getOnline_status().equals("online")) {
            holder.statusView.setBackgroundTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.dark_green)));
        } else {
            holder.statusView.setBackgroundTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.dark_red)));
        }

        holder.itemView.setOnClickListener(v -> {

            Intent myIntent = new Intent(activity, ChatActivity.class);
            myIntent.putExtra("receiver_name", person.getName());
            myIntent.putExtra("receiver_user_id", person.getUser_id());

            activity.startActivity(myIntent);
        });
    }

    @Override
    public int getItemCount() {
        if (usersList != null && usersList.size() > 0) {
            return usersList.size();
        }
        return 0;
    }

}
