package com.example.ta;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ta.ApiHelper.BaseApiService;
import com.example.ta.ApiHelper.RetrofitClient;
import com.example.ta.Pojo.Laporan;
import com.example.ta.Response.LaporanResponse;
import com.example.ta.Response.StoreResponse;
import com.example.ta.Session.SessionManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LaporanActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Button laporbtn;
    EditText namaPelapor, search;
    Spinner jenisLaporan;
    Double markerLat, markerLong;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        namaPelapor = findViewById(R.id.textNama);
        jenisLaporan = findViewById(R.id.jenisLaporan);
        search = findViewById(R.id.searchBox);
        laporbtn = findViewById(R.id.laporbtn);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search.clearFocus();
                    InputMethodManager in = (InputMethodManager)LaporanActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(search.getWindowToken(), 0);
                    String newLoc = search.getText().toString();
                    performSearch();
                    return true;
                }
                return false;
            }
        });

        laporbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFromUI();
            }
        });
    }

    private void performSearch() {
        search = findViewById(R.id.searchBox);
        Geocoder g = new Geocoder(getBaseContext());
        try {
            List<Address> daftar = g.getFromLocationName(search.getText().toString(),1);
            Address alamat = daftar.get(0);
            String namaAlamat = alamat.getAddressLine(0);
            Double getLong = alamat.getLongitude();
            Double getLat = alamat.getLatitude();
            Toast.makeText(this,"Move to "+ namaAlamat +" Lat:" + getLat + " Long:" +getLong,Toast.LENGTH_LONG).show();
            gotoPeta(getLong,getLat,15);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void gotoPeta(Double getLong, Double getLat, int zoom) {
        LatLng searchLocation = new LatLng(getLat, getLong);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchLocation,zoom));
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Criteria criteria = new Criteria();
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(criteria, true);
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        assert provider != null;
        Location location = locationManager.getLastKnownLocation(provider);
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng myPosition = new LatLng(latitude, longitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 15));

        // Add a marker in Sydney and move the camera

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                mMap.clear();
                MarkerOptions marker = new MarkerOptions()
                        .position(new LatLng(point.latitude, point.longitude))
                        .title("Lokasi Penjualan Pelaporan");
                mMap.addMarker(marker);
                markerLong = marker.getPosition().longitude;
                markerLat = marker.getPosition().latitude;
            }
        });
    }



    protected void getFromUI() {
        Double tvLat, tvLong;
        String namaLapor, jenisIDLapor;
        namaLapor = namaPelapor.getText().toString().trim();
        jenisIDLapor = jenisLaporan.getSelectedItem().toString().trim();
        tvLat = markerLat;
        tvLong = markerLong;

        sendToDB(namaLapor, tvLat, tvLong, jenisIDLapor);
    }

    protected void sendToDB(final String namaLapor, Double tvLat, Double tvLong, String jenisIDLapor) {
        BaseApiService apiService = RetrofitClient.getClient().create(BaseApiService.class);
        Call<LaporanResponse> call = apiService.laporanRequest(namaLapor, tvLat, tvLong, jenisIDLapor);
        call.enqueue(new Callback<LaporanResponse>() {
            @Override
            public void onResponse(Call<LaporanResponse> call, Response<LaporanResponse> response) {
                if(response.body().getCode().equals(400)){
                        Toast.makeText(LaporanActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }else if(response.body().getCode().equals(200)){
                    Toast.makeText(LaporanActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    LaporanActivity.this.finish();
                }
            }

            @Override
            public void onFailure(Call<LaporanResponse> call, Throwable t) {
//                Toast.makeText(LaporanActivity.this, "GAGAL", Toast.LENGTH_SHORT).show();
                Log.i("GAGAL", t.getMessage().toString());
            }
        });


    }
}
