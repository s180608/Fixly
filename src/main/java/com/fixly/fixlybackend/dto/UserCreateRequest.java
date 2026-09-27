package com.fixly.fixlybackend.dto;

public class UserCreateRequest {

    private String name;
    private String email;
    private String phone;
    private String password;

    public UserCreateRequest() {
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }
}