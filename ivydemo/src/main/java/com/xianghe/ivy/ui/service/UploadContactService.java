package com.xianghe.ivy.ui.service;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.luckongo.tthd.net.mapper.RespMapper;
import com.xianghe.ivy.app.IvyApp;
import com.xianghe.ivy.constant.Api;
import com.xianghe.ivy.http.request.NetworkRequest;
import com.xianghe.ivy.http.scheduler.SchedulerProvider;
import com.xianghe.ivy.model.FriendBean;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.utils.KLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 上传通讯录联系人
 */
public class UploadContactService extends IntentService {
    private static final String TAG = UploadContactService.class.getSimpleName();

    private static final String ACTION_FOO = "com.xianghe.ivy.ui.service.action.FOO";
    private static final String ACTION_BAZ = "com.xianghe.ivy.ui.service.action.BAZ";

    private static final String EXTRA_PARAM1 = "com.xianghe.ivy.ui.service.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.xianghe.ivy.ui.service.extra.PARAM2";

    public UploadContactService() {
        super("UploadContactService");
    }

    /**
     * @see IntentService
     */
    public static void startActionFoo(Context context, String param1, String param2) {
        KLog.e(TAG, "========startActionFoo");
        Intent intent = new Intent(context, UploadContactService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * @see IntentService
     */
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, UploadContactService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }
    }


    @SuppressLint("CheckResult")
    private void handleActionFoo(String param1, String param2) {
        KLog.e(TAG, "========handleActionFoo");
        //检测已绑定通讯录联系人信息
        NetworkRequest.INSTANCE.postMapByJAVA(Api.Route.Login.ADDRESSBOOK_CHECK,
                new HashMap<>(), new RespMapper(),
                new TypeToken<BaseResponse<List<FriendBean>>>() {
                }.getType()
        ).observeOn(SchedulerProvider.INSTANCE.ui())
                .subscribe(resp -> {
                    BaseResponse<List<FriendBean>> response = (BaseResponse<List<FriendBean>>) resp;
                    KLog.e(TAG, "========handleActionFoo resp " + response.getInfo());
                    List<FriendBean> bindList = new ArrayList<>();
                    if (response != null && response.getData() != null && response.getData().size() > 0) {//数据为空，未绑定通讯录
                        bindList.addAll(response.getData());
                    }
                    List<String> mobileList = bindContacts(bindList);
                    if (mobileList.size()==0) {
                        KLog.e(TAG, "=====没有本地联系人需要上传");
                        return;
                    }
                    //上传新增加的通讯录联系人
                    for (String mobile : mobileList) {

                    }
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("mobile", mobileList.get(0));
                    NetworkRequest.INSTANCE.postMapByJAVA(Api.Route.Login.ADDRESS_BINDING,
                            hashMap,
                            new RespMapper(),
                            new TypeToken<BaseResponse<String>>() {
                            }.getType())
                            .observeOn(SchedulerProvider.INSTANCE.ui())
                            .subscribe(res -> KLog.e(TAG, "========handleActionFoo res" + ((BaseResponse<String>) res).getInfo()),
                                    throwable -> {
                                        //上传失败，5分钟后，重新上传通讯录
                                        new Handler().postDelayed(() -> handleActionFoo("",""),5*60*1000);
                                        Log.e(TAG, "ADDRESS_BINDING-handleActionFoo: " + throwable.getMessage());
                                    });


                }, throwable -> {
                    //查询失败，5分钟后重新查询通讯录绑定信息
                    new Handler().postDelayed(() ->handleActionFoo("",""),5*60*1000);
                    Log.e(TAG, "ADDRESSBOOK_CHECK-handleActionFoo: " + throwable.getMessage());
                });

    }


    private void handleActionBaz(String param1, String param2) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    /**
     * 绑定通讯录
     *
     * @param friendBeanList 已绑定的用户信息
     */
    private List<String> bindContacts(List<FriendBean> friendBeanList) {
        StringBuilder uploadMobiles = new StringBuilder();
        List<String> mobileList = new ArrayList<>();
        String mobiles;
        ArrayList<FriendBean> contactList = IvyApp.getInstance().Contactslist;
        if (contactList.size() == 0) {
            //本地没有联系人
            return new ArrayList<>();
        }
        //找出未绑定的新手机号
        List<String> uploadContactList = new ArrayList<>();

        if (friendBeanList.size() == 0) {//服务端没有绑定记录,把整个本地通讯录添加上传
            for (FriendBean friendBean : contactList) {
                uploadContactList.add(friendBean.getMobile());
            }
        } else {
            for (FriendBean contactBean : contactList) {//本地通讯录联系人
                for (int i = 0; i < friendBeanList.size(); i++) {//已绑定通讯录联系人
                    FriendBean friendBean = friendBeanList.get(i);
                    if (friendBean.getMobile().equals(contactBean.getMobile())) {
                        break;
                    }
                    if (i == friendBeanList.size() - 1) {//本地联系人与已绑定联系人列表比较到最后一个
                        uploadContactList.add(contactBean.getMobile());
                    }
                }
            }
        }
        for (int i = 0; i < uploadContactList.size(); i++) {
            uploadMobiles.append(uploadContactList.get(i)).append(",");
            if ((i + 1) % 100 == 0) {//100的整数倍 一次最多100个记录，所以超过100个需要分组
                mobiles = uploadMobiles.substring(0,uploadMobiles.length()-1);
                mobileList.add(mobiles);
                uploadMobiles.delete(0,uploadMobiles.length());
            }
        }
        if (uploadMobiles.length() > 1) {
            mobiles = uploadMobiles.substring(0, uploadMobiles.length() - 1);
            mobileList.add(mobiles);
        }
        return mobileList;
    }
}
