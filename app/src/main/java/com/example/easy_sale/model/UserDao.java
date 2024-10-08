package com.example.easy_sale.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUsers(List<User> users);

    @Update
    void updateUser(User user);

    @Query("SELECT * FROM users")
    List<User> getAllUsers();

    @Query("SELECT * FROM users WHERE id = :userId")
    User getUserById(int userId);

    @Query("SELECT * FROM users WHERE email = :email")
    User getUserByEmail(String email);
    @Query("SELECT updatedAt FROM users WHERE id = :userId")
    String getLastUpdateTimeForUser(int userId);

    @Delete
    void deleteUser(User user);


    @Insert
    long insertUser(User user);


    @Insert
    void insertDeletedUserId(DeletedUser deletedUser);

    @Query("SELECT * FROM deleted_users WHERE userId = :userId")
    DeletedUser getDeletedUser(int userId);


    @Query("SELECT * FROM users WHERE createdAt IS NOT NULL")
    List<User> getRelevantUsers();

}

