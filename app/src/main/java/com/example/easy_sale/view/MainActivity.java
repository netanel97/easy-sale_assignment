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
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    private UserViewModel userViewModel;
    private UserRecyclerViewAdapter userAdapter;
    private RecyclerView recyclerView;
    private FloatingActionButton add_user_fab;
    private View bottomActionBar;
    private ImageButton actionEdit;
    private ImageButton actionDelete;
    private User selectedUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initButtons();
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserRecyclerViewAdapter(this);
        recyclerView.setAdapter(userAdapter);
        bottomActionBar.setOnClickListener(v -> {
        });
        // Observe users LiveData
        userViewModel.getUsers().observe(this, users -> {
            if (users != null) {
                userAdapter.setUsers(users);
            }
        });
        userViewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                showErrorDialog(error);
                userViewModel.clearError();
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
        add_user_fab.setOnClickListener(view -> openUserDialog(null));

        userAdapter.setOnItemLongClickListener(this::onItemLongClick);
        userAdapter.setOnItemClickListener(position -> deselectItem());


        actionEdit.setOnClickListener(v -> {
            if (selectedUser != null) {
                openUserDialog(selectedUser);
            }
        });

        actionDelete.setOnClickListener(v -> {
            if (selectedUser != null) {
                deleteUser(selectedUser);
            }
        });
    }
    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void onItemLongClick(int position) {
        selectedUser = userAdapter.getUser(position);
        bottomActionBar.setVisibility(View.VISIBLE);
        add_user_fab.setVisibility(View.GONE);
    }

    private void initButtons() {
        bottomActionBar = findViewById(R.id.bottomActionBar);
        recyclerView = findViewById(R.id.recyclerView);
        add_user_fab = findViewById(R.id.add_user_fab);
        actionDelete = findViewById(R.id.actionDelete);
        actionEdit = findViewById(R.id.actionEdit);
    }

    private void deselectItem() {
        selectedUser = null;
        bottomActionBar.setVisibility(View.GONE);
        add_user_fab.setVisibility(View.VISIBLE);
        userAdapter.clearSelection();
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

    private void openUserDialog(User user) {
        boolean isEditing = user != null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_user, null);
        builder.setView(dialogView);

        TextInputLayout emailInputLayout = dialogView.findViewById(R.id.emailInputLayout);
        TextInputLayout firstNameInputLayout = dialogView.findViewById(R.id.firstNameInputLayout);
        TextInputLayout lastNameInputLayout = dialogView.findViewById(R.id.lastNameInputLayout);
        TextInputLayout avatarInputLayout = dialogView.findViewById(R.id.avatarInputLayout);

        if (isEditing) {
            initInputLayouts(user, emailInputLayout, firstNameInputLayout, lastNameInputLayout, avatarInputLayout);
        }

        ImageButton closeButton = dialogView.findViewById(R.id.closeButton);
        builder.setTitle(isEditing ? "Edit User" : "Add New User");
        builder.setPositiveButton(isEditing ? "Save" : "Add", null);

        AlertDialog dialog = builder.create();

        closeButton.setOnClickListener(v -> dialog.dismiss());
        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(view -> {
                String email = getTextFromInputLayout(emailInputLayout);
                String firstName = getTextFromInputLayout(firstNameInputLayout);
                String lastName = getTextFromInputLayout(lastNameInputLayout);
                String avatar = getTextFromInputLayout(avatarInputLayout);

                if (validateInput(email, firstName, lastName, avatar)) {
                    if (isEditing) {
                        user.setEmail(email);
                        user.setFirst_name(firstName);
                        user.setLast_name(lastName);
                        user.setAvatar(avatar);
                        userViewModel.updateUser(user);
                    } else {
                        User newUser = new User();
                        newUser.setEmail(email);
                        newUser.setFirst_name(firstName);
                        newUser.setLast_name(lastName);
                        newUser.setAvatar(avatar);
                        userViewModel.createUser(newUser);
                    }
                    deselectItem();
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }
    private void initInputLayouts(User user, TextInputLayout emailInput, TextInputLayout firstNameInput,
                                  TextInputLayout lastNameInput, TextInputLayout avatarInput) {
        if (emailInput.getEditText() != null) emailInput.getEditText().setText(user.getEmail());
        if (firstNameInput.getEditText() != null) firstNameInput.getEditText().setText(user.getFirst_name());
        if (lastNameInput.getEditText() != null) lastNameInput.getEditText().setText(user.getLast_name());
        if (avatarInput.getEditText() != null) avatarInput.getEditText().setText(user.getAvatar());
    }

    private String getTextFromInputLayout(TextInputLayout inputLayout) {
        return inputLayout.getEditText() != null ? inputLayout.getEditText().getText().toString().trim() : "";
    }

    private boolean validateInput(String email, String firstName, String lastName, String avatar) {
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showErrorDialog("Please enter a valid email address");
            return false;
        }
        if (TextUtils.isEmpty(firstName)) {
            showErrorDialog("First name cannot be empty");
            return false;
        }
        if (TextUtils.isEmpty(lastName)) {
            showErrorDialog("Last name cannot be empty");
            return false;
        }
        if (TextUtils.isEmpty(avatar) || !Patterns.WEB_URL.matcher(avatar).matches()) {
            showErrorDialog("Please enter a valid URL for the avatar");
            return false;
        }
        return true;
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