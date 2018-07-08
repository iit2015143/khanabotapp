package com.anuragmalti.iamroot.khanabot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import cz.msebera.android.httpclient.Header;

public class Menus extends AppCompatActivity {

    public Context context;
    public static JSONArray adapterArray;
    public BottomNavigationView bnv;
    RecyclerView catmenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menus);
        context = this;
        notifychange();
        String title = getIntent().getStringExtra("title");
        TextView textView=(TextView)findViewById(R.id.topToolbar);
        textView.setText(title);
        catmenu =(RecyclerView)findViewById(R.id.catmenu);
        catmenu.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        catmenu.setAdapter(new VerticalMenuAdapter(this,adapterArray,"Menus"));

        bnv = (BottomNavigationView)findViewById(R.id.bnv);
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.cart:
                        selectme(0);
                        Intent intent = new Intent(context,AddtoCart.class);
                        startActivity(intent);
                        break;
                    case R.id.home:
                        break;
                    case R.id.orderstatus:
                        selectme(2);
                        Intent intent1 = new Intent(context,UserProfile.class);
                        startActivity(intent1);
                        break;

                }
                return true;
            }
        });
        selectme(1);
    }

    public void selectme(int id){
        bnv.getMenu().getItem(id).setChecked(true);
    }

    public void addtosharedpreferences(String cartstring){
        SharedPreferences prefs = getSharedPreferences("com.example.root.khanabot", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("mycart",cartstring);
        editor.commit();
        //Log.e("error onpause","saved to preferences");
    }

    @Override
    protected void onPause() {
        super.onPause();
        addtosharedpreferences(HomePage.mycart.toString());
    }
    public void notifychange(){
        ((TextView)findViewById(R.id.carttext)).setText(HomePage.mycart.length()+"");
    }
    @Override
    public void onResume(){
        super.onResume();
        notifychange();
        selectme(1);
        catmenu.setAdapter(new VerticalMenuAdapter(this,adapterArray,"Menus"));

    }
}
