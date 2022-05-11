package com.wwwjf.mvp.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.wwwjf.mvp.IMvpView;
import com.wwwjf.mvp.MvpController;
import com.wwwjf.mvp.helper.FragmentBackHandler;


public class LifeCycleMvpFragment extends Fragment implements IMvpView, FragmentBackHandler {

    private MvpController mvpController;

    @Override
    public MvpController getMvpController() {

        if (this.mvpController == null){
            mvpController = new MvpController();
        }
        return mvpController;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle == null){
            bundle = new Bundle();
        }

        MvpController mvpController = this.getMvpController();
        if (mvpController != null) {
            mvpController.onCreate(savedInstanceState, null, bundle);
            mvpController.onActivityCreated(savedInstanceState,null,bundle);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle == null){
            bundle = new Bundle();
        }

        MvpController mvpController = this.getMvpController();
        if (mvpController != null) {
            mvpController.onActivityCreated(savedInstanceState,null,bundle);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        MvpController mvpController = this.getMvpController();
        if (mvpController != null) {
            mvpController.onStart();
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        MvpController mvpController = this.getMvpController();
        if (mvpController != null) {
            mvpController.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        MvpController mvpController = this.getMvpController();
        if (mvpController != null) {
            mvpController.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        MvpController mvpController = this.getMvpController();
        if (mvpController != null) {
            mvpController.onStop();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        MvpController mvpController = this.getMvpController();
        if (mvpController != null){
            mvpController.onDestroyView();
        }
    }

    @Override
    public void onDestroy() {

        MvpController mvpController = this.getMvpController();
        if (mvpController != null) {
            mvpController.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        MvpController mvpController = this.getMvpController();
        if (mvpController != null) {
            mvpController.onSaveInstanceState(outState);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        MvpController mvpController = this.getMvpController();
        if (mvpController != null) {
            mvpController.onActivityResult(requestCode,resultCode,data);
        }
    }


    @Override
    public boolean onBackPressed() {
        return false;
    }
}
