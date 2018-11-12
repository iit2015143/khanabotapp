package com.anuragmalti.iamroot.khanabot;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RestaurantOneFrag extends Fragment {

    RecyclerView recyclerView;
    Context context;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = (View) inflater.inflate(
                R.layout.restaurant_one_frag, container, false);
        ImageView image = rootView.findViewById(R.id.image);

        context= getActivity();

        recyclerView = (RecyclerView)(rootView.findViewById(R.id.mymenu));

        try {
            if(RestaurantProfile.restaurantobj.has("deliversin")){
                String deliversin = RestaurantProfile.restaurantobj.getString("deliversin")+"min";
                ((TextView)rootView.findViewById(R.id.deliversin)).setText(deliversin);
            }
            String resname = RestaurantProfile.restaurantobj.getString("name");
            Picasso.with(context).load(RestClient.BASE_URL+"/"+RestaurantProfile.restaurantobj.getString("image")).into(image);

            ((TextView)rootView.findViewById(R.id.resname)).setText(resname);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        setadaptertomenu();
        return rootView;
    }

    public void setadaptertomenu(){
        MenuExpandableAdapter mCrimeExpandableAdapter = new MenuExpandableAdapter(context, generateCrimes());
        mCrimeExpandableAdapter.setCustomParentAnimationViewId(R.id.parent_list_item_expand_arrow);
        mCrimeExpandableAdapter.setParentClickableViewAnimationDefaultDuration();
        mCrimeExpandableAdapter.setParentAndIconExpandOnClick(true);
        recyclerView.setAdapter(mCrimeExpandableAdapter);
    }

    private ArrayList<ParentObject> generateCrimes() {
        ArrayList<ParentObject> crimes = new ArrayList<>();
        try {
            JSONObject restaurantobj = RestaurantProfile.restaurantobj;
            JSONObject menu = restaurantobj.getJSONObject("menu");
            JSONArray menunames = menu.names();
            for(int j=0; j<menunames.length();j++){
                Menu menuuu = new Menu(menunames.getString(j));
                ArrayList<Object> childList = new ArrayList<>();
                JSONObject leveltwo = menu.getJSONObject(menunames.getString(j));
                if(leveltwo.length()==0)
                    continue;
                JSONArray leveltwonames = leveltwo.names();
                for(int l=0; l<leveltwonames.length();l++){
                    //if(!(leveltwonames.getString(l).equals("Category"))){
                        JSONObject item = new JSONObject(leveltwo.getJSONObject(leveltwonames.getString(l)).toString());
                        item.put("name",leveltwonames.getString(l));
                        //item.put("price",leveltwo.getJSONArray(leveltwonames.getString(l)));
                        item.put("resname",restaurantobj.getString("name"));
                        item.put("number",restaurantobj.getString("number"));
                        item.put("levelone","menu");
                        item.put("leveltwo",menunames.getString(j));
                        item.put("quantity",0);
                        //item.put("leveltwo",leveltwonames.getString(l));
                        childList.add(new MenuChild(item));
                    //}
                }
                menuuu.setChildObjectList(childList);
                crimes.add(menuuu);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return crimes;
    }
    @Override
    public void onResume(){
        super.onResume();
        setadaptertomenu();
    }
}
