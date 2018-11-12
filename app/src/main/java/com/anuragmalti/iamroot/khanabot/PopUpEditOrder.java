package com.anuragmalti.iamroot.khanabot;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.WindowManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PopUpEditOrder extends AppCompatActivity {
    public RecyclerView restaurantcont;
    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up);
        context = this;

        WindowManager manager = (WindowManager) getSystemService(Activity.WINDOW_SERVICE);
        int width, height;
        WindowManager.LayoutParams params;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
            width = manager.getDefaultDisplay().getWidth();
            height = manager.getDefaultDisplay().getHeight();
        } else {
            Point point = new Point();
            manager.getDefaultDisplay().getSize(point);
            width = point.x;
            height = point.y;
        }

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(this.getWindow().getAttributes());
        lp.width = width;
        lp.height = height*3/4;
        this.getWindow().setAttributes(lp);

        this.setFinishOnTouchOutside(true);


        restaurantcont = (RecyclerView)findViewById(R.id.myPopUp);
        restaurantcont.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        restaurantcont.setAdapter(new EditOrderAdapter(this,new JSONArray(),""));

        Bundle b = getIntent().getExtras();
        int value = -1;
        if(b != null)
            value = b.getInt("position");
        try {
            setadapter(HomePage.mycart.getJSONObject(value).getJSONArray("order"),
                    HomePage.mycart.getJSONObject(value).getString("tonumber"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("error oncreate","in on create");

    }
    public void setadapter(JSONArray array, String number){
        restaurantcont.setAdapter(new EditOrderAdapter(context,array,number));
    }

    public void updatecart(Boolean add, JSONObject cart){

        Log.e("error cart","update cart processed");

        JSONObject cartItem  = null;
        try {
            cartItem = new JSONObject(cart.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            if(add) {
                boolean foundrest = false;
                for (int i = 0; i < HomePage.mycart.length(); i++) {
                    JSONObject cartobject = HomePage.mycart.getJSONObject(i);

                    if (cartobject.getString("tonumber").equals(cartItem.getString("number"))) {

                        foundrest = true;

                        int total = cartobject.getInt("total");
                        total += cartItem.getInt("price");
                        cartobject.put("total", total);

                        JSONArray order = cartobject.getJSONArray("order");
                        boolean founditem = false;
                        for (int j = 0; j < order.length(); j++) {
                            JSONObject cartObjectItem = order.getJSONObject(j);
                            if (cartObjectItem.getString("name").equals(cartItem.getString("name"))
                                    && cartObjectItem.getInt("index") == cartItem.getInt("index")
                                    && cartObjectItem.getInt("price") == cartItem.getInt("price")) {

                                founditem = true;

                                int quantity = cartObjectItem.getInt("quantity");
                                quantity++;
                                cartObjectItem.put("quantity", quantity);
                                break;

                            }
                        }

                        break;
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
                for (int i = 0; i < HomePage.mycart.length(); i++) {
                    JSONObject cartobject = HomePage.mycart.getJSONObject(i);

                    if (cartobject.getString("tonumber").equals(cartItem.getString("number"))) {

                        JSONArray order = cartobject.getJSONArray("order");
                        for (int j = 0; j < order.length(); j++) {
                            JSONObject cartObjectItem = order.getJSONObject(j);
                            if (cartObjectItem.getString("name").equals(cartItem.getString("name"))
                                    && cartObjectItem.getInt("index") == cartItem.getInt("index")
                                    && cartObjectItem.getInt("price") ==
                                    cartItem.getInt("price")) {

                                int quantity = cartObjectItem.getInt("quantity");
                                quantity--;

                                int total = cartobject.getInt("total");
                                total -= cartItem.getInt("price");
                                cartobject.put("total", total);

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
                            HomePage.mycart.remove(i);
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
            Log.e("error cart",e.toString());
        }
        Log.e("error cart",HomePage.mycart.toString());
    }

    public void addtosharedpreferences(String cartstring){
        SharedPreferences prefs = getSharedPreferences("com.example.root.khanabot", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("mycart", cartstring);
        editor.commit();
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.e("error in on", "in resume right now");
        addtosharedpreferences(HomePage.mycart.toString());
    }
}
