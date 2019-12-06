package com.example.ta.apihelper;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface BaseApiService {
    @FormUrlEncoded
    @POST("")
    Call<ResponseBody> loginRequest(@Field("username") String username,
                                    @Field("password") String password,
                                    @Field("token") String token;
}
