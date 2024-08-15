package com.example.easy_sale;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new UserController().fetchAllUsers(new UserController.CallBack_Users() {
            @Override
            public void ready(List<User> users) {
                for (int i = 0; i < users.size(); i++) {
                    System.out.println(users.get(i));
                }
            }

            @Override
            public void error(String message) {

            }
        });

    }
}