package com.example.standaloneagora;

import android.content.Context;
import android.widget.Toast;

public class Toasty {

    public void simpleMsg(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}


