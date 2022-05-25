package com.xianghe.ivy.model;

import java.io.Serializable;

public class AboutIvyBean implements Serializable {
    private static final long serialVersionUID = -5837308339820031609L;

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
