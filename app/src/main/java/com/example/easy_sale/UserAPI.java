package com.example.easy_sale;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserAPI {

    @GET("users")
    Call<UserResponse> getAllUsers(@Query("page") int page);

}
