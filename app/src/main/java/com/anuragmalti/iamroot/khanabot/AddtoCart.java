package com.anuragmalti.iamroot.khanabot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class AddtoCart extends AppCompatActivity {
    public RecyclerView cartItems;
    public String mode="cod";
    public String time="";
    public Button requestorder;
    public Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addto_cart);
        requestorder = (Button)findViewById(R.id.requestorder);
        try {
            notifychange();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        context = this;
         cartItems = (RecyclerView)findViewById(R.id.cartItems);
        cartItems.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        cartItems.setAdapter(new VerticalMenuAdapter(this,HomePage.mycart,"AddtoCart"));

        RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radiovisible);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.cod:
                        ((RadioGroup)findViewById(R.id.timevisible)).setVisibility(View.INVISIBLE);
                        mode="cod";
                        break;
                    case R.id.booktable:
                        mode="book";
                        ((RadioGroup)findViewById(R.id.timevisible)).setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
        ((RadioGroup)findViewById(R.id.timevisible)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.min20:
                        time="20";
                        break;
                    case R.id.min40:
                        time = "40";
                        break;
                    case R.id.min60:
                        time="60";
                        break;
                }
            }
        });
    }

    public void addtosharedpreferences(String cartstring){
        //Log.e("error str","cartstring");
        SharedPreferences prefs = getSharedPreferences("com.example.root.khanabot", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("mycart",cartstring);
        editor.commit();
        //Log.e("error onpause","saved to preferences");
        //Log.e("error str",getSharedPreferences("com.example.root.khanabot", Context.MODE_PRIVATE).
                //getString("mycart",""));
    }

    public void notifychange() throws JSONException {
        int ans = 0;
        for(int i=0;i<HomePage.mycart.length();i++){
            JSONObject cartitem = HomePage.mycart.getJSONObject(i);
            JSONArray price = cartitem.getJSONArray("price");
            int quantity = cartitem.getInt("quantity");
            ans += Integer.parseInt(price.getString(cartitem.getInt("index")))*quantity;
            //Log.e("in cart",cartitem.toString());
        }
        //Log.e("error addtocart","I have been called");
        ((TextView)findViewById(R.id.total)).setText("Rs "+ans);
    }

    @Override
    protected void onPause() {
        super.onPause();
        addtosharedpreferences(HomePage.mycart.toString());
    }
    public void requestorder(View view){
        if(HomePage.mycart.length()==0)
            return;
        requestorder.setEnabled(false);
        RequestParams params = new RequestParams();
        params.put("mode",mode);
        params.put("time",time);
        params.put("order",HomePage.mycart.toString());
        RestClient.post("/requestorder",params,new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //Log.e("cartresponse",response.toString());
                if(response.has("orders")){
                    try {
                        if(response.getString("orders").equals("requested")){
                            Toast.makeText(getApplicationContext(),"Order Requested",Toast.LENGTH_SHORT).show();
                            clearorder(null);
                            requestorder.setEnabled(true);
                            Intent intent = new Intent(context,UserProfile.class);
                            startActivity(intent);
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
                requestorder.setEnabled(true);
            }

        });
    }

    public void clearorder(View view){
        HomePage.mycart = new JSONArray();
        try {
            notifychange();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        cartItems.setAdapter(new VerticalMenuAdapter(this,HomePage.mycart,"AddtoCart"));
    }
}
