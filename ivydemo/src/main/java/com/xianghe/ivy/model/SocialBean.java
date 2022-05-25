package com.xianghe.ivy.model;

import java.io.Serializable;
import java.util.List;

public class SocialBean implements Serializable {
    private static final long serialVersionUID = -948955700054746826L;

    /**
     * total : 9
     * total_page : 5
     * list : [{"id":8272573,"avatar":null,"name":"13200880088","is_attention":0,"is_apply":0,"reg_type":1}]
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
         * id : 8272573
         * avatar : null
         * name : 13200880088
         * is_attention : 0
         * is_apply : 0
         * reg_type : 1
         */

        private int id;
        private String avatar;
        private String name;
        private int is_attention;
        private int is_apply;
        private int reg_type;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getIs_attention() {
            return is_attention;
        }

        public void setIs_attention(int is_attention) {
            this.is_attention = is_attention;
        }

        public int getIs_apply() {
            return is_apply;
        }

        public void setIs_apply(int is_apply) {
            this.is_apply = is_apply;
        }

        public int getReg_type() {
            return reg_type;
        }

        public void setReg_type(int reg_type) {
            this.reg_type = reg_type;
        }
    }
}
