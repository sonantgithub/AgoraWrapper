package com.example.agorawrapper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import io.socket.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import io.agora.rtc.RtcEngine;
import io.socket.client.Socket;
import io.socket.client.IO;


public class MainActivity extends AppCompatActivity {

    public RtcEngine mRtcEngine;
    private EditText mInputMessageView;
    public static final String maintag = "MainActivity";
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("https://signey-streaming-server.herokuapp.com");
        } catch (URISyntaxException e) {}
    }
    public static final String TAG = "MainActivity";

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
        mInputMessageView = findViewById(R.id.editText);

//        mSocket.connect(); // connect the socket
//
//        mSocket.on("62595458", onNewMessage); //Listen response coming from node
//
//        Log.d(TAG, "onCreate:3 ");
    }


    public void sendToFirebase(View view) {
//        String message = mInputMessageView.getText().toString().trim();
//        mInputMessageView.setText("");
//        mSocket.emit("62595458", message); // send message to the node
//        Log.d(TAG, "onCreate:4 ");
    }


//    private Emitter.Listener onNewMessage = new Emitter.Listener() {
//        @Override
//        public void call(final Object... args) {
//            MainActivity.this.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                      Log.d(TAG, "response of socket: "+args[0].toString());
//                    }
//                    catch (Exception e){
//                        Log.d(TAG, "run: "+e.getMessage());
//                    }
//                }
//            });
//        }
//    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        //  mSocket.off("login", "onLogin");
    }

}



//        Intent intent = null;
//        try {
//            intent = new Intent(this, Class.forName("com.example.standaloneagora.TestingActivity"));
//            startActivity(intent);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }


//     downloadAgoraCredentialsFromFirebase();
//        mRtcEngine = initializeAndJoinChannel("306d4292730a4434906dec9a80964d6f", "006306d4292730a4434906dec9a80964d6fIADY5QlHmBfUA8vCo1mFdvpDCvoGWNhFkvTlBpS2oR/WtuNzl6AAAAAAEAAJmhJv3xdAYgEAAQDeF0Bi", "Mohit");

