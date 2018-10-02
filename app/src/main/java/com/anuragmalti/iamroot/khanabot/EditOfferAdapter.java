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

//    public void setNothotdeals(JSONArray nothotdeals) {
//        this.nothotdeals = nothotdeals;
//    }

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
                Toast.makeText(context,"got name", Toast.LENGTH_SHORT).show();
                holder.name.setText(nothotdeals.getJSONObject(position).getString("name"));
                if(nothotdeals.getJSONObject(position).getString("minValue").compareTo("-1") != 0){
                    holder.minValue.setText("Min Value of the discount : "+nothotdeals.getJSONObject(position).getString("minValue"));
                }
                if(nothotdeals.getJSONObject(position).getString("minValue").compareTo("-1")!= 0){
                    holder.maxDiscount.setText("Max Discount :  " + nothotdeals.getJSONObject(position).getString("maxDiscount"));
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
                    offer.put("name",nothotdeals.getJSONObject(lastCheckedRB.getId()).getString("name"));
                    offer.put("minValue",nothotdeals.getJSONObject(lastCheckedRB.getId()).getString("minValue"));
                    offer.put("maxDiscount",nothotdeals.getJSONObject(lastCheckedRB.getId()).getString("maxDiscount"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.e("offer value",offer.toString());
                ((EditOfferPopUp)context).setOfferValue(offer);

               // HomePage.mycart.put(offer);
                if(!isApplicable(offer)){
                    holder.error.setVisibility(View.VISIBLE);
                }

                revisedTotal = getDiscount(offer);
                try {
                    HomePage.mycart.getJSONObject(positionOfParent).put("revisedTotal",revisedTotal);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    public boolean isApplicable(JSONObject offer){
        try {
            if(HomePage.mycart.getJSONObject(positionOfParent).getString("total").compareTo( offer.getString("minValue")) > 0){
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getDiscount(JSONObject offer){
        if(isApplicable(offer)){
            try {
                total = Integer.parseInt(HomePage.mycart.getJSONObject(positionOfParent).getString("total"));
                String name = offer.getString("name");
                if(name.contains("OFF")){
                        String discount = name.substring(3,name.length());
                        disc = Integer.parseInt(discount);
                        if(discTotal < Integer.parseInt(offer.getString("maxDiscount")) || (Integer.parseInt(offer.getString("maxDiscount") ) == -1) ) {
                            revisedTotal = ((100 - disc) * total) / 100;
                            Log.e("revisedTotal", " "+ revisedTotal);
                        }else if(discTotal > Integer.parseInt(offer.getString("maxDiscount")) && (Integer.parseInt(offer.getString("maxDiscount") ) != -1)){
                            revisedTotal = total - Integer.parseInt(offer.getString("maxDiscount"));
                            Log.e("revisedTotal1", " "+ revisedTotal);
                        }
                        discTotal = (disc*total)/100;



                        Log.e("discount is","discount is "+ disc + "revised total is " + revisedTotal + "total is "+ total);
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
