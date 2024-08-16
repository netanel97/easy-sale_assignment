package com.example.easy_sale;

import android.content.Context;
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

public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.UserViewHolder> {

    private List<User> users;
    private Context context;


    public UserRecyclerViewAdapter(Context context) {
        this.context = context;
        this.users = new ArrayList<>();
    }

    @NonNull
    @Override
    public UserRecyclerViewAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.item_card, parent, false);
        UserViewHolder viewHolder = new UserViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserRecyclerViewAdapter.UserViewHolder holder, int position) {
        User user = getItem(position);
        bind(user, holder);
    }

    private void bind(User user, UserViewHolder holder) {
        holder.nameTextView.setText(user.getFirst_name() + " " + user.getLast_name());
        holder.emailTextView.setText(user.getEmail());
        Glide.with(holder.itemView.getContext())
                .load(user.getAvatar())
                .circleCrop()
                .into(holder.avatarImageView);

    }

    private User getItem(int position) {
        ArrayList<User> users = new ArrayList<>(this.users);
        return users.get(position);
    }

    @Override
    public int getItemCount() {
        return users == null ? 0 : users.size();
    }

    public void addUsers(List<User> newUsers) {
        int startPosition = users.size();
        users.addAll(newUsers);
        notifyItemRangeInserted(startPosition, newUsers.size());
    }


    public class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView emailTextView;
        private ImageView avatarImageView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            avatarImageView = itemView.findViewById(R.id.avatarImageView);
        }
    }

}