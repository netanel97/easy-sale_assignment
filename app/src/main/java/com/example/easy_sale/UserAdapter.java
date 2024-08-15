package com.example.easy_sale;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> users = new ArrayList<>();

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void addUsers(List<User> newUsers) {
        int startPosition = users.size();
        users.addAll(newUsers);
        notifyItemRangeInserted(startPosition, newUsers.size());
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView emailTextView;
        private ImageView avatarImageView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            avatarImageView = itemView.findViewById(R.id.avatarImageView);
        }

        public void bind(User user) {
            nameTextView.setText(user.getFirst_name() + " " + user.getLast_name());
            emailTextView.setText(user.getEmail());
            Glide.with(itemView.getContext())
                    .load(user.getAvatar())
                    .circleCrop()
                    .into(avatarImageView);
        }
    }
}