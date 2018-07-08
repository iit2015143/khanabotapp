package com.anuragmalti.iamroot.khanabot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bignerdranch.expandablerecyclerview.Model.ParentObject;

import java.util.ArrayList;
import java.util.List;

public class Test extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.mymenu);
        MenuExpandableAdapter mCrimeExpandableAdapter = new MenuExpandableAdapter(this, generateCrimes());
        mCrimeExpandableAdapter.setCustomParentAnimationViewId(R.id.parent_list_item_expand_arrow);
        mCrimeExpandableAdapter.setParentClickableViewAnimationDefaultDuration();
        mCrimeExpandableAdapter.setParentAndIconExpandOnClick(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(mCrimeExpandableAdapter);
    }

    private ArrayList<ParentObject> generateCrimes() {
        List<Menu> crimes = new ArrayList<Menu>();
        crimes.add(new Menu("Chicken"));
        crimes.add(new Menu("Mutton"));
        crimes.add(new Menu("Paneer"));
        crimes.add(new Menu("Biryani"));
        crimes.add(new Menu("Mocktail"));
        crimes.add(new Menu("Cocktail"));
        ArrayList<ParentObject> parentObjects = new ArrayList<>();
        for (Menu crime : crimes) {

//            ArrayList<Object> childList = new ArrayList<>();
//            childList.add(new MenuChild(crime.mdate, crime.state));
//            crime.setChildObjectList(childList);
//            parentObjects.add(crime);

        }
        return parentObjects;
    }
}
