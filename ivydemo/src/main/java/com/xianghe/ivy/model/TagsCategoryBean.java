package com.xianghe.ivy.model;

import java.io.Serializable;

public class TagsCategoryBean implements Serializable {
    private static final long serialVersionUID = -5540197954310139441L;

    private int id;
    private String tags_name;
    private int type_code;


    private boolean isSelected;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTags_name() {
        return tags_name;
    }

    public void setTags_name(String tags_name) {
        this.tags_name = tags_name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getType_code() {
        return type_code;
    }

    public void setType_code(int type_code) {
        this.type_code = type_code;
    }
}
