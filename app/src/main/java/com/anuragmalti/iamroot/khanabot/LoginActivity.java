package com.anuragmalti.iamroot.khanabot;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

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
        RequestParams params = new RequestParams();
        params.put("number",number);

        RestClient.setCookieStore(new PersistentCookieStore(getApplicationContext()));
        RestClient.post("/number", params, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(context,response.toString(),Toast.LENGTH_SHORT).show();
                try {
                    String value = response.getString("otp");
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
                progressDialog.hide();
            }

            @Override
            public void onFinish() {
                //onLoginSuccess();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,JSONObject errorResponse){
                onLoginFailed("Request failed");
                //Toast.makeText(context,throwable.toString(),Toast.LENGTH_LONG).show();
                progressDialog.hide();
            }

        });
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
