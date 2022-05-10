package com.wwwjf.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;


import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 *权限管理
 */
public class PermissionManager {
    private static final int REQUEST_PERMISSION_CODE = 1000;

    private WeakReference<Object> mWeakRef;

    private PermissionListener mListener;

    private String tag;

    private CustomProgress mCustomProgress;

    private boolean needCustomEnsure;

    public PermissionManager(@NonNull Object context) {
        mWeakRef = new WeakReference<Object>(context);
    }

    /**
     * 权限授权申请
     *
     * @param permissions 要申请的权限
     * @param listener    申请成功之后的callback
     */
    public void requestPermissions(@NonNull Activity context,
                                   @Nullable PermissionListener listener,
                                   @NonNull String[] permissions,
                                   String tag, boolean needCustomEnsure) {
        this.needCustomEnsure = needCustomEnsure;
        if (listener != null) {
            mListener = listener;
        }

        if (!TextUtils.isEmpty(tag)) {
            this.tag = tag;
        }

        //没全部权限
        //判断是否有权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (hasPermissions(context, permissions).length != 0) {
                executePermissionsRequest(hasPermissions(context, permissions));
            } else if (mListener != null) { //有全部权限
                mListener.doAfterGrand(permissions, tag);
            }
        } else {
            mListener.doAfterGrand(permissions, tag);
        }
    }

    /**
     * 权限授权申请
     *
     * @param permissions 要申请的权限
     * @param listener    申请成功之后的callback
     */
    public void requestPermissions(@NonNull Activity context,
                                   @Nullable PermissionListener listener,
                                   @NonNull String[] permissions,
                                   String tag) {
        requestPermissions(context, listener, permissions, tag, false);
    }

    /**
     * 处理onRequestPermissionsResult
     */
    public void handleRequestPermissionsResult(final Context context, int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE:
                boolean allGranted = true;
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        allGranted = false;
                        break;
                    }

                    //检测小米手机
                    if (isMIUI()) {
                        if (!initMUIPermission(permissions[i])) {
                            executePermissionsRequest(new String[]{permissions[i]});
                        }
                    }
                }

                if (allGranted && mListener != null) {
                    mListener.doAfterGrand(permissions, tag);
                } else if (!allGranted && mListener != null) {
                    showMessageOKCancel(context, context.getString(R.string.common_no_permission_to_open), v -> {
                        destroy();
                        if (needCustomEnsure) {
                            //判断是否权限点击了不再询问
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                                if (!shouldShowRequestPermissionRationale(permissions)) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    // 根据包名打开对应的设置界面
                                    if (isWeakNotNull() && mWeakRef.get() instanceof Activity) {
                                        intent.setData(Uri.parse("package:" + context.getPackageName()));
                                        ((Activity) mWeakRef.get()).startActivityForResult(intent, BaseConstant.Permission.REQUEST_CODE);
                                    } else if (isWeakNotNull() &&mWeakRef.get() instanceof Fragment) {
                                        intent.setData(Uri.parse("package:" + context.getPackageName()));
                                        ((Fragment) mWeakRef.get()).startActivityForResult(intent, BaseConstant.Permission.REQUEST_CODE);
                                    }
                                    return;
                                }
                            }
                            mListener.doAfterDeniedEnsure(permissions, tag);
                        } else {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            // 根据包名打开对应的设置界面
                            if (isWeakNotNull() &&mWeakRef.get() instanceof Activity) {
                                intent.setData(Uri.parse("package:" + context.getPackageName()));
                                ((Activity) mWeakRef.get()).startActivityForResult(intent, BaseConstant.Permission.REQUEST_CODE);
                            } else if (isWeakNotNull() &&mWeakRef.get() instanceof Fragment) {
                                intent.setData(Uri.parse("package:" + context.getPackageName()));
                                ((Fragment) mWeakRef.get()).startActivityForResult(intent, BaseConstant.Permission.REQUEST_CODE);
                            }
                        }
                    }, v -> {
                        destroy();
                        mListener.doAfterDeniedCancel(permissions, tag);
                    });
                }
        }
    }

    private boolean isWeakNotNull() {
        return mWeakRef != null && mWeakRef.get() != null;
    }

    /**
     * 判断小米MIUI系统中授权管理中对应的权限授取
     *
     * @return false 存在核心的未收取的权限   true 核心权限已经全部授权
     */
    private boolean initMUIPermission(String permission) {
        AppOpsManager appOpsManager = null;
        String MUIPermission = "";
        //解析权限对比
        //获取权限的最后一个点的数据
        //特殊权限判断
        if (permission != null) {
            if (permission.contains("ACCESS_COARSE_LOCATION")) {
                MUIPermission = AppOpsManager.OPSTR_COARSE_LOCATION;
            } else if (permission.contains("ACCESS_FINE_LOCATION")) {
                MUIPermission = AppOpsManager.OPSTR_FINE_LOCATION;
            } else {
                String substring = permission.substring(permission.lastIndexOf(".") + 1).toLowerCase();
                MUIPermission = "android:" + substring;
            }
        }


        int cameraOp = -1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !"".equals(MUIPermission)) {
            if (isWeakNotNull() &&mWeakRef.get() instanceof Activity) {
                appOpsManager = (AppOpsManager) ((Activity) mWeakRef.get()).getSystemService(Context.APP_OPS_SERVICE);
                assert appOpsManager != null;
                cameraOp = appOpsManager.checkOp(MUIPermission, Binder.getCallingUid(), ((Activity) mWeakRef.get()).getPackageName());
            } else if (isWeakNotNull() &&mWeakRef.get() instanceof Fragment) {
                FragmentActivity activity = ((Fragment) mWeakRef.get()).getActivity();
                if (activity != null)
                    appOpsManager = (AppOpsManager) activity.getSystemService(Context.APP_OPS_SERVICE);
                assert appOpsManager != null;
                cameraOp = appOpsManager.checkOp(MUIPermission, Binder.getCallingUid(), activity.getPackageName());
            }
            if (cameraOp != -1) {
                if (cameraOp == AppOpsManager.MODE_IGNORED) {
                    return false;
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 检查手机是否是miui系统
     */
    public boolean isMIUI() {
        String device = Build.MANUFACTURER;
        if (device.equals("Xiaomi")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断是否具有某权限
     */

    public String[] hasPermissions(@NonNull Activity context, String[] perms) {
        ArrayList<String> permissionList = new ArrayList<>();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return permissionList.toArray(new String[permissionList.size()]);
        }
        for (String perm : perms) {
            //判断是activity还是fragment
            boolean hasPerm = context.checkSelfPermission(perm) == PackageManager.PERMISSION_GRANTED;
            if (!hasPerm) {
                permissionList.add(perm);
            }
        }
        return permissionList.toArray(new String[permissionList.size()]);
    }


    /**
     * 执行申请,兼容fragment
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void executePermissionsRequest(@NonNull String[] perms) {
        if (isWeakNotNull() &&mWeakRef.get() instanceof Activity) {
            ((Activity) mWeakRef.get()).requestPermissions(perms, PermissionManager.REQUEST_PERMISSION_CODE);
        } else if (isWeakNotNull() &&mWeakRef.get() instanceof Fragment) {
            ((Fragment) mWeakRef.get()).requestPermissions(perms, PermissionManager.REQUEST_PERMISSION_CODE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean shouldShowRequestPermissionRationale(String[] perms) {
        if (isWeakNotNull() &&mWeakRef.get() instanceof Activity) {
            for (int i = 0; i < perms.length; i++) {
                if (!((Activity) mWeakRef.get()).shouldShowRequestPermissionRationale(perms[i])) {
                    return false;
                }
            }
            return true;
        } else if (isWeakNotNull() &&mWeakRef.get() instanceof Fragment) {
            FragmentActivity activity = ((Fragment) mWeakRef.get()).getActivity();
            if (activity != null) {
                for (int i = 0; i < perms.length; i++) {
                    if (!((Activity) mWeakRef.get()).shouldShowRequestPermissionRationale(perms[i])) {
                        return false;
                    }
                }
                return true;
            } else {
                return true;
            }
        }
        return false;
    }

    public void showMessageOKCancel(Context context, String message, View.OnClickListener okListener, View.OnClickListener cancelListener) {
        if (mCustomProgress == null) {
            mCustomProgress = new CustomProgress(context);
        }
        mCustomProgress.show(message, "", cancelListener, okListener, false, null);
    }

    public boolean isShowing() {
        return mCustomProgress != null && mCustomProgress.isShowing();
    }

    public void destroy() {
        if (mCustomProgress != null) {
            mCustomProgress.dismiss();
        }
    }


    public interface PermissionListener {

        void doAfterGrand(String[] permission, String tag);

        void doAfterDeniedCancel(String[] permission, String tag);

        void doAfterDeniedEnsure(String[] permission, String tag);
    }

    public static abstract class SimplePermissionListener implements PermissionListener {
        @Override
        public void doAfterGrand(String[] permission, String tag) {

        }

        @Override
        public void doAfterDeniedCancel(String[] permission, String tag) {

        }

        @Override
        public void doAfterDeniedEnsure(String[] permission, String tag) {

        }
    }

}
