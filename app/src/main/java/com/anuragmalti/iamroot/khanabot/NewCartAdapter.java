package com.anuragmalti.iamroot.khanabot;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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

        int revisedTotal = -1;
        try {
            final JSONObject restaurantobject = restaurantcart.getJSONObject(position);
            JSONArray order = restaurantobject.getJSONArray("order");
            String summary = "";
            for(int i =0; i<order.length(); i++){
                summary += order.getJSONObject(i).getString("name").replaceAll("_"," ") + " x " +
                        order.getJSONObject(i).getInt("quantity") + " = " +
                        order.getJSONObject(i).getInt("quantity")*
                                order.getJSONObject(i).getInt("price")+ "\n";
            }
            holder.resname.setText(restaurantobject.getString("resname"));
            holder.summary.setText(summary);

            holder.totalrest.setText("Rs "+restaurantobject.getInt("total"));
            JSONArray mode = restaurantobject.getJSONArray("restmode");
            holder.cod.setVisibility(View.GONE);
            holder.book.setVisibility(View.GONE);
            holder.orderRequest.setEnabled(true);
            for(int i =0; i<mode.length(); i++){
                if(mode.getString(i).equals("cod")){
                    holder.cod.setVisibility(View.VISIBLE);
                }
                else{
                    holder.book.setVisibility(View.VISIBLE);
                    if(i==0){
                        holder.book.setChecked(true);
                    }
                }
            }
            if(restaurantobject.has("offer")){
                holder.offer.setText(restaurantobject.getJSONObject("offer").getString("name"));
                if(isApplicable(restaurantobject.getJSONObject("offer"),restaurantobject)){
                    holder.notApplicable.setVisibility(View.GONE);
                    revisedTotal = getDiscount(restaurantobject.getJSONObject("offer"),restaurantobject);
                    restaurantobject.put("revisedTotal",revisedTotal);
                    String prev = "<font color='#CCCCCC'>"+"Rs "+restaurantobject.getString("total")+"</font>";
                    holder.totalrest.setText(Html.fromHtml(prev +"<br/>"+
                    "Rs "+restaurantobject.getString("revisedTotal")));
                }
                else{
                    holder.notApplicable.setVisibility(View.VISIBLE);
                    restaurantobject.remove("offer");
                    restaurantobject.remove("revisedTotal");
                }
            }

//            holder.totalrest.setText("Rs " + restaurantcart.getJSONObject(position).getInt("total"));
//            if(HomePage.mycart.getJSONObject(position).getString("revisedTotal").compareTo("0")!= 0){
//                holder.totalrest.setText("Rs "+restaurantcart.getJSONObject(position).getInt("revisedTotal"));
//            }else {
//                holder.totalrest.setText("Rs " + restaurantcart.getJSONObject(position).getInt("total"));
//            }

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
                try {
                    holder.totalrest.setText("Rs " + restaurantcart.getJSONObject(position).getInt("total"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        holder.orderRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("error arguments",holder.cod.isChecked() +
                holder.spinner.getSelectedItem().toString());
                try {
                    restaurantobject.put("time",holder.spinner.getSelectedItem().toString());
                    restaurantobject.put("mode",holder.cod.isChecked()?"cod":"book");
                    restaurantobject.put("status","Pending");
                    ((NewCart)context).makeRequest(position);
                    holder.orderRequest.setEnabled(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        } catch (JSONException e) {
            Log.e("error json",e.toString());
            e.printStackTrace();
        }
    }

    public boolean isApplicable(JSONObject offer, JSONObject restaurant){
        try {
            if(restaurant.getInt("total")>=offer.getInt("minValue")){
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("error json",e.toString());
        }
        return false;
    }

    public int getDiscount(JSONObject offer, JSONObject restaurant){
        try {
            int total = Integer.parseInt(restaurant.getString("total"));
            String name = offer.getString("name");
            if(name.contains("OFF")){
                String discount = name.substring(3,name.length());
                int disc = Integer.parseInt(discount);
                int discTotal = (disc*total)/100;
                if(offer.getInt("maxDiscount")!=-1 && discTotal > offer.getInt("maxDiscount")){
                    discTotal = Integer.parseInt(offer.getString("maxDiscount"));
                }
                int revisedTotal = total - discTotal;
                Log.e("error discount","discount is "+ discTotal + "revised total is " + revisedTotal + "total is "+ total);
                return revisedTotal;
            }

            else if(name.contains("CASH")){
                String cashBack = name.substring(4,name.length());
                int disc = Integer.parseInt(cashBack);
                int revisedTotal = total - disc;
                Log.e("disc","discount is "+ disc + "revised total is " + revisedTotal);
                return revisedTotal;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }


    @Override
    public int getItemCount() {
        return restaurantcart.length();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView resname,summary,totalrest,offer,notApplicable;
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
            notApplicable = view.findViewById(R.id.notApplicable);
        }

    }
}

