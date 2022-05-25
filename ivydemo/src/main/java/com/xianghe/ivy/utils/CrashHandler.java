package com.xianghe.ivy.utils;


import android.content.Context;
import android.os.Environment;
import android.widget.Toast;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";

    private static CrashHandler mInstance = null;

    private Context mAppCtx;
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private CrashHandler(Context ctx) {
        mAppCtx = ctx.getApplicationContext();
    }

    public static CrashHandler getInstance(Context ctx) {
        if (mInstance == null) {
            synchronized (CrashHandler.class) {
                if (mInstance == null) {
                    mInstance = new CrashHandler(ctx);
                }
            }
        }
        return mInstance;
    }

    public void init() {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        KLog.e(TAG, "e" + e);
        if (e == null) {
            return;
        }

        Toast.makeText(mAppCtx, "程序异常", Toast.LENGTH_SHORT).show();
        File file = new File(Environment.getExternalStorageDirectory(), "ivycrash/" + System.currentTimeMillis() + ".txt");
        saveCrashInfo2File(e, file);
    }

    private void saveCrashInfo2File(Throwable e, File out) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(out);
            e.printStackTrace(pw);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
    }
}
