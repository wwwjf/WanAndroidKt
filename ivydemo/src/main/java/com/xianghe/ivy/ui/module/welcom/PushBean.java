package com.xianghe.ivy.ui.module.welcom;

public class PushBean {

    /**
     * status : 1
     * info : ok
     * data : null
     * api_version : 1.1.0
     */

    private int status;
    private String info;
    private Object data;
    private String api_version;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getApi_version() {
        return api_version;
    }

    public void setApi_version(String api_version) {
        this.api_version = api_version;
    }
}
