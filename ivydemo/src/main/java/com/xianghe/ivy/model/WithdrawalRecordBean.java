package com.xianghe.ivy.model;

import java.io.Serializable;

/**
 * @创建者 Allen
 * @创建时间 2019/4/23 14:15
 * @描述      提现记录结果bean
 */
public class WithdrawalRecordBean implements Serializable {
        /**
         * title : 提现到支付宝
         * typeUrl : http://www.baidu.com
         * createTime : 2019-04-28 11:37:05     提现时间
         * price : 2495.00
         * status : 	提现状态   0 申请中，1 成功，-1 失败
         * statusDetail : 	状态描述
         */

        private String title;
        private String typeUrl;
        private String createTime;
        private String price;
        private int status;
        private String statusDetail;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTypeUrl() {
            return typeUrl;
        }

        public void setTypeUrl(String typeUrl) {
            this.typeUrl = typeUrl;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getStatusDetail() {
            return statusDetail;
        }

        public void setStatusDetail(String statusDetail) {
            this.statusDetail = statusDetail;
        }
}
