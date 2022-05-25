package com.xianghe.ivy.model;

import java.io.Serializable;

/**
 * 成为会员实体类
 */
public class MemberFeeBean implements Serializable {
    private static final long serialVersionUID = 1125081389307868435L;

    /**
     * id : 1
     * name : 会员费用
     * member_fee : 598.00
     */

    private int id;
    private String name;
    private String member_fee;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMember_fee() {
        return member_fee;
    }

    public void setMember_fee(String member_fee) {
        this.member_fee = member_fee;
    }
}
