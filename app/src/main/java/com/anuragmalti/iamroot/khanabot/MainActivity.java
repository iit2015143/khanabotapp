package com.anuragmalti.iamroot.khanabot;

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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.HashMap;
import java.util.Map;


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
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
        context = this;
    }

    public void chekappversion(){

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, MySingleton.BASE_URL+"/appversion", null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Response: " , response.toString());
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
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.e("Response: " , error.toString());
                        Toast.makeText(context,"Internet Connection Failed",Toast.LENGTH_LONG).show();
                    }
                });

// Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }

    public void checkuuidandnumber(){
        String number = getprefsvalue("number");
        String uuid = getprefsvalue("uuid");
        if(number.equals("") || uuid.equals("")){
            Intent intent = new Intent(context,LoginActivity.class);
            Log.e("error", "in checkuuidand number");
            startActivity(intent);
            finish();
        }
        else
        dologin(number,uuid);
    }

    public void dologin(String number, String uuid){

        Map<String, String> params = new HashMap<String, String>();
        params.put("number",number);
        params.put("uuid",uuid);

        CustomObjectRequest customRequest = new CustomObjectRequest(Request.Method.POST,MySingleton.BASE_URL+"/login",params,new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                Log.e("Response: " , response.toString());

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
                            Log.e("error", "in dologin");

                            Intent intent = new Intent(context,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
                Log.e("Response: " , error.toString());
                Toast.makeText(context,"Internet Connection Failed",Toast.LENGTH_LONG).show();
            }
        });

        MySingleton.getInstance(this).addToRequestQueue(customRequest);

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

    @Override
    public void onResume(){
        super.onResume();
        chekappversion();
    }
}
