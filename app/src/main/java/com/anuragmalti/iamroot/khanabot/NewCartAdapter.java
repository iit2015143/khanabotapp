package com.anuragmalti.iamroot.khanabot;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
            JSONArray mode = restaurantcart.getJSONObject(position).getJSONArray("mode");
            holder.cod.setVisibility(View.GONE);
            holder.book.setVisibility(View.GONE);
            for(int i =0; i<mode.length(); i++){
                if(mode.getString(i).equals("cod")){
                    holder.cod.setVisibility(View.VISIBLE);
                }
                else{
                    holder.book.setVisibility(View.VISIBLE);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String[] items = new String[]{"Usual", "40mins", "1hour","2hour"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.layout_spinner_item, items);
        holder.spinner.setAdapter(adapter);

        holder.editOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NewCart)context).editOrder(position);
            }
        });

        holder.offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NewCart)context).openOffer(position);
            }
        });
        holder.orderRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("error arguments",holder.cod.isChecked() +
                holder.spinner.getSelectedItem().toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurantcart.length();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView resname,summary,totalrest,offer;
        public RadioButton cod, book;
        public Spinner spinner;
        public Button editOrder,orderRequest;
        public RadioGroup radioGroup;
        public View view;
        public MyViewHolder(View view) {
            super(view);
            this.view = view;
            orderRequest = view.findViewById(R.id.orderRequest);
            spinner = view.findViewById(R.id.spinner);
            cod = view.findViewById(R.id.cod);
            book = view.findViewById(R.id.book);
            offer = view.findViewById(R.id.offer);
            resname = view.findViewById(R.id.resname);
            radioGroup = view.findViewById(R.id.mode);
            summary = view.findViewById(R.id.summary);
            editOrder = view.findViewById(R.id.editOrder);
            totalrest = view.findViewById(R.id.totalRest);

        }

    }
}

