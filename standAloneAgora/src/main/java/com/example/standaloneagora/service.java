package com.example.standaloneagora;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class service extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //  Toast.makeText(this, "Service started by user."+intent.getStringExtra("channelName"), Toast.LENGTH_LONG).show();
        String currentTimeMiles = intent.getStringExtra("channelName");
        try {
            Socket mSocket;
            mSocket = IO.socket("https://signey-streaming-server.herokuapp.com");
            mSocket.connect(); // connect the socket
            mSocket.on(currentTimeMiles, onNewMessage); //Listen response coming from node
            mSocket.emit("killChannel", currentTimeMiles); // send message to the node

        } catch (URISyntaxException e) {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
     // Toast.makeText(this, "Service destroyed by user.", Toast.LENGTH_LONG).show();
    }
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d("TAG", "call: ");
        }
    };
}
