package com.example.agorawrapper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
    public static final String maintag = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = null;
        try {
            intent = new Intent(this, Class.forName("com.example.standaloneagora.TestingActivity"));
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
   //     downloadAgoraCredentialsFromFirebase();
       //        mRtcEngine = initializeAndJoinChannel("306d4292730a4434906dec9a80964d6f", "006306d4292730a4434906dec9a80964d6fIADY5QlHmBfUA8vCo1mFdvpDCvoGWNhFkvTlBpS2oR/WtuNzl6AAAAAAEAAJmhJv3xdAYgEAAQDeF0Bi", "Mohit");

    }

}