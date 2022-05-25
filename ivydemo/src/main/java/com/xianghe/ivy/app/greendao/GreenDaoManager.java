package com.xianghe.ivy.app.greendao;


import androidx.annotation.Nullable;

import com.xianghe.ivy.app.IvyApp;
import com.xianghe.ivy.entity.dao.DaoMaster;
import com.xianghe.ivy.entity.dao.DaoSession;
import com.xianghe.ivy.entity.dao.MovieItemDbDao;
import com.xianghe.ivy.entity.dao.MovieTaskCacheDao;
import com.xianghe.ivy.entity.dao.SearchHistoryDao;
import com.xianghe.ivy.entity.dao.TagsCategoryDao;
import com.xianghe.ivy.entity.dao.UploadTaskCacheDao;
import com.xianghe.ivy.entity.dao.VideoPushCacheDao;

/**
 * author:  ycl
 * date:  2018/11/9 17:15
 * desc:
 */
public class GreenDaoManager {
    private volatile static GreenDaoManager sDaoManager;
    private final String BD_NAME = "ivy.db";
    private DaoSession mDaoSession;

    public void init() {

        mDaoSession = new DaoMaster(new IvySqliteOpenHelper(IvyApp.getInstance().getApplicationContext(), BD_NAME)
                .getWritableDatabase()).newSession();
    }

    public static GreenDaoManager getInstance() {
        if (sDaoManager == null) {
            synchronized (GreenDaoManager.class) {
                if (sDaoManager == null) {
                    sDaoManager = new GreenDaoManager();
                }
            }
        }
        return sDaoManager;
    }


    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    /**
     * 视频发布页面 - 标签类型
     *
     * @return
     */
    public TagsCategoryDao getTagsCategoryDao() {
        return mDaoSession != null ? mDaoSession.getTagsCategoryDao() : null;
    }

    public MovieTaskCacheDao getMovieTaskDao() {
        return mDaoSession != null ? mDaoSession.getMovieTaskCacheDao() : null;
    }

    public UploadTaskCacheDao getUploadTaskCacheDao() {
        return mDaoSession != null ? mDaoSession.getUploadTaskCacheDao() : null;
    }

    /**
     * 搜索历史记录
     * @return
     */
    public MovieItemDbDao getMovieItemDao() {
        return mDaoSession != null ? mDaoSession.getMovieItemDbDao() : null;
    }

    /**
     * 搜索历史记录
     * @return
     */
    public SearchHistoryDao getSearchHistoryDao() {
        return mDaoSession != null ? mDaoSession.getSearchHistoryDao() : null;
    }

    @Nullable
    public VideoPushCacheDao getVideoPushCacheDao(){
        return mDaoSession != null ? mDaoSession.getVideoPushCacheDao() : null;
    }
}
