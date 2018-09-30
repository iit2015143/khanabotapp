package com.anuragmalti.iamroot.khanabot;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

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
            DateFormat curdate = new SimpleDateFormat("dd-MM-yy");
            String curdatestr = curdate.format(date);
            dateFormatted += ("\n" + curdatestr);
            holder.status.setText(perorder.getString("status"));
            switch (perorder.getString("status")){
                case "Pending":
                    holder.view.setBackground(ContextCompat.getDrawable(context, R.drawable.orderbackground));
                    break;
                case "Accepted":
                    holder.view.setBackground(ContextCompat.getDrawable(context, R.drawable.orderbackgroundgreen));
                    break;
                case "Outfordelivery":
                    holder.view.setBackground(ContextCompat.getDrawable(context, R.drawable.orderbackgrounddarkgreen));
                    break;
                case "Declined":
                    holder.view.setBackground(ContextCompat.getDrawable(context, R.drawable.orderbackgroundred));
                    break;
            }
            if(perorder.getString("status").equals("Pending")){
                holder.cancelorder.setVisibility(View.VISIBLE);
                holder.cancelorder.setEnabled(true);
                holder.cancelorder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id = null;
                        try {
                            id = perorder.getString("id");
                            String status = "Declined";
                            String number = perorder.getString("fromnumber");
                            String tonumber = perorder.getString("tonumber");
                            holder.cancelorder.setEnabled(false);
                            ((OrderHistory)context).sendrequest(id,status,number,tonumber);
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
