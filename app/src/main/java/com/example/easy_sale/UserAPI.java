package com.example.easy_sale;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserAPI {
    @GET("users")
    Call<UserResponse> getAllUsers(@Query("page") int page);

    @PUT("users/{id}")
    Call<User> updateUser(@Path("id") int id, @Body User user);}
