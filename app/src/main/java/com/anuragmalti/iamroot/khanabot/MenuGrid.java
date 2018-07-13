package com.anuragmalti.iamroot.khanabot;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MenuGrid extends AppCompatActivity {

    ViewPager pager ;
    Context context;
    private LinearLayout llContainer;
    private android.support.v7.widget.SearchView etSearch;
    private ListView lvProducts;

    private ArrayList<Product> mProductArrayList = new ArrayList<Product>();
    private FilterableAdapter adapter1;
    public static JSONObject data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_grid);
        GridView gridView = (GridView)findViewById(R.id.gridView);
        context = this;
        gridView.setAdapter(new CustomAdapter(this,data));
        initialize();

        pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(new CustomPageAdapter(this,new JSONArray()));

        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) findViewById(R.id.searchview);
        EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.base));
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ListView listView = (ListView)findViewById(R.id.listView);
                if(hasFocus){
                    Toast.makeText(context,"Made visible",Toast.LENGTH_SHORT).show();
                    listView.setVisibility(View.VISIBLE);
                    v.setBackground(ContextCompat.getDrawable(context,R.drawable.roundedcornerfacebook));
                }
                else {
                    Toast.makeText(context,"Made invisible",Toast.LENGTH_SHORT).show();
                    listView.setVisibility(View.INVISIBLE);
                    v.setBackground(ContextCompat.getDrawable(context,R.drawable.searchbarbg));
                }
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    int num = pager.getCurrentItem();

                    if(num+1>2)
                        num=-1;

                    pager.setCurrentItem(num+1);

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        etSearch.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter1.getFilter().filter(newText);
                return false;
            }
        });
    }

    public void searchviewclicked(View v){
        ((android.support.v7.widget.SearchView)v).onActionViewExpanded();
    }

    public void addtocartclicked(View v){
        Intent intent = new Intent(this,AddtoCart.class);
        startActivity(intent);
    }
    private void initialize() {
        etSearch = (android.support.v7.widget.SearchView) findViewById(R.id.searchview);
        lvProducts = (ListView)findViewById(R.id.listView);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();


        try {
            adapter1 = new FilterableAdapter(this, data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        lvProducts.setAdapter(adapter1);
    }
}
