package com.example.standaloneagora;

import android.content.Context;
import android.widget.Toast;

public class Toasty {
    private static void simpleMsg (Context c ,String s)
    {
        Toast.makeText( c,s, Toast.LENGTH_SHORT).show();
    }
}
