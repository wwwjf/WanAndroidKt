package com.xianghe.ivy.model;

public class BindTypeBean {
//     "uid": 8273029,        // 用户ID
//             "account_type": 1,    // 账号类型(1:支付宝账号;2:微信账号)
//             "account_token": "支付宝user_id",    // 支付账号唯一标识（微信：OpenId;支付宝：user_id;H5:为空）
//             "account": "",    // 账号(H5绑定支付宝不为空)
//             "real_name": "",    // 真实名称(H5绑定支付宝不为空)
//             "nick_name": "test nick name",    // 昵称（微信、支付宝绑定账号不为空）
//             "avatar": "",    // 头像
//             "sex": 1, // 性别(0:没有选择;1:男;2:女;)

    public long uid;
    public int account_type;
    public String account_token;
    public String account;
    public String real_name;
    public String nick_name;
    public String avatar;
    public String sex;
}
