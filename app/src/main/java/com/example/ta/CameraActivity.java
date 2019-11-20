package com.example.ta;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import static android.location.LocationManager.*;
import static com.example.ta.R.string.dateformat;

public class CameraActivity extends AppCompatActivity implements LocationListener {
    private static final int kodekamera = 222;
    private static final int MY_PERMISSIONS_REQUEST_WRITE = 223;
    Button b1;
    ImageView iv;
    TextView label1, label2;
    EditText Longtext, Lattext;
    Button geobtn;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        askWritePermission();
        setContentView(R.layout.activity_camera);


        label1 = findViewById(R.id.longitude);
        label2 = findViewById(R.id.lat);
        Longtext = findViewById(R.id.textLong);
        Lattext = findViewById(R.id.textLat);
        geobtn = findViewById(R.id.geobtn);
        b1 = findViewById(R.id.fotobtn);
        iv = findViewById(R.id.imageView);

        LocationManager mylocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                File imagesFolder = new File(Environment.getExternalStorageDirectory(), "Hasil Foto");
//                imagesFolder.mkdirs();
//                Date d = new Date();
//                CharSequence s = DateFormat.format(getString(dateformat), d.getTime());
//                File image = new File(imagesFolder, s.toString() + ".jpg");
//                Uri uriSavedImage = Uri.fromFile(image);
//                it.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                startActivityForResult(it, kodekamera);


            }
        });

        geobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CameraActivity.this, GeoActivity.class);
                i.putExtra("lat", Lattext.getText().toString());
                i.putExtra("long", Longtext.getText().toString());
                startActivity(i);
            }
        });
    }

    private void askWritePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int cameraPermission = this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE);
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case (kodekamera):
                    try {
                        prosesKamera(data);
                        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    Activity#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for Activity#requestPermissions for more details.
                            return;
                        }
                        lm.requestLocationUpdates(GPS_PROVIDER, 20000, 10000, this);

                        label1.setVisibility(View.VISIBLE);
                        label2.setVisibility(View.VISIBLE);
                        Longtext.setVisibility(View.VISIBLE);
                        Lattext.setVisibility(View.VISIBLE);
                        geobtn.setVisibility(View.VISIBLE);

                        Longtext.setFocusable(false);
                        Longtext.setEnabled(false);
                        Longtext.setCursorVisible(false);
                        Longtext.setKeyListener(null);
                        Lattext.setFocusable(false);
                        Lattext.setEnabled(false);
                        Lattext.setCursorVisible(false);
                        Lattext.setKeyListener(null);


                        b1.setVisibility(View.GONE);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }
    private void prosesKamera(Intent datanya) throws IOException {
        Bitmap bm;
        bm = (Bitmap) datanya.getExtras().get("data");
        iv.setImageBitmap(bm);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            File output = new File(dir, "simpan.png");
        FileOutputStream fo = new FileOutputStream(output);
        fo.write(byteArray);
        fo.flush();
        fo.close();

    }

    @Override
    public void onLocationChanged(Location location) {
        double lngdb = location.getLongitude();
        double latdb = location.getLatitude();
        Longtext.setText(String.valueOf(lngdb));
        Lattext.setText(String.valueOf(latdb));
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
