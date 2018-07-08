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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VerticalMenuAdapter extends RecyclerView.Adapter<VerticalMenuAdapter.MyViewHolder>{

    public Context context;
    public JSONArray nothotdeals;
    public String decider;
    public VerticalMenuAdapter verticalMenuAdapter;

    public VerticalMenuAdapter(Context context,JSONArray hot,String decider){
        this.context = context;
//        try {
//            nothotdeals = new JSONArray(hot.toString());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        nothotdeals = hot;
        this.decider = decider;
        verticalMenuAdapter = this;
    }

    @NonNull
    @Override
    public VerticalMenuAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menuchild,parent,false);
        return new VerticalMenuAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        try {
            final JSONObject nothotdeal = nothotdeals.getJSONObject(position);
            holder.foodname.setText(nothotdeal.getString("name"));
            holder.nameofrest.setText(nothotdeal.getString("resname"));
            final JSONArray price = nothotdeal.getJSONArray("price");
            holder.price.setText("Rs "+ price.getString(price.length()-1));
            switch (decider){
                case "AddtoCart":
                    holder.change.setText(nothotdeal.getInt("quantity")+"");
                    holder.price.setText("Rs "+ price.getString(nothotdeal.getInt("index")));
                    break;
            }
            nothotdeal.put("index",price.length()-1);
            if(price.length()==1){
                holder.radiovisible.setVisibility(View.GONE);
            }
            else{
                holder.radiovisible.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if(checkedId==R.id.full) {
                            try {
                                holder.price.setText("Rs "+ price.getString(price.length()-1));
                                nothotdeal.put("index",price.length()-1);
                                holder.change.setText(0+"");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else if(checkedId ==R.id.half){
                            try {
                                holder.price.setText("Rs "+ price.getString(0));
                                nothotdeal.put("index",0);
                                holder.change.setText(0+"");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }

            holder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer value = Integer.parseInt(holder.change.getText().toString());
                    value++;
                    holder.change.setText(value.toString());
                    HomePage.updatecart(true,nothotdeal);
                    //Log.e("error nothot",nothotdeal.toString());
                    switch (decider){
                        case "AddtoCart":
                            try {
                                ((AddtoCart)context).notifychange();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "Menus":
                            ((Menus)context).notifychange();
                            break;
                    }
                }
            });

            holder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer value = Integer.parseInt(holder.change.getText().toString());
                    if(value>0) {
                        value--;
                        holder.change.setText(value.toString());
                        HomePage.updatecart(false,nothotdeal);
                        //Log.e("error nothot",nothotdeal.toString());
                    }
                    switch (decider){
                        case "AddtoCart":
                            try {
                                ((AddtoCart)context).notifychange();
                                if(value==0){
                                    //notifyDataSetChanged();
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position,nothotdeals.length());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "Menus":
                            ((Menus)context).notifychange();
                            break;
                    }
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
        public RadioButton half,full;
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
            half = (RadioButton)(view.findViewById(R.id.half));
            full = (RadioButton)(view.findViewById(R.id.full));
            radiovisible = (RadioGroup)(view.findViewById(R.id.radiovisible));
            switch (decider){
                case "AddtoCart":
                    radiovisible.setVisibility(View.GONE);
                    break;
            }
        }

    }
}
