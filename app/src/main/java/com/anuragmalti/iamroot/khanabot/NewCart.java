package com.anuragmalti.iamroot.khanabot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class NewCart extends AppCompatActivity {

    public Context context;
    private EditText editAddress;
    private TextView address;
    public RecyclerView restaurantcont;
    private Button add;
    private String name,offerMaxDiscount,offerMinValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_cart);

        context = this;
        editAddress = (EditText)findViewById(R.id.editAddress);
        address = (TextView)findViewById(R.id.address);
        address.setText(HomePage.address);
        add = (Button)findViewById(R.id.changeAdd);

        if(address.getText().toString() == null || address.getText().toString()==""){
            editAddress.setVisibility(View.VISIBLE);
            editAddress.requestFocus();
            address.setVisibility(View.GONE);
            add.setText("Add");
        }

        restaurantcont = (RecyclerView)findViewById(R.id.restaurantcont);
        restaurantcont.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        restaurantcont.setAdapter(new NewCartAdapter(this,HomePage.mycart));
        Button clearCart = (Button)findViewById(R.id.clearCart);
        clearCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearcart();
            }
        });
        ((Button)findViewById(R.id.changeAdd)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchaddchange();
            }
        });

    }

    public void clearcart(){
        HomePage.mycart = new JSONArray();
        try {
            notifychange();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setadapter();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                name = data.getStringExtra("offerName");
                offerMaxDiscount = data.getStringExtra("offerMaxDiscount");
                offerMinValue = data.getStringExtra("offerMinValue");
                Log.e("offer values", name + offerMinValue + offerMaxDiscount);
            }
        }
    }

    public void switchaddchange() {

        if(add.getText().toString().compareTo("Change") == 0){
            editAddress.setVisibility(View.VISIBLE);
            editAddress.requestFocus();
            address.setVisibility(View.GONE);
            add.setText("Add");
            return;
        }


        if(add.getText().toString().compareTo("Add") == 0){
            //Toast.makeText(context,"inside add",Toast.LENGTH_LONG).show();
            if(editAddress.getText().toString().compareTo("") != 0) {
                //Toast.makeText(context,"inside if add",Toast.LENGTH_LONG).show();
                address.setText(editAddress.getText().toString());
                editAddress.setVisibility(View.GONE);
                address.setVisibility(View.VISIBLE);
                add.setText("Change");
            }
            else if(editAddress.getText().toString().compareTo("") == 0){
                Toast.makeText(context,"Please enter a valid address",Toast.LENGTH_LONG).show();
            }
        }

    }

    public void orderRequest(View view) {

    }

    public void editOrder(int position) {
        Intent intent = new Intent(context,PopUpEditOrder.class);
        Bundle b = new Bundle();
        b.putInt("position", position);
        intent.putExtras(b);
        startActivity(intent);
    }

    public void openOffer(int position) {
        Intent intent = new Intent(context,EditOfferPopUp.class);
        Bundle b = new Bundle();
        b.putInt("position", position);
        intent.putExtras(b);
        startActivityForResult(intent,1);
    }

    public void notifychange() throws JSONException {
        int ans = 0;
        for(int i=0;i<HomePage.mycart.length();i++){
            JSONObject cartitem = HomePage.mycart.getJSONObject(i);
            int price = cartitem.getInt("total");
            ans += price;
        }
        ((TextView)findViewById(R.id.total)).setText("Rs "+ans);
    }

    @Override
    public void onResume(){
        super.onResume();
        setadapter();
        try {
            notifychange();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setadapter(){
        restaurantcont.setAdapter(new NewCartAdapter(this,HomePage.mycart));
    }

    protected void onPause() {
        super.onPause();
        Log.e("error in on", "in resume right now");
        addtosharedpreferences(HomePage.mycart.toString());
    }

    public void addtosharedpreferences(String cartstring){
        SharedPreferences prefs = getSharedPreferences("com.example.root.khanabot", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("mycart", cartstring);
        editor.commit();
    }

    public void makeRequest(final int position){
        Log.e("error req","error in making request");
        if(address.getText().toString() == null || address.getText().toString()==""){
            editAddress.setVisibility(View.VISIBLE);
            address.setVisibility(View.GONE);
            add.setText("Add");
            editAddress.requestFocus();
        }
        else{
            JSONObject restaurantobject=new JSONObject();
            try {
                restaurantobject = HomePage.mycart.getJSONObject(position);
                restaurantobject.put("address",address.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Map<String, String> params = new HashMap<String, String>();
            params.put("order",restaurantobject.toString());

            CustomObjectRequest customRequest = new CustomObjectRequest(Request.Method.POST,MySingleton.BASE_URL+"/requestordernew",params,new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                    Log.e("Response: " , response.toString());

                    if(response.has("orders")) {
                        try {
                            if(response.getString("orders").equals("requested"))
                                HomePage.mycart.remove(position);
                            setadapter();
                            notifychange();
                            Intent intent = new Intent(context,OrderHistory.class);
                            startActivity(intent);
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
            MySingleton.getInstance(this).addToRequestQueue(customRequest);

//            RestClient.post("/requestordernew", params, new JsonHttpResponseHandler(){
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                    if(response.has("orders")) {
//                        try {
//                            if(response.getString("orders").equals("requested"))
//                            HomePage.mycart.remove(position);
//                            setadapter();
//                            notifychange();
//                            Intent intent = new Intent(context,OrderHistory.class);
//                            startActivity(intent);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//
//                @Override
//                public void onFinish() {
//                    //onLoginSuccess();
//                }
//                @Override
//                public void onFailure(int statusCode, Header[] headers, Throwable throwable,JSONObject errorResponse){
//                    Log.e("error request","request failed");
//                }
//            });
        }
    }
}
