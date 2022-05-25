package com.xianghe.ivy.entity.db;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 标签分类
 */
@Entity
public class TagsCategory {
    @Id(autoincrement = true)
    @Expose(serialize = false,deserialize = false)
    public Long lid; // 数据库的id

    @Property
    @SerializedName(value = "id")
    public int tid;
    @Property
    @SerializedName(value = "type_code")
    public int type_code;
    @Property
    public String tags_name;


    @Transient
    @Expose(serialize = false,deserialize = false)
    private boolean isSelected;

    public TagsCategory(int tid, String tags_name) {
        this.tid = tid;
        this.tags_name = tags_name;
    }

    @Generated(hash = 1867333300)
    public TagsCategory(Long lid, int tid, int type_code, String tags_name) {
        this.lid = lid;
        this.tid = tid;
        this.type_code = type_code;
        this.tags_name = tags_name;
    }

    @Generated(hash = 799100805)
    public TagsCategory() {
    }

    public Long getLid() {
        return lid;
    }

    public void setLid(Long lid) {
        this.lid = lid;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public int getType_code() {
        return type_code;
    }

    public void setType_code(int type_code) {
        this.type_code = type_code;
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

    public boolean getIsSelected() {
        return this.isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
