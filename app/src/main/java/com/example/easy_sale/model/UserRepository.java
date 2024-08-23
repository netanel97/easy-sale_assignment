package com.example.easy_sale.model;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserRepository {
    private static final String BASE_URL = "https://reqres.in/api/";
    private UserDao userDao;
    private UserAPI userAPI;
    private ExecutorService executorService;
    private Handler mainHandler;

    public UserRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        userDao = db.userDao();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userAPI = retrofit.create(UserAPI.class);
        executorService = Executors.newFixedThreadPool(4); // Create a thread pool
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public void fetchUsers(int page, RepositoryCallback<List<User>> callback) {
        userAPI.getAllUsers(page).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> users = response.body().getData();
                    filterAndMergeUsers(page, users, callback);
                } else {
                    callback.onError("Failed to fetch users");
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    private void filterAndMergeUsers(int page, List<User> apiUsers, final RepositoryCallback<List<User>> callback) {
        CompletableFuture.supplyAsync(() -> {
            List<User> filteredUsers = new ArrayList<>();
            if (page == 1) {
                List<User> relevantUsers = userDao.getRelevantUsers();
                filteredUsers.addAll(relevantUsers);
            }

            for (User apiUser : apiUsers) {
                DeletedUser deletedUser = userDao.getDeletedUser(apiUser.getId());
                if (deletedUser == null) { // not found in the deleted table
                    String localUpdateTime = userDao.getLastUpdateTimeForUser(apiUser.getId());
                    if (localUpdateTime != null && !localUpdateTime.isEmpty()) {
                        User localUser = userDao.getUserById(apiUser.getId());
                        if (localUser != null) {
                            filteredUsers.add(localUser);
                        } else {
                            filteredUsers.add(apiUser);
                        }
                    } else {
                        filteredUsers.add(apiUser);
                    }
                }
            }
            userDao.insertUsers(filteredUsers);
            return filteredUsers;
        }, executorService).thenAcceptAsync(mergedUsers -> {
            mainHandler.post(() -> callback.onSuccess(mergedUsers));
        }, executorService);
    }

    public void createNewUser(User user, final RepositoryCallback<User> callback) {
        userAPI.createUser(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User createdUser = response.body();
                    checkExistingUser(createdUser, callback);
                } else {
                    callback.onError("Failed to create user");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    private void checkExistingUser(final User user, final RepositoryCallback<User> callback) {
        CompletableFuture.supplyAsync(() -> {
            User existingUser = userDao.getUserByEmail(user.getEmail());
            return existingUser == null;
        }, executorService).thenAcceptAsync(isNewUser -> {
            if (isNewUser) {
                insertUser(user, callback);
            } else {
                mainHandler.post(() -> callback.onError("Cannot create user with the same email in the list"));
            }
        }, executorService);
    }

    private void insertUser(final User user, final RepositoryCallback<User> callback) {
        CompletableFuture.supplyAsync(() -> {
            userDao.insertUser(user);
            return user;
        }, executorService).thenAcceptAsync(insertedUser -> {
            mainHandler.post(() -> callback.onSuccess(insertedUser));
        }, executorService).exceptionally(e -> {
            mainHandler.post(() -> callback.onError("Error inserting user: " + e.getMessage()));
            return null;
        });
    }


    public void deleteUser(User user, final RepositoryCallback<User> callback) {
        userAPI.deleteUser(user.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 204) {
                    deleteUserFromDbAndMarkAsDeleted(user, callback);
                } else {
                    callback.onError("Failed to delete user from server");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    private void deleteUserFromDbAndMarkAsDeleted(final User user, final RepositoryCallback<User> callback) {
        CompletableFuture.runAsync(() -> {
            userDao.deleteUser(user);
            userDao.insertDeletedUserId(new DeletedUser(user.getId()));
        }, executorService).thenRun(() -> {
            mainHandler.post(() -> callback.onSuccess(user));
        });
    }


    public void updateUser(User user, final RepositoryCallback<User> callback) {
        userAPI.updateUser(user.getId(), user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User updateResponse = response.body();
                    if (updateResponse.getFirst_name() != null && !updateResponse.getFirst_name().isEmpty()) {
                        user.setFirst_name(updateResponse.getFirst_name());

                    }
                    if (updateResponse.getLast_name() != null && updateResponse.getLast_name().isEmpty()) {
                        user.setLast_name(updateResponse.getLast_name());

                    }
                    if (updateResponse.getEmail() != null && !updateResponse.getEmail().isEmpty()) {
                        user.setEmail(updateResponse.getEmail());
                    }
                    if (updateResponse.getAvatar() != null && !updateResponse.getEmail().isEmpty()) {
                        user.setAvatar(updateResponse.getAvatar());
                    }
                    user.setUpdatedAt(updateResponse.getUpdatedAt());

                    updateUserInDb(user, callback);
                } else {
                    callback.onError("Failed to update user");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    private void updateUserInDb(final User user, final RepositoryCallback<User> callback) {
        CompletableFuture.runAsync(() -> {
            userDao.updateUser(user);
        }, executorService).thenRun(() -> {
            mainHandler.post(() -> callback.onSuccess(user));
        });
    }


    public interface RepositoryCallback<T> {
        void onSuccess(T result);

        void onError(String error);
    }

    public void cleanup() {
        executorService.shutdown();
    }
}