package com.example.ta.ApiHelper;

import com.example.ta.Response.LoginResponse;
import com.example.ta.Response.LokasiResponse;
import com.example.ta.Response.StoreResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface BaseApiService {
    @FormUrlEncoded
    @POST("login")
    Call<LoginResponse> loginRequest(
            @Field("username") String username,
            @Field("password") String password);

    @FormUrlEncoded
    @POST("lokasi-penjualan/store")
    Call<StoreResponse> sampleRequest(
            @Field("id_jenis_sample") Integer id_jenis_sample,
            @Field("latitude_lokasi_penjualan") Double latitude_lokasi_penjualan,
            @Field("longitude_lokasi_penjualan") Double longitude_lokasi_penjualan,
            @Field("nama_lokasi_penjualan") String nama_lokasi_penjualan,
            @Field("foto_lokasi_penjualan") String foto_lokasi_penjualan,
            @Field("remember_token") String remember_token);

    @GET("lokasi-penjualan/get")
    Call<LokasiResponse> getLokasiPenjualan();

}

