package com.example.ta.Response;
import com.example.ta.Pojo.Laporan;

public class LaporanResponse {
    private Integer code;
    private Laporan data;
    private String message;

    public Integer getCode() {
        return code;
    }

    public Laporan getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}

