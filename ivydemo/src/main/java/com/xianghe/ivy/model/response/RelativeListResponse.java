package com.xianghe.ivy.model.response;

import com.xianghe.ivy.model.FriendBean;

import java.io.Serializable;
import java.util.List;

/**
 * 亲友圈列表分页数据
 * @author Administrator
 */
public class RelativeListResponse implements Serializable {

    /**
     * totalPage : 1
     * list : []
     */

    private int totalPage;
    private List<FriendBean> list;

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public List<FriendBean> getList() {
        return list;
    }

    public void setList(List<FriendBean> list) {
        this.list = list;
    }
}
