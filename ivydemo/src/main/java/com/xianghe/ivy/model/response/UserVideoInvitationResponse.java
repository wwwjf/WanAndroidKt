package com.xianghe.ivy.model.response;

import java.io.Serializable;
import java.util.List;

public class UserVideoInvitationResponse implements Serializable {

    private static final long serialVersionUID = 7198344038306681685L;
    /**
     * total : 3
     * total_page : 1
     * list : [{"id":3057,"uid":8273309,"recipient_uid":8272980,"created_at":1559318400,"is_callback":1,"avater":"","name":"用户7859915175288","total":3}]
     */

    private int total;
    private int total_page;
    private List<ListBean> list;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotal_page() {
        return total_page;
    }

    public void setTotal_page(int total_page) {
        this.total_page = total_page;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * id : 3057
         * uid : 8273309
         * recipient_uid : 8272980
         * created_at : 1559318400
         * is_callback : 1
         * avater :
         * name : 用户7859915175288
         * total : 3
         */

        private int id;
        private int uid;
        private int recipient_uid;
        private long created_at;
        private int is_callback;
        private String avater;
        private String name;
        private int total;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public int getRecipient_uid() {
            return recipient_uid;
        }

        public void setRecipient_uid(int recipient_uid) {
            this.recipient_uid = recipient_uid;
        }

        public long getCreated_at() {
            return created_at;
        }

        public void setCreated_at(long created_at) {
            this.created_at = created_at;
        }

        public int getIs_callback() {
            return is_callback;
        }

        public void setIs_callback(int is_callback) {
            this.is_callback = is_callback;
        }

        public String getAvater() {
            return avater;
        }

        public void setAvater(String avater) {
            this.avater = avater;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }
}
