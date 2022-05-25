package com.xianghe.ivy.model;

import java.io.Serializable;

public class ErrorBean implements Serializable {
    private static final long serialVersionUID = -254486887739181170L;

    /**
     * file : /usr/share/nginx/ivy_dev/app/Helper/Common.php
     * line : 480
     * e_class1 : App\Exceptions\ApiException
     */

    private String file;
    private int line;
    private String e_class1;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getE_class1() {
        return e_class1;
    }

    public void setE_class1(String e_class1) {
        this.e_class1 = e_class1;
    }
}
