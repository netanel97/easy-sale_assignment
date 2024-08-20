package com.example.easy_sale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private OnUserEditClickListener editClickListener;
    private OnUserDeleteClickListener deleteClickListener;
    private OnItemLongClickListener longClickListener;
    private OnItemClickListener clickListener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public interface OnUserEditClickListener {
        void onUserEditClick(User user);
    }

    public interface OnUserDeleteClickListener {
        void onUserDeleteClick(User user);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public UserRecyclerViewAdapter(Context context, OnUserEditClickListener editListener,
                                   OnUserDeleteClickListener deleteListener,
                                   OnItemLongClickListener longListener,
                                   OnItemClickListener clickListener) {
        this.context = context;
        this.users = new ArrayList<>();
        this.editClickListener = editListener;
        this.deleteClickListener = deleteListener;
        this.longClickListener = longListener;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user, position == selectedPosition);
    }

//    public HomeRecyclerViewAdapter setCategoryItemClickListener(FoodItemClickListener foodItemClickListener) {
//        this.listener = foodItemClickListener;
//        return this;
//    }


    @Override
    public int getItemCount() {
        return users.size();
    }

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    public void setSelectedPosition(int position) {
        int previousSelected = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(previousSelected);
        notifyItemChanged(selectedPosition);
    }

    public void clearSelection() {
        int previousSelected = selectedPosition;
        selectedPosition = RecyclerView.NO_POSITION;
        notifyItemChanged(previousSelected);
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView emailTextView;
        private ImageView avatarImageView;
        private Button editButton;
        private Button deleteButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            avatarImageView = itemView.findViewById(R.id.avatarImageView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    longClickListener.onItemLongClick(position);
                    return true;
                }
                return false;
            });

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    clickListener.onItemClick(position);
                }
            });
        }

        public void bind(User user, boolean isSelected) {
            nameTextView.setText(user.getFirst_name() + " " + user.getLast_name());
            emailTextView.setText(user.getEmail());
            Glide.with(itemView.getContext())
                    .load(user.getAvatar())
                    .circleCrop()
                    .into(avatarImageView);

            editButton.setVisibility(isSelected ? View.VISIBLE : View.GONE);
            deleteButton.setVisibility(isSelected ? View.VISIBLE : View.GONE);

            editButton.setOnClickListener(v -> {
                if (editClickListener != null) {
                    editClickListener.onUserEditClick(user);
                }
            });

            deleteButton.setOnClickListener(v -> {
                if (deleteClickListener != null) {
                    deleteClickListener.onUserDeleteClick(user);
                }
            });
        }
    }
}