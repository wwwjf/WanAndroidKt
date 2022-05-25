package com.xianghe.ivy.ui.push;

import java.io.Serializable;

public class PushModel implements Serializable {
    /**
     * type : 0
     */

    private int type;

    private long id;

    public PushModel() {
    }

    public PushModel(int type, long id) {
        this.type = type;
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
