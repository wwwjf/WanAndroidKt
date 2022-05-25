package com.xianghe.ivy.model;

import java.io.Serializable;

/**
 * 会员推荐人信息
 */
public class Referrer implements Serializable {

    private static final long serialVersionUID = 3124032490714707266L;
    /**
     * uid : 8273532
     * parent_id : 8273700
     * parent_invitation_code : 880078
     * ascription : null
     * locality_name : null
     * is_shareholder:0
     * is_modify:0
     */

    private int uid;
    private int parent_id;
    private String parent_invitation_code;
    /**
     * 运营中心ID
     */
    private String ascription;
    /**
     * 运营中心名称
     */
    private String locality_name;
    /**
     * 股东身份(0:不是股东;1:是股东)
     */
    private int is_shareholder;
    /**
     * 是否可以修改(0:不可以;1:可以)
     */
    private int is_modify;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getParent_id() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
    }

    public String getParent_invitation_code() {
        return parent_invitation_code;
    }

    public void setParent_invitation_code(String parent_invitation_code) {
        this.parent_invitation_code = parent_invitation_code;
    }

    public String getAscription() {
        return ascription;
    }

    public void setAscription(String ascription) {
        this.ascription = ascription;
    }

    public String getLocality_name() {
        return locality_name;
    }

    public void setLocality_name(String locality_name) {
        this.locality_name = locality_name;
    }

    public int getIs_shareholder() {
        return is_shareholder;
    }

    public void setIs_shareholder(int is_shareholder) {
        this.is_shareholder = is_shareholder;
    }

    public int getIs_modify() {
        return is_modify;
    }

    public void setIs_modify(int is_modify) {
        this.is_modify = is_modify;
    }
}
