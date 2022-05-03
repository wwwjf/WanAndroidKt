package com.wwwjf.hookdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.File;

public class HookDemoActivity extends AppCompatActivity {


    private static final String TAG = HookDemoActivity.class.getSimpleName();


    private void loge(Object message){
        Log.e(TAG,"---"+message);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loge("onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        loge("onStart");
    }


    @Override
    protected void onResume() {
        super.onResume();
        loge("onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        loge("onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        loge("onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loge("onDestroy");
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hook_demo);
        loge("onCreate");
        File externalFilesDir = getExternalFilesDir("");
        String path = externalFilesDir.getPath();
        loge("path="+path);

    }

    public void mainToHook(View view) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(getPackageName(),HookActivity.class.getName()));
//        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);相当于startActivityForresult
        intent.putExtra("fakeComponentName",new ComponentName(getPackageName(),ProxyActivity.class.getName()));
        intent.putExtra("realComponentName",new ComponentName(getPackageName(),HookActivity.class.getName()));
        startActivity(intent);
    }

    public void mainToHook2(View view) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(getPackageName(),Hook2Activity.class.getName()));
        intent.putExtra("fakeComponentName",new ComponentName(getPackageName(),ProxyActivity.class.getName()));
        intent.putExtra("realComponentName",new ComponentName(getPackageName(),Hook2Activity.class.getName()));
        startActivity(intent);
    }

    public void mainToProxy(View view) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(getPackageName(), ProxyActivity.class.getName()));
        intent.putExtra("fakeComponentName",new ComponentName(getPackageName(),ProxyActivity.class.getName()));
        intent.putExtra("realComponentName",new ComponentName(getPackageName(),ProxyActivity.class.getName()));
        startActivity(intent);
    }

}