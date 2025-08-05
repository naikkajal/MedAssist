package com.example.login1;

public class CreateUserRequest {
    private String name;
    private String password;
    private String dob;
    private String email;
    private String mobile;
    private String bloodGroup;

    public CreateUserRequest(String name, String password, String dob, String email, String mobile, String bloodGroup) {
        this.name = name;
        this.password = password;
        this.dob = dob;
        this.email = email;
        this.mobile = mobile;
        this.bloodGroup = bloodGroup;
    }

    // Add getters if needed
}
