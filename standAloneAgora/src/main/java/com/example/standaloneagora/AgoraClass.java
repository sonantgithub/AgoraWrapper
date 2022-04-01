package com.example.standaloneagora;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

public class AgoraClass extends AppCompatActivity {

    public static final String TAG = "AgoraClass";

    public RtcEngine uniqueRtcengin;
    public View remoteViewPublic;
    public Context publicContext;
    public int numberOfHits, uniqueIDSonantClint;


    public void startStreaming(String stringYouWantToConvert, int uniqueID, Context getBaseContent, View localView, View remoteView) {
        numberOfHits++;
        uniqueIDSonantClint = uniqueID;
        HashMap hashMap = new HashMap();
        hashMap.put("framedata", stringYouWantToConvert);
        FirebaseDatabase.getInstance("https://signeywebdb.firebaseio.com/").getReference().child("unitysign/showsign").updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        HashMap hashMapTwo = new HashMap();
                        hashMapTwo.put("booleanForSameWord", "true");
                        FirebaseDatabase.getInstance("https://signeywebdb.firebaseio.com/").getReference().child("unitysign/conditionalBooleanFlags").updateChildren(hashMapTwo)
                                .addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {

                                        initializeAndJoinChannel("3b39f5b040784990a19ed302ea47f051", "0063b39f5b040784990a19ed302ea47f051IAAYC+VljV5ZjNM0nqW0qYarJieyv9zAHxEH+dj1q/s3Xh8PEr0AAAAAIgCXeLre5CdFYgQAAQA3akhiAgA3akhiAwA3akhiBAA3akhi", "Mohit5", getBaseContent, localView, remoteView);


                                    }
                                });
                    }
                });


    }


    public void initializeAndJoinChannel(String appId, String token, String channelName, Context context, View view, View remoteView) {
        RtcEngine mRtcEngine;
        try {
            mRtcEngine = RtcEngine.create(context, appId, mRtcEventHandler);
        } catch (Exception e) {
            throw new RuntimeException("Check the error.");
        }

        // By default, video is disabled, and you need to call enableVideo to start a video stream.
        mRtcEngine.enableVideo();

        FrameLayout container = (FrameLayout) view;
        // Call CreateRendererView to create a SurfaceView object and add it as a child to the FrameLayout.
        SurfaceView surfaceView = RtcEngine.CreateRendererView(context);
        container.addView(surfaceView);
        // Pass the SurfaceView object to Agora so that it renders the local video.
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0));

        // Join the channel with a token.
        mRtcEngine.joinChannel(token, channelName, "", 0);
        remoteViewPublic = remoteView;
        publicContext = context;

        Log.d(TAG, "process 1: ");

        uniqueRtcengin = mRtcEngine;
    }

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {

        @Override
        // Listen for the remote user joining the channel to get the uid of the user.
        public void onUserJoined(int uid, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "process 2: ");
                    // Call setupRemoteVideo to set the remote video view after getting uid from the onUserJoined callback.
                    setupRemoteVideo(uid, remoteViewPublic);
                }
            });
        }
    };

    public void setupRemoteVideo(int uid, View remoteview) {
        Log.d(TAG, "process 3: ");
        FrameLayout container = (FrameLayout) remoteview;
        SurfaceView surfaceView = RtcEngine.CreateRendererView(publicContext);
        surfaceView.setZOrderMediaOverlay(true);
        container.addView(surfaceView);
        uniqueRtcengin.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid, 1));
        HashMap hashMap = new HashMap();
        hashMap.put(String.valueOf(uniqueIDSonantClint), numberOfHits);
        FirebaseDatabase.getInstance("https://signeywebdb.firebaseio.com/").getReference().child("unitysign/sonantStreamingUser").updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uniqueRtcengin.leaveChannel();
        uniqueRtcengin.destroy();
    }
}