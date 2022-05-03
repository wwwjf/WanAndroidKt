package com.wwwjf.hookdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

public class HookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hook);
    }

    public void hookToSecond(View view) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(getPackageName(), SecondActivity.class.getName()));
        startActivity(intent);
    }
}