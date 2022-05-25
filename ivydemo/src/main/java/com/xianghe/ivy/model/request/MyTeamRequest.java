package com.xianghe.ivy.model.request;

public class MyTeamRequest {

    //    参数名	必选	类型	说明
//    userId	是	string	用户ID
//    userType	是	string	区分：1:我的收益，2: 运营中心收益
//    page	是	Int	页码
//    pageSize	是	Int	每页最大数量
//    search	否	String	搜索的用户ID或手机号 （如果没有用过搜索功能，不用传search、begin、end）
//    begin	否	String	开始时间：年-月-日
//    end	否	String	结束时间：年-月-日
    public String userId;
    public String userType;
    public int page=1;
    public int pageSize = 20;
    public String search;
    public String begin;
    public String end;
}
