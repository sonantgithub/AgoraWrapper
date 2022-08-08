package com.example.standaloneagora;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

public class SigneyStreamingClass extends AppCompatActivity {

    public static final String maintag = "SigneyStreamingClass";

    public RtcEngine uniqueRtcengin;
    public View remoteViewPublic, gifView;
    public Context publicContext;
    public String clientIdCopy, clientPasswordcopy, stringToConvertCopy;
    public String tempChannelName, tempAppId, tempTokenNumber;
    public String time, userComingForFirstTime;
    public long timeOfStreamingStart, timeOfStreamingStopped, totalTimeOfStreming;
    public int countOfTotalHits;


    public void startStreaming(String clientId, String clientPassword, Context context, String stringToConvert, View view, View gifId) {

        //Need to public parameters
        publicContext = context;
        remoteViewPublic = view;
        gifView = gifId;
        clientIdCopy = clientId;
        clientPasswordcopy = clientPassword;
        stringToConvertCopy = stringToConvert;
        FirebaseApp.initializeApp(publicContext);

        if ("no".equals(userComingForFirstTime)) {

            Log.d(maintag, "startStreaminggy: " + userComingForFirstTime);

            sendStringToFirebase();
        } else {
            validationOfCredentials();
            Log.d(maintag, "startStreaminggn: " + userComingForFirstTime);
        }

    }

    /**
     * Process1 <h3>We are checking if client is valid or not</h3>
     */
    private void validationOfCredentials() {
        Toast.makeText(publicContext, "validating credentials", Toast.LENGTH_SHORT).show();

        FirebaseDatabase.getInstance("https://signeystreamingdb.firebaseio.com/").getReference()
                .child("clients").child(clientIdCopy).child("credentials").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String password = snapshot.child("password").getValue().toString();
                if (password.equals(clientPasswordcopy)) {
                    turnOnNeedNewChannelFlag();
                } else {
                    //toasty.simpleMsg(publicContext, "Wrong Password");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //toasty.simpleMsg(publicContext, "Wrong clientId");
            }
        });
    }

    /**
     * Process 2 <h3>Need to turn on the flag to get the client credentials</h3>
     */
    private void turnOnNeedNewChannelFlag() {
        Toast.makeText(publicContext, "creating new channel", Toast.LENGTH_SHORT).show();

        time = String.valueOf(System.currentTimeMillis() / 1000);
        HashMap hashMap = new HashMap();
        hashMap.put(time, "Pending");
        FirebaseDatabase.getInstance("https://signeystreamingdb.firebaseio.com/").getReference()
                .child("zchannelsAwaitingAssignments").updateChildren(hashMap).addOnCompleteListener(task -> listenIfAnyChannelIsCreatedOrNot());
    }


    /**
     * Process 3 <h3>Listening if any new channel is created or not</h3>
     */
    private void listenIfAnyChannelIsCreatedOrNot() {
        Toast.makeText(publicContext, "listing for new channel", Toast.LENGTH_SHORT).show();

        FirebaseDatabase.getInstance("https://signeystreamingdb.firebaseio.com/").getReference()
                .child("zchannelsAwaitingResponse").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.hasChild(time)) {
                    Log.d(maintag, "onDataChange: i am here 1 ");

                    tempAppId = snapshot.child(time).child("appId").getValue().toString();
                    tempTokenNumber = snapshot.child(time).child("token").getValue().toString();

                    Log.d(maintag, "onDataChange: 15" + tempAppId + tempTokenNumber);
                    startUnityStreaming();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Process 4 <h3>Download data of the temporary channel name assign to user</h3>
     */
    private void downloadCredentilasOfChannnel() {
        FirebaseDatabase.getInstance("https://signeystreamingdb.firebaseio.com/").getReference()
                .child("channelsAwaitingResponse").child(time).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Process 5.1 <h3>Starting streaming </h3>
     */

    private void startUnityStreaming() {

        initializeAndJoinChannel(tempAppId, tempTokenNumber, time, publicContext);

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
        Log.d(maintag, "process 3: ");
        FrameLayout container = (FrameLayout) remoteview;
        SurfaceView surfaceView = RtcEngine.CreateRendererView(publicContext);
        surfaceView.setZOrderMediaOverlay(true);
        container.addView(surfaceView);
        uniqueRtcengin.disableAudio();
        uniqueRtcengin.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid, 1));


// Storing data into SharedPreferences for
        userComingForFirstTime = "no";
        listenForIfUserStillUsingChannel();

    }

    /**
     * Process 5.2 <h3>listen For If User Still Using Channel</h3>
     */
    private void listenForIfUserStillUsingChannel() {
        timeOfStreamingStart = (System.currentTimeMillis() / 1000);
        FirebaseDatabase.getInstance("https://signeystreamingdb.firebaseio.com/").getReference()
                .child("zchannelsAwaitingResponse").child(time).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("isChannelDestroyed")) {

                    String leaveChannelIfUserIsNotUsing = snapshot.child("isChannelDestroyed").getValue().toString();
                    Log.d(maintag, "onDataChange: 5 " + leaveChannelIfUserIsNotUsing);
                    if ("true".equals(leaveChannelIfUserIsNotUsing)) {
                        Log.d(maintag, "onDataChange:6 ");
                        Toast.makeText(publicContext, "stop streaming", Toast.LENGTH_SHORT).show();


                        uniqueRtcengin.leaveChannel();
                        uniqueRtcengin.destroy();
                        gifView.setVisibility(View.VISIBLE);
                        Glide.with(publicContext)
                                .load(R.raw.defposegif)
                                .centerCrop()
                                .into((ImageView) gifView);

                        userComingForFirstTime = "null";
                        timeOfStreamingStopped = (System.currentTimeMillis() / 1000);

                        totalTimeOfStreming = timeOfStreamingStopped - timeOfStreamingStart;

                        uploadTotalTimeOfStreaming();

                    }

                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void uploadTotalTimeOfStreaming() {
        HashMap hashMap = new HashMap();
        hashMap.put("totalTime", totalTimeOfStreming);
        hashMap.put("totalHits", countOfTotalHits);

        FirebaseDatabase.getInstance("https://signeystreamingdb.firebaseio.com/").getReference().child("clients")
                .child(clientIdCopy).child("usage").child(time).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

            }
        });
    }

    public void sendStringToFirebase() {
        countOfTotalHits++;
        HashMap hashMap = new HashMap();
        hashMap.put("framedata", stringToConvertCopy);
        hashMap.put("isUserStillUsingChannel", "true");
        FirebaseDatabase.getInstance("https://signeystreamingdb.firebaseio.com/").getReference().child("zchannelsAwaitingResponse")
                .child(time).updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        HashMap hashMapTwo = new HashMap();
                        hashMapTwo.put("booleanForSameWord", "true");
                        FirebaseDatabase.getInstance("https://signeystreamingdb.firebaseio.com/").getReference().child("zchannelsAwaitingResponse")
                                .child(time).updateChildren(hashMapTwo)
                                .addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {

                                    }
                                });
                    }
                });
    }

    public void stopStreaming() {
        Toast.makeText(publicContext, "stop streaming", Toast.LENGTH_SHORT).show();
        uniqueRtcengin.leaveChannel();
        uniqueRtcengin.destroy();
        userComingForFirstTime = "null";
        timeOfStreamingStopped = (System.currentTimeMillis() / 1000);

        totalTimeOfStreming = timeOfStreamingStopped - timeOfStreamingStart;

        uploadTotalTimeOfStreaming();
    }

}

