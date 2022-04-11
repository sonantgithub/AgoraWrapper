package com.example.standaloneagora;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class TestingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);
    }

    public void intentToTheNextActivity(View view) {

        Intent intent = new Intent(this, AgoraActivity.class);
        startActivity(intent);
    }
}