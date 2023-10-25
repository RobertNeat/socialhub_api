package com.springboot.socialhub_api.api.model;

public class LoginResponse {
    private String token;
    private int userId;

    public LoginResponse(String token, int userId){
        this.token=token;
        this.userId=userId;
    }

    public String getToken() {
        return token;
    }

    public int getUserId() {
        return userId;
    }
}
