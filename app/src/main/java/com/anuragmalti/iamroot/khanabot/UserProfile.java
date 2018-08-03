package com.anuragmalti.iamroot.khanabot;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class UserProfile extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public Context context;
    public JSONArray adapterArray=new JSONArray();
    public RecyclerView catmenu;
    public SwipeRefreshLayout swiper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        context = this;
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

    @Override
    public void onRefresh() {
        makerequest();
    }
}
