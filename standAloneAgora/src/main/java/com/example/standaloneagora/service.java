package com.example.standaloneagora;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import java.util.List;
import io.socket.emitter.Emitter;

public class service extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this, "Service started by user."+intent.getStringExtra("channelName"), Toast.LENGTH_LONG).show();
        String nameOfActivity = intent.getStringExtra("activityName");

     new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ActivityManager am = (ActivityManager)service.this.getSystemService(ACTIVITY_SERVICE);
                List< ActivityManager.RunningTaskInfo > taskInfo = am.getRunningTasks(1);
                ComponentName componentInfo = taskInfo.get(0).topActivity;
                String className = componentInfo.getClassName();
                Toast.makeText(service.this, className, Toast.LENGTH_LONG).show();
//                if (nameOfActivity.equals(className))
//                {
//                    Toast.makeText(service.this,"App is turn on", Toast.LENGTH_LONG).show();
//                }
//                else
//                {
//                    Toast.makeText(service.this,"App is off", Toast.LENGTH_LONG).show();
//                }
            }
        }, 5000);





     //        String currentTimeMiles = intent.getStringExtra("channelName");
//        try {
//            Socket mSocket;
//            mSocket = IO.socket("https://signey-streaming-server.herokuapp.com");
//            mSocket.connect(); // connect the socket
//            mSocket.on(currentTimeMiles, onNewMessage); //Listen response coming from node
//            mSocket.emit("killChannel", currentTimeMiles); // send message to the node
//
//        } catch (URISyntaxException e) {
//            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
//        }
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
