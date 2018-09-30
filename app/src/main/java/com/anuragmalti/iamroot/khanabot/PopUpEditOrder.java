package com.anuragmalti.iamroot.khanabot;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;

import org.json.JSONArray;
import org.json.JSONException;

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
        restaurantcont.setAdapter(new EditOrderAdapter(this,new JSONArray()));

        Bundle b = getIntent().getExtras();
        int value = -1;
        if(b != null)
            value = b.getInt("position");
        try {
            setadapter(HomePage.mycart.getJSONObject(value).getJSONArray("order"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void setadapter(JSONArray array){
        restaurantcont.setAdapter(new EditOrderAdapter(context,array));
    }
}
