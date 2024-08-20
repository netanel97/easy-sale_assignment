package com.example.easy_sale;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "deleted_users")
public class DeletedUser {
    @PrimaryKey
    public int userId;

    public DeletedUser(int userId) {
        this.userId = userId;
    }

    public DeletedUser() {
    }

    public int getUserId() {
        return userId;
    }

    public DeletedUser setUserId(int userId) {
        this.userId = userId;
        return this;
    }
}