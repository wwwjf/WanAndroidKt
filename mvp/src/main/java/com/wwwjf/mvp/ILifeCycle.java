package com.wwwjf.mvp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

public interface ILifeCycle {

    void onCreate(Bundle savedInstanceState, Intent intent, Bundle getArguments);


    void onActivityCreated(Bundle savedInstanceState, Intent intent, Bundle getArguments);


    void onStart();


    void onResume();


    void onPause();


    void onStop();


    void onDestroyView();


    void onViewDestroy();


    void onDestroy();


    void onNewIntent(Intent intent);


    void onActivityResult(int requestCode, int resultCode, @Nullable Intent data);


    void onSaveInstanceState(Bundle outState);


    void attachView(IMvpView iMvpView);
}
