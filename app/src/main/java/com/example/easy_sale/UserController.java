package com.example.easy_sale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.List;

public class UserController {
    private static final String BASE_URL = "https://reqres.in/api/";
    private CallBack_Users callBack_Users;
    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean hasMorePages = true;

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

    public void fetchNextPage(CallBack_Users callBackUsers) {
        if (isLoading || !hasMorePages) { // do not fetch more data if i reach to max pages
            return;
        }

        isLoading = true;
        setCallBackUsers(callBackUsers);

        Call<UserResponse> call = getAPI().getAllUsers(currentPage);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                isLoading = false;
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userResponse = response.body();
                    currentPage++;
                    hasMorePages = currentPage <= userResponse.total_pages;
                    callBack_Users.ready(userResponse.data, hasMorePages);
                } else {
                    callBack_Users.error("Failed to fetch users");
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                isLoading = false;
                callBack_Users.error("Network error: " + t.getMessage());
            }
        });
    }

    public interface CallBack_Users {
        void ready(List<User> users, boolean hasMorePages);
        void error(String message);
    }
}