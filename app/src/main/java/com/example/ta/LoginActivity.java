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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ta.Session.SessionManager;
import com.example.ta.apihelper.BaseApiService;
import com.example.ta.apihelper.UtilsApi;

import org.json.JSONException;
import org.json.JSONObject;

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
    BaseApiService mApiService;
    SessionManager sessionManager;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        login = findViewById(R.id.loginbtn);
        mContext = this;
            mApiService = UtilsApi.getAPIService();
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
        mApiService.loginRequest(username.getText().toString().trim(), password.getText().toString())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.isSuccessful()){
                            loading.dismiss();
                            try{
                                JSONObject jsonRESULTS = new JSONObject(response.body().string());
                                if(jsonRESULTS.getString("code").equals("200")){
                                    Toast.makeText(mContext, jsonRESULTS.get("message").toString(), Toast.LENGTH_SHORT).show();
//                                    sessionManager.setUser(jsonRESULTS);
                                    Intent goToActivity = new Intent(LoginActivity.this, CameraActivity.class);
                                    startActivity(goToActivity);
                                    finish();
                                } else{
                                    Toast.makeText(mContext, jsonRESULTS.get("message").toString(), Toast.LENGTH_SHORT).show();
//                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else{
                            loading.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("debug", "onFailure: ERROR > " + t.toString());
                        loading.dismiss();
                    }
                });
    }
}
