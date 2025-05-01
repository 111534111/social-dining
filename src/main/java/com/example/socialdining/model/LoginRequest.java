package com.example.socialdining.model;

public class LoginRequest {
    private String email;
    private String password;
    // 用於註冊時傳入暱稱資訊
    private String username;

    public LoginRequest() {}

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
}
