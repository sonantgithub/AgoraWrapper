package com.example.standaloneagora;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class StartSignStreaming extends AppCompatActivity {
    private static final String TAG = "StartSignStreaming";
    public RtcEngine uniqueRtcengin;
    public View remoteViewPublic, gifView;
    public Context publicContext;
    public String clientIdCopy, clientPasswordcopy, stringToConvertCopy;
    public String tempChannelName, tempAppId, tempTokenNumber;
    public String userComingForFirstTime, currentTimeMiles, dontLetMethodCallingTwoTimes;
    public int countOfTotalHits, countOfNumberOfHit;
    ProgressDialog progress;
    private Socket mSocket;

    public void start(String clientId, String clientPassword, Context context, String stringToConvert, View view, View gifId) {
        publicContext = context;
        remoteViewPublic = view;
        gifView = gifId;
        clientIdCopy = clientId;
        clientPasswordcopy = clientPassword;
        stringToConvertCopy = stringToConvert;
        giveRunTimePermission();


    }

    private void giveRunTimePermission() {
        Activity activity = (Activity) publicContext;
        Dexter.withActivity(activity)
                // below line is use to request the number of
                // permissions which are required in our app.
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                // after adding permissions we are
                // calling an with listener method.
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        // this method is called when all permissions are granted
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            if ("no".equals(userComingForFirstTime)) {

                                Log.d("maintag", "startStreaminggy: " + userComingForFirstTime);

                                sendStringToTheFirebase();
                            } else {

                                validationOfUserANDturnOnNewChannelFlag();
                                Log.d("maintag", "startStreaminggn: " + userComingForFirstTime);
                            }

                        }
                        // check for permanent denial of any permission
                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permanently,dgo1
                            // we will show user a dialog message.
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<com.karumi.dexter.listener.PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).withErrorListener(new PermissionRequestErrorListener() {
            // this method is use to handle error
            // in runtime permissions
            @Override
            public void onError(DexterError error) {
                // we are displaying a toast message for error message.
                Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
            }
        })
                // below line is use to run the permissions
                // on same thread and to check the permissions
                .onSameThread().check();


    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(publicContext);

        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 101);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(publicContext, "Permission required", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    private void validationOfUserANDturnOnNewChannelFlag() {
        progress = new ProgressDialog(publicContext);
        progress.setTitle("ALERT");
        progress.setMessage("initializing streaming");
        progress.setCancelable(false);
        progress.show();

        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) publicContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        double heightTemp = metrics.heightPixels / 3.6;
        String cropDouble = String.valueOf(heightTemp);
        String height = cropDouble.substring(0, cropDouble.indexOf("."));
        String width = String.valueOf(metrics.widthPixels / 3);
        Log.d(TAG, "validationOfUserANDturnOnNewChannelFlag: " + height + ":" + width);

        RequestQueue requestQueue = com.android.volley.toolbox.Volley.newRequestQueue(publicContext);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://us-central1-dsiapp-103c4.cloudfunctions.net/validationOfTheUser", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("MAINTAG", "123onResponse: " + response);

                if (response.contains("Match Strings")) {
                    currentTimeMiles = response.substring(response.indexOf(",") + 1, response.toString().length());
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
                params.put("height", width);
                params.put("width", height);
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
        progress.dismiss();
        uniqueRtcengin.disableAudio();
        uniqueRtcengin.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid, 1));
        userComingForFirstTime = "no";
        isUserStillUsingChannel();

    }

    private void sendStringToTheFirebase() {
        countOfTotalHits++;
        dontLetMethodCallingTwoTimes = "true";
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
                if (dontLetMethodCallingTwoTimes.equals("true")) {
                    params.put("StringToConvert", stringToConvertCopy);
                    params.put("currentTimeMiles", currentTimeMiles);
                    params.put("countOfTotalHits", String.valueOf(countOfTotalHits));
                    Log.d(TAG, "sendStringToTheFirebase: checkIfMethodCallingTwoTimes123");
                    dontLetMethodCallingTwoTimes = "false";
                }

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

    private void isUserStillUsingChannel() {
        {
            try {
                mSocket = IO.socket("https://signey-streaming-server.herokuapp.com");
            } catch (URISyntaxException e) {
            }
        }
        mSocket.connect(); // connect the socket
        mSocket.on(currentTimeMiles, onNewMessage); //Listen response coming from node
        mSocket.emit("getTheChannelName", currentTimeMiles); // send message to the node
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Activity activity = (Activity) publicContext;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if ("yes".equals(args[0].toString())) {
                            uniqueRtcengin.leaveChannel();
                            uniqueRtcengin.destroy();
                            userComingForFirstTime = "null";
                            mSocket.emit("killChannel", currentTimeMiles); // send message to the node
                            gifView.setVisibility(View.VISIBLE);
                            Glide.with(publicContext)
                                    .load(R.raw.defposegif)
                                    .centerCrop()
                                    .into((ImageView) gifView);
                        }

                    } catch (Exception e) {
                        Log.d(TAG, "run: " + e.getMessage());
                    }
                }
            });
        }
    };

    public void stopStreaming() {
        Activity activity = (Activity) publicContext;
        Intent intent = new Intent(activity, service.class);
        intent.putExtra("activityName",activity.getClass().getSimpleName());
        publicContext.startService(intent);

        //  uniqueRtcengin.leaveChannel();
        // uniqueRtcengin.destroy();
        // userComingForFirstTime = "null";
    }

}
