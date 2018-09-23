package com.anuragmalti.iamroot.khanabot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

public class NewCart extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_cart);
        RecyclerView restaurantcont = (RecyclerView)findViewById(R.id.restaurantcont);

    }
}
