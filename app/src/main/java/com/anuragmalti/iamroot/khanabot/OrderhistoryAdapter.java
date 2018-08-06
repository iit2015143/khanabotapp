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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

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
            holder.cancelorder.setVisibility(View.GONE);
            final JSONObject perorder = orderhistory.getJSONObject(position);
            long time = perorder.getLong("id");
            time = time/10000;
            Date date = new Date(time);
            DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
            String dateFormatted = formatter.format(date);
            holder.status.setText(perorder.getString("status"));
            if(perorder.getString("status").equals("Pending")){
                holder.cancelorder.setVisibility(View.VISIBLE);
                holder.cancelorder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id = null;
                        try {
                            id = perorder.getString("id");
                            String status = "Declined";
                            String number = perorder.getString("fromnumber");
                            String tonumber = perorder.getString("tonumber");
                            ((UserProfile)context).sendrequest(id,status,number,tonumber);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
            holder.summary.setText(perorder.getString("summary"));
            holder.orderid.setText(perorder.getString("id"));
            holder.total.setText(perorder.getString("total"));
            holder.number.setText(perorder.getString("tonumber"));
            holder.resname.setText(perorder.getJSONArray("order").getJSONObject(0).getString("resname"));
            holder.time.setText(dateFormatted);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return orderhistory.length();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView orderid,summary,total,status,number,resname,time;
        public View view;
        public Button cancelorder;
        public MyViewHolder(View view) {
            super(view);
            this.view = view;
            orderid = (TextView)(view.findViewById(R.id.orderid));
            summary=(TextView)(view.findViewById(R.id.summary));
            total=(TextView)(view.findViewById(R.id.total));
            status = (TextView)(view.findViewById(R.id.status));
            number =(TextView)(view.findViewById(R.id.number));
            resname = (TextView)(view.findViewById(R.id.resname));
            time = (TextView)(view.findViewById(R.id.time));
            cancelorder = (Button)(view.findViewById(R.id.cancel));
        }

    }
}
