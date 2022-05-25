package com.xianghe.ivy.model;

import java.io.Serializable;

// FIXME generate failure  field _$XPath247
public class GetTokenBean implements Serializable {


    /**
     * AccessKeyId : STS.NJj11UqzyHqxessNhmRfj8dZs
     * AccessKeySecret : HaaKCoLoBRdTKU19KhVCMWBaw97Pwkdi6LKDQdEKJh8f
     * Expiration : 2018-11-01T09:17:39Z
     * SecurityToken : CAIS+wJ1q6Ft5B2yfSjIr4nfeovhnKVY/7OTZ1XCqmg4XulG16HxkTz2IHBNdHduAuwWtf80mW1X6foYlqJ4T55IQ1Dza8J148z/L6o9586T1fau5Jko1bdycAr6Umwqta2/SuH9S8ynkJ7PD3nPii50x5bjaDymRCbLGJaViJlhHNZ1Ow6jdmhpCctxLAlvo9N4UHzKLqSVLwLNiGjdB1YKwg1nkjFT5KCy3sC74BjTh0GYr+gOvNbVI4O4V8B2IIwdI9Cux75ffK3bzAtN7wRL7K5skJFc/TDOsrP6BEJKsTGHKPbz+N9iJxNiHOtYfZRJt//hj/Z1l/XOnoDssXZ3MPpSTj7USfLHoq7NE/j7Mc0iJ/SpeSaP8KjWacKk61t7MSpAZF8bIopwEBIqV0xwEAO9A7S85VXHbj2kT6W4y6ws2fJ3tQ64pIrUeQPTE+7CintFZcBgNVlbPhoXzHHndbQdbwtPflVoCrCYV4xvawpErqflrYBJnbbsFZI8GoABkkfH7F3+5X5jJSh/4czp2h6L3xcGrbCVNv0SyH6CR1XEFLsCJQoLSVsen7oHXu52MIdahF9ilN45/h8mbT0O7gI/5oUJT4qaVDORf4kQ02vR//MBoFV/cGrumuXmc8F6ZoL/RhYSkYPoaEp2udWHB7mXzmeRfivJPucgPft5I8U=
     * bucketName : ivy-test
     * endpoint : oss-cn-shenzhen.aliyuncs.com
     * callbackUrl : http://local.wy.com/api/index_callback
     * callbackBody : filename=${object}&size=${size}&bucket=${bucket}&etag=${etag}&mimeType=${mimeType}&system=${x:system}&ticket=${x:ticket}&path=${x:path}&media_id=${x:media_id}
     * xpath : {"avatar":"avatar/b722/201811/2018110109f5e2a5bcbe2aaba808d72fb701a00f","video":"video/b722/201811/20181101abf6b06fd754fafb5096aa9ca1f9242e","cover":"cover/b722/201811/20181101aab8b4b380ae478b294d3acd18071acc","music":"music/b722/201811/20181101317987bca3c43de5f65f4ef248fd5af6","shareholder_sign":"shareholder_sign/a8c4/20181102/201811021116178ae682cb418a3a57a0cba1ff71","idcard":"idcard/a8c4/20181107/20181107cae60430a9e86b6d19202f2b8a5530bc"}
     * expires_in : 3000
     */

    private String AccessKeyId;
    private String AccessKeySecret;
    private String Expiration;
    private String SecurityToken;
    private String bucketName;
    private String endpoint;
    private String callbackUrl;
    private String callbackBody;
    private XpathBean xpath;
    private int expires_in;
    private OSSBean oss_cdn;

    public String getAccessKeyId() {
        return AccessKeyId;
    }

    public void setAccessKeyId(String AccessKeyId) {
        this.AccessKeyId = AccessKeyId;
    }

    public String getAccessKeySecret() {
        return AccessKeySecret;
    }

    public void setAccessKeySecret(String AccessKeySecret) {
        this.AccessKeySecret = AccessKeySecret;
    }

    public String getExpiration() {
        return Expiration;
    }

    public void setExpiration(String Expiration) {
        this.Expiration = Expiration;
    }

    public String getSecurityToken() {
        return SecurityToken;
    }

    public void setSecurityToken(String SecurityToken) {
        this.SecurityToken = SecurityToken;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getCallbackBody() {
        return callbackBody;
    }

    public void setCallbackBody(String callbackBody) {
        this.callbackBody = callbackBody;
    }

    public XpathBean getXpath() {
        return xpath;
    }

    public void setXpath(XpathBean xpath) {
        this.xpath = xpath;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public OSSBean getOss_cdn() {
        return oss_cdn;
    }

    public void setOss_cdn(OSSBean oss_cdn) {
        this.oss_cdn = oss_cdn;
    }

    public static class XpathBean implements Serializable{
        /**
         * avatar : avatar/b722/201811/2018110109f5e2a5bcbe2aaba808d72fb701a00f
         * video : video/b722/201811/20181101abf6b06fd754fafb5096aa9ca1f9242e
         * cover : cover/b722/201811/20181101aab8b4b380ae478b294d3acd18071acc
         * music : music/b722/201811/20181101317987bca3c43de5f65f4ef248fd5af6
         * shareholder_sign : shareholder_sign/a8c4/20181102/201811021116178ae682cb418a3a57a0cba1ff71
         * idcard : idcard/a8c4/20181107/20181107cae60430a9e86b6d19202f2b8a5530bc
         */

        private String avatar;
        private String video;
        private String cover;
        private String music;
        private String shareholder_sign;
        private String idcard;
        private String member_avatar;

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getVideo() {
            return video;
        }

        public void setVideo(String video) {
            this.video = video;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public String getMusic() {
            return music;
        }

        public void setMusic(String music) {
            this.music = music;
        }

        public String getShareholder_sign() {
            return shareholder_sign;
        }

        public void setShareholder_sign(String shareholder_sign) {
            this.shareholder_sign = shareholder_sign;
        }

        public String getIdcard() {
            return idcard;
        }

        public void setIdcard(String idcard) {
            this.idcard = idcard;
        }

        public String getMember_avatar() {
            return member_avatar;
        }

        public void setMember_avatar(String member_avatar) {
            this.member_avatar = member_avatar;
        }
    }

    public static class OSSBean implements Serializable{
        private int cdn_enable;
        private String cdn_url;
        private String cdn_key;

        public int getCdn_enable() {
            return cdn_enable;
        }

        public void setCdn_enable(int cdn_enable) {
            this.cdn_enable = cdn_enable;
        }

        public String getCdn_url() {
            return cdn_url;
        }

        public void setCdn_url(String cdn_url) {
            this.cdn_url = cdn_url;
        }

        public String getCdn_key() {
            return cdn_key;
        }

        public void setCdn_key(String cdn_key) {
            this.cdn_key = cdn_key;
        }
    }
}
