package com.anuragmalti.iamroot.khanabot;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class NewCart extends AppCompatActivity {

    public Context context;
    private EditText editAddress;
    private TextView address;
    private Button add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_cart);


        context = this;
        editAddress = (EditText)findViewById(R.id.editAddress);
        address = (TextView)findViewById(R.id.address);
        address.setText(HomePage.address);
        add = (Button)findViewById(R.id.changeAdd);

        if(address.getText().toString() == null || address.getText().toString()==""){
            editAddress.setVisibility(View.VISIBLE);
            address.setVisibility(View.GONE);
            add.setText("Add");
        }

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

    public void onClick(View view) {
        //Toast.makeText(context,"button click",Toast.LENGTH_LONG).show();
        //Toast.makeText(context,add.getText().toString(),Toast.LENGTH_LONG).show();

        if(add.getText().toString().compareTo("Change") == 0){
            //Toast.makeText(context,"inside change",Toast.LENGTH_LONG).show();
            editAddress.setVisibility(View.VISIBLE);
            address.setVisibility(View.GONE);
            add.setText("Add");
            return;
        }


        if(add.getText().toString().compareTo("Add") == 0){
            Toast.makeText(context,"inside add",Toast.LENGTH_LONG).show();
            if(editAddress.getText().toString().compareTo("") != 0) {
                Toast.makeText(context,"inside if add",Toast.LENGTH_LONG).show();
                address.setText(editAddress.getText().toString());
                editAddress.setVisibility(View.GONE);
                address.setVisibility(View.VISIBLE);
                add.setText("Change");
            }
            else if(editAddress.getText().toString().compareTo("") == 0){
                Toast.makeText(context,"Please enter a valid address",Toast.LENGTH_LONG).show();
            }
        }

    }

    public void orderRequest(View view) {

    }

    public void editOrder(int position) {
        Intent intent = new Intent(context,PopUpEditOrder.class);
        Bundle b = new Bundle();
        b.putInt("position", position);
        intent.putExtras(b);
        startActivity(intent);
    }

    public void openOffer(View view) {
        Intent intent = new Intent(context,PopUpActivity.class);
        startActivity(intent);
    }

    public void notifychange() throws JSONException {
        int ans = 0;
        for(int i=0;i<HomePage.mycart.length();i++){
            JSONObject cartitem = HomePage.mycart.getJSONObject(i);
            int price = cartitem.getInt("price");
            int quantity = cartitem.getInt("quantity");
            ans += price*quantity;
            ////Log.e("in cart",cartitem.toString());
        }
        ////Log.e("error addtocart","I have been called");
        ((TextView)findViewById(R.id.total)).setText("Rs "+ans);
    }
}
