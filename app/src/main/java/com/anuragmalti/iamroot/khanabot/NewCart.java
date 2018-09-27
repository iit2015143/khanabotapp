package com.anuragmalti.iamroot.khanabot;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

public class NewCart extends AppCompatActivity {

    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_cart);
        context = this;
        RecyclerView restaurantcont = (RecyclerView)findViewById(R.id.restaurantcont);
        restaurantcont.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        restaurantcont.setAdapter(new NewCartAdapter(this,HomePage.mycart));
        Button requestorder = (Button)findViewById(R.id.requestorder);
        requestorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,PopUpActivity.class);
                startActivity(intent);
            }
        });
    }
}
