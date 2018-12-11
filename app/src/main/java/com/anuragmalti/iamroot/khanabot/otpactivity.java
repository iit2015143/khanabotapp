package com.anuragmalti.iamroot.khanabot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.chaos.view.PinView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class otpactivity extends AppCompatActivity {

    EditText et1;
    EditText et2;
    EditText et3;
    EditText et4;
    PinView pinView;
    Context context;
    String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpactivity);

        number = getIntent().getStringExtra("number");
        context = this;

        pinView = (PinView)findViewById(R.id.pinView);
        pinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==5){
                    Map<String, String> params = new HashMap<String,String>();
                    params.put("otp",pinView.getText().toString());

                    CustomObjectRequest customRequest = new CustomObjectRequest(Request.Method.POST,MySingleton.BASE_URL+"/otp",params,new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            Log.e("Response: " , response.toString());
                            try {
                                if(response.has("otp")) {
                                    String value = response.getString("otp");
                                    if (value.equals("sent")) {
                                        show("otp sent");
                                    } else if (value.equals("timeout")) {
                                        show("OTP timeout");
                                    } else if (value.equals("invalid"))
                                        show("Invalid otp");
                                }
                                else if(response.has("uuid")){
                                    addtosharedpref("uuid",response.getString("uuid"));
                                    addtosharedpref("number",number);
                                    Intent intent = new Intent(context,AllowPermission.class);
                                    startActivity(intent);
                                    finish();
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
                    MySingleton.getInstance(context).addToRequestQueue(customRequest);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void show(String showme){
        Toast.makeText(getBaseContext(), showme, Toast.LENGTH_LONG).show();
    }

    public void resendotp(View view){
        //to be implemented
        Map<String, String> params = new HashMap<String,String>();
        params.put("number",number);

        CustomObjectRequest customRequest = new CustomObjectRequest(Request.Method.POST,MySingleton.BASE_URL+"/number",params,new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                Log.e("Response: " , response.toString());

                Toast.makeText(context,response.toString(),Toast.LENGTH_SHORT).show();
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
    public void addtosharedpref(String key,String value){
        SharedPreferences prefs = getSharedPreferences("com.example.root.khanabot",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key,value);
        editor.commit();
    }


}
