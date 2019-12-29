package com.example.ta.Adapter;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ta.Pojo.Lokasi;
import com.example.ta.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;


public class MapItemAdapter implements GoogleMap.InfoWindowAdapter {
    private Activity context;
    Lokasi lokasi;

    TextView infoAlamat, infoStatus, infoNamaData, infoScore, infoRank;
    ImageView infoImage;

    public MapItemAdapter(Activity context, Lokasi lokasi){
        this.context = context;
        this.lokasi = lokasi;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = context.getLayoutInflater().inflate(R.layout.marker_info, null);

        Lokasi mLokasi = (Lokasi) marker.getTag();
        infoAlamat = view.findViewById(R.id.infoAlamat);
        infoStatus = view.findViewById(R.id.infoStatus);
        infoNamaData = view.findViewById(R.id.infoDataset);
        infoImage = view.findViewById(R.id.infoImage);
        infoNamaData.setText(marker.getTitle());
        infoRank = view.findViewById(R.id.infoRank);
        infoScore = view.findViewById(R.id.infoScore);
        //infoNamaData.setText(lokasi.getNama_lokasi_penjualan());
        try {
            Picasso.get().load(mLokasi.getFoto_lokasi_penjualan()).into(infoImage);
            Log.i("URL: ", mLokasi.getFoto_lokasi_penjualan());
        }catch (Exception e){
            Log.i("ERROR", e.getMessage());
        }

        getAlamat(mLokasi.getLatitude_lokasi_penjualan(), mLokasi.getLongitude_lokasi_penjualan());

        infoStatus.setText(mLokasi.getStatus_klasifikasi());
        infoRank.setText("Rank: " + mLokasi.getRank_klasifikasi());
        infoScore.setText("Score: " + mLokasi.getScore_klasifikasi());

        Log.i("STATUS LATITUDE:", infoStatus.getText().toString());
        return view;

    }

    public void getAlamat(Double latitude_lokasi_penjualan, Double longitude_lokasi_penjualan){
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        List<Address> addresses  = null;
        try {
            addresses = geocoder.getFromLocation(latitude_lokasi_penjualan,longitude_lokasi_penjualan,10);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String address = addresses.get(0).getAddressLine(0);
        infoAlamat.setText(address);
    }
}
