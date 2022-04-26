package com.example.standaloneagora;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import io.agora.rtc.RtcEngine;

public class StartSignStreaming extends AppCompatActivity {
    public RtcEngine uniqueRtcengin;
    public View remoteViewPublic, gifView;
    public Context publicContext;
    public String clientIdCopy, clientPasswordcopy, stringToConvertCopy;
    public String tempChannelName, tempAppId, tempTokenNumber;
    public String time, userComingForFirstTime;
    public long timeOfStreamingStart, timeOfStreamingStopped, totalTimeOfStreming;
    public int countOfTotalHits;

    public void start(String clientId, String clientPassword, Context context, String stringToConvert, View view, View gifId) {
        publicContext = context;
        remoteViewPublic = view;
        gifView = gifId;
        clientIdCopy = clientId;
        clientPasswordcopy = clientPassword;
        stringToConvertCopy = stringToConvert;

        validationOfUser();

    }

    private void validationOfUser() {

        RequestQueue requestQueue = com.android.volley.toolbox.Volley.newRequestQueue(publicContext);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://us-central1-dsiapp-103c4.cloudfunctions.net/validationOfTheUser", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TAG", "onResponse: "+response.toString());
                if (response.toString().equals("Matching strings")){
                    Toast.makeText(publicContext, "Matched credentials", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(publicContext, "Wrong credentials", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(publicContext, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String,String>();
                params.put("IdOfClient",clientIdCopy);
                params.put("PasswordOfClient",clientPasswordcopy);
                return params;
            }

            @Override
            public Map<String,String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("content-type","application/x-www-form-urlencoded");
                return params;
            }

        };
        requestQueue.add(stringRequest);

    }
}
