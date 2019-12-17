package com.example.ta;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ta.ApiHelper.BaseApiService;
import com.example.ta.ApiHelper.RetrofitClient;
import com.example.ta.Response.LoginResponse;
import com.example.ta.Response.StoreResponse;
import com.example.ta.Session.SessionManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.location.LocationManager.*;
import static com.example.ta.R.string.dateformat;

public class CameraActivity extends AppCompatActivity implements LocationListener {
    private static final int kodekamera = 222;
    private static final int MY_PERMISSIONS_REQUEST_WRITE = 223;
    Button b1;
    ImageView iv;
    TextView label1, label2, label3, spinnerLabel;
    EditText Longtext, Lattext, Nametext;
    Button geobtn;
    Spinner spinnerName;
    Integer id;
    ProgressDialog dialog;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        askWritePermission();
        setContentView(R.layout.activity_camera);


        label1 = findViewById(R.id.longitude);
        label2 = findViewById(R.id.lat);
        label3 = findViewById(R.id.name);
        Longtext = findViewById(R.id.textLong);
        Lattext = findViewById(R.id.textLat);
        Nametext = findViewById(R.id.namalokasi);
        geobtn = findViewById(R.id.lokasibtn);
        spinnerLabel = findViewById(R.id.spinnerName);
        spinnerName = (Spinner) findViewById(R.id.spinner);


        b1 = findViewById(R.id.fotobtn);
        iv = findViewById(R.id.foto_lokasi);

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
                getFromUi();
//                Intent i = new Intent(CameraActivity.this, GeoActivity.class);
//                i.putExtra("lat", Lattext.getText().toString());
//                i.putExtra("long", Longtext.getText().toString());
//                startActivity(i);
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
                        dialog = new ProgressDialog(CameraActivity.this);
                        dialog.setMessage("Loading..");
                        dialog.show();

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
                        lm.requestLocationUpdates(GPS_PROVIDER, 20000, 20, this);

                        label1.setVisibility(View.VISIBLE);
                        label2.setVisibility(View.VISIBLE);
                        Longtext.setVisibility(View.VISIBLE);
                        Lattext.setVisibility(View.VISIBLE);
                        geobtn.setVisibility(View.VISIBLE);
                        label3.setVisibility(View.VISIBLE);
                        Nametext.setVisibility(View.VISIBLE);
                        spinnerName.setVisibility(View.VISIBLE);
                        spinnerLabel.setVisibility(View.VISIBLE);

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
        Log.i("CHECK BITMAT", datanya.getExtras().toString());
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
        try {
            getAlamat(latdb,lngdb);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Longtext.setText(String.valueOf(lngdb));
        Lattext.setText(String.valueOf(latdb));
        dialog.dismiss();
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

    public void getAlamat(Double latitude, Double longitude) throws IOException {

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();

        Log.i("KECAMATAN", knownName);
        Nametext.setText(knownName);
    }

    protected void getFromUi(){
        Double lat;
        Double lng;
        Integer jenis = 0;
        String namaData;
        String base64;

        lat = Double.valueOf(Lattext.getText().toString());
        lng = Double.valueOf(Longtext.getText().toString());

        if(spinnerName.getSelectedItem().toString().equals("Bakso")){
            jenis = 1;
        }else if(spinnerName.getSelectedItem().toString().equals("Tahu")){
            jenis = 2;
        }else{
            Log.i("SPINNER SELECTED",spinnerName.getSelectedItem().toString());
        }

        namaData = Nametext.getText().toString();
        base64 = convertToBase64();
//
//        Log.i("DATA","================");
//        Log.i("DATA",lat.toString());
//        Log.i("DATA",lng.toString());
//        Log.i("DATA",jenis.toString());
//        Log.i("DATA",namaData);
//        Log.i("DATA",base64);
//        Log.i("DATA","================");

        kirimKeDewi(lat,lng,jenis,namaData,base64);

    }

    protected void kirimKeDewi(Double lat,Double lng,Integer jenis,String namaData,String base64){
        Log.i("KIRIM KE DEWI","TEEST");
        SessionManager sessionManager = new SessionManager(this);
        BaseApiService apiService = RetrofitClient.getClient().create(BaseApiService.class);
        Call<StoreResponse> call = apiService.sampleRequest(jenis,lat,lng,namaData,base64,sessionManager.getUser().getRemember_token());
        call.enqueue(new Callback<StoreResponse>() {
            @Override
            public void onResponse(Call<StoreResponse> call, Response<StoreResponse> response) {
                Log.i("STATUS POST", response.body().getCode().toString());
                Toast.makeText(CameraActivity.this, "BERHASIL", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Call<StoreResponse> call, Throwable t) {
                Log.i("STATUS POST", t.getMessage());
                Toast.makeText(CameraActivity.this, t.getMessage().toString(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    protected String convertToBase64(){
        iv.buildDrawingCache();
        Bitmap bitmap = iv.getDrawingCache();
        String img_str;
        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
        byte[] image=stream.toByteArray();
        System.out.println("byte array:"+image);

        return img_str = "data:image/jpeg;base64," + Base64.encodeToString(image, 0);
//        System.out.println("string:"+img_str);
    }
}
