package com.xianghe.ivy.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AliOssToken implements Serializable {
    private static final long serialVersionUID = -4903706240488305600L;


    /**
     * AccessKeyId : STS.NKJtZfeJnkNDFteqSRDn3tSrb
     * AccessKeySecret : AGWA2dTPhmTEMzybP1trGJDmhHZdPrSDYfjJvz8kMha5
     * Expiration : 2018-08-06T07:42:13Z
     * SecurityToken : CAISogN1q6Ft5B2yfSjIr4j/P+DSiJVP3IyvRFLUlVMHSOEfm5bZgDz2IH1KfHhvBekXvvo/nWpW6fgSlqJ4T55IQ1Dza8J148zta9U4286T1fau5Jko1bcrcAr6Umxzta2/SuH9S8ynkJ7PD3nPii50x5bjaDymRCbLGJaViJlhHNZ1Ow6jdmhpCctxLAlvo9N4UHzKLqSVLwLNiGjdB1YKwg1nkjFT5KCy3sC74BjTh0GYr+gOvNbVI4O4V8B2IIwdI9Cux75ffK3bzAtN7wRL7K5skJFc/TDOsrP6BEJKsTGHKPbz+N9iJxNiHJJYfZRJt//hj/Z1l/XOnoDssXZ3MPpSTj7USfL+ornNE/j7Mc0iJ/SpeSaB+O2kFbTJhCUNRF9cdmE6ctE6eHhrEk5uGHOIZoWa03PnXjCFYo2o8tlvjsEklw23rYLRdwTeGuXI60tCZM9gNXFPHgUNwGnsfpUBdwFxaF59D96XN94uMkgP9v+x7F2MBnQ6kC4J7+eNbvfXq70Zbp7kQpVF3IwSaZJLqWI2SE7tTLajmsqPv/fpmlU9GoABnKi4xsxWbSO5YlXbwo6UZ6JmazgQCE15XSilk3UeokI+yzWURJntp9KVdsl4arQdiCeZIz/b3L5IFCZXiuzAWUrgj69y6s7CuEXS80nQlQjK7jPKBjEeYxIVEgs/U0ulMda1nCzYUFJHP0Kzt/zysS/HQRta4o15PTGPSMeSP88=
     * callbackUrl : http://www.iweiying.com/api/index_callback
     * callbackBody : filename=${object}&size=${size}&bucket=${bucket}&etag=${etag}&mimeType=${mimeType}&system=${x:system}&ticket=${x:ticket}&path=${x:path}&media_id=${x:media_id}
     * x:path : 6d79/201808/2018080693fe127ab42d615d004fcf87321f2959
     */

    private String AccessKeyId;
    private String AccessKeySecret;
    private String Expiration;
    private String SecurityToken;
    private String callbackUrl;
    private String bucketName;
    private String endpoint;

    public String getBuckName() {
        return bucketName;
    }

    public void setBuckName(String buckName) {
        this.bucketName = buckName;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    private String callbackBody;

    @SerializedName("x:path")
    private String XPath;

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

    public String getXPath() {
        return XPath;
    }


    public void setXPath(String xPath) {
        this.XPath = xPath;
    }

    @Override
    public String toString() {
        return "AliOssToken{" +
                "AccessKeyId='" + AccessKeyId + '\'' +
                ", AccessKeySecret='" + AccessKeySecret + '\'' +
                ", Expiration='" + Expiration + '\'' +
                ", SecurityToken='" + SecurityToken + '\'' +
                ", callbackUrl='" + callbackUrl + '\'' +
                ", callbackBody='" + callbackBody + '\'' +
                ", XPath='" + XPath + '\'' +
                '}';
    }
}
