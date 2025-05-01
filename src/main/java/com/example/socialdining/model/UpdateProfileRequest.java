package com.example.socialdining.model;

public class UpdateProfileRequest {
    // 使用者識別依據
    private String email;
    private String nickname;
    // 若前端留空表示不更新密碼
    private String password;
    // 新增欄位：變更密碼時必須提供舊密碼
    private String oldPassword;

    public UpdateProfileRequest() {}

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getOldPassword() {
        return oldPassword;
    }
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }
}
