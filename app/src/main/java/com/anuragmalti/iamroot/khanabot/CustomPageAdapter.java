package com.anuragmalti.iamroot.khanabot;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class CustomPageAdapter extends PagerAdapter {

    private Context context;
    public JSONArray offers;

    public  CustomPageAdapter(Context con,JSONArray offers){
        context = con;
        this.offers = offers;
    }


    @Override
    public int getCount() {
        return offers.length();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.pageroffers, container, false);
        ImageView imagecontainer = (ImageView)(view.findViewById(R.id.image));
        try {
            JSONObject offer = offers.getJSONObject(position);
            ((TextView)((view).findViewById(R.id.offertext))).setText(offer.getString("name"));
            Picasso.with(context).load(RestClient.BASE_URL+"/"+offer.getString("image")).into(imagecontainer);

        } catch (JSONException e) {
            //e.printStackTrace();
            Log.e("error",e.toString());
        }

        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }
}
