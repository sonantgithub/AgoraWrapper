package com.example.standaloneagora;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

public class AgoraActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    AgoraClass agoraClass = new AgoraClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agora);

        Log.d(TAG, "onCreate: AgoraActivity");

agoraClass.initializeAndJoinChannel("306d4292730a4434906dec9a80964d6f", "006306d4292730a4434906dec9a80964d6fIAArrGwEm53R0ZIXvtT6EbhbAVi6HVqZSYXfQiV/oGIDwONzl6AAAAAAEAAJmhJv685DYgEAAQDozkNi", "Mohit", getBaseContext(),findViewById(R.id.local_video_view_container),findViewById(R.id.remote_video_view_container));


        //     downloadAgoraCredentialsFromFirebase();
  //initializeAndJoinChannel("306d4292730a4434906dec9a80964d6f", "006306d4292730a4434906dec9a80964d6fIAArrGwEm53R0ZIXvtT6EbhbAVi6HVqZSYXfQiV/oGIDwONzl6AAAAAAEAAJmhJv685DYgEAAQDozkNi", "Mohit",getBaseContext(),R.id.local_video_view_container);
    }

//    public void initializeAndJoinChannel(String appId, String token, String channelName, Context context,int view) {
//        RtcEngine mRtcEngine = null;
//        try {
//           mRtcEngine = RtcEngine.create(context, appId, mRtcEventHandler);
//        } catch (Exception e) {
//            throw new RuntimeException("Check the error.");
//        }
//
//        // By default, video is disabled, and you need to call enableVideo to start a video stream.
//        mRtcEngine.enableVideo();
//
//        FrameLayout container = findViewById(view);
//        // Call CreateRendererView to create a SurfaceView object and add it as a child to the FrameLayout.
//        SurfaceView surfaceView = RtcEngine.CreateRendererView(context);
//        container.addView(surfaceView);
//        // Pass the SurfaceView object to Agora so that it renders the local video.
//        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0));
//
//        // Join the channel with a token.
//        mRtcEngine.joinChannel(token, channelName, "", 0);
//
//        Log.d(TAG, "process 1: ");
//
//        uniqueRtcengin = mRtcEngine;
//    }

//    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
//
//        @Override
//        // Listen for the remote user joining the channel to get the uid of the user.
//        public void onUserJoined(int uid, int elapsed) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Log.d(TAG, "process 2: ");
//                    // Call setupRemoteVideo to set the remote video view after getting uid from the onUserJoined callback.
//                    setupRemoteVideo(uid);
//                }
//            });
//        }
//    };

//    public void setupRemoteVideo(int uid) {
//        Log.d(TAG, "process 3: ");
//        FrameLayout container = findViewById(R.id.remote_video_view_container);
//        SurfaceView surfaceView = RtcEngine.CreateRendererView(getApplicationContext());
//        surfaceView.setZOrderMediaOverlay(true);
//        container.addView(surfaceView);
//        uniqueRtcengin.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid));
//    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        uniqueRtcengin.leaveChannel();
//        uniqueRtcengin.destroy();
//    }
}