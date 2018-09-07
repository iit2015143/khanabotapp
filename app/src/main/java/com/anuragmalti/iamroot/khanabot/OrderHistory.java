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

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
                        Intent intent = new Intent(context,Cart.class);
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
        RestClient.get("/orderhistory",null,new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                //Toast.makeText(context,response.toString(),Toast.LENGTH_SHORT).show();
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

            @Override
            public void onFinish() {
                //onLoginSuccess();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,JSONObject errorResponse){
                ////Log.e("error failure","connection failed in orderhistory");
            }

        });
    }

    public void sendrequest(final String orderid, final String status, String fromnumber,String tonumber){
        RequestParams params = new RequestParams();
        params.put("id",orderid);
        params.put("status",status);
        params.put("fromnumber",fromnumber);
        params.put("tonumber",tonumber);

        RestClient.get("/changeorderstatuscustomer",params,new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
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

            @Override
            public void onFinish() {
                //onLoginSuccess();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,JSONObject errorResponse){
                Log.e("error failure","connection failed in orderhistory");
            }

        });
    }

    public void notifychange(){
        ((TextView)findViewById(R.id.carttext)).setText(HomePage.mycart.length()+"");

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
