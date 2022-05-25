package com.xianghe.ivy.model;

import java.io.Serializable;

/**
 * @创建者 Allen
 * @创建时间 2019/4/23 14:12
 * @描述      提现请求结果的bean
 */
public class WithdrawalBean implements Serializable {

    private String message;
    private String withdraw_min_money;
    private String team_max_num;

    public String getWithdraw_min_money() {
        return withdraw_min_money;
    }

    public void setWithdraw_min_money(String withdraw_min_money) {
        this.withdraw_min_money = withdraw_min_money;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTeam_max_num() {
        return team_max_num;
    }

    public void setTeam_max_num(String team_max_num) {
        this.team_max_num = team_max_num;
    }
}
