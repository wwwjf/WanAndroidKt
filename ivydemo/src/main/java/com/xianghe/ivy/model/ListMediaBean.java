package com.xianghe.ivy.model;

import java.io.Serializable;

public class ListMediaBean implements Serializable {
    private static final long serialVersionUID = 4038937536353008080L;
    /**
     * title : 实在
     * description :
     * avatar : http://ivy-test.oss-cn-shenzhen.aliyuncs.com/74ba/avatar/20181206/201812063a15bb1cb0b013ce37b63a2c574142311544059600902.png?security-token=CAISlQJ1q6Ft5B2yfSjIr4j5O4%2FHpoxK%2Ba3SbUWIrG0ZXNZ1t7fRgDz2IHBNdHduAuwWtf80mW1X6foYlqJ4T55IQ1Dza8J148zYNPBw8s6T1fau5Jko1beXewHKeSOZsebWZ%2BLmNqS%2FHt6md1HDkAJq3LL%2Bbk%2FMdle5MJqP%2B%2FEFA9MMRVv6F3kkYu1bPQx%2FssQXGGLMPPK2SH7Qj3HXEVBjt3gb6wZ24r%2FtxdaHuFiMzg%2B46JdM%2BN%2BgesD7P5E3bMsuCofk5oEsKPqdihw3wgNR6aJ7gJZD%2FTr6pdyHCzFTmU7WarqKrIYzfFQiPfVnRPEY9uKPnPl5q%2FHVkJ%2Fs1xFOMOdaXiLSXom8x9HeH%2BekJluivi134JemGoABelvQgc9vSge8Ug2GGMekp%2BcAzACKWhpLIZngQLpra9Ro%2Fh7osPwxOIo0uVCzbIhLuwonjK%2BtriXjoDwO4cgQJLY8tUkcTYNX6qWsxjuoHulSc8M4ihY3w5kwUiyKQWDZhJGNqpySnWj9waiyg1WVeT464HcGAsAD%2FgOc4idcsGc%3D&OSSAccessKeyId=STS.NKLp5sKSkNo9oc9HmLPYYXrzb&Expires=1544429164&Signature=bJ%2Fl6KeGouqkGWTC%2BHyOGziclLY%3D
     * cover : http://ivy-test.oss-cn-shenzhen.aliyuncs.com/cover/bdc7/20181116/20181116afac154e4fdc9a3501743aecef9dea191542354625151.png?security-token=CAISlQJ1q6Ft5B2yfSjIr4j5O4%2FHpoxK%2Ba3SbUWIrG0ZXNZ1t7fRgDz2IHBNdHduAuwWtf80mW1X6foYlqJ4T55IQ1Dza8J148zYNPBw8s6T1fau5Jko1beXewHKeSOZsebWZ%2BLmNqS%2FHt6md1HDkAJq3LL%2Bbk%2FMdle5MJqP%2B%2FEFA9MMRVv6F3kkYu1bPQx%2FssQXGGLMPPK2SH7Qj3HXEVBjt3gb6wZ24r%2FtxdaHuFiMzg%2B46JdM%2BN%2BgesD7P5E3bMsuCofk5oEsKPqdihw3wgNR6aJ7gJZD%2FTr6pdyHCzFTmU7WarqKrIYzfFQiPfVnRPEY9uKPnPl5q%2FHVkJ%2Fs1xFOMOdaXiLSXom8x9HeH%2BekJluivi134JemGoABelvQgc9vSge8Ug2GGMekp%2BcAzACKWhpLIZngQLpra9Ro%2Fh7osPwxOIo0uVCzbIhLuwonjK%2BtriXjoDwO4cgQJLY8tUkcTYNX6qWsxjuoHulSc8M4ihY3w5kwUiyKQWDZhJGNqpySnWj9waiyg1WVeT464HcGAsAD%2FgOc4idcsGc%3D&OSSAccessKeyId=STS.NKLp5sKSkNo9oc9HmLPYYXrzb&Expires=1544429164&Signature=9TanHR%2FL%2B7Kg2X%2FHuDAPZicH%2FEo%3D
     * media_id:692
     */

    private int media_id;
    private String title;
    private String description;
    private String avatar;
    private String cover;
    private long uid;
    private int is_private;
    /**
     * 提示语：默认为空
     * 不为空时弹出提示语，不允许跳转
     */
    private int tips;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getMedia_id() {
        return media_id;
    }

    public void setMedia_id(int media_id) {
        this.media_id = media_id;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public int getIs_private() {
        return is_private;
    }

    public void setIs_private(int is_private) {
        this.is_private = is_private;
    }

    public int getTips() {
        return tips;
    }

    public void setTips(int tips) {
        this.tips = tips;
    }
}