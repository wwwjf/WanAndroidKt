package com.xianghe.ivy.ui.media.premission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.xianghe.ivy.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: ycl
 * @Date: 2018/10/26 14:04
 * @Desc:
 */
public class PermissionUtils {
    public PermissionUtils() throws Exception {
        throw new Exception("not init ");
    }

    public static boolean requestPermissions(Activity activity,String[]  needPermissionList, int currentRequestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 被用户拒绝的权限列表
            List<String> rejectPermissionList = new ArrayList<>();
            for (String p : needPermissionList) {
                if (ContextCompat.checkSelfPermission(activity, p) !=
                        PackageManager.PERMISSION_GRANTED) {
                    rejectPermissionList.add(p);
                }
            }
            // 只申请被用户拒绝过了的权限
            if (!rejectPermissionList.isEmpty()) {
                String[] permission = rejectPermissionList.toArray(new String[rejectPermissionList.size()]);
                ActivityCompat.requestPermissions(activity, permission, currentRequestCode);
                return true;
            } else {
                return false;
            }

        } else {
            return false;
        }
    }

    public static boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults,
                                                      int currentRequestCode, Context context) {
        if (requestCode == currentRequestCode) {
            for (int results : grantResults) {
                if (results != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, context.getString(R.string.common_permission_forbidden), Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static void requestPermissions(Activity context, String[] permissions, int reqCode, Runnable runnable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 被用户拒绝的权限列表
            List<String> mPermissionList = new ArrayList<>();
            for (String p : permissions) {
                if (ActivityCompat.checkSelfPermission(context, p) != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(p);
                }
            }
            // 只申请被用户拒绝过了的权限
            if (!mPermissionList.isEmpty()) {
                String[] permission = mPermissionList.toArray(new String[mPermissionList.size()]);
                ActivityCompat.requestPermissions(context, permission, reqCode);
            } else {
                runnable.run();
            }
        } else {
            runnable.run();
        }
    }

    public static void onRequestPermissionsResult(boolean isReq, int[] grantResults, Runnable okRun, Runnable deniRun) {
        if (isReq) {
            if (grantResults.length > 0) {
                for (int results : grantResults) {
                    if (results != PackageManager.PERMISSION_GRANTED) {
                        deniRun.run();
                        return;
                    }
                }
                okRun.run();
            } else {
                deniRun.run();
            }
        }
    }

}
