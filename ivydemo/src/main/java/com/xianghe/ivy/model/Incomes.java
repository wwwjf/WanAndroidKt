package com.xianghe.ivy.model;

import java.io.Serializable;

/**
 * 我的个人收益/运营中心收益
 */
public class Incomes implements Serializable {
    private static final long serialVersionUID = 6728451641073978567L;


    /**
     *  earnings  : xxx
     *  allEarnings : xxx
     *  yesterdayEarnings  : xxx
     *  todayAddMember  : xxx
     *  yesterdayAddMember  : xxx
     *  allMember  : xxx
     * message : 提示信息
     */

    /**
     * 可提现收益
     */
    private String earnings;
    /**
     * 累积收益
     */
    private String allEarnings;
    private String yesterdayEarnings;
    private String todayAddMember;
    private String yesterdayAddMember;
    private String allMember;
    private String message;

    public String getEarnings() {
        return earnings;
    }

    public void setEarnings(String earnings) {
        this.earnings = earnings;
    }

    public String getAllEarnings() {
        return allEarnings;
    }

    public void setAllEarnings(String allEarnings) {
        this.allEarnings = allEarnings;
    }

    public String getYesterdayEarnings() {
        return yesterdayEarnings;
    }

    public void setYesterdayEarnings(String yesterdayEarnings) {
        this.yesterdayEarnings = yesterdayEarnings;
    }

    public String getTodayAddMember() {
        return todayAddMember;
    }

    public void setTodayAddMember(String todayAddMember) {
        this.todayAddMember = todayAddMember;
    }

    public String getYesterdayAddMember() {
        return yesterdayAddMember;
    }

    public void setYesterdayAddMember(String yesterdayAddMember) {
        this.yesterdayAddMember = yesterdayAddMember;
    }

    public String getAllMember() {
        return allMember;
    }

    public void setAllMember(String allMember) {
        this.allMember = allMember;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
