package com.example.ta.Adapter;

import android.app.Activity;
import android.content.Context;
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

import java.util.Collections;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;

public class MapItemAdapter implements GoogleMap.InfoWindowAdapter {
    private Activity context;
    Lokasi lokasi;

    TextView infoAlamat, infoStatus, infoNamaData;
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

        infoAlamat = view.findViewById(R.id.infoAlamat);
        infoStatus = view.findViewById(R.id.infoStatus);
        infoNamaData = view.findViewById(R.id.infoDataset);
        infoImage = view.findViewById(R.id.infoImage);
        infoAlamat.setText(lokasi.getNama_lokasi_penjualan());
        infoNamaData.setText(lokasi.getNama_lokasi_penjualan());
        try {
            Picasso.get().load("https://i.imgur.com/DvpvklR.png").into(infoImage);
        }catch (Exception e){
            Log.i("ERRR", e.getMessage());
        }

        return view;
    }
}
