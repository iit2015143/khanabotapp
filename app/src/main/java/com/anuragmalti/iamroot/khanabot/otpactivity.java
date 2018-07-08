package com.anuragmalti.iamroot.khanabot;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

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
                    RequestParams params = new RequestParams();
                    params.put("otp",pinView.getText().toString());
                    RestClient.post("/otp",params,new JsonHttpResponseHandler(){

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            //Toast.makeText(context,response.toString(),Toast.LENGTH_SHORT).show();
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
        RequestParams params = new RequestParams();
        params.put("number",number);
        RestClient.post("/number", params, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(context,response.toString(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                //onLoginSuccess();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,JSONObject errorResponse){
                //Toast.makeText(context,throwable.toString(),Toast.LENGTH_LONG).show();
            }

        });
    }
    public void addtosharedpref(String key,String value){
        SharedPreferences prefs = getSharedPreferences("com.example.root.khanabot",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key,value);
        editor.commit();
    }


}
