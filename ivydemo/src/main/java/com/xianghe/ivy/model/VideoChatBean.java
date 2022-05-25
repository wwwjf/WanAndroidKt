package com.xianghe.ivy.model;

import android.content.Context;
import android.text.TextUtils;

import com.xianghe.ivy.R;

/**
 * author:  ycl
 * date:  2019/3/5 14:22
 * desc:
 */
public class VideoChatBean {
    private String room_id;
    private String recipient_username;
    private int sex; //性别0：未选择 1：男 2：女
    private String avatar;
    private int is_online; // 0：不在线;1:在线

    public VideoChatBean(String room_id, String recipient_username, int sex, String avatar, int is_online) {
        this.room_id = room_id;
        this.recipient_username = recipient_username;
        this.sex = sex;
        this.avatar = avatar;
        this.is_online = is_online;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public String getRecipient_username() {
        return recipient_username;
    }

    public void setRecipient_username(String recipient_username) {
        this.recipient_username = recipient_username;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getIs_online() {
        return is_online;
    }

    public void setIs_online(int is_online) {
        this.is_online = is_online;
    }

    public static String errorMessage(Context context, int status,String message) {
        switch (status) {
            case 10253:
                return context.getString(R.string.video_chat_invitation_error);
            case 10254:
                return context.getString(R.string.cant_video_chat_with_myself);
            case 10255:
                return context.getString(R.string.cant_start_video_chat_yet);
            case 10256:
                return context.getString(R.string.The_other_party_is_busy);
            case 10257:
                return context.getString(R.string.connection_error);
            case 10258:
                return context.getString(R.string.the_other_party_is_offline);
            default:
                if (message != null || !TextUtils.isEmpty(message)) {
                    return message;
                } else {
                    return context.getString(R.string.call_failed);
                }
        }
    }
}
