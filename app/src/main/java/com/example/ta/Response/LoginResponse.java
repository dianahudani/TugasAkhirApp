package com.example.ta.Response;

import com.example.ta.Pojo.User;

public class LoginResponse {

    public Integer getCode() {
        return code;
    }

    public User getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    private Integer code;
    private User data;
    private String message;
}
