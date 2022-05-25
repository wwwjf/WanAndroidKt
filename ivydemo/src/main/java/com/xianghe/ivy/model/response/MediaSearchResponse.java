package com.xianghe.ivy.model.response;

import com.xianghe.ivy.model.ListMediaBean;

import java.io.Serializable;
import java.util.List;

public class MediaSearchResponse implements Serializable {
        private static final long serialVersionUID = 299020721304483036L;
        /**
         * next_page : 0
         * list : [{"title":"实在","description":"","avatar":"http://ivy-test.oss-cn-shenzhen.aliyuncs.com/74ba/avatar/20181206/201812063a15bb1cb0b013ce37b63a2c574142311544059600902.png?security-token=CAISlQJ1q6Ft5B2yfSjIr4j5O4%2FHpoxK%2Ba3SbUWIrG0ZXNZ1t7fRgDz2IHBNdHduAuwWtf80mW1X6foYlqJ4T55IQ1Dza8J148zYNPBw8s6T1fau5Jko1beXewHKeSOZsebWZ%2BLmNqS%2FHt6md1HDkAJq3LL%2Bbk%2FMdle5MJqP%2B%2FEFA9MMRVv6F3kkYu1bPQx%2FssQXGGLMPPK2SH7Qj3HXEVBjt3gb6wZ24r%2FtxdaHuFiMzg%2B46JdM%2BN%2BgesD7P5E3bMsuCofk5oEsKPqdihw3wgNR6aJ7gJZD%2FTr6pdyHCzFTmU7WarqKrIYzfFQiPfVnRPEY9uKPnPl5q%2FHVkJ%2Fs1xFOMOdaXiLSXom8x9HeH%2BekJluivi134JemGoABelvQgc9vSge8Ug2GGMekp%2BcAzACKWhpLIZngQLpra9Ro%2Fh7osPwxOIo0uVCzbIhLuwonjK%2BtriXjoDwO4cgQJLY8tUkcTYNX6qWsxjuoHulSc8M4ihY3w5kwUiyKQWDZhJGNqpySnWj9waiyg1WVeT464HcGAsAD%2FgOc4idcsGc%3D&OSSAccessKeyId=STS.NKLp5sKSkNo9oc9HmLPYYXrzb&Expires=1544429164&Signature=bJ%2Fl6KeGouqkGWTC%2BHyOGziclLY%3D","cover":"http://ivy-test.oss-cn-shenzhen.aliyuncs.com/cover/bdc7/20181116/20181116afac154e4fdc9a3501743aecef9dea191542354625151.png?security-token=CAISlQJ1q6Ft5B2yfSjIr4j5O4%2FHpoxK%2Ba3SbUWIrG0ZXNZ1t7fRgDz2IHBNdHduAuwWtf80mW1X6foYlqJ4T55IQ1Dza8J148zYNPBw8s6T1fau5Jko1beXewHKeSOZsebWZ%2BLmNqS%2FHt6md1HDkAJq3LL%2Bbk%2FMdle5MJqP%2B%2FEFA9MMRVv6F3kkYu1bPQx%2FssQXGGLMPPK2SH7Qj3HXEVBjt3gb6wZ24r%2FtxdaHuFiMzg%2B46JdM%2BN%2BgesD7P5E3bMsuCofk5oEsKPqdihw3wgNR6aJ7gJZD%2FTr6pdyHCzFTmU7WarqKrIYzfFQiPfVnRPEY9uKPnPl5q%2FHVkJ%2Fs1xFOMOdaXiLSXom8x9HeH%2BekJluivi134JemGoABelvQgc9vSge8Ug2GGMekp%2BcAzACKWhpLIZngQLpra9Ro%2Fh7osPwxOIo0uVCzbIhLuwonjK%2BtriXjoDwO4cgQJLY8tUkcTYNX6qWsxjuoHulSc8M4ihY3w5kwUiyKQWDZhJGNqpySnWj9waiyg1WVeT464HcGAsAD%2FgOc4idcsGc%3D&OSSAccessKeyId=STS.NKLp5sKSkNo9oc9HmLPYYXrzb&Expires=1544429164&Signature=9TanHR%2FL%2B7Kg2X%2FHuDAPZicH%2FEo%3D"}]
         */

        private int next_page;
        private List<ListMediaBean> list;

        public int getNext_page() {
            return next_page;
        }

        public void setNext_page(int next_page) {
            this.next_page = next_page;
        }

        public List<ListMediaBean> getList() {
            return list;
        }

        public void setList(List<ListMediaBean> list) {
            this.list = list;
        }


    }