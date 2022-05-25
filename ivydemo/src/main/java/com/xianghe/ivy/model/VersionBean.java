package com.xianghe.ivy.model;

import java.io.Serializable;

public class VersionBean implements Serializable {
    private static final long serialVersionUID = 3533281055808124024L;

    private String url;
    /**
     * 版本号
     */
    private String version;
    /**
     * 最低版本号
     */
    private String ask_version;
    /**
     * 更新内容
     */
    private String make_content;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAsk_version() {
        return ask_version;
    }

    public void setAsk_version(String ask_version) {
        this.ask_version = ask_version;
    }

    public String getMake_content() {
        return make_content;
    }

    public void setMake_content(String make_content) {
        this.make_content = make_content;
    }
}
