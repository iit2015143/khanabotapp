package com.anuragmalti.iamroot.khanabot;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Model.ParentObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Overviewfrag extends Fragment {

    RecyclerView recyclerView;
    Context context;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = (View) inflater.inflate(
                R.layout.fragment_overviewfrag, container, false);
        context= getActivity();

        recyclerView = (RecyclerView)(rootView.findViewById(R.id.mymenu));

        try {
            String resname = RestaurantProfile.restaurantobj.getString("name");
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
                JSONArray leveltwonames = leveltwo.names();
                for(int l=0; l<leveltwonames.length();l++){
                    if(!(leveltwonames.getString(l).equals("Category"))){
                        JSONObject item = new JSONObject();
                        item.put("name",leveltwonames.getString(l));
                        item.put("price",leveltwo.getJSONArray(leveltwonames.getString(l)));
                        item.put("resname",restaurantobj.getString("name"));
                        item.put("number",restaurantobj.getString("number"));
                        item.put("levelone","menu");
                        item.put("leveltwo",menunames.getString(j));
                        item.put("leveltwo",leveltwonames.getString(l));
                        childList.add(new MenuChild(item));
                    }
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
