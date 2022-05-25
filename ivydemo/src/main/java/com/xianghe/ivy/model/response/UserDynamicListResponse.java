package com.xianghe.ivy.model.response;

import com.xianghe.ivy.model.UserDynamicBean;

import java.io.Serializable;
import java.util.List;

public class UserDynamicListResponse implements Serializable {


    private static final long serialVersionUID = -7201761574656962960L;
    /**
     *
     * next_page : 0
     * list : [{"id":488,"type":1,"uid":8272574,"name":"13200550055","avatar":"http://ivy-test.oss-cn-shenzhen.aliyuncs.com/4389/201808/201808299a76efb03e94902e6de6e23e60790c62.png?","media_id":367,"content":"测试评论193","cover":"http://ivy-test.oss-cn-shenzhen.aliyuncs.com/4389/201810/2018101145ce55e40cdaf74735702414582e572b.png?","duration":7686,"created_at":"2018-11-05 15:27:00","descript":"评论了你的影片"},{"id":487,"type":1,"uid":8272574,"name":"13200550055","media_id":367,"content":"测试评论192","cover":"http://ivy-test.oss-cn-shenzhen.aliyuncs.com/4389/201810/2018101145ce55e40cdaf74735702414582e572b.png?","duration":7692,"created_at":"2018-11-05 15:27","descript":"评论了你的影片"}]
     */

    private int next_page;
    private List<UserDynamicBean> list;

    public int getNext_page() {
        return next_page;
    }

    public void setNext_page(int next_page) {
        this.next_page = next_page;
    }

    public List<UserDynamicBean> getList() {
        return list;
    }

    public void setList(List<UserDynamicBean> list) {
        this.list = list;
    }
}
