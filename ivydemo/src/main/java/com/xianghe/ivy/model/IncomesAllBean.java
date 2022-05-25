package com.xianghe.ivy.model;

import java.io.Serializable;

/**
 * 我的收益和运营中心收益金额
 */
public class IncomesAllBean implements Serializable {
    private static final long serialVersionUID = 6342686506269245029L;


    /**
     * earnings : xxx
     * operatingEarnings : xxx
     */

    /**
     * 我的收益
     */
    private String earnings;

    /**
     * 运营中心收益
     */
    private String operatingEarnings;

    public String getEarnings() {
        return earnings;
    }

    public void setEarnings(String earnings) {
        this.earnings = earnings;
    }

    public String getOperatingEarnings() {
        return operatingEarnings;
    }

    public void setOperatingEarnings(String operatingEarnings) {
        this.operatingEarnings = operatingEarnings;
    }

    @Override
    public String toString() {
        return "IncomesAllBean{" +
                "earnings='" + earnings + '\'' +
                ", operatingEarnings='" + operatingEarnings + '\'' +
                '}';
    }
}
