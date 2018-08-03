package com.anuragmalti.iamroot.khanabot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh(){
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        ////Log.e("Token ", "Refreshed token: " + refreshedToken);

        sendRegistrationToServer(refreshedToken);
    }

    public void addtosharedpreferences(String key , String cartstring){
        SharedPreferences prefs = getSharedPreferences("com.example.root.khanabot", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key,cartstring);
        editor.commit();
        ////Log.e("saved notificationid","saved to preferences");
    }

    public void sendRegistrationToServer(String token){
        addtosharedpreferences("notificationid",token);
        addtosharedpreferences("notificationstatus","");
    }

}

