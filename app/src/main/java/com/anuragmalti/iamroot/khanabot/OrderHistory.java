package com.anuragmalti.iamroot.khanabot;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class OrderHistory extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public Context context;
    public JSONArray adapterArray=new JSONArray();
    public RecyclerView catmenu;
    public SwipeRefreshLayout swiper;
    public BottomNavigationView bnv;
    public static JSONArray responseArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderhistory);
        context = this;
        bnv = (BottomNavigationView)findViewById(R.id.bnv);

        swiper = (SwipeRefreshLayout)(findViewById(R.id.swipeme));
        swiper.setOnRefreshListener(this);
        String title = getIntent().getStringExtra("title");
        TextView textView=(TextView)findViewById(R.id.topToolbar);
        textView.setText(getSharedPreferences("com.example.root.khanabot",Context.MODE_PRIVATE).
        getString("number","9471762689"));
        catmenu =(RecyclerView)findViewById(R.id.orderstatus);
        catmenu.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        catmenu.setAdapter(new OrderhistoryAdapter(context,adapterArray));
        swiper.setRefreshing(true);
        makerequest();

        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.cart:
                        selectme(0);
                        Intent intent = new Intent(context,NewCart.class);
                        startActivity(intent);
                        break;
                    case R.id.home:
                        selectme(1);
                        Intent intent2 = new Intent(context,HomePage.class);
                        startActivity(intent2);
                        break;
                    case R.id.orderstatus:
                        selectme(2);
                        break;
                    case R.id.search:
                        selectme(3);
                        Intent intent1 = new Intent(context,Search.class);
                        startActivity(intent1);
                        break;
                }
                return true;
            }
        });

    }

    public void selectme(int id){
        bnv.getMenu().getItem(id).setChecked(true);
    }


    public void makerequest(){

        Log.e("entered","inside makerequest");
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.GET, MySingleton.BASE_URL+"/orderhistory", null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("Response: " , response.toString());
                        try {
                            swiper.setRefreshing(false);
//                    for(int i=0;i<response.length();i++){
//                        JSONObject perorder = response.getJSONObject(i);
//                        ////Log.e("orderhistory",perorder.toString());
//                    }
                            catmenu.setAdapter(new OrderhistoryAdapter(context,response));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //TODO: Handle error
                        Log.e("Response: " , error.toString());
                        Toast.makeText(context,"Internet Connection Failed",Toast.LENGTH_LONG).show();
                    }
                });

// Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }

    public void sendrequest(final String orderid, final String status, String fromnumber,String tonumber){

        Map<String, String> params = new HashMap<String, String>();
        params.put("id",orderid);
        params.put("status",status);
        params.put("fromnumber",fromnumber);
        params.put("tonumber",tonumber);
        Log.e("params",params.toString());

        CustomObjectRequest customRequest = new CustomObjectRequest(Request.Method.GET,MySingleton.BASE_URL+"/changeorderstatuscustomer",params,new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                Log.e("Response: " , response.toString());

                if(response.has("status")){
                    try {
                        if(response.getString("status").equals("changed")){

                        }
                        else{
                            Toast.makeText(context,response.getString("status"),Toast.LENGTH_LONG).show();
                        }
                        swiper.setRefreshing(true);
                        makerequest();
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
        MySingleton.getInstance(context).addToRequestQueue(customRequest);

    }

    public void notifychange(){
        int total = 0;
        for(int i=0; i<HomePage.mycart.length(); i++){
            try {
                JSONObject cartobject = HomePage.mycart.getJSONObject(i);
                JSONArray order = cartobject.getJSONArray("order");
                for(int j=0; j<order.length();j++){
                    total +=order.getJSONObject(j).getInt("quantity");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ((TextView)findViewById(R.id.carttext)).setText(total+"");
        if(HomePage.mycart.length()==0)
            ((RelativeLayout)findViewById(R.id.cartcontainer)).setVisibility(View.INVISIBLE);
        else
            ((RelativeLayout)findViewById(R.id.cartcontainer)).setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume(){
        super.onResume();
        Search.responseArray = responseArray;
        notifychange();
        selectme(2);
    }

    @Override
    public void onRefresh() {
        makerequest();
    }
}
