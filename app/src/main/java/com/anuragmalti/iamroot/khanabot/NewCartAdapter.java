package com.anuragmalti.iamroot.khanabot;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NewCartAdapter extends RecyclerView.Adapter<NewCartAdapter.MyViewHolder>{

    public Context context;
    public JSONArray restaurantcart;
    public String decider;
    public NewCartAdapter NewCartAdapter;

    public NewCartAdapter(Context context,JSONArray hot){
        this.context = context;
        restaurantcart = hot;
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
        try {
            JSONArray order = restaurantcart.getJSONObject(position).getJSONArray("order");
            String summary = "";
            for(int i =0; i<order.length(); i++){
                summary += order.getJSONObject(i).getString("name") + " x " +
                        order.getJSONObject(i).getInt("quantity") + " = " +
                        order.getJSONObject(i).getInt("quantity")*
                                order.getJSONObject(i).getInt("price")+ ", ";
            }
            holder.resname.setText(restaurantcart.getJSONObject(position).getString("resname"));
            holder.summary.setText(summary);
            holder.totalrest.setText("Rs "+restaurantcart.getJSONObject(position).getInt("total"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.editOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NewCart)context).editOrder(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurantcart.length();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView resname,summary,totalrest;
        public Button editOrder;
        public View view;
        public MyViewHolder(View view) {
            super(view);
            this.view = view;
            resname = view.findViewById(R.id.resname);
            summary = view.findViewById(R.id.summary);
            editOrder = view.findViewById(R.id.editOrder);
            totalrest = view.findViewById(R.id.totalRest);
        }

    }
}

