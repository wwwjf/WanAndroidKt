package com.xianghe.ivy.model.response;

import com.xianghe.ivy.model.ListUserBean;

import java.io.Serializable;
import java.util.List;

public class UserSearchResponse implements Serializable {
        private static final long serialVersionUID = 1813529208968247427L;
        /**
         * next_page : 0
         * list : [{"is_friend":0,"is_attention":0,"uid":"8272560","username":"ivy-2","avatar":"http://ivy-test.oss-cn-shenzhen.aliyuncs.com/avatar/386e/20181113/20181113b532d60333feec73b19a7d35586620ec154208955928.png?security-token=CAISlQJ1q6Ft5B2yfSjIr4j5O4%2FHpoxK%2Ba3SbUWIrG0ZXNZ1t7fRgDz2IHBNdHduAuwWtf80mW1X6foYlqJ4T55IQ1Dza8J148zYNPBw8s6T1fau5Jko1beXewHKeSOZsebWZ%2BLmNqS%2FHt6md1HDkAJq3LL%2Bbk%2FMdle5MJqP%2B%2FEFA9MMRVv6F3kkYu1bPQx%2FssQXGGLMPPK2SH7Qj3HXEVBjt3gb6wZ24r%2FtxdaHuFiMzg%2B46JdM%2BN%2BgesD7P5E3bMsuCofk5oEsKPqdihw3wgNR6aJ7gJZD%2FTr6pdyHCzFTmU7WarqKrIYzfFQiPfVnRPEY9uKPnPl5q%2FHVkJ%2Fs1xFOMOdaXiLSXom8x9HeH%2BekJluivi134JemGoABelvQgc9vSge8Ug2GGMekp%2BcAzACKWhpLIZngQLpra9Ro%2Fh7osPwxOIo0uVCzbIhLuwonjK%2BtriXjoDwO4cgQJLY8tUkcTYNX6qWsxjuoHulSc8M4ihY3w5kwUiyKQWDZhJGNqpySnWj9waiyg1WVeT464HcGAsAD%2FgOc4idcsGc%3D&OSSAccessKeyId=STS.NKLp5sKSkNo9oc9HmLPYYXrzb&Expires=1544429164&Signature=Xup0HLQP6eNXN9uhb19EVefPtXQ%3D"}]
         */

        private int next_page;
        private List<ListUserBean> list;

        public int getNext_page() {
            return next_page;
        }

        public void setNext_page(int next_page) {
            this.next_page = next_page;
        }

        public List<ListUserBean> getList() {
            return list;
        }

        public void setList(List<ListUserBean> list) {
            this.list = list;
        }


    }