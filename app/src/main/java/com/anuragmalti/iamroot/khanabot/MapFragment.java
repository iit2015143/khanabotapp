package com.anuragmalti.iamroot.khanabot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cz.msebera.android.httpclient.Header;

public class MapFragment extends FragmentActivity implements GoogleMap.OnCameraIdleListener,OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private Marker marker;
    private Context context;
    private Button lowbar;
    private LinearLayout upbar;
    private PlaceAutocompleteFragment autocompleteFragment;
    private int rqstsync = 0;
    private SearchView searchView;
    private String address;

    private final int[] MAP_TYPES = { GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_TERRAIN,
            GoogleMap.MAP_TYPE_NONE };
    private int curMapTypeIndex = 1;
    private int idle = 0;//animateupbar(0);
    RelativeLayout luxury;
    RelativeLayout premium;
    RelativeLayout pocketfriendly;
    RelativeLayout valueformoney;
    public LatLng finalLatlng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        context = this;
        //Log.e("Error : " ,"This hell is not loading motherfucker");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        obtainfragandapiclient();

        initbarsandsetlisteners();

    }

    public void obtainfragandapiclient(){

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //setHasOptionsMenu(true);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks( this )
                .addOnConnectionFailedListener( this )
                .addApi( LocationServices.API )
                .build();


        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            String TAG="ERROR";
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                LatLng latlng = place.getLatLng();
                //animatelowbar(0);
                showMarker(latlng);
                //Log.e(TAG, "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                //Log.e(TAG, "An error occurred: " + status);
            }
        });
        autocompleteFragment.getView().setBackgroundColor(0xBBFFFFFF);
        //autocompleteFragment.getView().setLayoutParams(new LayoutPara);
        ((EditText)autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).
                setTextColor(getResources().getColor(R.color.black));
        ((EditText)autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).
                setTextSize(15.0f);


    }


    public void initbarsandsetlisteners(){

        lowbar = (Button)findViewById(R.id.confirm);
        upbar = (LinearLayout)findViewById(R.id.upbar);
        //lowbar.setVisibility(View.INVISIBLE);
        ////Log.e("error in oncreate","Height : "+lowbar.getHeight());
        //settoastmsgtoall();
    }

//    public void settoastmsgtoall(){
//        premium.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context,"Not available at your location",Toast.LENGTH_SHORT).show();
//            }
//        });
//        valueformoney.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context,"Not available at your location",Toast.LENGTH_SHORT).show();
//            }
//        });
//        pocketfriendly.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context,"Not available at your location",Toast.LENGTH_SHORT).show();
//            }
//        });
//        luxury.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context,"Not available at your location",Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        if(gMap==null){
            Toast.makeText(this, "How is it null", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
            //Log.e("Error: ", "Why the hell this does not get called");
            googleMap = gMap;
            initListeners();
        }
        //Log.e("error in onmapready","Height : "+lowbar.getHeight());
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void initListeners() {
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnMapLongClickListener(this);
        googleMap.setOnInfoWindowClickListener( this );
        googleMap.setOnMapClickListener(this);
        googleMap.setOnCameraIdleListener(this);
        googleMap.setOnMyLocationButtonClickListener(this);
        googleMap.setPadding(0,150,0,120);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        //animatelowbar(0);
        showMarker(latLng);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        //animatelowbar(0);
        showMarker(latLng);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //marker.showInfoWindow();
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        ////Log.e("error in onstart","Height : "+lowbar.getHeight());
    }

    @Override
    public void onStop() {
        super.onStop();
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        gotocurrentlocation();
    }

    private void initCamera(final LatLng latlng ) {
        CameraPosition position = CameraPosition.builder()
                .target(latlng)
                .zoom( 16f )
                .bearing( 0.0f )
                .tilt( 0.0f )
                .build();

        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {

                //Log.e("error : ","on finish inside animatecamera called");
            }

            @Override
            public void onCancel() {
                //Log.e("error","am i getting cancelled");
            }
        });

        googleMap.setMapType( MAP_TYPES[curMapTypeIndex] );
        googleMap.setTrafficEnabled( true );
        googleMap.setMyLocationEnabled( true );
        googleMap.getUiSettings().setZoomControlsEnabled( true );
        ////Log.e("error : ","inside init camera function");
        ////Log.e("error in initcamera","Height : "+lowbar.getHeight());

    }

    private String getAddressFromLatLng( LatLng latLng ) {
        Geocoder geocoder = new Geocoder( context );

        address = "";
        try {
            address = geocoder
                    .getFromLocation( latLng.latitude, latLng.longitude, 1 )
                    .get( 0 ).getAddressLine( 0 );
        } catch (IOException e ) {
        }

        return address;
    }

    public void showMarker(LatLng latLng){

        //Log.e("Error in showmarker : ",""+latLng.latitude+" "+latLng.longitude);

        if (marker != null) {
            marker.remove();
        }
        address = getAddressFromLatLng( latLng );
        finalLatlng =latLng;
        marker = googleMap.addMarker(new MarkerOptions()
                .position(latLng).title( address)
                .draggable(true).visible(true));

        ((EditText)autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setText(address);
        //marker.showInfoWindow();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if(lowbar.getVisibility()==View.INVISIBLE) {
//                    lowbar.setAlpha(0.0f);
//                    //Log.e("error",lowbar.getHeight()+"");
//                    //Log.e("error in transy",lowbar.getTranslationY()+"");
//                    lowbar.setTranslationY(lowbar.getHeight());
//                    //Log.e("error in transy",lowbar.getTranslationY()+"");
//                    lowbar.setVisibility(View.VISIBLE);
//                }
//                animatelowbar(1);
//            }
//        },4000);
        rqstsync++;
        int temp = rqstsync;

        //makerequest(temp,latLng);
        //Log.e("Error ",""+latLng.latitude+ " "+latLng.longitude);

        initCamera(latLng);
    }

//    public void handleresponse(JSONArray response) throws JSONException {
//        for(int i =0; i<response.length();i++){
//            final String str;
//            str = response.getString(i);
//            switch(str){
//                case "Pocket_friendly":
//                    pocketfriendly.findViewWithTag("pocketfriendly").setBackground(getResources().getDrawable(R.drawable.shape_circle));
//                    pocketfriendly.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            processreq(str);
////                            Intent intent = new Intent(context,MenuGrid.class);
////                            startActivity(intent);
//                        }
//                    });
//                    break;
//                case "Luxury":
//                    luxury.findViewWithTag("luxury").setBackground(getResources().getDrawable(R.drawable.shape_circle));
//                    luxury.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            processreq(str);
////                            Intent intent = new Intent(context,MenuGrid.class);
////                            startActivity(intent);
//                        }
//                    });
//                    break;
//                case "Premium":
//                    premium.findViewWithTag("premium").setBackground(getResources().getDrawable(R.drawable.shape_circle));
//                    premium.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            processreq(str);
////                            Intent intent = new Intent(context,MenuGrid.class);
////                            startActivity(intent);
//                        }
//                    });
//                    break;
//                case "Value_for_money":
//                    valueformoney.findViewWithTag("valueformoney").setBackground(getResources().getDrawable(R.drawable.shape_circle));
//                    valueformoney.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            processreq(str);
////                            Intent intent = new Intent(context,MenuGrid.class);
////                            startActivity(intent);
//                        }
//                    });
//                    break;
//            }
//        }
//    }

//    public void processreq(String str){
//
//        RequestParams params = null;
//        rqstsync++;
//        final int temp = rqstsync;
//        RestClient.get("/category/"+str, params, new JsonHttpResponseHandler(){
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                //Toast.makeText(context,response.toString(),Toast.LENGTH_SHORT).show();
//                MenuGrid.data = response;
//                Intent intent  = new Intent(context,MenuGrid.class);
//                startActivity(intent);
//            }
//
//            @Override
//            public void onFinish() {
//                //onLoginSuccess();
//            }
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable,JSONObject errorResponse){
//                Toast.makeText(context,throwable.toString(),Toast.LENGTH_LONG).show();
//            }
//        });
//    }

    @Override
    public void onCameraIdle() {
        //Toast.makeText(context,"camera idle fired",Toast.LENGTH_SHORT).show();
        //Log.e("Error: ","Camera stopped");
        ////Log.e("error in cameraidle","Height : "+lowbar.getHeight());

    }

    public void gotocurrentlocation(){
        mCurrentLocation = LocationServices
                .FusedLocationApi
                .getLastLocation( mGoogleApiClient );

        showMarker(new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude()));

    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(context,"gps button clicked",Toast.LENGTH_SHORT).show();
        ////Log.e("Error : ", "gps butto clicked");
        //animatelowbar(0);
        //animateupbar(0);
        idle = 0;
        idle = 0;
        gotocurrentlocation();
        return true;
    }

    public void confirm(View view){
        //Toast.makeText(this,"Address is : "+ address ,Toast.LENGTH_SHORT).show();
        JSONObject location = new JSONObject();
        try {
            location.put("lat",finalLatlng.latitude);
            location.put("long",finalLatlng.longitude);
            addtosharedpref("location",location.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HomePage.address = address;
        Intent intent = new Intent(this,HomePage.class);
        intent.putExtra("address",address);
        startActivity(intent);
    }

    public void addtosharedpref(String key,String value){
        SharedPreferences prefs = getSharedPreferences("com.example.root.khanabot",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key,value);
        editor.commit();
    }

}