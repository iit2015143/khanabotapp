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

public class TrueOverview extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = (View) inflater.inflate(
                R.layout.fragment_true_overview, container, false);
        Context context= getActivity();
        try {
            String resname = RestaurantProfile.restaurantobj.getString("name");
            ((TextView)rootView.findViewById(R.id.resname)).setText(resname);
            String number = RestaurantProfile.restaurantobj.getString("number");
            ((TextView)rootView.findViewById(R.id.number)).setText(number);
            String rating = RestaurantProfile.restaurantobj.getString("rating");
            ((TextView)rootView.findViewById(R.id.rating)).setText(rating);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rootView;
    }
}
