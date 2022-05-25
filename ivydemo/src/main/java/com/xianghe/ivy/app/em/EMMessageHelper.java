package com.xianghe.ivy.app.em;

import com.xianghe.ivy.utils.KLog;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * author:  ycl
 * date:  2019/2/25 10:39
 * desc:
 */
public class EMMessageHelper {
    private static final String TAG = "EMMessageHelper";
    private Set<String> atMeGroupList = null;
    private static EMMessageHelper instance = null;

    // 控制显示视频拨打历史
    public static boolean isShowVideoCallHistory = false;

    // 1 请求  2 拒绝  3 接受 4 挂断 5 录制视频 6 终止视频录制（isMe 只是在终止视频的时候使用） 7 对方正在忙
    public static final String REQUEST = "1";
    public static final String REFUSE = "2";
    public static final String ACCEPT = "3";
    public static final String CLOSE = "4";
    public static final String RECORD_START = "5";
    public static final String RECORD_END = "6";
    public static final String REFUSE_BUSY = "7";

    public static EMMessageHelper getInstance() {
        if (instance == null) {
            synchronized (EMMessageHelper.class) {
                if (instance == null) {
                    instance = new EMMessageHelper();
                }
            }
        }
        return instance;
    }

    public EMMessageHelper() {
        atMeGroupList = EMPreferenceManager.getInstance().getAtMeGroups();
        if (atMeGroupList == null) {
            atMeGroupList = new HashSet<String>();
        }
        KLog.d(TAG, "EMMessageHelper: init: atMeGroupList.size() " + atMeGroupList.size());
    }

    public Set<String> getAtMeGroups() {
        return atMeGroupList;
    }

    public boolean hasAtMeGroups() {
        return atMeGroupList != null && !atMeGroupList.isEmpty();
    }

    public void removeAtMeGroup(String groupId) {
        KLog.d(TAG, "removeAtMeGroup: " + groupId);
        if (atMeGroupList.contains(groupId)) {
            atMeGroupList.remove(groupId);
            EMPreferenceManager.getInstance().setAtMeGroups(atMeGroupList);
        }
    }

    // 可能清除的时候，还在添加信息，此处存在遗失 bug ，暂不处理，清除应该清除上一次状态的
    public synchronized void clearAtMeGroup() {
        if (atMeGroupList != null) {
            KLog.d(TAG, "clearAtMeGroup: ");
            atMeGroupList.clear();
            EMPreferenceManager.getInstance().clearAtMeGroups();
        }
    }

    public synchronized void addAtMeGroup(String str) {
        KLog.d(TAG, "addAtMeGroup: " + str);
        if (!atMeGroupList.contains(str)) {
            atMeGroupList.add(str);
        }
    }

    public synchronized boolean hasAtMeMsg(String groupId) {
        return atMeGroupList.contains(groupId);
    }


    /**
     * 具体换信接收到消息的处理类，一般接收到的消息，如果没有在界面看到就需要存储在本地文件中，待下次进入界面，直接查看消息
     */
    public synchronized void parseMessages(List<Object> messages) {
        if (messages == null || messages.isEmpty()) {
            return;
        }
        int size = atMeGroupList.size();
        KLog.d(TAG, "messages.size " + messages.size() + "  atMeGroupList.size: " + size);

        //只要有改变就存储最新的状态
        if (atMeGroupList.size() != size) {
            KLog.d(TAG, "setAtMeGroups");
            EMPreferenceManager.getInstance().setAtMeGroups(atMeGroupList);
            // 有拨打信息就设置true
            EMMessageHelper.isShowVideoCallHistory = true;
        }
    }
}
