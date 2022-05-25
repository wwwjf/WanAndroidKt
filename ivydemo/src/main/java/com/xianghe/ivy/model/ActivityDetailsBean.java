package com.xianghe.ivy.model;

import java.io.Serializable;
import java.util.List;

/**
 * @创建者 Allen
 * @创建时间 2019/3/28 14:41
 * @描述      活动内容详情bean
 */
public class ActivityDetailsBean  implements Serializable {


    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * id : 14
         * content : null
         * start_time : 2019-01-15 10:00:00
         * end_time : 2019-05-18 10:00:00
         * activity_type : 2
         * created_at : 2019-03-04 14:32:11
         * updated_at : 2019-03-28 15:22:40
         * activity_photos : http://ivytest.i-weiying.com/c4ca/201903/201903288b89158676efe0b5dfb27eb6f875d4f4?auth_key=1553769105-0-0-f07a3860f74bbf43b3c5c5d941d47eb2
         */

        private int id;
        private Object content;
        private String start_time;
        private String end_time;
        private String activity_type;
        private String created_at;
        private String updated_at;
        private String activity_photos;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Object getContent() {
            return content;
        }

        public void setContent(Object content) {
            this.content = content;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public String getActivity_type() {
            return activity_type;
        }

        public void setActivity_type(String activity_type) {
            this.activity_type = activity_type;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public String getActivity_photos() {
            return activity_photos;
        }

        public void setActivity_photos(String activity_photos) {
            this.activity_photos = activity_photos;
        }
    }
}
