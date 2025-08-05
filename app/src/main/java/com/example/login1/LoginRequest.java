package com.example.login1;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("email")
    private String name;

    private String password;

    public LoginRequest(String name, String password) {
        this.name = name;
        this.password = password;
    }
}




