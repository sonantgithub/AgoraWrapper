package com.example.standaloneagora;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

public class StartSignStreaming extends AppCompatActivity {
    private static final String TAG = "StartSignStreaming";
    public RtcEngine uniqueRtcengin;
    public View remoteViewPublic, gifView;
    public Context publicContext;
    public String clientIdCopy, clientPasswordcopy, stringToConvertCopy;
    public String tempChannelName, tempAppId, tempTokenNumber;
    public String time, userComingForFirstTime, currentTimeMiles;
    public long timeOfStreamingStopped, totalTimeOfStreming;
    public int countOfTotalHits;
    public int countOfNumberOfHit;
    ProgressDialog progress;

    public void start(String clientId, String clientPassword, Context context, String stringToConvert, View view, View gifId) {
        publicContext = context;
        remoteViewPublic = view;
        gifView = gifId;
        clientIdCopy = clientId;
        clientPasswordcopy = clientPassword;
        stringToConvertCopy = stringToConvert;
        if ("no".equals(userComingForFirstTime)) {

            Log.d("maintag", "startStreaminggy: " + userComingForFirstTime);

            sendStringToTheFirebase();
        } else {

            validationOfUserANDturnOnNewChannelFlag();
            Log.d("maintag", "startStreaminggn: " + userComingForFirstTime);
        }


    }

    private void validationOfUserANDturnOnNewChannelFlag() {
        Toast.makeText(publicContext, "here", Toast.LENGTH_SHORT).show();
        progress = new ProgressDialog(publicContext);
        progress.setTitle("ALERT");
        progress.setMessage("initializing streaming");
        progress.setCancelable(false);
        progress.show();
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        publicContext.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        String height = String.valueOf(displayMetrics.heightPixels);
//        String width = String.valueOf(displayMetrics.widthPixels);

        RequestQueue requestQueue = com.android.volley.toolbox.Volley.newRequestQueue(publicContext);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://us-central1-dsiapp-103c4.cloudfunctions.net/validationOfTheUser", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("MAINTAG", "123onResponse: " + response.toString());

                if (response.toString().contains("Match Strings")) {
                    Toast.makeText(publicContext, "right credentials", Toast.LENGTH_SHORT).show();
                    currentTimeMiles = response.toString().substring(response.toString().indexOf(",") + 1, response.toString().length());
                    Log.d("MAINTAG", "onResponseCurrentTimeMiles: " + currentTimeMiles);
                    progress.setMessage("waiting for server response");
                    listenForTokenAndAppId();
                } else {
                    Toast.makeText(publicContext, "wrong credentials", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("MAINTAG", "123onError: " + error.getMessage());

                Toast.makeText(publicContext, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("IdOfClient", clientIdCopy);
                params.put("PasswordOfClient", clientPasswordcopy);
//                params.put("height", height);
//                params.put("width", width);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("content-type", "application/x-www-form-urlencoded");
                return params;
            }

        };
        requestQueue.add(stringRequest);

    }

    private void listenForTokenAndAppId() {

        if (countOfNumberOfHit < 25) {
            RequestQueue requestQueue = com.android.volley.toolbox.Volley.newRequestQueue(publicContext);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://us-central1-dsiapp-103c4.cloudfunctions.net/ListenIfAnyChannelIsCreatedOrNot", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.toString().equals("inelseblock")) {
                        Log.d("MAINTAG", "onResponseListener1: " + response.toString() + countOfNumberOfHit);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                listenForTokenAndAppId();
                            }
                        }, 1000);
                    } else {
                        try {
                            Log.d("MAINTAG", "onResponseListener1: Stop" + response.toString());
                            JSONObject jsonObject = new JSONObject(response);
                            tempAppId = String.valueOf(jsonObject.get("appId"));
                            tempTokenNumber = String.valueOf(jsonObject.get("token"));
                            Log.d("MAINTAG", "onResponse: " + tempChannelName + tempAppId + tempTokenNumber);
                            progress.dismiss();
                            initializeAndJoinChannel(tempAppId, tempTokenNumber, currentTimeMiles, publicContext);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("MAINTAG", "onErrorListener1: " + error.toString() + currentTimeMiles);


                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("currentTimeMiles", currentTimeMiles);

                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("content-type", "application/x-www-form-urlencoded");
                    return params;
                }

            };
            requestQueue.add(stringRequest);
            countOfNumberOfHit++;
        } else {
            Toast.makeText(publicContext, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    public void initializeAndJoinChannel(String appId, String token, String channelName, Context context) {
        RtcEngine mRtcEngine;
        try {
            mRtcEngine = RtcEngine.create(context, appId, mRtcEventHandler);
        } catch (Exception e) {
            throw new RuntimeException("Check the error.");
        }

        mRtcEngine.enableVideo();

        mRtcEngine.joinChannel(token, channelName, "", 0);
        uniqueRtcengin = mRtcEngine;

    }

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {

        @Override
        // Listen for the remote user joining the channel to get the uid of the user.
        public void onUserJoined(int uid, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setupRemoteVideo(uid, remoteViewPublic);
                }
            });
        }
    };

    public void setupRemoteVideo(int uid, View remoteview) {
        FrameLayout container = (FrameLayout) remoteview;
        SurfaceView surfaceView = RtcEngine.CreateRendererView(publicContext);
        surfaceView.setZOrderMediaOverlay(true);
        container.addView(surfaceView);
        uniqueRtcengin.disableAudio();
        uniqueRtcengin.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid, 1));
        userComingForFirstTime = "no";

    }

    private void sendStringToTheFirebase() {
        countOfTotalHits++;
        RequestQueue requestQueue = com.android.volley.toolbox.Volley.newRequestQueue(publicContext);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://us-central1-dsiapp-103c4.cloudfunctions.net/sendStringtoFrameData", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("MAINTAG", "sendStringToTheFirebase" + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("StringToConvert", stringToConvertCopy);
                params.put("currentTimeMiles", currentTimeMiles);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("content-type", "application/x-www-form-urlencoded");
                return params;
            }

        };
        requestQueue.add(stringRequest);
    }

    public void stopStreaming() {
        Toast.makeText(publicContext, "stop streaming", Toast.LENGTH_SHORT).show();
        uniqueRtcengin.leaveChannel();
        uniqueRtcengin.destroy();
        userComingForFirstTime = "null";
    }

    private void isUserStillUsingChannel() {
        //  Log.d("MAINTAG",mRtcEventHandler.onRemoteVideoStateChanged(0,0,0,0));
    }


}
