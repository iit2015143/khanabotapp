package com.anuragmalti.iamroot.khanabot;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EditOfferAdapter extends RecyclerView.Adapter<EditOfferAdapter.MyViewHolder> {

    public Context context;
    public JSONArray nothotdeals;
    public String decider;
    private EditOfferAdapter editOfferAdapter;
    private RadioButton lastCheckedRB = null;
    private JSONObject offer = new JSONObject();
    private int positionOfParent;
    int revisedTotal,total,disc,tempTotal,discTotal;

    public EditOfferAdapter(Context context,JSONArray hot, int value){
        this.context = context;
        this.nothotdeals = hot;
        this.decider = decider;
        editOfferAdapter = this;
        this.positionOfParent = value;
    }


    @NonNull
    @Override
    public EditOfferAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.editoffer,parent,false);
        return new EditOfferAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final EditOfferAdapter.MyViewHolder holder, final int position) {
        Log.e("error", nothotdeals.toString());
        try {
            if(nothotdeals.getJSONObject(position).has("name")) {
                //Toast.makeText(context,"got name", Toast.LENGTH_SHORT).show();
                holder.name.setText(nothotdeals.getJSONObject(position).getString("name"));
                if(nothotdeals.getJSONObject(position).getString("minValue").compareTo("-1") != 0){
                    holder.minValue.setText("Minimum bill for discount: "+nothotdeals.getJSONObject(position).getString("minValue"));
                }
                else{
                    holder.minValue.setText("Minimumn bill");
                }
                if(nothotdeals.getJSONObject(position).getString("maxDiscount").compareTo("-1")!= 0){
                    holder.maxDiscount.setText("Maximum Discount :  " + nothotdeals.getJSONObject(position).getString("maxDiscount"));
                }
                else{
                    holder.maxDiscount.setText("Maximum Discount : no limit");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        int id = (position);

        RadioButton rb = new RadioButton(context);
        rb.setId(id);
            //rb.setText(price);

        holder.radioGroup.addView(rb);


        holder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checked_rb = (RadioButton) group.findViewById(checkedId);
                checked_rb.setChecked(true);
                if (lastCheckedRB != null) {
                    lastCheckedRB.setChecked(false);

                }
                //store the clicked radiobutton
                lastCheckedRB = checked_rb;
                //lastCheckedRB.setChecked(true);
                try {
                    //redundancy
                    offer.put("name",nothotdeals.getJSONObject(lastCheckedRB.getId()).getString("name"));
                    offer.put("minValue",nothotdeals.getJSONObject(lastCheckedRB.getId()).getString("minValue"));
                    offer.put("maxDiscount",nothotdeals.getJSONObject(lastCheckedRB.getId()).getString("maxDiscount"));
                    offer = nothotdeals.getJSONObject(lastCheckedRB.getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.e("offer value",offer.toString());
                ((EditOfferPopUp)context).setOfferValue(offer);

               // HomePage.mycart.put(offer);
                if(!isApplicable(offer,holder)){

                    try {
                        HomePage.mycart.getJSONObject(positionOfParent).remove("revisedTotal");
                        HomePage.mycart.getJSONObject(positionOfParent).remove("offer");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("error json",e.toString());
                    }

                }
                else {

                    revisedTotal = getDiscount(offer,holder);
                    try {
                        HomePage.mycart.getJSONObject(positionOfParent).put("revisedTotal", revisedTotal);
                        HomePage.mycart.getJSONObject(positionOfParent).put("offer",offer);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }
        });
    }

    public boolean isApplicable(JSONObject offer, MyViewHolder holder){
        try {
            JSONObject restaurant = HomePage.mycart.getJSONObject(positionOfParent);
            if(restaurant.getInt("total")>=offer.getInt("minValue")){
                if(offer.has("mode")){
                    if(offer.getJSONArray("mode").length()==2)
                        return true;
                    else{
                        Log.e("offer error",offer.getJSONArray("mode").getString(0)+ " "+restaurant.getString("mode"));
                        if(offer.getJSONArray("mode").getString(0).equals(restaurant.getString("mode")))
                            return true;

                        holder.error.setVisibility(View.VISIBLE);
                        holder.error.setText("Offer not applicable on " + restaurant.getString("mode"));
                        return false;
                    }
                }
                return true;
            }
            holder.error.setVisibility(View.VISIBLE);
            holder.error.setText("Your bill does not reach minimum amount of Rs "+offer.getString("minValue"));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("error json",e.toString());
        }
        return false;
    }

    public int getDiscount(JSONObject offer, MyViewHolder holder){
        if(isApplicable(offer,holder)){
            try {
                total = Integer.parseInt(HomePage.mycart.getJSONObject(positionOfParent).getString("total"));
                String name = offer.getString("name");
                if(name.contains("OFF")){
                    String discount = name.substring(3,name.length());
                    disc = Integer.parseInt(discount);
                    discTotal = (disc*total)/100;
                    if(offer.getInt("maxDiscount")!=-1 && discTotal > offer.getInt("maxDiscount")){
                        discTotal = Integer.parseInt(offer.getString("maxDiscount"));
                        Log.e("revisedTotal1", " "+ revisedTotal);
                    }
                    revisedTotal = total - discTotal;
                    Log.e("error discount","discount is "+ discTotal + "revised total is " + revisedTotal + "total is "+ total);
                    return revisedTotal;
                }

                else if(name.contains("CASH")){
                    String cashBack = name.substring(4,name.length());
                    disc = Integer.parseInt(cashBack);
                    revisedTotal = total - disc;
                    Log.e("disc","discount is "+ disc + "revised total is " + revisedTotal);
                    return revisedTotal;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }


    @Override
    public int getItemCount() {
        return nothotdeals.length();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView name,maxDiscount,minValue,error;
        public View view;
        public RadioButton offer;
        public RadioGroup radioGroup;
        public MyViewHolder(View view) {
            super(view);
            this.view = view;
            radioGroup = view.findViewById(R.id.offer);
            name = view.findViewById(R.id.name);
            maxDiscount = view.findViewById(R.id.maxDiscount);
            minValue = view.findViewById(R.id.minOrder);
            error = view.findViewById(R.id.error);
        }

    }



}
