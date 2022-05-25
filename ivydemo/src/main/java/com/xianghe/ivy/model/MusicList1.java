package com.xianghe.ivy.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MusicList1 {
    /**
     * total : 8
     * totalPage : 1
     * list : [{"id":18,"music_name":"serverSound","music_like":"http://ivytest.i-weiying.com/c4ca/201810/20181011fd7c6324f69b23c3ba274982323d405d?auth_key=1552643573-0-0-aafcb35f11810f8d934770bdff3be73e","template_id":"18","time_length":"05:02"},{"id":17,"music_name":"风筝误 - 刘珂矣","music_like":"http://ivytest.i-weiying.com/music/%E9%A3%8E%E7%AD%9D%E8%AF%AF%20-%20%E5%88%98%E7%8F%82%E7%9F%A3.mp3?auth_key=1552643573-0-0-74c6f953009fbe965488d942d78c7895","template_id":"17","time_length":"03:58"},{"id":6,"music_name":"父亲 - 筷子兄弟","music_like":"http://ivytest.i-weiying.com/music/%E7%88%B6%E4%BA%B2%20-%20%E7%AD%B7%E5%AD%90%E5%85%84%E5%BC%9F.mp3?auth_key=1552643573-0-0-3321eb6616e6f1b334bf713218f3c151","template_id":"6","time_length":"02:50"},{"id":5,"music_name":"美丽的神话 - 孙楠,韩红","music_like":"http://ivytest.i-weiying.com/music/%E7%BE%8E%E4%B8%BD%E7%9A%84%E7%A5%9E%E8%AF%9D%20-%20%E5%AD%99%E6%A5%A0%2C%E9%9F%A9%E7%BA%A2.mp3?auth_key=1552643573-0-0-1e99a71cb18d9782e0c463960d855c57","template_id":"5","time_length":"03:06"},{"id":4,"music_name":"表白 - 萧亚轩","music_like":"http://ivytest.i-weiying.com/music/%E8%A1%A8%E7%99%BD%20-%20%E8%90%A7%E4%BA%9A%E8%BD%A9.mp3?auth_key=1552643573-0-0-758b5a91b11e3dd2651ca67b957c74d2","template_id":"4","time_length":"02:11"},{"id":3,"music_name":"违背的青春 - 薛之谦","music_like":"http://ivytest.i-weiying.com/music/%E8%BF%9D%E8%83%8C%E7%9A%84%E9%9D%92%E6%98%A5%20-%20%E8%96%9B%E4%B9%8B%E8%B0%A6.mp3?auth_key=1552643573-0-0-5ec832c1c32fad1d270ef470d17d9751","template_id":"3","time_length":"03:00"},{"id":2,"music_name":"青春 (Live) - 毛不易","music_like":"http://ivytest.i-weiying.com/music/%E9%9D%92%E6%98%A5%20%28Live%29%20-%20%E6%AF%9B%E4%B8%8D%E6%98%93.mp3?auth_key=1552643573-0-0-4e198a353a44e3fca4581c61f8463aff","template_id":"2","time_length":"02:33"},{"id":1,"music_name":"风筝误 - 刘珂矣","music_like":"http://ivytest.i-weiying.com/music/%E9%A3%8E%E7%AD%9D%E8%AF%AF%20-%20%E5%88%98%E7%8F%82%E7%9F%A3.mp3?auth_key=1552643573-0-0-74c6f953009fbe965488d942d78c7895","template_id":"1","time_length":"03:10"}]
     */

    @SerializedName("total")
    private int mTotal;
    @SerializedName("totalPage")
    private int mTotalPage;
    @SerializedName("list")
    private ArrayList<MusicBean> mList;

    public int getTotal() {
        return mTotal;
    }

    public void setTotal(int total) {
        mTotal = total;
    }

    public int getTotalPage() {
        return mTotalPage;
    }

    public void setTotalPage(int totalPage) {
        mTotalPage = totalPage;
    }

    public ArrayList<MusicBean> getList() {
        return mList;
    }

    public void setList(ArrayList<MusicBean> list) {
        mList = list;
    }
}