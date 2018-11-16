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

public class VerticalMenuAdapter extends RecyclerView.Adapter<VerticalMenuAdapter.MyViewHolder>{

    public Context context;
    public JSONArray nothotdeals;
    public String decider;
    public VerticalMenuAdapter verticalMenuAdapter;

    public VerticalMenuAdapter(Context context,JSONArray hot,String decider){
        this.context = context;
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
            holder.foodname.setText(nothotdeal.getString("name").replaceAll("_"," "));
            holder.nameofrest.setText(nothotdeal.getString("resname").replaceAll("_"," "));
            final JSONArray price = nothotdeal.getJSONArray("price");
            holder.price.setText("Rs "+ price.getString(price.length()-1));
            holder.change.setText(nothotdeal.getInt("quantity")+"");
            switch (decider){
                case "Cart":
                    holder.price.setText("Rs "+ price.getString(nothotdeal.getInt("index")));
                    break;
                case "ItemsByCategory":
                    nothotdeal.put("index",price.length()-1);
                    holder.radiovisible.setVisibility(View.VISIBLE);
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


            }

            holder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer value = null;
                    try {
                        value = nothotdeal.getInt("quantity");
                        value++;
                        nothotdeal.put("quantity",value);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    holder.change.setText(value.toString());
                    HomePage.updatecart(true,nothotdeal);
                    Log.e("error cart","update cart request made");
                    //Toast.makeText(context,"why aint u printing",Toast.LENGTH_SHORT).show();
                    switch (decider){
                        case "Cart":
                            try {
                                ((Cart)context).notifychange();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "ItemsByCategory":
                            ((ItemsByCategory)context).notifychange();
                            break;
                    }
                }
            });

            holder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer value = null;
                    try {
                        value = nothotdeal.getInt("quantity");
                        if(value>0) {
                            value--;
                            nothotdeal.put("quantity",value);
                            holder.change.setText(value.toString());
                            HomePage.updatecart(false,nothotdeal);
                            ////Log.e("error nothot",nothotdeal.toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    switch (decider){
                        case "Cart":
                            try {
                                ((Cart)context).notifychange();
                                if(value==0){
                                    //notifyDataSetChanged();
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position,nothotdeals.length());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "ItemsByCategory":
                            ((ItemsByCategory)context).notifychange();
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
                case "Cart":
                    radiovisible.setVisibility(View.GONE);
                    break;
            }
        }

    }
}
