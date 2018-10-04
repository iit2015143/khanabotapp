package com.anuragmalti.iamroot.khanabot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class EditOfferPopUp extends AppCompatActivity {

    public JSONArray offerResponse;
    public Context cont;
    public RecyclerView restaurantcont;
    public EditOfferAdapter editOfferAdapter;
    private JSONObject offerValue = new JSONObject();
    private Button apply;
    int value;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_offer);
        cont = this;
        apply = (Button)findViewById(R.id.apply);

        restaurantcont = (RecyclerView)findViewById(R.id.editOfferPopUp);
        restaurantcont.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        restaurantcont.setAdapter(new EditOfferAdapter(this,new JSONArray(),-1));


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

        makerequest();
        Bundle b = getIntent().getExtras();
        value = -1;
        if(b != null)
            value = b.getInt("position");


        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("offer value in editoffe",offerValue.toString());
                Intent intent = new Intent();
                try {
                    intent.putExtra("offerMinValue",  offerValue.getString("minValue").toString());
                    intent.putExtra("offerMaxDiscount",  offerValue.getString("maxDiscount").toString());
                    intent.putExtra("offerName",  offerValue.getString("name").toString());
                } catch (JSONException e) {
                      e.printStackTrace();
                }
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    public void makerequest(){
        RequestParams params = new RequestParams();
        try {
            params.put("number",HomePage.mycart.getJSONObject(value).getString("tonumber"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Toast.makeText(getBaseContext(),"inside make request",Toast.LENGTH_SHORT).show();
        Log.e("request","inside make request");
        RestClient.get("/getoffers",params,new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                //Toast.makeText(context,"inside success",Toast.LENGTH_LONG).show();
                offerResponse = response;
                // notifychange();
                Log.e("response",response.toString());
                Log.e("offerresponse",offerResponse.toString());
                //Toast.makeText(context,"got offers",Toast.LENGTH_SHORT).show();
                setMyAdapter(offerResponse);
            }

            @Override
            public void onFinish() {
                //onLoginSuccess();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,JSONObject errorResponse){
                Log.e("error failure","connection failed in orderhistory");

                //Toast.makeText(context,"Internet connection failed",Toast.LENGTH_SHORT).show();
            }

        });
    }

    public void setOfferValue(JSONObject offerValue) {
        this.offerValue = offerValue;
    }

    public void setMyAdapter(JSONArray offerResponse){
        restaurantcont.setAdapter(new EditOfferAdapter(cont,offerResponse,value));
    }
}
