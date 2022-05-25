package com.xianghe.ivy.manager.download;

import android.content.Context;

import com.xianghe.ivy.app.greendao.GreenDaoManager;
import com.xianghe.ivy.entity.dao.MovieTaskCacheDao;
import com.xianghe.ivy.entity.db.MovieTaskCache;

import java.util.List;

public class MoveTaskCacheManager implements IMovieDownloadManager.CacheManager {

    private final MovieTaskCacheDao mDao;

    public MoveTaskCacheManager(Context context) {
        mDao = GreenDaoManager.getInstance().getMovieTaskDao();
    }

    @Override
    public void insert(MovieTaskCache cache) {
        if (mDao == null) {
            return;
        }
        mDao.insert(cache);
    }

    @Override
    public void remove(MovieTaskCache cache) {
        if (mDao == null) {
            return;
        }
        List<MovieTaskCache> cacheList = mDao.queryBuilder().where(MovieTaskCacheDao.Properties.LocalPath.eq(cache.getLocalPath())).list();
        mDao.deleteInTx(cacheList);
    }

    @Override
    public void removeAll() {
        if (mDao == null) {
            return;
        }
        mDao.deleteAll();
    }

    @Override
    public List<MovieTaskCache> loadAll(boolean clear) {
        if (mDao == null) {
            return null;
        }
        List<MovieTaskCache> caches = mDao.loadAll();

        if (clear) {
            mDao.deleteAll();
        }
        return caches;
    }
}
