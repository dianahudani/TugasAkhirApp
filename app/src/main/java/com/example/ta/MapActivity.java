package com.example.ta;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager lm;
    private LocationManager ll;
    private GoogleMap mapForMarker;
    EditText txtlong, txtlat;

    FloatingActionButton btn, fabReload;
    SessionManager sessionManager;
    MapItemAdapter mapItemAdapter;

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
        getDataDewi();

        txtlat = findViewById(R.id.lati);
        txtlong = findViewById(R.id.longi);
        btn = findViewById(R.id.fab1);
        fabReload = findViewById(R.id.fabReload);

        btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(MapActivity.this, "Berhasil Logout :3", Toast.LENGTH_SHORT).show();
                sessionManager.logout();
                return false;
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
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


        fabReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MapActivity.this, "Sedang memuat :p", Toast.LENGTH_SHORT).show();
                getDataDewi();
            }
        });


    }

    private class locListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            Double lat = location.getLatitude();
            Double lng = location.getLongitude();
            txtlat.setText(String.valueOf(lat));
            txtlong.setText(String.valueOf(lng));

            Toast.makeText(getBaseContext(), "Lat: " + lat + ", Long: " + lng,
                    Toast.LENGTH_LONG).show();

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
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
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng myPosition = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().position(myPosition).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 15));

        // Add a marker in Sydney and move the camera
    }

    protected void getDataDewi(){
        BaseApiService apiService = RetrofitClient.getClient().create(BaseApiService.class);
        Call<LokasiResponse> call = apiService.getLokasiPenjualan();
        call.enqueue(new Callback<LokasiResponse>() {
            @Override
            public void onResponse(Call<LokasiResponse> call, Response<LokasiResponse> response) {
                Log.i("DIANA WKWKW", response.body().getData().get(0).getNama_lokasi_penjualan());
                iterasiDewi(response.body().getData());
            }

            @Override
            public void onFailure(Call<LokasiResponse> call, Throwable t) {
                Log.i("ERRORNYA", t.getMessage());
            }
        });

    }

    protected void iterasiDewi(List<Lokasi> lokasiList){
        mapForMarker.clear();

        for (int i=0 ; i<lokasiList.size() ; i++){
            LatLng myPosition = new LatLng(lokasiList.get(i).getLatitude_lokasi_penjualan(), lokasiList.get(i).getLongitude_lokasi_penjualan());
            if(Integer.parseInt(lokasiList.get(i).getId_lokasi_penjualan()) > 4){
                Marker newMarker = mapForMarker.addMarker(new MarkerOptions().position(myPosition).title(lokasiList.get(i).getNama_lokasi_penjualan()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                MapItemAdapter mapItemAdapter = new MapItemAdapter(this,lokasiList.get(i));
                mapForMarker.setInfoWindowAdapter(mapItemAdapter);
            }else{
                Marker newMarker = mapForMarker.addMarker(new MarkerOptions().position(myPosition).title(lokasiList.get(i).getNama_lokasi_penjualan()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                MapItemAdapter mapItemAdapter = new MapItemAdapter(this,lokasiList.get(i));
                mapForMarker.setInfoWindowAdapter(mapItemAdapter);

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

    protected void putPin(double latitude, double longitude){

    }




}


