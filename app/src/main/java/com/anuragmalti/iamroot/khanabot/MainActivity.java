package com.anuragmalti.iamroot.khanabot;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
GoogleApiClient.OnConnectionFailedListener{

    public LocationCallback mLocationCallback;
    public LocationRequest locationRequest;
    Context context;
    final int PERMISSION_ACCESS_FINE_LOCATION = 1;
    public static final int REQUEST_CHECK_SETTINGS = 0x1;
    public GoogleApiClient mGoogleApiClient;
    public Location mCurrentLocation;
    public int flag = 0;
    public int GAC=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        chekappversion();
    }

    public void chekappversion(){
        RequestParams param = new RequestParams();
        param = null;
        RestClient.get("/appversion",param,new JsonHttpResponseHandler(){

            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //Toast.makeText(context,response.toString(),Toast.LENGTH_SHORT).show();
                    if(response.has("version")){
                        try {
                            String sversion = response.getString("version");
                            if(sversion.equals(getResources().getString(R.string.appversion))){
                                checkuuidandnumber();
                            }
                            else{
//                                Intent intent = new Intent(context,Update.class);
//                                startActivity(intent);
//                                finish();
                                final String appPackageName = getPackageName();
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                }
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
            }

            @Override
            public void onFinish() {
                //onLoginSuccess();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,JSONObject errorResponse){
                show("Request failed");
                //Toast.makeText(context,throwable.toString(),Toast.LENGTH_LONG).show();
            }

        });

    }

    public void checkuuidandnumber(){
        String number = getprefsvalue("number");
        String uuid = getprefsvalue("uuid");
        if(number.equals("") || uuid.equals("")){
            Intent intent = new Intent(context,LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else
        dologin(number,uuid);
    }

    public void dologin(String number, String uuid){

        RequestParams params = new RequestParams();
        params.put("number",number);
        params.put("uuid",uuid);

        RestClient.setCookieStore(new PersistentCookieStore(getApplicationContext()));
        RestClient.post("/login", params, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //Toast.makeText(context,response.toString(),Toast.LENGTH_SHORT).show();
                try {
                    if(response.has("loggedin")){
                        if(response.getBoolean("loggedin")){
                            trylocation();
//                            Intent intent = new Intent(context,HomePage.class);
//                            startActivity(intent);
//                            finish();
                        }
                        else{
                            Toast.makeText(context,"This account is logged in on other device, log in again",Toast.LENGTH_LONG).show();
                            ((MainActivity)context).addtosharedpref("notificationstatus","notupdated");
                            Intent intent = new Intent(context,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                //onLoginSuccess();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,JSONObject errorResponse){
                Toast.makeText(context,"Internet connection failed",Toast.LENGTH_SHORT).show();
            }

        });

    }

    public void show(String showme){
        Toast.makeText(getBaseContext(), showme, Toast.LENGTH_LONG).show();
    }

    public String getprefsvalue(String key){
        return getSharedPreferences("com.example.root.khanabot",Context.MODE_PRIVATE).getString(key,"");
    }

    public void trylocation(){
        if(checkpermission()){
            permissionGranted();
        }
        else{
            Intent intent = new Intent(context,AllowPermission.class);
                            startActivity(intent);
                            finish();
        }
    }

    public boolean checkpermission(){

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    public void permissionGranted(){
        checklocationservices();
        //Log.e("Error permgrant","inpermissiongranted");
    }

    public void checklocationservices(){
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(context).checkLocationSettings(builder.build());
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                //Log.e("error","task completed");
                try {
                    LocationSettingsResponse response =
                            task.getResult(ApiException.class);
                    connectgoogleclient();
                } catch (ApiException ex) {
                    switch (ex.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                //Log.e("error","resolution required");
                                ResolvableApiException resolvableApiException =
                                        (ResolvableApiException) ex;
                                resolvableApiException
                                        .startResolutionForResult(MainActivity.this,
                                                REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {

                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                            break;
                    }
                }
            }
        });
    }

    public void connectgoogleclient(){
        if(GAC==0) {
            GAC=1;
            mGoogleApiClient = new GoogleApiClient.Builder(this, this, this).addApi(LocationServices.API).build();
        }
        if(mGoogleApiClient!=null){
            mGoogleApiClient.connect();
        }
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

    public void gotocurrentlocation() throws JSONException {
        mCurrentLocation = LocationServices
                .FusedLocationApi
                .getLastLocation( mGoogleApiClient );

        if(flag==1)
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,mLocationCallback);

        ////Log.e("error",mCurrentLocation.toString());
        if(mCurrentLocation!=null) {
            ////Log.e("Errorcurrentlocation", mCurrentLocation.toString());
            JSONObject location = new JSONObject();
            location.put("lat", mCurrentLocation.getLatitude() + "");
            location.put("long", mCurrentLocation.getLongitude() + "");
            addtosharedpref("location", location.toString());

            String address = getAddressFromLatLng(new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude()));
            Intent intent = new Intent(this,HomePage.class);
            HomePage.address = address;
            intent.putExtra("address",address);
            startActivity(intent);
            finish();
        }
        else{
//            Intent intent = new Intent(this,OnLocation.class);
//            startActivity(intent);
//            finish();
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult result) {
                    //Log.e("location",result.getLastLocation().toString());
                    try {
                        gotocurrentlocation();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onLocationAvailability(LocationAvailability locationAvailability) { }
            };
            flag = 1;

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest,mLocationCallback , null);

        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        //Log.e("permissionresult", "User agreed to make required location settings changes.");
                        connectgoogleclient();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(context,"We need location service to serve you better.",Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                }
                break;
        }
    }

    public void addtosharedpref(String key,String value){
        SharedPreferences prefs = getSharedPreferences("com.example.root.khanabot",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key,value);
        editor.commit();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            gotocurrentlocation();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Log.e("MainActivity","connected");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
