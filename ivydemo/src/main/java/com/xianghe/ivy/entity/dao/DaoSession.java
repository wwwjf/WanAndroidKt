package com.xianghe.ivy.entity.dao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.xianghe.ivy.entity.db.MovieItemDb;
import com.xianghe.ivy.entity.db.MovieTaskCache;
import com.xianghe.ivy.entity.db.SearchHistory;
import com.xianghe.ivy.entity.db.TagsCategory;
import com.xianghe.ivy.entity.db.UploadTaskCache;
import com.xianghe.ivy.entity.db.VideoPushCache;

import com.xianghe.ivy.entity.dao.MovieItemDbDao;
import com.xianghe.ivy.entity.dao.MovieTaskCacheDao;
import com.xianghe.ivy.entity.dao.SearchHistoryDao;
import com.xianghe.ivy.entity.dao.TagsCategoryDao;
import com.xianghe.ivy.entity.dao.UploadTaskCacheDao;
import com.xianghe.ivy.entity.dao.VideoPushCacheDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig movieItemDbDaoConfig;
    private final DaoConfig movieTaskCacheDaoConfig;
    private final DaoConfig searchHistoryDaoConfig;
    private final DaoConfig tagsCategoryDaoConfig;
    private final DaoConfig uploadTaskCacheDaoConfig;
    private final DaoConfig videoPushCacheDaoConfig;

    private final MovieItemDbDao movieItemDbDao;
    private final MovieTaskCacheDao movieTaskCacheDao;
    private final SearchHistoryDao searchHistoryDao;
    private final TagsCategoryDao tagsCategoryDao;
    private final UploadTaskCacheDao uploadTaskCacheDao;
    private final VideoPushCacheDao videoPushCacheDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        movieItemDbDaoConfig = daoConfigMap.get(MovieItemDbDao.class).clone();
        movieItemDbDaoConfig.initIdentityScope(type);

        movieTaskCacheDaoConfig = daoConfigMap.get(MovieTaskCacheDao.class).clone();
        movieTaskCacheDaoConfig.initIdentityScope(type);

        searchHistoryDaoConfig = daoConfigMap.get(SearchHistoryDao.class).clone();
        searchHistoryDaoConfig.initIdentityScope(type);

        tagsCategoryDaoConfig = daoConfigMap.get(TagsCategoryDao.class).clone();
        tagsCategoryDaoConfig.initIdentityScope(type);

        uploadTaskCacheDaoConfig = daoConfigMap.get(UploadTaskCacheDao.class).clone();
        uploadTaskCacheDaoConfig.initIdentityScope(type);

        videoPushCacheDaoConfig = daoConfigMap.get(VideoPushCacheDao.class).clone();
        videoPushCacheDaoConfig.initIdentityScope(type);

        movieItemDbDao = new MovieItemDbDao(movieItemDbDaoConfig, this);
        movieTaskCacheDao = new MovieTaskCacheDao(movieTaskCacheDaoConfig, this);
        searchHistoryDao = new SearchHistoryDao(searchHistoryDaoConfig, this);
        tagsCategoryDao = new TagsCategoryDao(tagsCategoryDaoConfig, this);
        uploadTaskCacheDao = new UploadTaskCacheDao(uploadTaskCacheDaoConfig, this);
        videoPushCacheDao = new VideoPushCacheDao(videoPushCacheDaoConfig, this);

        registerDao(MovieItemDb.class, movieItemDbDao);
        registerDao(MovieTaskCache.class, movieTaskCacheDao);
        registerDao(SearchHistory.class, searchHistoryDao);
        registerDao(TagsCategory.class, tagsCategoryDao);
        registerDao(UploadTaskCache.class, uploadTaskCacheDao);
        registerDao(VideoPushCache.class, videoPushCacheDao);
    }
    
    public void clear() {
        movieItemDbDaoConfig.clearIdentityScope();
        movieTaskCacheDaoConfig.clearIdentityScope();
        searchHistoryDaoConfig.clearIdentityScope();
        tagsCategoryDaoConfig.clearIdentityScope();
        uploadTaskCacheDaoConfig.clearIdentityScope();
        videoPushCacheDaoConfig.clearIdentityScope();
    }

    public MovieItemDbDao getMovieItemDbDao() {
        return movieItemDbDao;
    }

    public MovieTaskCacheDao getMovieTaskCacheDao() {
        return movieTaskCacheDao;
    }

    public SearchHistoryDao getSearchHistoryDao() {
        return searchHistoryDao;
    }

    public TagsCategoryDao getTagsCategoryDao() {
        return tagsCategoryDao;
    }

    public UploadTaskCacheDao getUploadTaskCacheDao() {
        return uploadTaskCacheDao;
    }

    public VideoPushCacheDao getVideoPushCacheDao() {
        return videoPushCacheDao;
    }

}
