package com.example.ta.Response;

import com.example.ta.Pojo.Lokasi;

import java.util.List;

public class LokasiResponse {
    public Integer getCode() {
        return code;
    }

    public List<Lokasi> getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    private Integer code;
    private List<Lokasi> data;
    private String message;
}
