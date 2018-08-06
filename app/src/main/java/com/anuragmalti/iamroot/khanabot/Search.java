package com.anuragmalti.iamroot.khanabot;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Search extends Activity {

    public Context context;
    public FilterableSearchAdapter adapter;
    public RecyclerView recyclerView;
    public static JSONArray responseArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        context = this;
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));


        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView)findViewById(R.id.searchview);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((android.support.v7.widget.SearchView)v).onActionViewExpanded();
            }
        });

        EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.base));
//        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(hasFocus){
//                    Toast.makeText(context,"Made visible",Toast.LENGTH_SHORT).show();
//                    recyclerView.setVisibility(View.VISIBLE);
//                    v.setBackground(ContextCompat.getDrawable(context,R.drawable.roundedcornerfacebook));
//                }
//                else {
//                    Toast.makeText(context,"Made invisible",Toast.LENGTH_SHORT).show();
//                    recyclerView.setVisibility(View.INVISIBLE);
//                    v.setBackground(ContextCompat.getDrawable(context,R.drawable.searchbarbg));
//                }
//            }
//        });

        JSONArray entiremenu=new JSONArray();
        ////Log.e("error category",responseArray.toString());
        for(int i=0; i<responseArray.length();i++){
            try {
                JSONObject restaurantobj = responseArray.getJSONObject(i);
                JSONObject menu = restaurantobj.getJSONObject("menu");
                JSONArray menunames = menu.names();
                for(int j=0; j<menunames.length();j++){
                    JSONObject leveltwo = menu.getJSONObject(menunames.getString(j));
                    if(leveltwo.length()==0)
                        continue;
                    //Log.e("leveltwo",leveltwo.toString());
                    JSONArray leveltwonames = leveltwo.names();
                    for(int l=0; l<leveltwonames.length();l++){
                        JSONObject item = new JSONObject(leveltwo.getJSONObject(leveltwonames.getString(l)).toString());

                        item.put("name",leveltwonames.getString(l));
                        item.put("resname",restaurantobj.getString("name"));
                        item.put("number",restaurantobj.getString("number"));
                        item.put("levelone","menu");
                        item.put("leveltwo",menunames.getString(j));
                        entiremenu.put(item);
                    }
                }

            }
            catch (JSONException e) {
                e.printStackTrace();
                ////Log.e("error catch",e.toString());
            }
        }

        adapter = new FilterableSearchAdapter(this,entiremenu);
        recyclerView.setAdapter(adapter);
        
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Log.e("textchanged", "text changed right now");
                adapter.getFilter().filter(newText);
                return false;
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


}
