package com.anuragmalti.iamroot.khanabot;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OrderhistoryAdapter extends RecyclerView.Adapter<OrderhistoryAdapter.MyViewHolder>{

    public Context context;
    public JSONArray orderhistory;

    public OrderhistoryAdapter(Context context,JSONArray hot){
        this.context = context;
        orderhistory = hot;
    }

    @NonNull
    @Override
    public OrderhistoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderhistoryitem,parent,false);
        return new OrderhistoryAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        try {
            JSONObject perorder = orderhistory.getJSONObject(position);
            holder.status.setText(perorder.getString("status"));
            holder.summary.setText(perorder.getString("summary"));
            holder.orderid.setText(perorder.getString("id"));
            holder.total.setText(perorder.getString("total"));
            holder.number.setText(perorder.getString("tonumber"));
            holder.resname.setText(perorder.getJSONArray("order").getJSONObject(0).getString("resname"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return orderhistory.length();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView orderid,summary,total,status,number,resname;
        public View view;
        public MyViewHolder(View view) {
            super(view);
            this.view = view;
            orderid = (TextView)(view.findViewById(R.id.orderid));
            summary=(TextView)(view.findViewById(R.id.summary));
            total=(TextView)(view.findViewById(R.id.total));
            status = (TextView)(view.findViewById(R.id.status));
            number =(TextView)(view.findViewById(R.id.number));
            resname = (TextView)(view.findViewById(R.id.resname));
        }

    }
}