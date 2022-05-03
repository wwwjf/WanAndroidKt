package com.wwwjf.hookdemo.hotfix;

import android.app.Application;
import android.os.Build;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Hotfix {
    public static void installPatch(Application app, File file) {

        ClassLoader classLoader = app.getClassLoader();
        List<File> fileList = new ArrayList<>();
        if (file.exists()){
            fileList.add(file);
        }
        File dexOptDir = app.getCacheDir();
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            NewClassLoaderInject.inject(app,classLoader,fileList);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            }
        }
    }

    public static Object[] makePathElements(Object dexPathlist, ArrayList<File> files,
                                            File optimizeDirectory,
                                            ArrayList<IOException> suppressedExceptions){

        return null;
    }
}
