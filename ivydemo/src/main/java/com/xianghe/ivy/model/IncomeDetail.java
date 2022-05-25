package com.xianghe.ivy.model;

import java.io.Serializable;

/**
 * 收益明细（个人收益明细、运营中心收益）
 */
public class IncomeDetail implements Serializable {
    private static final long serialVersionUID = 885980097150204080L;


    /**
     * num : -1.00
     * remark : 提现扣除
     * creatTime : 2019-04-29 20:42:54
     * credit_type : 1
     * jieyu : 0
     * status : 0
     * icon :
     */

    private String num;
    private String remark;
    private String creatTime;
    /**
     * 来源,1支付宝2微信3分润4其他
     */
    private String credit_type;
    /**
     * 账户结余
     */
    private String jieyu;
    /**
     * 0普通状态。1处理中2处理失败，退还3完成
     */
    private int status;
    /**
     * 图片地址
     */
    private String icon;

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(String creatTime) {
        this.creatTime = creatTime;
    }

    public String getCredit_type() {
        return credit_type;
    }

    public void setCredit_type(String credit_type) {
        this.credit_type = credit_type;
    }

    public String getJieyu() {
        return jieyu;
    }

    public void setJieyu(String jieyu) {
        this.jieyu = jieyu;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
