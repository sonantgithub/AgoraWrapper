package com.example.standaloneagora;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

public class AgoraClass extends AppCompatActivity {

    public static final String TAG = "AgoraClass";

    public RtcEngine uniqueRtcengin;
    public View remoteViewPublic;
    public Context publicContext;


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
        uniqueRtcengin.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uniqueRtcengin.leaveChannel();
        uniqueRtcengin.destroy();
    }
}