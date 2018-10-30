package com.anuragmalti.iamroot.khanabot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ItemsByCategory extends AppCompatActivity {

    public Context context;
    public JSONArray adapterArray;
    public VerticalMenuAdapter adapter;
    public BottomNavigationView bnv;
    RecyclerView catmenu;
    private String title;
    private ShimmerFrameLayout mShimmerViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itembycategory);
        context = this;
        adapterArray=new JSONArray();
                notifychange();
        title = getIntent().getStringExtra("title");
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        mShimmerViewContainer.startShimmerAnimation();
        TextView textView=(TextView)findViewById(R.id.topToolbar);
        textView.setText(title);
        catmenu =(RecyclerView)findViewById(R.id.catmenu);
        catmenu.setLayoutManager(new LinearLayoutManager(ItemsByCategory.this,LinearLayoutManager.VERTICAL,false));

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
                        Intent intent2=new Intent(context,HomePage.class);
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

        //AsyncTask asyncTask=new LoadCategoryData().execute();
    }

    public void selectme(int id){
        bnv.getMenu().getItem(id).setChecked(true);
    }

    public void addtosharedpreferences(String cartstring){
        SharedPreferences prefs = getSharedPreferences("com.example.root.khanabot", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("mycart",cartstring);
        editor.commit();
        ////Log.e("error onpause","saved to preferences");
    }

    @Override
    protected void onPause() {
        super.onPause();
        addtosharedpreferences(HomePage.mycart.toString());
       // mShimmerViewContainer.stopShimmerAnimation();
    }
    public void notifychange(){
        ((TextView)findViewById(R.id.carttext)).setText(HomePage.mycart.length()+"");
        if(HomePage.mycart.length()==0)
            ((RelativeLayout)findViewById(R.id.cartcontainer)).setVisibility(View.INVISIBLE);
        else
            ((RelativeLayout)findViewById(R.id.cartcontainer)).setVisibility(View.VISIBLE);
    }
    @Override
    public void onResume() {
        super.onResume();
        notifychange();
        selectme(1);
        if(mShimmerViewContainer.getVisibility()==View.VISIBLE){
            mShimmerViewContainer.startShimmerAnimation();
        }
        AsyncTask<Void, Void, Void> asyncTask=new LoadCategoryData();
        asyncTask.execute(null,null,null);
    }

    public void getData(int position){
        JSONArray responseArray = HomePage.responseArray;
        JSONArray catmenujson=new JSONArray();
        //Log.d("error category",responseArray.toString());
        for(int i=0; i<responseArray.length();i++){
            try {
                JSONObject restaurantobj = responseArray.getJSONObject(i);
                JSONObject menu = restaurantobj.getJSONObject("menu");
                JSONArray menunames = menu.names();
                for(int j=0; j<menunames.length();j++){
                    JSONObject leveltwo = menu.getJSONObject(menunames.getString(j));
                    if(leveltwo.length()==0)
                        continue;
                    //Log.e("leveltwo",leveltwo.toString());
                    JSONArray leveltwonames = leveltwo.names();
                    for(int l=0; l<leveltwonames.length();l++){
                        JSONObject item = new JSONObject(leveltwo.getJSONObject(leveltwonames.getString(l)).toString());
                        JSONArray Category = item.getJSONArray("category");
                        for(int k=0; k<Category.length();k++){
                            if(Category.getString(k).equals(title)){

                                item.put("name",leveltwonames.getString(l));
                                item.put("resname",restaurantobj.getString("name"));
                                item.put("number",restaurantobj.getString("number"));
                                item.put("levelone","menu");
                                item.put("leveltwo",menunames.getString(j));
                                catmenujson.put(item);
                            }
                        }
                    }
                }

            }
            catch (JSONException e) {
                e.printStackTrace();
                ////Log.e("error catch",e.toString());
            }
        }
        //Log.d("error cat",catmenujson.toString());
        adapterArray=catmenujson;

    }

    class LoadCategoryData extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int position = getIntent().getIntExtra("position", -1);
            getData(position);
            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            adapter=new VerticalMenuAdapter(ItemsByCategory.this,adapterArray,"ItemsByCategory");
            catmenu.setAdapter(adapter);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);

                }
            });
            //Log.d("Post", "called");
        }
    }
}
