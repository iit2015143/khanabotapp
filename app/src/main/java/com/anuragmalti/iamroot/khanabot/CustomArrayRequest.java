package com.anuragmalti.iamroot.khanabot;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class CustomArrayRequest extends Request<JSONArray> {

    private Listener<JSONArray> listener;
    private Map<String, String> params;
    public String mUrl;

    public CustomArrayRequest(String url, Map<String, String> params,
                               Listener<JSONArray> reponseListener, Response.ErrorListener errorListener) {
        super(Request.Method.GET, url, errorListener);
        this.listener = reponseListener;
        this.params = params;
        this.mUrl = url;
    }

    public CustomArrayRequest(int method, String url, Map<String, String> params,
                               Listener<JSONArray> reponseListener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = reponseListener;
        this.params = params;
        this.mUrl = url;
    }

    protected Map<String, String> getParams()
            throws com.android.volley.AuthFailureError {
        return params;
    };

    @Override
    protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONArray(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    public String getUrl() {
        StringBuilder stringBuilder = new StringBuilder(mUrl);
        int i = 1;
        for (Map.Entry<String,String> entry: params.entrySet()) {
            String key;
            String value;
            try {
                key = URLEncoder.encode(entry.getKey(), "UTF-8");
                value = URLEncoder.encode(entry.getValue(), "UTF-8");
                if(i == 1) {
                    stringBuilder.append("?" + key + "=" + value);
                } else {
                    stringBuilder.append("&" + key + "=" + value);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            i++;

        }
        String url = stringBuilder.toString();

        return url;
    }

    @Override
    protected void deliverResponse(JSONArray response) {
        // TODO Auto-generated method stub
        listener.onResponse(response);
    }
}