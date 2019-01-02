package com.anuragmalti.iamroot.khanabot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RestaurantProfile extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public static JSONObject restaurantobj;
    public BottomNavigationView bnv;
    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_profile);
        context=this;
        notifychange();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        bnv = (BottomNavigationView)findViewById(R.id.bnv);
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.cart:
                        selectme(0);
                        Intent intent = new Intent(context,NewCart.class);
                        startActivity(intent);
                        break;
                    case R.id.home:
                        selectme(1);
                        Intent intent2 = new Intent(context,HomePage.class);
                        startActivity(intent2);
                        break;
                    case R.id.orderstatus:
                        selectme(2);
                        Intent intent1 = new Intent(context,OrderHistory.class);
                        startActivity(intent1);
                        break;
                    case R.id.search:
                        selectme(3);
                        Intent intent3 = new Intent(context,Search.class);
                        startActivity(intent3);
                        break;


                }
                return true;
            }
        });
        selectme(1);
        JSONArray jarray = new JSONArray();
        jarray.put(restaurantobj);
        Search.responseArray=jarray;
    }

    public void selectme(int id){
        bnv.getMenu().getItem(id).setChecked(true);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new RestaurantOneFrag(), "Menu");
        adapter.addFragment(new RestaurantTwoFrag(), "Overview");
        viewPager.setAdapter(adapter);
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void addtosharedpreferences(String cartstring){
        SharedPreferences prefs = getSharedPreferences("com.example.root.khanabot", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("mycart",cartstring);
        editor.commit();
        //Log.e("error onpause","saved to preferences");
    }

    @Override
    protected void onPause() {
        super.onPause();
        addtosharedpreferences(HomePage.mycart.toString());
    }

    public void notifychange(){
        int total = 0;
        for(int i=0; i<HomePage.mycart.length(); i++){
            try {
                JSONObject cartobject = HomePage.mycart.getJSONObject(i);
                JSONArray order = cartobject.getJSONArray("order");
                for(int j=0; j<order.length();j++){
                    total +=order.getJSONObject(j).getInt("quantity");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ((TextView)findViewById(R.id.carttext)).setText(total+"");
        if(HomePage.mycart.length()==0)
            ((RelativeLayout)findViewById(R.id.cartcontainer)).setVisibility(View.INVISIBLE);
        else
            ((RelativeLayout)findViewById(R.id.cartcontainer)).setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume(){
        super.onResume();
        notifychange();
        selectme(1);
    }
}
