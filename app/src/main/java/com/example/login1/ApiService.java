package com.example.login1;
import com.example.login1.LoginRequest;
import com.example.login1.LoginResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.GET;
public interface ApiService {
    @POST("api/user/")
    Call<CreateUserResponse> signupUser(@Body CreateUserRequest request);

    @POST("/api/user/login")
    Call<LoginResponse> loginUser(@Body LoginRequest request);

    @GET("/api/product/search")
    Call<List<Product>> searchProducts(@Query("productname") String name);
}
