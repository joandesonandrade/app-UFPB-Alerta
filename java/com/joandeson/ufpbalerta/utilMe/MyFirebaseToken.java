package com.joandeson.ufpbalerta.utilMe;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.joandeson.ufpbalerta.util;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JoHN on 12/06/2018.
 */

public class MyFirebaseToken extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String token = FirebaseInstanceId.getInstance().getToken();

        sendRegistrationToServer(token);

    }

    private void sendRegistrationToServer(final String token) {

        SharedPreferences sp = getSharedPreferences("setting",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("token",token);
        editor.apply();

        util e = new util();
        String url = e.getUrl()+"/register_token/";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("ufpb_token", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", String.valueOf(error));
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("token", token);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(postRequest);
    }
}
