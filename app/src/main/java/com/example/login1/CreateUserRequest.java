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

    // Getters - Required for Retrofit/Gson to serialize to JSON
    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getDob() {
        return dob;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }
}
