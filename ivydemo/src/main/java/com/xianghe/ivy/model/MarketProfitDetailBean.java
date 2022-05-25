package com.xianghe.ivy.model;

import java.io.Serializable;

public class MarketProfitDetailBean implements Serializable {
//    参数名	类型	说明
//    title	string	收益途径
//    avatar	string	图标
//    creatTime	string	提现时间
//    earningType	string	收益类型
//    price	string	提现金额
//    status	string	审核状态
//    statusDateil	string	审核详情
//    account	string	账户余额
//    message	string	提示信息

//    "num": "-1.00",
//            "remark": "提现扣除", 收益途径
//            "creatTime": "2019-04-29 20:42:54"
//            "credit_type":"1"//来源,1支付宝2微信3分润4其他
//            "jieyu":0//账户结余
//            "status":0//普通状态。1处理中2处理失败，退还3完成
//            "icon":""//图片地址

    public String remark;
    public String icon;
    public String creatTime;
    public String num;
    public String jieyu;
    public int status;
    public int credit_type;
}
