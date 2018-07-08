package com.anuragmalti.iamroot.khanabot;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class HorizontalCategory extends RecyclerView.Adapter<HorizontalCategory.MyViewHolder>{

    public Context context;
    public ArrayList<String> category;

    public HorizontalCategory(Context context, ArrayList<String> category){
        this.context = context;
        this.category= category;
    }

    @NonNull
    @Override
    public HorizontalCategory.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category,parent,false);
        return new HorizontalCategory.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONArray responseArray = ((HomePage)context).responseArray;
                JSONArray catmenu=new JSONArray();
                //Log.e("error category",responseArray.toString());
                for(int i=0; i<responseArray.length();i++){
                    try {
                        JSONObject restaurantobj = responseArray.getJSONObject(i);
                        JSONObject menu = restaurantobj.getJSONObject("menu");
                        JSONArray menunames = menu.names();
                        for(int j=0; j<menunames.length();j++){
                            JSONObject leveltwo = menu.getJSONObject(menunames.getString(j));
                            JSONArray Category = leveltwo.getJSONArray("Category");
                            for(int k=0; k<Category.length();k++){
                                if(Category.getString(k).equals(category.get(position))){
                                    JSONArray leveltwonames = leveltwo.names();
                                    for(int l=0; l<leveltwonames.length();l++){
                                        if(!(leveltwonames.getString(l).equals("Category"))){
                                            JSONObject item = new JSONObject();
                                            item.put("name",leveltwonames.getString(l));
                                            item.put("price",leveltwo.getJSONArray(leveltwonames.getString(l)));
                                            item.put("resname",restaurantobj.getString("name"));
                                            item.put("number",restaurantobj.getString("number"));
                                            item.put("levelone","menu");
                                            item.put("leveltwo",menunames.getString(j));
                                            item.put("leveltwo",leveltwonames.getString(l));
                                            catmenu.put(item);
                                        }
                                    }
                                }
                            }
                        }

                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                        //Log.e("error catch",e.toString());
                    }
                }
                //Log.e("error cat",catmenu.toString());
                Menus.adapterArray=catmenu;
                ((HomePage)context).categoryclicked(category.get(position));
            }
        });
        holder.category.setText(category.get(position));
    }

    @Override
    public int getItemCount() {
        return category.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView category;
        public View view;
        public MyViewHolder(View view) {
            super(view);
            category = (TextView) view.findViewById(R.id.category);
            this.view = view;
        }
    }
}