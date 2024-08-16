package com.example.easy_sale;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private UserController userController;
    private UserRecyclerViewAdapter userAdapter;
    private RecyclerView recyclerView;
    private boolean isLoading = false;
    private boolean hasMorePages = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userController = new UserController();
        userAdapter = new UserRecyclerViewAdapter(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && hasMorePages &&
                        (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0) {
                    loadMoreUsers();
                }
            }
        });

        loadMoreUsers();
    }

    private void loadMoreUsers() {
        isLoading = true;
        Log.d("pttt","here");
        userController.fetchNextPage(new UserController.CallBack_Users() {
            @Override
            public void ready(List<User> users, boolean morePages) {
                userAdapter.addUsers(users);
                isLoading = false;
                hasMorePages = morePages;
            }

            @Override
            public void error(String message) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                isLoading = false;
            }
        });
    }
}