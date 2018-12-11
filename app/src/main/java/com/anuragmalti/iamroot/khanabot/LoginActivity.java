package com.anuragmalti.iamroot.khanabot;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    public Context context;
    @BindView(R.id.input_email) EditText mobile_number;
    //@BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.btn_login) Button _loginButton;
    //@BindView(R.id.link_signup) TextView _signupLink;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        ButterKnife.bind(this);
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed("Validation failed");
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String number = mobile_number.getText().toString();
        Map<String, String> params = new HashMap<String, String>();
        params.put("number",number);

        //RestClient.setCookieStore(new PersistentCookieStore(getApplicationContext()));

        CustomObjectRequest customRequest = new CustomObjectRequest(Request.Method.POST,MySingleton.BASE_URL+"/number",params,new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                Log.e("Response: " , response.toString());

                Toast.makeText(context,response.toString(),Toast.LENGTH_SHORT).show();
                try {
                    String value = response.getString("otp");
                    progressDialog.hide();
                    if(value.equals("sent")){
                        onLoginSuccess();
                    }
                    else if(value.equals("timeout")){
                        onLoginFailed("OTP timeout");
                    }
                    else
                        onLoginFailed("Invalid otp");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
                Log.e("Response: " , error.toString());
                Toast.makeText(context,"Internet Connection Failed",Toast.LENGTH_LONG).show();
            }
        });
        MySingleton.getInstance(this).addToRequestQueue(customRequest);

    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        Intent intent = new Intent(this,otpactivity.class);
        intent.putExtra("number",mobile_number.getText().toString());
        startActivity(intent);
        finish();
    }

    public void onLoginFailed(String showme) {
        Toast.makeText(getBaseContext(), showme, Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = mobile_number.getText().toString();
        Boolean matches = Pattern.matches("[0-9]{10}",email);

        if (email.equals("") || !matches) {
            mobile_number.setError("Enter a valid mobile number");
            valid = false;
        } else {
            mobile_number.setError(null);
        }
        return valid;
    }
}
