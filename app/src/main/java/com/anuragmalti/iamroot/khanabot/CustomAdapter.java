package com.anuragmalti.iamroot.khanabot;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Iterator;

public class CustomAdapter extends BaseAdapter {
    Context context;
    JSONObject data;
    private static LayoutInflater inflater=null;
    public CustomAdapter(Activity mainActivity, JSONObject data) {

        context=mainActivity;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        //return messlist.length();
        return data.length();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView cat_name;
        LinearLayout cat_image;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.pageroffers, null);
//        holder.cat_name = (TextView)rowView.findViewById(R.id.cat_name) ;
//        holder.cat_image=(LinearLayout) rowView.findViewById(R.id.cat_image);
        holder.cat_name.setText(get(position));
        //Log.e("Object from list",get(position));

//        try {
//            holder.messname.setText(messlist.getJSONObject(position).getString("messname"));
//            holder.rating.setText(/*messlist.getJSONObject(position).getString("rating")*/"Rating : 5");
//            holder.price.setText(messlist.getJSONObject(position).getString("fee"));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
//                Intent intent = new Intent(context,MessView.class);
//                JSONObject mess;

                try {
//                    mess = messlist.getJSONObject(position);
//                    String message = mess.toString();
//                    intent.putExtra("message", message);
//                    context.startActivity(intent);
                    Toast.makeText(context,"I am position num "+position , Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        return rowView;
    }

    public String get(int position){
        Iterator<String> keys = data.keys();
        int i = 0;
        while(keys.hasNext()){
            if(position== i)
                return keys.next();
            keys.next();
            i++;
        }
        return null;
    }

}
