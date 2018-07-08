package com.anuragmalti.iamroot.khanabot;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
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

    Context context;
    final int PERMISSION_ACCESS_FINE_LOCATION = 1;
    public GoogleApiClient mGoogleApiClient;
    public Location mCurrentLocation;
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
                                Intent intent = new Intent(context,Update.class);
                                startActivity(intent);
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

        mGoogleApiClient=new GoogleApiClient.Builder(this, this,this).addApi(LocationServices.API).build();

        if(mGoogleApiClient!=null){
            mGoogleApiClient.connect();
        }
        //Log.e("Error permgrant","inpermissiongranted");


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
        if(mCurrentLocation!=null) {
            //Log.e("Errorcurrentlocation", mCurrentLocation.toString());
            JSONObject location = new JSONObject();
            location.put("lat", mCurrentLocation.getLatitude() + "");
            location.put("long", mCurrentLocation.getLongitude() + "");
            addtosharedpref("location", location.toString());
            String address = getAddressFromLatLng(new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude()));
            Intent intent = new Intent(this,HomePage.class);
            intent.putExtra("address",address);
            startActivity(intent);
            finish();
        }
        else{
            Intent intent = new Intent(this,OnLocation.class);
            startActivity(intent);
            finish();
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
