package com.xianghe.ivy.model;

import com.xianghe.ivy.weight.indexbar.bean.BaseIndexPinyinBean;

import java.io.Serializable;

/**
 * 联系人,实现侧边栏字母继承BaseIndexPinyinBean类
 */
public class ContactBean extends BaseIndexPinyinBean implements Serializable {
    private static final long serialVersionUID = -4293440621166133581L;

    private long contactId;
    private String contactName;
    private String contactPhone;
    private int status;
    /**
     * 同一个号码一天内只能邀请一次
     * 是否允许再次邀请：0=不允许  1=允许再次邀请
     */
    private int invite_allow;

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String getTarget() {
        return contactName;
    }

    public int getInvite_allow() {
        return invite_allow;
    }

    public void setInvite_allow(int invite_allow) {
        this.invite_allow = invite_allow;
    }
}
