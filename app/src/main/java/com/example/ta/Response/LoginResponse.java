package com.example.ta.Response;

import com.example.ta.Pojo.User;

public class LoginResponse {

    public Integer getError() {
        return error;
    }

    public User getMessage() {
        return message;
    }

    private Integer error;
    private User message;

}

