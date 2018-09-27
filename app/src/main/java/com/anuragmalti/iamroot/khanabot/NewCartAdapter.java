package com.anuragmalti.iamroot.khanabot;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NewCartAdapter extends RecyclerView.Adapter<NewCartAdapter.MyViewHolder>{

    public Context context;
    public JSONArray nothotdeals;
    public String decider;
    public NewCartAdapter NewCartAdapter;

    public NewCartAdapter(Context context,JSONArray hot){
        this.context = context;
        nothotdeals = hot;
        this.decider = decider;
        NewCartAdapter = this;
    }

    @NonNull
    @Override
    public NewCartAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurantcart,parent,false);
        return new NewCartAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView add,remove;
        public TextView change,foodname,nameofrest,price;
        public View view;
        public RadioButton half,full;
        public RadioGroup radiovisible;
        public MyViewHolder(View view) {
            super(view);
            this.view = view;
        }

    }
}

