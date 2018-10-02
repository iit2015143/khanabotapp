package com.anuragmalti.iamroot.khanabot;

import android.content.Context;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EditOrderAdapter extends RecyclerView.Adapter<EditOrderAdapter.MyViewHolder>{

    public Context context;
    public JSONArray nothotdeals;
    public String decider;
    public EditOrderAdapter verticalMenuAdapter;
    public String restNumber;

    public EditOrderAdapter(Context context,JSONArray hot, String restNumber){
        this.context = context;
        nothotdeals = hot;
        this.decider = decider;
        verticalMenuAdapter = this;
        this.restNumber = restNumber;
    }

    @NonNull
    @Override
    public EditOrderAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menuchild,parent,false);
        return new EditOrderAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position){

        try {
            final JSONObject nothotdeal = nothotdeals.getJSONObject(position);
            holder.foodname.setText(nothotdeal.getString("name").replaceAll("_"," "));
            holder.nameofrest.setText("");
            int price = nothotdeal.getInt("price");
            holder.price.setText("Rs "+ price);
            Log.e("error edit order",nothotdeal.toString());
            holder.change.setText(nothotdeal.getString("quantity"));

            holder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer value = Integer.parseInt(holder.change.getText().toString());
                    value++;
                    holder.change.setText(value.toString());
                    try {
                        ((PopUpEditOrder)context).updatecart(true,(new JSONObject(nothotdeal.toString())).
                                put("number",restNumber));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.e("error cart","update cart request made");
                    Toast.makeText(context,"why aint u printing",Toast.LENGTH_SHORT).show();
//                    try {
//                        ((Cart)context).notifychange();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                }
            });

            holder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer value = Integer.parseInt(holder.change.getText().toString());
                    if(value>0) {
                        value--;
                        holder.change.setText(value.toString());
                        try {
                            ((PopUpEditOrder)context).updatecart(false,(new JSONObject(nothotdeal.toString())).
                                    put("number",restNumber));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ////Log.e("error nothot",nothotdeal.toString());
                    }
//                    try {
//                        ((Cart)context).notifychange();
                        if(value==0){
                            //notifyDataSetChanged();
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position,nothotdeals.length());
                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return nothotdeals.length();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView add,remove;
        public TextView change,foodname,nameofrest,price;
        public View view;
        public RadioGroup radiovisible;
        public MyViewHolder(View view) {
            super(view);
            this.view = view;
            change = (TextView)(view.findViewById(R.id.change));
            add = (ImageView)(view.findViewById(R.id.add));
            remove = (ImageView)(view.findViewById(R.id.remove));
            foodname=(TextView)(view.findViewById(R.id.foodname));
            nameofrest=(TextView)(view.findViewById(R.id.resname));
            price = (TextView)(view.findViewById(R.id.price));
            radiovisible = (RadioGroup)(view.findViewById(R.id.radiovisible));
            radiovisible.setVisibility(View.GONE);
        }

    }
}
