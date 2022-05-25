package com.xianghe.ivy.entity.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.xianghe.ivy.entity.db.MovieTaskCache;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "MOVIE_TASK_CACHE".
*/
public class MovieTaskCacheDao extends AbstractDao<MovieTaskCache, Long> {

    public static final String TABLENAME = "MOVIE_TASK_CACHE";

    /**
     * Properties of entity MovieTaskCache.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property LocalPath = new Property(1, String.class, "localPath", false, "LOCAL_PATH");
        public final static Property DownloadUrl = new Property(2, String.class, "downloadUrl", false, "DOWNLOAD_URL");
        public final static Property Type = new Property(3, int.class, "type", false, "TYPE");
        public final static Property Uid = new Property(4, int.class, "uid", false, "UID");
        public final static Property Data = new Property(5, String.class, "data", false, "DATA");
    }


    public MovieTaskCacheDao(DaoConfig config) {
        super(config);
    }
    
    public MovieTaskCacheDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"MOVIE_TASK_CACHE\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"LOCAL_PATH\" TEXT," + // 1: localPath
                "\"DOWNLOAD_URL\" TEXT," + // 2: downloadUrl
                "\"TYPE\" INTEGER NOT NULL ," + // 3: type
                "\"UID\" INTEGER NOT NULL ," + // 4: uid
                "\"DATA\" TEXT);"); // 5: data
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"MOVIE_TASK_CACHE\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, MovieTaskCache entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String localPath = entity.getLocalPath();
        if (localPath != null) {
            stmt.bindString(2, localPath);
        }
 
        String downloadUrl = entity.getDownloadUrl();
        if (downloadUrl != null) {
            stmt.bindString(3, downloadUrl);
        }
        stmt.bindLong(4, entity.getType());
        stmt.bindLong(5, entity.getUid());
 
        String data = entity.getData();
        if (data != null) {
            stmt.bindString(6, data);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, MovieTaskCache entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String localPath = entity.getLocalPath();
        if (localPath != null) {
            stmt.bindString(2, localPath);
        }
 
        String downloadUrl = entity.getDownloadUrl();
        if (downloadUrl != null) {
            stmt.bindString(3, downloadUrl);
        }
        stmt.bindLong(4, entity.getType());
        stmt.bindLong(5, entity.getUid());
 
        String data = entity.getData();
        if (data != null) {
            stmt.bindString(6, data);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public MovieTaskCache readEntity(Cursor cursor, int offset) {
        MovieTaskCache entity = new MovieTaskCache( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // localPath
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // downloadUrl
            cursor.getInt(offset + 3), // type
            cursor.getInt(offset + 4), // uid
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5) // data
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, MovieTaskCache entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setLocalPath(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setDownloadUrl(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setType(cursor.getInt(offset + 3));
        entity.setUid(cursor.getInt(offset + 4));
        entity.setData(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(MovieTaskCache entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(MovieTaskCache entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(MovieTaskCache entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
