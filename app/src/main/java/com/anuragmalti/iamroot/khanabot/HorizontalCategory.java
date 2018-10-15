package com.anuragmalti.iamroot.khanabot;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
           ((HomePage)context).categoryclicked(category.get(position),position);
            }
        });
        holder.category.setText(category.get(position));
        Picasso.with(context).load(RestClient.BASE_URL+"/"+category.get(position)+".jpg").into(holder.image);
    }

    @Override
    public int getItemCount() {
        return category.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView category;
        public View view;
        public ImageView image;
        public MyViewHolder(View view) {
            super(view);
            category = (TextView) view.findViewById(R.id.category);
            image = (ImageView) view.findViewById(R.id.image);
            this.view = view;
        }
    }
}