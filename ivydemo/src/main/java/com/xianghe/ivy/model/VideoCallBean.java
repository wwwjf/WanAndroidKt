package com.xianghe.ivy.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.xianghe.ivy.network.GsonHelper;

/**
 * author:  ycl
 * date:  2019/2/21 11:39
 * desc:
 */
public class VideoCallBean implements Parcelable {
    private String accept_username; // 此 username 是接收方的 userName ，环信的账号
    private boolean isOnline; // 判断接收方是否在线
    private String roomID;
    private String requestType; // 1 请求  2 拒绝  3 接受  4 挂断（分2端） 5 录制视频 6 终止视频录制


    // ---------- 发送方信息 ----------
    private long uid;
    private String username;
    private String avatar;
    private String birthday;
    private String signature;
    @Expose(serialize = false, deserialize = false)
    private transient long dialTime;// 拨打时间  需要发送的时候，单独设定8s
    private int sex; // 1 男
    // ---------- 发送方信息 ----------


    public VideoCallBean(String accept_username, boolean isOnline, String roomID, String requestType, long uid, String username, String avatar, String birthday, String signature, int sex) {
        this.accept_username = accept_username;
        this.isOnline = isOnline;
        this.roomID = roomID;
        this.requestType = requestType;
        this.uid = uid;
        this.username = username;
        this.avatar = avatar;
        this.birthday = birthday;
        this.signature = signature;
        this.sex = sex;
    }


    public String getAccept_username() {
        return accept_username;
    }

    public void setAccept_username(String accept_username) {
        this.accept_username = accept_username;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public long getDialTime() {
        return dialTime;
    }

    public void setDialTime(long dialTime) {
        this.dialTime = dialTime;
    }

    public String toJson() {
        return GsonHelper.getsGson().toJson(this);
    }

    public VideoCallBean fromJson(String json) {
        return GsonHelper.getsGson().fromJson(json, VideoCallBean.class);
    }

    @Override
    public String toString() {
        return "VideoCallBean{" +
                "accept_username='" + accept_username + '\'' +
                ", isOnline=" + isOnline +
                ", roomID='" + roomID + '\'' +
                ", requestType='" + requestType + '\'' +
                ", uid='" + uid + '\'' +
                ", username='" + username + '\'' +
                ", avatar='" + avatar + '\'' +
                ", birthday='" + birthday + '\'' +
                ", signature='" + signature + '\'' +
                ", dialTime=" + dialTime +
                ", sex=" + sex +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.accept_username);
        dest.writeByte(this.isOnline ? (byte) 1 : (byte) 0);
        dest.writeString(this.roomID);
        dest.writeString(this.requestType);
        dest.writeLong(this.uid);
        dest.writeString(this.username);
        dest.writeString(this.avatar);
        dest.writeString(this.birthday);
        dest.writeString(this.signature);
        dest.writeInt(this.sex);
    }

    protected VideoCallBean(Parcel in) {
        this.accept_username = in.readString();
        this.isOnline = in.readByte() != 0;
        this.roomID = in.readString();
        this.requestType = in.readString();
        this.uid = in.readLong();
        this.username = in.readString();
        this.avatar = in.readString();
        this.birthday = in.readString();
        this.signature = in.readString();
        this.sex = in.readInt();
    }

    public static final Creator<VideoCallBean> CREATOR = new Creator<VideoCallBean>() {
        @Override
        public VideoCallBean createFromParcel(Parcel source) {
            return new VideoCallBean(source);
        }

        @Override
        public VideoCallBean[] newArray(int size) {
            return new VideoCallBean[size];
        }
    };
}
