package com.example.standaloneagora;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

public class AgoraActivity extends AppCompatActivity {
    public static final String TAG = "AgoraActivity";
    AgoraClass agoraClass = new AgoraClass();
    RtcEngine uniqueRtcengin;
    Button button;
    EditText editText;
    SigneyStreamingClass signeyStreamingClass = new SigneyStreamingClass();
    Toasty toasty = new Toasty();
    StartSignStreaming startSignStreaming = new StartSignStreaming();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agora);
        editText = findViewById(R.id.edittextBox);
        button = findViewById(R.id.showSign);
        startSignStreaming.start("c1", "c1@password",AgoraActivity.this, editText.getText().toString(), findViewById(R.id.remote_video_view_container),findViewById(R.id.gifView));
    }
    public void SendDataToFireBase(View view) {
         startSignStreaming.start("c1", "c1@password",AgoraActivity.this, editText.getText().toString(), findViewById(R.id.remote_video_view_container),findViewById(R.id.gifView));
    }
    @Override
    protected void onStop() {
        super.onStop();
        startSignStreaming.stopStreaming();
    }
}