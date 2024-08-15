package com.example.easy_sale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.ArrayList;
import java.util.List;

public class UserController {
    private static final String BASE_URL = "https://reqres.in/api/";
    private CallBack_Users callBack_Users;

    private UserController setCallBackUsers(CallBack_Users callBack_Users) {
        this.callBack_Users = callBack_Users;
        return this;
    }

    private UserAPI getAPI() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(UserAPI.class);
    }

    public void fetchAllUsers(CallBack_Users callBackUsers) {
        setCallBackUsers(callBackUsers);
        fetchUsersRecursively(1, new ArrayList<>());
    }

    private void fetchUsersRecursively(int page, List<User> allUsers) {
        Call<UserResponse> call = getAPI().getAllUsers(page);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userResponse = response.body();
                    allUsers.addAll(userResponse.data);

                    if (page < userResponse.total_pages) {
                        fetchUsersRecursively(page + 1, allUsers);
                    } else {
                        callBack_Users.ready(allUsers);
                    }
                } else {
                    callBack_Users.error("Failed to fetch users");
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                callBack_Users.error("Network error: " + t.getMessage());
            }
        });
    }

    public interface CallBack_Users {
        void ready(List<User> users);
        void error(String message);
    }
}