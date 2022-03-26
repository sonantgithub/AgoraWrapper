package com.example.agorawrapper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;


public class MainActivity extends AppCompatActivity {

    public RtcEngine mRtcEngine;
    public static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


   //     downloadAgoraCredentialsFromFirebase();
               mRtcEngine = initializeAndJoinChannel("306d4292730a4434906dec9a80964d6f", "006306d4292730a4434906dec9a80964d6fIADY5QlHmBfUA8vCo1mFdvpDCvoGWNhFkvTlBpS2oR/WtuNzl6AAAAAAEAAJmhJv3xdAYgEAAQDeF0Bi", "Mohit");

    }

    private void downloadAgoraCredentialsFromFirebase() {

        FirebaseDatabase.getInstance("https://testdbsigneybooks.firebaseio.com/").getReference().child("AgoraCredentials").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String appID = dataSnapshot.child("appID").getValue().toString();
                String temporaryToken = dataSnapshot.child("temporaryToken").getValue().toString();
                String channelName = dataSnapshot.child("channelName").getValue().toString();


                Log.d(TAG, "onDataChange: " + appID + temporaryToken + channelName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {

        @Override
        // Listen for the remote user joining the channel to get the uid of the user.
        public void onUserJoined(int uid, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "run: ");
                    // Call setupRemoteVideo to set the remote video view after getting uid from the onUserJoined callback.
                    setupRemoteVideo(uid);
                }
            });
        }
    };

    public void setupRemoteVideo(int uid) {
        Log.d(TAG, "setupRemoteVideo: ");
        FrameLayout container = findViewById(R.id.remote_video_view_container);
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getApplicationContext());
        surfaceView.setZOrderMediaOverlay(true);
        container.addView(surfaceView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid));
    }

    public RtcEngine initializeAndJoinChannel(String appId, String token, String channelName) {
        Log.d(TAG, "initializeAndJoinChannel: ");


        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), appId, mRtcEventHandler);
        } catch (Exception e) {
            throw new RuntimeException("Check the error.");
        }

        // By default, video is disabled, and you need to call enableVideo to start a video stream.
        mRtcEngine.enableVideo();

        FrameLayout container = findViewById(R.id.local_video_view_container);
        // Call CreateRendererView to create a SurfaceView object and add it as a child to the FrameLayout.
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        container.addView(surfaceView);
        // Pass the SurfaceView object to Agora so that it renders the local video.
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0));

        // Join the channel with a token.
        mRtcEngine.joinChannel(token, channelName, "", 0);

        return mRtcEngine;

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRtcEngine.leaveChannel();
        mRtcEngine.destroy();
    }
}