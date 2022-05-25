package com.xianghe.ivy.model;

import java.io.Serializable;

public class ListUserBean implements Serializable {
    private static final long serialVersionUID = -8862588424866582297L;
    /**
     * is_friend : 0
     * is_attention : 0
     * uid : 8272560
     * uid_html: <span style='color: #ffffff;'>8272549</span>
     * username : ivy-2
     * avatar : http://ivy-test.oss-cn-shenzhen.aliyuncs.com/avatar/386e/20181113/20181113b532d60333feec73b19a7d35586620ec154208955928.png?security-token=CAISlQJ1q6Ft5B2yfSjIr4j5O4%2FHpoxK%2Ba3SbUWIrG0ZXNZ1t7fRgDz2IHBNdHduAuwWtf80mW1X6foYlqJ4T55IQ1Dza8J148zYNPBw8s6T1fau5Jko1beXewHKeSOZsebWZ%2BLmNqS%2FHt6md1HDkAJq3LL%2Bbk%2FMdle5MJqP%2B%2FEFA9MMRVv6F3kkYu1bPQx%2FssQXGGLMPPK2SH7Qj3HXEVBjt3gb6wZ24r%2FtxdaHuFiMzg%2B46JdM%2BN%2BgesD7P5E3bMsuCofk5oEsKPqdihw3wgNR6aJ7gJZD%2FTr6pdyHCzFTmU7WarqKrIYzfFQiPfVnRPEY9uKPnPl5q%2FHVkJ%2Fs1xFOMOdaXiLSXom8x9HeH%2BekJluivi134JemGoABelvQgc9vSge8Ug2GGMekp%2BcAzACKWhpLIZngQLpra9Ro%2Fh7osPwxOIo0uVCzbIhLuwonjK%2BtriXjoDwO4cgQJLY8tUkcTYNX6qWsxjuoHulSc8M4ihY3w5kwUiyKQWDZhJGNqpySnWj9waiyg1WVeT464HcGAsAD%2FgOc4idcsGc%3D&OSSAccessKeyId=STS.NKLp5sKSkNo9oc9HmLPYYXrzb&Expires=1544429164&Signature=Xup0HLQP6eNXN9uhb19EVefPtXQ%3D
     */

    /**
     * 是否亲友 0-否，1-是
     */
    private int is_friend;
    /**
     * 是否关注 0-未关注，1-已关注
     */
    private int is_attention;
    private long uid;
    private String uid_html;
    private String username;
    private String avatar;

    public int getIs_friend() {
        return is_friend;
    }

    public void setIs_friend(int is_friend) {
        this.is_friend = is_friend;
    }

    public int getIs_attention() {
        return is_attention;
    }

    public void setIs_attention(int is_attention) {
        this.is_attention = is_attention;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getUid_html() {
        return uid_html;
    }

    public void setUid_html(String uid_html) {
        this.uid_html = uid_html;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}