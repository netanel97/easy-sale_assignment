package com.example.easy_sale;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements
        UserRecyclerViewAdapter.OnUserEditClickListener,
        UserRecyclerViewAdapter.OnUserDeleteClickListener,
        UserRecyclerViewAdapter.OnItemLongClickListener,
        UserRecyclerViewAdapter.OnItemClickListener {

    private UserViewModel userViewModel;
    private UserRecyclerViewAdapter userAdapter;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private boolean isItemSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserRecyclerViewAdapter(this, this, this, this, this);
        recyclerView.setAdapter(userAdapter);

        // Setup FAB
        fab = findViewById(R.id.fab);

        // Observe users LiveData
        userViewModel.getUsers().observe(this, users -> {
            if (users != null) {
                userAdapter.setUsers(users);
            }
        });

        // Observe error LiveData
        userViewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });

        // Setup scroll listener for pagination
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0) {
                    userViewModel.loadUsers();
                }
            }
        });

        userViewModel.loadUsers();
    }

    @Override
    public void onUserEditClick(User user) {
        openEditUserDialog(user);
    }

    @Override
    public void onUserDeleteClick(User user) {
        new AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete this user?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    userViewModel.deleteUser();
                    deselectItem();
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onItemLongClick(int position) {
        userAdapter.setSelectedPosition(position);
        isItemSelected = true;
        fab.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(int position) {
        if (isItemSelected) {
            deselectItem();
        }
    }

    private void deselectItem() {
        userAdapter.clearSelection();
        isItemSelected = false;
        fab.setVisibility(View.VISIBLE);
    }


    private void openEditUserDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit User");

        // Inflate the dialog layout
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_user, null);
        builder.setView(view);

        // Find views in the dialog layout
        EditText emailEditText = view.findViewById(R.id.emailEditText);
        EditText firstNameEditText = view.findViewById(R.id.firstNameEditText);
        EditText lastNameEditText = view.findViewById(R.id.lastNameEditText);
        EditText avatarEditText = view.findViewById(R.id.avatarEditText);

        // Set initial values
        emailEditText.setText(user.getEmail() != null ? user.getEmail() : "");
        firstNameEditText.setText(user.getFirst_name() != null ? user.getFirst_name() : "");
        lastNameEditText.setText(user.getLast_name() != null ? user.getLast_name() : "");
        avatarEditText.setText(user.getAvatar() != null ? user.getAvatar() : "");

        // Set up the buttons
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newEmail = emailEditText.getText().toString();
            String newFirstName = firstNameEditText.getText().toString();
            String newLastName = lastNameEditText.getText().toString();
            String newAvatar = avatarEditText.getText().toString();

            user.setEmail(newEmail);
            user.setFirst_name(newFirstName);
            Log.d("pttt", "user" + user);
            user.setLast_name(newLastName);
            user.setAvatar(newAvatar);

            // Call ViewModel to update user
            userViewModel.updateUser(user);
            deselectItem();

        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        // Show the dialog
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userViewModel.cleanTask();
    }
}