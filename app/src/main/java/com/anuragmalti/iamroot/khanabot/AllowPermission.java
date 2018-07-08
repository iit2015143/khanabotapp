package com.anuragmalti.iamroot.khanabot;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class AllowPermission extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks ,
GoogleApiClient.OnConnectionFailedListener{

    private Location mCurrentLocation;
    private GoogleApiClient mGoogleApiClient;
    private Context context;

    final int PERMISSION_ACCESS_FINE_LOCATION = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allow_permission);
        context=this;
        if(checkpermission()){
            permissionGranted();
        }
    }

    public void allowPermission(View view){
        if(checkpermission()) {
            permissionGranted();
        }
    }

    public boolean checkpermission(){
        boolean result = false;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    PERMISSION_ACCESS_FINE_LOCATION);
        }
        else
            result = true;
        return result;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted();
                }
                else {
                    Toast.makeText(this, "We Need your location to serve you better!", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }
    public void permissionGranted(){

        mGoogleApiClient=new GoogleApiClient.Builder(this, this,this).addApi(LocationServices.API).build();

        if(mGoogleApiClient!=null){
            mGoogleApiClient.connect();
        }
        //Log.e("Error permgrant","inpermissiongranted");


    }
    public void gotocurrentlocation(){
        mCurrentLocation = LocationServices
                .FusedLocationApi
                .getLastLocation( mGoogleApiClient );

        //Log.e("Errorcurrentlocation","ingotocurrentlocation");
        JSONObject location = new JSONObject();
        try {
            location.put("lat", mCurrentLocation.getLatitude() + "");
            location.put("long", mCurrentLocation.getLongitude() + "");
            addtosharedpref("location", location.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String address = getAddressFromLatLng(new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude()));
        Intent intent = new Intent(this,HomePage.class);
        intent.putExtra("address",address);
        startActivity(intent);
        finish();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        gotocurrentlocation();
        //Log.e("Error connected","connected");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private String getAddressFromLatLng( LatLng latLng ) {
        Geocoder geocoder = new Geocoder( context );

        String address = "";
        try {
            address = geocoder
                    .getFromLocation( latLng.latitude, latLng.longitude, 1 )
                    .get( 0 ).getAddressLine( 0 );
        } catch (IOException e ) {
        }

        return address;
    }
    public void addtosharedpref(String key,String value){
        SharedPreferences prefs = getSharedPreferences("com.example.root.khanabot",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key,value);
        editor.commit();
    }
}
