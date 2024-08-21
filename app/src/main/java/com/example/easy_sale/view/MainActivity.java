package com.example.easy_sale.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easy_sale.R;
import com.example.easy_sale.viewModel.UserViewModel;
import com.example.easy_sale.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private UserViewModel userViewModel;
    private UserRecyclerViewAdapter userAdapter;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserRecyclerViewAdapter(this);
        recyclerView.setAdapter(userAdapter);
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

        userAdapter.setItemClickListener(position -> deselectItem(), position -> {
            userAdapter.setSelectedPosition(position);
            fab.setVisibility(View.GONE);
        }, user -> deleteUser(user), user -> openEditUserDialog(user));
    }


    public void deleteUser(User user) {
        new AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete this user?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    userViewModel.deleteUser(user);
                    deselectItem();
                })
                .setNegativeButton("No", null)
                .show();
    }


    private void deselectItem() {
        userAdapter.clearSelection();
        fab.setVisibility(View.VISIBLE);
    }


    private void openEditUserDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_user, null);
        builder.setView(dialogView);

        EditText emailEditText = dialogView.findViewById(R.id.emailEditText);
        EditText firstNameEditText = dialogView.findViewById(R.id.firstNameEditText);
        EditText lastNameEditText = dialogView.findViewById(R.id.lastNameEditText);
        EditText avatarEditText = dialogView.findViewById(R.id.avatarEditText);

        initEditText(user, emailEditText, firstNameEditText, lastNameEditText, avatarEditText);
        ImageButton closeButton = dialogView.findViewById(R.id.closeButton);

        builder.setPositiveButton("Save", null);

        AlertDialog dialog = builder.create();

        closeButton.setOnClickListener(v -> dialog.dismiss());
        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(view -> {
                String newEmail = emailEditText.getText().toString().trim();
                String newFirstName = firstNameEditText.getText().toString().trim();
                String newLastName = lastNameEditText.getText().toString().trim();
                String newAvatar = avatarEditText.getText().toString().trim();

                if (validateInput(newEmail, newFirstName, newLastName, newAvatar)) {
                    user.setEmail(newEmail);
                    user.setFirst_name(newFirstName);
                    user.setLast_name(newLastName);
                    user.setAvatar(newAvatar);
                    userViewModel.updateUser(user);
                    deselectItem();
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }

    private boolean validateInput(String email, String firstName, String lastName, String avatar) {
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Please enter a valid email address");
            return false;
        }
        if (TextUtils.isEmpty(firstName)) {
            showToast("First name cannot be empty");
            return false;
        }
        if (TextUtils.isEmpty(lastName)) {
            showToast("Last name cannot be empty");
            return false;
        }
        if (TextUtils.isEmpty(avatar) || !Patterns.WEB_URL.matcher(avatar).matches()) {
            showToast("Please enter a valid URL for the avatar");
            return false;
        }
        return true;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void initEditText(User user, EditText emailEditText, EditText firstNameEditText, EditText lastNameEditText, EditText avatarEditText) {
        emailEditText.setText(user.getEmail() != null ? user.getEmail() : "");
        firstNameEditText.setText(user.getFirst_name() != null ? user.getFirst_name() : "");
        lastNameEditText.setText(user.getLast_name() != null ? user.getLast_name() : "");
        avatarEditText.setText(user.getAvatar() != null ? user.getAvatar() : "");

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        userViewModel.cleanTask();
    }
}