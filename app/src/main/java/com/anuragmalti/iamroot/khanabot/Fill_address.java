package com.anuragmalti.iamroot.khanabot;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import cz.msebera.android.httpclient.Header;

public class Fill_address extends AppCompatActivity {

    private RecyclerView recyclerView=null;
    private RecyclerView.Adapter rAdapter;
    private EditText location;
    private Context context;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_address);
        context = this;
        Intent intent = getIntent();
        address = intent.getStringExtra("address");
        location = (EditText)findViewById(R.id.location);
        location.setText(address);
        rAdapter = new HorizontalAdapter();
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL,false));
        recyclerView.setAdapter(rAdapter);
        recyclerView.setNestedScrollingEnabled(false);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder>{

        @NonNull
        @Override
        public HorizontalAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address,parent,false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context,"Postion no: "+position,Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return 5;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView txtView;
            public View view;
            public MyViewHolder(View view) {
                super(view);
                txtView = (TextView) view.findViewById(R.id.address);
                this.view = view;
            }
        }
    }

    public void skip(View view){
        //Toast.makeText(context,"skipped",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,HomePage.class);
        intent.putExtra("address",address);
        startActivity(intent);
    }
    public void saveAddress(View view){
        String locality = ((TextView)findViewById(R.id.locality)).getText().toString();
        String houseorflatno = ((TextView)findViewById(R.id.houseorflatno)).getText().toString();
        makerequest(locality,houseorflatno,address);
        address = houseorflatno+" "+locality+" "+address;
        Intent intent = new Intent(context,HomePage.class);
        intent.putExtra("address",address);
        startActivity(intent);
    }
    public void makerequest(final String locality, final String houseorflatno, final String gLocation){
        RequestParams params = new RequestParams();
        params.put("lat","25");
        params.put("long","82");
        params.put("locality",locality);
        params.put("houseorflatno",houseorflatno);
        params.put("gLocation",gLocation);
        //RestClient.setCookieStore(new PersistentCookieStore(getApplicationContext()));
        RestClient.get("/savelocation", params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //Toast.makeText(context,response.toString(),Toast.LENGTH_SHORT).show();
                ////Log.e("Error makerequest","request completed");
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
}

