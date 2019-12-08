package com.example.ta;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ta.ApiHelper.RetrofitClient;
import com.example.ta.Pojo.User;
import com.example.ta.Response.LoginResponse;
import com.example.ta.Session.SessionManager;
import com.example.ta.ApiHelper.BaseApiService;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText username, password;
    Button login;
    ProgressDialog loading;
    Context mContext;
    SessionManager sessionManager;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        login = findViewById(R.id.loginbtn);
        mContext = this;
        initComponents();
    }

    private void initComponents() {
        username = findViewById(R.id.username);
        password = findViewById(R.id.pass);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading = ProgressDialog.show(mContext, null, "Harap Tunggu..", true, false);
                requestLogin();
            }
        });
    }

    private void requestLogin() {

        BaseApiService apiService = RetrofitClient.getClient().create(BaseApiService.class);
        Call<LoginResponse> call = apiService.loginRequest(username.getText().toString().trim(), password.getText().toString());
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(response.isSuccessful()){
                    loading.dismiss();
                    try{
                        if(response.body().getCode().equals(200)){
                            Toast.makeText(mContext, response.body().getMessage().toString(), Toast.LENGTH_SHORT).show();
                            Intent goToActivity = new Intent(LoginActivity.this, CameraActivity.class);
                            startActivity(goToActivity);
                            finish();
                        } else{
                            Toast.makeText(mContext, response.body().getMessage().toString(), Toast.LENGTH_SHORT).show();
//                                    finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else{
                    loading.dismiss();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.i("FAILED", t.getMessage());
                final Toast toast = Toast.makeText(getApplicationContext(), "Terjadi Kesalahan :(", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }

}
