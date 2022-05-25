package com.xianghe.ivy.model;

import com.google.gson.annotations.SerializedName;

public class QrCodeInfo {
    /**
     * qr_code : null
     */

    @SerializedName("avatar")
    private String avatar;
    @SerializedName("qr_code")
    private String qrCode;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}