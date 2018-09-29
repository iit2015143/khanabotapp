package com.anuragmalti.iamroot.khanabot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class HomePage extends AppCompatActivity {

    public BottomNavigationView bnv;
    public Context context;
    public ViewPager viewPager;
    public RecyclerView hotdeal;
    public RecyclerView toprated;
    public RecyclerView category;
    public RecyclerView restaurants;
    public JSONArray responseArray=new JSONArray();
    public static JSONArray mycart;
    public static String address;
    public int currenttime;
    public Runnable runnable;
    public Handler refresh;
    public JSONArray Offers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        try {
            initializecart();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        context = this;

        //address = getIntent().getStringExtra("address");
        ((TextView)findViewById(R.id.location)).setText(address);
        notifychange();

        String location = getprefsvalue("location");
        if(location.equals("")){
            //////Log.e("Fatal error","location not found");
        }
        try {
            JSONObject Location = new JSONObject(location.toString());
            double lat = Location.getDouble("lat");
            double longitude = Location.getDouble("long");
            getcurrenttime(lat,longitude);
            //////Log.e("Fatal error","request made");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        viewPager = (ViewPager)findViewById(R.id.viewPager);
        viewPager.setAdapter(new CustomPageAdapter(context,new JSONArray()));


        hotdeal = (RecyclerView)findViewById(R.id.hotdeals);
        hotdeal.setAdapter(new HorizontalHotDeal(this,new JSONArray()));
        hotdeal.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        hotdeal.setNestedScrollingEnabled(false);

        toprated = (RecyclerView)findViewById(R.id.toprated);
        toprated.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        toprated.setAdapter(new HorizontalHotDeal(this,new JSONArray()));
        toprated.setNestedScrollingEnabled(false);

        category = (RecyclerView)findViewById(R.id.category);
        category.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        category.setAdapter(new HorizontalCategory(this,new ArrayList<String>()));
        category.setNestedScrollingEnabled(false);

        restaurants = (RecyclerView)findViewById(R.id.viewRestaurant);
        restaurants.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        restaurants.setAdapter(new HorizontalRestaurants(this,new JSONArray()));
        restaurants.setNestedScrollingEnabled(false);

        //loadadapters();

        bnv = (BottomNavigationView)findViewById(R.id.bnv);
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.cart:
                        selectme(0);
                        Intent intent = new Intent(context,Cart.class);
                        startActivity(intent);
                        break;
                    case R.id.home:
                        break;
                    case R.id.orderstatus:
                        selectme(2);
                        Intent intent1 = new Intent(context,OrderHistory.class);
                        startActivity(intent1);
                        break;
                    case R.id.search:
                        selectme(3);
                        Intent intent2 = new Intent(context,Search.class);
                        startActivity(intent2);
                        break;

                }
                return true;
            }
        });
        selectme(1);

        ((LinearLayout)findViewById(R.id.locationbar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationBarClicked();
            }
        });

        checknotificationstatus();

        refresh = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                refresh.postDelayed(runnable,3000);
                pagerlooper();
            }
        };
        //for null pointer exception
        Search.responseArray = responseArray;
    }
    public void addtocart(JSONObject jsonObject){
        Integer value = 1;
        String valuestr = bnv.getMenu().getItem(0).getTitle().toString();
        if(valuestr.equals("cart"))
            bnv.getMenu().getItem(0).setTitle(value.toString());
        else {
            value = Integer.parseInt(valuestr);
            value++;
            bnv.getMenu().getItem(0).setTitle(value.toString());
        }
    }

    public void getcurrenttime(final double lat, final double longitude){


        RestClient.get("/currenttime", null, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if(response.has("currenttime")){
                    try {
                        currenttime = response.getInt("currenttime");
                        //Log.e("currenttime",currenttime+"");
                        makerequest(new LatLng(lat,longitude));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFinish() {
                //onLoginSuccess();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,JSONObject errorResponse){
                //Toast.makeText(context,throwable.toString(),Toast.LENGTH_LONG).show();
            }
        });

    }

    public void makerequest(LatLng latLng){

        //////Log.e("Error ",""+latLng.latitude+ " "+latLng.longitude);

        RequestParams params = new RequestParams();
        params.put("lat",latLng.latitude + "");
        params.put("long",latLng.longitude + "");
        params.put("gLocation",address);
        RestClient.post("/sharelocation", params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                responseArray = response;
                if(responseArray.length()==0){
                    ((ViewPager)findViewById(R.id.viewPager)).setBackground(getResources().getDrawable(R.drawable.notavailable));
                }
                else
                    ((ViewPager)findViewById(R.id.viewPager)).setBackground(getResources().getDrawable(R.drawable.nooffer));

                setmyadapters();
//                ////Log.e("errror category",category.toString());
//                ////Log.e("Error hotdeal",hotdeals.toString());
//                ////Log.e("Error toprateds",toprateds.toString());

            }

            @Override
            public void onFinish() {
                //onLoginSuccess();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,JSONObject errorResponse){
                //Toast.makeText(context,throwable.toString(),Toast.LENGTH_LONG).show();
            }
        });

    }
    public void locationBarClicked(){
        Intent intent = new Intent(context,MapFragment.class);
        startActivity(intent);
    }

    public void categoryclicked(String str){
        Intent intent=new Intent(context,ItemsByCategory.class);
        intent.putExtra("title",str);
        startActivity(intent);
    }

    public void showrestaurantpage(){
        Intent intent=new Intent(context,RestaurantProfile.class);
        startActivity(intent);
    }
    public void selectme(int id){
        bnv.getMenu().getItem(id).setChecked(true);
    }

    public static void updatecart(Boolean add, JSONObject cart){

        Log.e("error cart","update cart processed");

        JSONObject cartItem  = null;
        try {
            cartItem = new JSONObject(cart.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            if(add) {
                for (int i = 0; i < mycart.length(); i++) {
                    JSONObject cartobject = mycart.getJSONObject(i);

                    if (cartobject.getString("number").equals(cartItem.getString("number"))) {

                        int total = cartobject.getInt("total");
                        total += cartItem.getJSONArray("price").getInt(cartItem.getInt("index"));
                        cartobject.put("total", total);

                        JSONArray order = cartobject.getJSONArray("order");
                        for (int j = 0; j < order.length(); j++) {
                            JSONObject cartObjectItem = order.getJSONObject(j);
                            if (cartObjectItem.getString("name").equals(cartItem.getString("name"))
                                    && cartObjectItem.getInt("index") == cartItem.getInt("index")
                                    && cartObjectItem.getInt("price") ==
                                    cartItem.getJSONArray("price").getInt(cartItem.getInt("index"))) {

                                int quantity = cartObjectItem.getInt("quantity");
                                quantity++;
                                cartObjectItem.put("quantity", quantity);
                                break;

                            }
                            if (j == order.length() - 1) {
                                JSONObject item = new JSONObject();
                                item.put("name", cartItem.getString("name"));
                                item.put("index", cartItem.getInt("index"));
                                item.put("length", cartItem.getJSONArray("price").length());
                                item.put("quantity", 1);
                                item.put("price", cartItem.getJSONArray("price").getInt(cartItem.getInt("index")));
                                order.put(item);
                            }
                        }
                        break;
                    } else if (i == mycart.length() - 1) {
                        JSONObject restObject = new JSONObject();
                        restObject.put("resname", cartItem.getString("resname"));
                        restObject.put("tonumber", cartItem.getString("number"));
                        restObject.put("total", cartItem.getJSONArray("price").getInt(cartItem.getInt("index")));
                        JSONArray order = new JSONArray();
                        JSONObject item = new JSONObject();
                        item.put("name", cartItem.getString("name"));
                        item.put("index", cartItem.getInt("index"));
                        item.put("length", cartItem.getJSONArray("price").length());
                        item.put("quantity", 1);
                        item.put("price", cartItem.getJSONArray("price").getInt(cartItem.getInt("index")));
                        order.put(item);
                        restObject.put("order", order);
                        mycart.put(restObject);
                    }


                    //Log.e("error in loop out if",cartobject.toString());
//                if(cartobject.getString("number").equals(cartItem.getString("number"))
//                        && cartobject.getString("name").equals(cartItem.getString("name"))
//                        && cartobject.getString("index").equals(cartItem.getString("index"))
//                        && cartobject.getString("levelone").equals(cartItem.getString("levelone"))){
//                    //Log.e("error in loop in if",cartobject.toString());
//                    found = true;
//                    index = i;
//                    quantity = Integer.parseInt(cartobject.getString("quantity"));
//                    //Log.e("error cartobject",cartobject.toString());
//                    //Log.e("error cartItem",cartItem.toString());
//                    break;
//                }
                }
            }
            else{
                for (int i = 0; i < mycart.length(); i++) {
                    JSONObject cartobject = mycart.getJSONObject(i);

                    if (cartobject.getString("number").equals(cartItem.getString("number"))) {

                        int total = cartobject.getInt("total");
                        total += cartItem.getJSONArray("price").getInt(cartItem.getInt("index"));
                        cartobject.put("total", total);

                        JSONArray order = cartobject.getJSONArray("order");
                        for (int j = 0; j < order.length(); j++) {
                            JSONObject cartObjectItem = order.getJSONObject(j);
                            if (cartObjectItem.getString("name").equals(cartItem.getString("name"))
                                    && cartObjectItem.getInt("index") == cartItem.getInt("index")
                                    && cartObjectItem.getInt("price") ==
                                    cartItem.getJSONArray("price").getInt(cartItem.getInt("index"))) {

                                int quantity = cartObjectItem.getInt("quantity");
                                quantity--;
                                //condition if quantity == 0 remove item

                                if(quantity == 0){
                                    order.remove(j);
                                }

                                cartObjectItem.put("quantity", quantity);
                                break;

                            }
                        }
                        //if order remains zero;
                        if(order.length()==0){
                            mycart.remove(i);
                        }
                        break;
                    }
                }
            }

//            if(found){
//                if(add){
//                    quantity++;
//                    cartItem.put("quantity",quantity);
//                    mycart.put(index,cartItem);
//                }
//                else{
//                    quantity--;
//                    if(quantity>0) {
//                        cartItem.put("quantity", quantity);
//                        mycart.put(index, cartItem);
//                    }
//                    else if(quantity==0){
//                        mycart.remove(index);
//                    }
//                }
//            }
//            else{
//                if(add){
//                    cartItem.put("quantity",1);
//                    mycart.put(cartItem);
//                }
//            }
//            //Log.e("error cartlen",mycart.length()+"");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("error cart",mycart.toString());
    }
    public void initializecart() throws JSONException {
        SharedPreferences prefs = getSharedPreferences("com.example.root.khanabot",Context.MODE_PRIVATE);
        String cartstring = prefs.getString("mycart",(new JSONArray()).toString());
        mycart = new JSONArray(cartstring);

    }
    public void addtosharedpreferences(String key,String cartstring){
        SharedPreferences prefs = getSharedPreferences("com.example.root.khanabot",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key,cartstring);
        editor.commit();
        //////Log.e("preferences",key+cartstring);
    }

    public void checknotificationstatus(){
        if(!notifstatusset()){
            addtoserver(getSharedPreferences("com.example.root.khanabot",Context.MODE_PRIVATE).
                    getString("notificationid",""));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        addtosharedpreferences("mycart",mycart.toString());
        refresh.removeCallbacks(runnable);
    }
    public void notifychange(){
        ((TextView)findViewById(R.id.carttext)).setText(HomePage.mycart.length()+"");
        if(HomePage.mycart.length()==0)
            ((RelativeLayout)findViewById(R.id.cartcontainer)).setVisibility(View.INVISIBLE);
        else
            ((RelativeLayout)findViewById(R.id.cartcontainer)).setVisibility(View.VISIBLE);

    }

    @Override
    public void onResume(){
        super.onResume();
        selectme(1);
        notifychange();
        setmyadapters();
        runnable.run();
    }
    public boolean notifstatusset(){
        SharedPreferences prefs = getSharedPreferences("com.example.root.khanabot",Context.MODE_PRIVATE);
        String uuid = prefs.getString("notificationstatus","");
        if(uuid.equals("updated"))
            return true;
        return false;
    }
    public void addtoserver(String token){
        RequestParams params = new RequestParams();
        params.put("notificationid",token);
        RestClient.get("/savenotificationid",params,new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //Toast.makeText(context,response.toString(),Toast.LENGTH_SHORT).show();
                try {
                    if(response.has("notificationid")) {
                        String value = response.getString("notificationid");
                        if (value.equals("updated")) {
                            ((HomePage)context).addtosharedpreferences("notificationstatus","updated");
                            //////Log.e("Sent ","notificationid sent to server");
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFinish() {
                //onLoginSuccess();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,JSONObject errorResponse){
                //////Log.e("error failure","connection failed in notificationid updation");
            }

        });
    }

    public String getprefsvalue(String key){
        return getSharedPreferences("com.example.root.khanabot",Context.MODE_PRIVATE).getString(key,"");
    }

    public void setmyadapters(){
        JSONArray hotdeals = new JSONArray();
        JSONArray toprateds = new JSONArray();
        ArrayList<String> categori = new ArrayList<String>();
        final JSONArray Offers = new JSONArray();


        for(int i=0; i<responseArray.length();i++){
            int uptime=0;
            int downtime=0;
            try {
                JSONObject restaurantobj = responseArray.getJSONObject(i);
                if(restaurantobj.has("availability")){
                    uptime = restaurantobj.getJSONObject("availability").getInt("uptime");
                    downtime = restaurantobj.getJSONObject("availability").getInt("downtime");

                    if(uptime<downtime){
                        if(currenttime>=uptime && currenttime<downtime){

                        }
                        else{
                            responseArray.remove(i);
                            i--;
                            //Log.e("responsearraylength",responseArray.length()+"");
                            continue;
                        }
                    }
                    else{
                        if(currenttime>=uptime){

                        }
                        else if(currenttime<downtime){

                        }
                        else{
                            responseArray.remove(i);
                            i--;
                            //Log.e("responsearraylength",responseArray.length()+"");
                            continue;
                        }
                    }
                }

                JSONArray HotDeals = null;
                JSONArray TopRateds = restaurantobj.getJSONArray("TopRated");
                HotDeals = restaurantobj.getJSONArray("HotDeals");
                JSONArray offers = restaurantobj.getJSONArray("Offers");
                for(int j=0;j<offers.length();j++){
                    JSONObject offer = offers.getJSONObject(j);
                    Offers.put(offer);
                }
                for (int j = 0; j < HotDeals.length(); j++) {
                    JSONObject hotdeal = null;
                    hotdeal = HotDeals.getJSONObject(j);
                    hotdeal.put("resname", restaurantobj.getString("name"));
                    hotdeal.put("levelone","HotDeals");
                    hotdeal.put("index",0);
                    hotdeal.put("number",restaurantobj.getString("number"));
                    if(restaurantobj.has("deliversin")){
                        String deliversin = restaurantobj.getString("deliversin")+ "min";
                        hotdeal.put("deliversin",deliversin);
                    }
                    hotdeals.put(hotdeal);
                }
                for(int j=0;j<TopRateds.length();j++){
                    JSONObject TopRated = TopRateds.getJSONObject(j);
                    int index = TopRated.getInt("Ind");
                    //Log.e("error toprated",TopRated.toString());
                    JSONObject myTopRated = new JSONObject();
                    JSONObject menu = restaurantobj.getJSONObject("menu");
                    myTopRated.put("name", TopRated.getString("SubCategory"));
                    myTopRated.put("price",new JSONArray().put(menu.getJSONObject(TopRated.
                            getString("Category")).getJSONObject(TopRated.getString("SubCategory")).
                            getJSONArray("price").getInt(index)));
                    myTopRated.put("resname",restaurantobj.getString("name"));
                    myTopRated.put("number",restaurantobj.getString("number"));
                    myTopRated.put("levelone","menu");
                    myTopRated.put("leveltwo",TopRated.getString("Category"));
                    myTopRated.put("index",index);
                    myTopRated.put("image",menu.getJSONObject(TopRated.
                            getString("Category")).getJSONObject(TopRated.getString("SubCategory")).
                            getString("image"));
                    if(restaurantobj.has("deliversin")){
                        String deliversin =restaurantobj.getString("deliversin")+ "min";
                        myTopRated.put("deliversin",deliversin);
                    }
                    toprateds.put(myTopRated);
                }
                JSONObject menu = restaurantobj.getJSONObject("menu");
                JSONArray menuNames = menu.names();

                for(int j=0; j<menuNames.length();j++){
                    JSONObject leveltwo = menu.getJSONObject(menuNames.getString(j));
                    if(leveltwo.length()==0)
                        continue;
                    JSONArray leveltwonames = leveltwo.names();
                    for(int k =0; k<leveltwonames.length();k++) {
                        JSONObject item = leveltwo.getJSONObject(leveltwonames.getString(k));
                        if(item.has("availability")){
                            uptime = item.getJSONObject("availability").getInt("uptime");
                            downtime = item.getJSONObject("availability").getInt("downtime");

                            if(uptime<downtime){
                                if(currenttime>=uptime && currenttime<downtime){

                                }
                                else{
                                    leveltwo.remove(leveltwonames.getString(k));
                                    //Log.e("leveltwolength",leveltwo.length()+"");
                                    continue;
                                }
                            }
                            else{
                                if(currenttime>=uptime){

                                }
                                else if(currenttime<downtime){

                                }
                                else{
                                    leveltwo.remove(leveltwonames.getString(k));
                                    //Log.e("leveltwolength",leveltwo.length()+"");
                                    continue;
                                }
                            }
                        }


                        //Log.e("item",item.toString());
                        JSONArray Category = item.getJSONArray("category");
                        for (int l = 0; l < Category.length(); l++) {
                            if (categori.indexOf(Category.getString(l)) == -1) {
                                categori.add(Category.getString(l));
                            }
                        }
                    }
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
                Log.e("error catch",e.toString());
                //Log.e("category",categori.toString());
            }
        }
        hotdeal.setAdapter(new HorizontalHotDeal(context,hotdeals));
        toprated.setAdapter(new HorizontalHotDeal(context,toprateds));
        restaurants.setAdapter(new HorizontalRestaurants(context,responseArray));
        category.setAdapter(new HorizontalCategory(context,categori));
        viewPager.setAdapter(new CustomPageAdapter(context,Offers));
        Search.responseArray = responseArray;
        OrderHistory.responseArray = responseArray;
        ((HomePage)context).Offers=Offers;
        runnable.run();
    }

    public void pagerlooper(){

        int num = viewPager.getCurrentItem();
        if(num+1 >= Offers.length())
            num=-1;
        viewPager.setCurrentItem(num+1);
    }
}
