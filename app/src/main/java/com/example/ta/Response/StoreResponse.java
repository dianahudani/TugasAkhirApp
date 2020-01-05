package com.example.ta.Response;

import com.example.ta.Pojo.Dataset;


public class StoreResponse {
    private Integer code;
    private Dataset data;
    private String message;

    public Integer getCode() {
        return code;
    }

    public Dataset getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
