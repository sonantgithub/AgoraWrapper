package com.example.standaloneagora;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Toasty {
    Context contextlib;
    DatabaseReference reference = FirebaseDatabase.getInstance("https://signeystreamingdb.firebaseio.com/").getReference();

    public void simpleMsg(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public void testingofFirebase(Context context, String text) {

        FirebaseApp.initializeApp(contextlib);

        HashMap hashMap = new HashMap();
        hashMap.put("test", "test");


        reference.child("testing").updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                    }
                });

    }
}