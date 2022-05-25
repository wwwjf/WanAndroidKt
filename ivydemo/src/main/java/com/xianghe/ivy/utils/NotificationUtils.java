package com.xianghe.ivy.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;

import androidx.core.app.NotificationCompat;

import com.xianghe.ivy.R;

/**
 * author:  ycl
 * date:  2019/3/27 11:37
 * desc:
 */
public class NotificationUtils {


    public static void dismissNotification(NotificationManager manager, int id) {
        KLog.d(  "dismissNotification: ");
        if (manager != null) {
            manager.cancel(id);
        }
    }

    public static NotificationManager showNotification(Context context, String contentTitle, String contentText, int id ,Intent intent) {
        KLog.d(  "showNotification: ");
        //获取系统通知服务
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //创建 PendingIntent
        if (intent == null) {
            intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());//获取启动Activity
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, -1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

//        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        Notification notification = new NotificationCompat.Builder(context)
                //   contentTitle =null ,就值显示contentText 到 contentTitle 位置
                .setContentTitle(contentTitle != null ? contentTitle : contentText)
                .setContentText(contentTitle != null ? contentText : null)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.logo_icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.logo_icon))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
//                .setSound(sound)
//                .setVibrate(new long[]{0,1000,1000,1000})
                .build();
        manager.notify(id, notification);
        return manager;
    }
}
