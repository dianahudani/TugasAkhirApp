package com.example.ta.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ta.Pojo.Lokasi;
import com.example.ta.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

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
        Picasso.get().load("http://i.imgur.com/DvpvklR.png").into(infoImage);

        return view;
    }
}
