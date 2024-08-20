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

public class MainActivity extends AppCompatActivity implements UserRecyclerViewAdapter.OnUserEditClickListener {
    private UserViewModel userViewModel;
    private UserRecyclerViewAdapter userAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserRecyclerViewAdapter(this, this);
        recyclerView.setAdapter(userAdapter);

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
            Log.d("pttt","user" + user);
            user.setLast_name(newLastName);
            user.setAvatar(newAvatar);

            // Call ViewModel to update user
            userViewModel.updateUser(user);
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