package com.wwwjf.mvp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.wwwjf.mvp.presenter.LifeCycleMvpPresenter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MvpController implements ILifeCycle{

    //存放P层的实例
    private Set<ILifeCycle> lifeCycles = new HashSet<>();

    public static MvpController newInstance(){
        return new MvpController();
    }


    public <T> void savePresenter(LifeCycleMvpPresenter<T> lifeCycleMvpPresenter) {
        lifeCycles.add(lifeCycleMvpPresenter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState, Intent intent, Bundle getArguments) {

        Iterator<ILifeCycle> iterator = lifeCycles.iterator();
        while (iterator.hasNext()){
            ILifeCycle presenter = iterator.next();
            if (presenter == null){
                intent = new Intent();
            }

            if (getArguments == null){
                getArguments = new Bundle();
            }

            presenter.onCreate(savedInstanceState,intent,getArguments);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState, Intent intent, Bundle getArguments) {

        Iterator<ILifeCycle> iterator = lifeCycles.iterator();
        while (iterator.hasNext()){
            ILifeCycle presenter = iterator.next();
            if (presenter == null){
                intent = new Intent();
            }

            if (getArguments == null){
                getArguments = new Bundle();
            }

            presenter.onActivityCreated(savedInstanceState,intent,getArguments);
        }
    }

    @Override
    public void onStart() {

        Iterator<ILifeCycle> iterator = lifeCycles.iterator();
        while (iterator.hasNext()){
            ILifeCycle presenter = iterator.next();
            presenter.onStart();
        }
    }

    @Override
    public void onResume() {

        Iterator<ILifeCycle> iterator = lifeCycles.iterator();
        while (iterator.hasNext()){
            ILifeCycle presenter = iterator.next();
            presenter.onResume();
        }
    }

    @Override
    public void onPause() {

        Iterator<ILifeCycle> iterator = lifeCycles.iterator();
        while (iterator.hasNext()){
            ILifeCycle presenter = iterator.next();
            presenter.onPause();
        }
    }

    @Override
    public void onStop() {

        Iterator<ILifeCycle> iterator = lifeCycles.iterator();
        while (iterator.hasNext()){
            ILifeCycle presenter = iterator.next();
            presenter.onStop();
        }
    }

    @Override
    public void onDestroyView() {

        Iterator<ILifeCycle> iterator = lifeCycles.iterator();
        while (iterator.hasNext()){
            ILifeCycle presenter = iterator.next();
            presenter.onDestroyView();
        }
    }

    @Override
    public void onViewDestroy() {

        Iterator<ILifeCycle> iterator = lifeCycles.iterator();
        while (iterator.hasNext()){
            ILifeCycle presenter = iterator.next();
            presenter.onViewDestroy();
        }
    }

    @Override
    public void onDestroy() {

        Iterator<ILifeCycle> iterator = lifeCycles.iterator();
        while (iterator.hasNext()){
            ILifeCycle presenter = iterator.next();
            presenter.onDestroy();
        }
    }

    @Override
    public void onNewIntent(Intent intent) {

        Iterator<ILifeCycle> iterator = lifeCycles.iterator();
        while (iterator.hasNext()){
            ILifeCycle presenter = iterator.next();
            if (intent == null){
                intent = new Intent();
            }
            presenter.onNewIntent(intent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        Iterator<ILifeCycle> iterator = lifeCycles.iterator();
        while (iterator.hasNext()){
            ILifeCycle presenter = iterator.next();
            presenter.onActivityResult(requestCode,resultCode,data);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        Iterator<ILifeCycle> iterator = lifeCycles.iterator();
        while (iterator.hasNext()){
            ILifeCycle presenter = iterator.next();
            presenter.onSaveInstanceState(outState);
        }
    }

    @Override
    public void attachView(IMvpView iMvpView) {

    }

}
