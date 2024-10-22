package com.anuragmalti.iamroot.khanabot;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class HorizontalRestaurants extends RecyclerView.Adapter<HorizontalRestaurants.MyViewHolder>{

    public Context context;
    public JSONArray restaurantArray;

    public HorizontalRestaurants(Context context, JSONArray restaurantArray){
        this.context = context;
        this.restaurantArray=restaurantArray;
    }

    @NonNull
    @Override
    public HorizontalRestaurants.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant,parent,false);
        return new HorizontalRestaurants.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    RestaurantProfile.restaurantobj= restaurantArray.getJSONObject(position);
                    ((HomePage)context).showrestaurantpage();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        try {
            JSONObject Restaurant = restaurantArray.getJSONObject(position);
            if(Restaurant.has("deliversin")){
                String deliversin = Restaurant.getString("deliversin")+ "min";
                holder.deliversin.setText(deliversin);
            }
            if(Restaurant.has("specifics")){
                String specifics = Restaurant.getString("specifics");
                holder.specifics.setText(specifics);
            }
            Picasso.with(context).load(RestClient.BASE_URL + "/"+Restaurant.getString("image")).into(holder.image);
            holder.resname.setText(Restaurant.getString("name").replaceAll("_"," "));
            holder.rating.setText(Restaurant.getString("rating"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return restaurantArray.length();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView price,resname,specifics,rating,deliversin;
        public ImageView image;
        public View view;
        public MyViewHolder(View view) {
            super(view);
            this.view = view;
            price = (TextView)(view.findViewById(R.id.price));
            resname =(TextView)(view.findViewById(R.id.resname));
            specifics=(TextView)(view.findViewById(R.id.specifics));
            rating=(TextView)(view.findViewById(R.id.rating));
            image = (ImageView)(view.findViewById(R.id.image));
            deliversin = (TextView)(view.findViewById(R.id.deliversin));
        }
    }
}
