package com.anuragmalti.iamroot.khanabot;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FilterableSearchAdapter extends RecyclerView.Adapter<FilterableSearchAdapter.MyViewHolder>
implements Filterable{

    public Context context;
    public JSONArray contactListFiltered;
    public String decider;
    public FilterableSearchAdapter FilterableSearchAdapter;
    public JSONArray nothotdeals;

    public FilterableSearchAdapter(Context context,JSONArray hot){
        this.context = context;
        nothotdeals = hot;
        FilterableSearchAdapter = this;
        contactListFiltered = nothotdeals;
    }

    @NonNull
    @Override
    public FilterableSearchAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menuchild,parent,false);
        return new FilterableSearchAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        try {
            final JSONObject nothotdeal = contactListFiltered.getJSONObject(position);
            Log.e("quantity", nothotdeals.getJSONObject(0).toString());
            holder.foodname.setText(nothotdeal.getString("name").replaceAll("_"," "));
            holder.nameofrest.setText(nothotdeal.getString("resname"));
            final JSONArray price = nothotdeal.getJSONArray("price");
            holder.price.setText("Rs "+ price.getString(price.length()-1));

            holder.radiovisible.setVisibility(View.VISIBLE);
            if(price.length()==1){
                holder.radiovisible.setVisibility(View.GONE);
                holder.change.setText(""+nothotdeal.getJSONArray("quantity").getInt(0));
            }
            else{

                if(nothotdeal.getInt("index")==0) {
                    holder.half.setChecked(true);
                    holder.change.setText("" + nothotdeal.getJSONArray("quantity").getInt(0));
                }
                else {

                    holder.full.setChecked(true);
                    holder.price.setText("Rs "+ price.getString(1));
                    holder.change.setText("" + nothotdeal.getJSONArray("quantity").getInt(1));
                }

                holder.radiovisible.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if(checkedId == R.id.full) {
                            try {
                                holder.price.setText("Rs "+ price.getString(1));
                                nothotdeal.put("index",1);
                                holder.change.setText("" + nothotdeal.getJSONArray("quantity").getInt(1));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else if(checkedId ==R.id.half){
                            try {
                                holder.price.setText("Rs "+ price.getString(0));
                                nothotdeal.put("index",0);
                                holder.change.setText("" + nothotdeal.getJSONArray("quantity").getInt(0));
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
                    try {
                            int index = nothotdeal.getInt("index");
                            JSONArray quantity = nothotdeal.getJSONArray("quantity");
                            Integer value = quantity.getInt(index);
                            value++;
                            quantity.put(index,value);
                            holder.change.setText(value.toString());
                            HomePage.updatecart(true,nothotdeal);
                            Log.e("quantity",nothotdeal.toString());
                            ((Search)context).notifychange();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            holder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    try {
                        int index = nothotdeal.getInt("index");
                        JSONArray quantity = nothotdeal.getJSONArray("quantity");
                        Integer value = quantity.getInt(index);
                        if(value>0) {
                            value--;
                            quantity.put(index,value);
                            holder.change.setText(value.toString());
                            HomePage.updatecart(false,nothotdeal);
                            Log.e("quantity",nothotdeal.toString());
                            ////Log.e("error nothot",nothotdeal.toString());
                        }
                        ((Search)context).notifychange();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("exception", e.toString());
        }

    }

    @Override
    public int getItemCount() {
        return contactListFiltered.length();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    contactListFiltered = nothotdeals;
                } 
                else {
                    try {
                        JSONArray filteredList = new JSONArray();
                        for (int i=0; i<nothotdeals.length();i++) {

                            // name match condition. this might differ depending on your requirement
                            // here we are looking for name or phone number match
                            String[] splited = charString.split("\\s+");
                            for(int j=0; j<splited.length; j++) {

                                if (nothotdeals.getJSONObject(i).getString("name").toLowerCase().contains(splited[j].toLowerCase())) {
                                    if(j==splited.length -1)
                                    filteredList.put(nothotdeals.getJSONObject(i));
                                    //Log.e("filtering", filteredList.toString());
                                }
                                else
                                    break;
                            }
                        }
                        contactListFiltered = filteredList;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                contactListFiltered = (JSONArray)results.values;
                notifyDataSetChanged();
            }
        };
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
        }
    }
}

