package com.example.ta;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import com.example.ta.Adapter.MapItemAdapter;
import com.example.ta.ApiHelper.BaseApiService;
import com.example.ta.ApiHelper.RetrofitClient;
import com.example.ta.Pojo.Lokasi;
import com.example.ta.Response.LoginResponse;
import com.example.ta.Response.LokasiResponse;
import com.example.ta.Session.SessionManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager lm;
    private LocationManager ll;
    private GoogleMap mapForMarker;
    EditText txtlong, txtlat, search;

    private FloatingActionButton fab_main, loginFab, recFab, fabReload;
    private Animation fab_open, fab_close, fab_clock, fab_anticlock;
//        btn = findViewById(R.id.fab1);
    SessionManager sessionManager;
    MapItemAdapter mapItemAdapter;
    Spinner tipeData;

    Boolean isOpen = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        sessionManager = new SessionManager(this);
        getDataLokasi();

        fab_main = findViewById(R.id.fabMain);
        recFab = findViewById(R.id.recFab);
        loginFab = findViewById(R.id.loginFab);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_anticlock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_anticlock);
        fab_clock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_clock);
        search = findViewById(R.id.searchBox);
        final TextView loginText = findViewById(R.id.login_text);
        final TextView recText = findViewById(R.id.rec_text);

        txtlat = findViewById(R.id.lati);
        txtlong = findViewById(R.id.longi);
        tipeData = findViewById(R.id.spinner);
        fabReload = findViewById(R.id.fabReload);

        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOpen) {

                    loginText.setVisibility(View.INVISIBLE);
                    recText.setVisibility(View.INVISIBLE);
                    loginFab.startAnimation(fab_close);
                    recFab.startAnimation(fab_close);
                    fab_main.startAnimation(fab_anticlock);
                    recFab.setClickable(false);
                    loginFab.setClickable(false);
                    isOpen = false;
                } else {
                    recText.setVisibility(View.VISIBLE);
                    loginText.setVisibility(View.VISIBLE);
                    recFab.startAnimation(fab_open);
                    loginFab.startAnimation(fab_open);
                    fab_main.startAnimation(fab_clock);
                    loginFab.setClickable(true);
                    recFab.setClickable(true);
                    isOpen = true;
                }
            }
        });

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search.clearFocus();
                    InputMethodManager in = (InputMethodManager)MapActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(search.getWindowToken(), 0);
                    String newLoc = search.getText().toString();
                    performSearch();
                    return true;
                }
                return false;
            }
        });

        fab_main.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(MapActivity.this, "Berhasil Logout :3", Toast.LENGTH_SHORT).show();
                sessionManager.logout();
                return false;
            }
        });

        loginFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sessionManager.getUser() != null){
                    Intent in = new Intent(MapActivity.this, CameraActivity.class);
                    startActivity(in);
                }else{
                    Intent in = new Intent(MapActivity.this, LoginActivity.class);
                    startActivity(in);
                }
            }
        });

        recFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MapActivity.this, LaporanActivity.class);
                startActivity(i);
            }
        });

//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(sessionManager.getUser() != null){
//                    Intent in = new Intent(MapActivity.this, CameraActivity.class);
//                    startActivity(in);
//                }else{
//                    Intent in = new Intent(MapActivity.this, LoginActivity.class);
//                    startActivity(in);
//                }
//            }
//        });
//
//
        fabReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MapActivity.this, "Sedang memuat :p", Toast.LENGTH_SHORT).show();
                getDataLokasi();
            }
        });

        tipeData.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getDataLokasi();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    private void performSearch(){
        search = findViewById(R.id.searchBox);
        Geocoder g = new Geocoder(getBaseContext());
        try {
            List<android.location.Address> daftar = g.getFromLocationName(search.getText().toString(),1);
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
        mapForMarker = googleMap;
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
        assert location != null;
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng myPosition = new LatLng(latitude, longitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 15));

        // Add a marker in Sydney and move the camera
    }

    protected void getDataLokasi(){
        BaseApiService apiService = RetrofitClient.getClient().create(BaseApiService.class);
        Call<LokasiResponse> call = apiService.getLokasiPenjualan();
        call.enqueue(new Callback<LokasiResponse>() {
            @Override
            public void onResponse(Call<LokasiResponse> call, Response<LokasiResponse> response) {
                iterasiList(response.body().getData());
            }

            @Override
            public void onFailure(Call<LokasiResponse> call, Throwable t) {
                Log.i("ERRORNYA", t.getMessage());
            }
        });

    }

    protected void iterasiList(List<Lokasi> lokasiList){
        mapForMarker.clear();
        tipeData = findViewById(R.id.spinner);

        for (int i=0 ; i<lokasiList.size() ; i++){
            LatLng myPosition = new LatLng(lokasiList.get(i).getLatitude_lokasi_penjualan(), lokasiList.get(i).getLongitude_lokasi_penjualan());
            if(tipeData.getSelectedItem().toString().equals("Bakso")) {
                if (lokasiList.get(i).getId_jenis_sample().equals("1")) {
                    Marker newMarker = mapForMarker.addMarker(new MarkerOptions().position(myPosition).title(lokasiList.get(i).getNama_lokasi_penjualan()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                    newMarker.setTag(lokasiList.get(i));
                    MapItemAdapter mapItemAdapter = new MapItemAdapter(this, lokasiList.get(i));
                    mapForMarker.setInfoWindowAdapter(mapItemAdapter);
                }
            }
            else if(tipeData.getSelectedItem().toString().equals("Semua Jenis Sampel")){
                    Marker newMarker = mapForMarker.addMarker(new MarkerOptions().position(myPosition).title(lokasiList.get(i).getNama_lokasi_penjualan()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    newMarker.setTag(lokasiList.get(i));
                    MapItemAdapter mapItemAdapter = new MapItemAdapter(this, lokasiList.get(i));
                    mapForMarker.setInfoWindowAdapter(mapItemAdapter);
            }
            else if(tipeData.getSelectedItem().toString().equals("Tahu")) {
                if (lokasiList.get(i).getId_jenis_sample().equals("2")) {
                    Marker newMarker = mapForMarker.addMarker(new MarkerOptions().position(myPosition).title(lokasiList.get(i).getNama_lokasi_penjualan()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    newMarker.setTag(lokasiList.get(i));
                    MapItemAdapter mapItemAdapter = new MapItemAdapter(this, lokasiList.get(i));
                    mapForMarker.setInfoWindowAdapter(mapItemAdapter);
                }

            }


        }

        mapForMarker.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.i("TEST MARKER", marker.getTitle().toString());
                return false;
            }
        });

    }





}


