package com.xianghe.ivy.entity.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 搜索历史记录
 */
@Entity
public class SearchHistory {

    @Id(autoincrement = true)
    private Long id;

    /**
     * 用户uid
     */
    private long uid;

    /**
     * 搜索关键字
     */
    private String searchKey;

    /**
     * 搜索时间戳
     */
    private long searchTimestamp;

    @Generated(hash = 1406080412)
    public SearchHistory(Long id, long uid, String searchKey,
            long searchTimestamp) {
        this.id = id;
        this.uid = uid;
        this.searchKey = searchKey;
        this.searchTimestamp = searchTimestamp;
    }

    @Generated(hash = 1905904755)
    public SearchHistory() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getUid() {
        return this.uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getSearchKey() {
        return this.searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public long getSearchTimestamp() {
        return this.searchTimestamp;
    }

    public void setSearchTimestamp(long searchTimestamp) {
        this.searchTimestamp = searchTimestamp;
    }
}
