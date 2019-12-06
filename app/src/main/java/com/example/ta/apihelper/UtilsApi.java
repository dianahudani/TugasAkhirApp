package com.example.ta.apihelper;

public class UtilsApi {
    public static final String BASE_URL_API = "10.151.31.192";

    public static BaseApiService getAPIService(){
        return RetrofitClient.getClient(BASE_URL_API).create(BaseApiService.class);
    }

}
