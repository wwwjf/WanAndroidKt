package com.xianghe.ivy.entity.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.xianghe.ivy.entity.db.UploadTaskCache;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "UPLOAD_TASK_CACHE".
*/
public class UploadTaskCacheDao extends AbstractDao<UploadTaskCache, Long> {

    public static final String TABLENAME = "UPLOAD_TASK_CACHE";

    /**
     * Properties of entity UploadTaskCache.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property FileId = new Property(1, String.class, "fileId", false, "FILE_ID");
        public final static Property FilePath = new Property(2, String.class, "filePath", false, "FILE_PATH");
        public final static Property Type = new Property(3, int.class, "type", false, "TYPE");
        public final static Property Uid = new Property(4, long.class, "uid", false, "UID");
        public final static Property Title = new Property(5, String.class, "title", false, "TITLE");
        public final static Property CoverUrl = new Property(6, String.class, "coverUrl", false, "COVER_URL");
        public final static Property UploadName = new Property(7, String.class, "uploadName", false, "UPLOAD_NAME");
        public final static Property UploadUrl = new Property(8, String.class, "uploadUrl", false, "UPLOAD_URL");
        public final static Property UploadDir = new Property(9, String.class, "uploadDir", false, "UPLOAD_DIR");
        public final static Property HasWaterMark = new Property(10, boolean.class, "hasWaterMark", false, "HAS_WATER_MARK");
        public final static Property Desc = new Property(11, String.class, "desc", false, "DESC");
        public final static Property Director = new Property(12, String.class, "director", false, "DIRECTOR");
        public final static Property Player = new Property(13, String.class, "player", false, "PLAYER");
        public final static Property Location = new Property(14, String.class, "location", false, "LOCATION");
        public final static Property OutFilePath = new Property(15, String.class, "outFilePath", false, "OUT_FILE_PATH");
        public final static Property HasAddHeaderInfo = new Property(16, boolean.class, "hasAddHeaderInfo", false, "HAS_ADD_HEADER_INFO");
    }


    public UploadTaskCacheDao(DaoConfig config) {
        super(config);
    }
    
    public UploadTaskCacheDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"UPLOAD_TASK_CACHE\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"FILE_ID\" TEXT," + // 1: fileId
                "\"FILE_PATH\" TEXT," + // 2: filePath
                "\"TYPE\" INTEGER NOT NULL ," + // 3: type
                "\"UID\" INTEGER NOT NULL ," + // 4: uid
                "\"TITLE\" TEXT," + // 5: title
                "\"COVER_URL\" TEXT," + // 6: coverUrl
                "\"UPLOAD_NAME\" TEXT," + // 7: uploadName
                "\"UPLOAD_URL\" TEXT," + // 8: uploadUrl
                "\"UPLOAD_DIR\" TEXT," + // 9: uploadDir
                "\"HAS_WATER_MARK\" INTEGER NOT NULL ," + // 10: hasWaterMark
                "\"DESC\" TEXT," + // 11: desc
                "\"DIRECTOR\" TEXT," + // 12: director
                "\"PLAYER\" TEXT," + // 13: player
                "\"LOCATION\" TEXT," + // 14: location
                "\"OUT_FILE_PATH\" TEXT," + // 15: outFilePath
                "\"HAS_ADD_HEADER_INFO\" INTEGER NOT NULL );"); // 16: hasAddHeaderInfo
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"UPLOAD_TASK_CACHE\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, UploadTaskCache entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String fileId = entity.getFileId();
        if (fileId != null) {
            stmt.bindString(2, fileId);
        }
 
        String filePath = entity.getFilePath();
        if (filePath != null) {
            stmt.bindString(3, filePath);
        }
        stmt.bindLong(4, entity.getType());
        stmt.bindLong(5, entity.getUid());
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(6, title);
        }
 
        String coverUrl = entity.getCoverUrl();
        if (coverUrl != null) {
            stmt.bindString(7, coverUrl);
        }
 
        String uploadName = entity.getUploadName();
        if (uploadName != null) {
            stmt.bindString(8, uploadName);
        }
 
        String uploadUrl = entity.getUploadUrl();
        if (uploadUrl != null) {
            stmt.bindString(9, uploadUrl);
        }
 
        String uploadDir = entity.getUploadDir();
        if (uploadDir != null) {
            stmt.bindString(10, uploadDir);
        }
        stmt.bindLong(11, entity.getHasWaterMark() ? 1L: 0L);
 
        String desc = entity.getDesc();
        if (desc != null) {
            stmt.bindString(12, desc);
        }
 
        String director = entity.getDirector();
        if (director != null) {
            stmt.bindString(13, director);
        }
 
        String player = entity.getPlayer();
        if (player != null) {
            stmt.bindString(14, player);
        }
 
        String location = entity.getLocation();
        if (location != null) {
            stmt.bindString(15, location);
        }
 
        String outFilePath = entity.getOutFilePath();
        if (outFilePath != null) {
            stmt.bindString(16, outFilePath);
        }
        stmt.bindLong(17, entity.getHasAddHeaderInfo() ? 1L: 0L);
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, UploadTaskCache entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String fileId = entity.getFileId();
        if (fileId != null) {
            stmt.bindString(2, fileId);
        }
 
        String filePath = entity.getFilePath();
        if (filePath != null) {
            stmt.bindString(3, filePath);
        }
        stmt.bindLong(4, entity.getType());
        stmt.bindLong(5, entity.getUid());
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(6, title);
        }
 
        String coverUrl = entity.getCoverUrl();
        if (coverUrl != null) {
            stmt.bindString(7, coverUrl);
        }
 
        String uploadName = entity.getUploadName();
        if (uploadName != null) {
            stmt.bindString(8, uploadName);
        }
 
        String uploadUrl = entity.getUploadUrl();
        if (uploadUrl != null) {
            stmt.bindString(9, uploadUrl);
        }
 
        String uploadDir = entity.getUploadDir();
        if (uploadDir != null) {
            stmt.bindString(10, uploadDir);
        }
        stmt.bindLong(11, entity.getHasWaterMark() ? 1L: 0L);
 
        String desc = entity.getDesc();
        if (desc != null) {
            stmt.bindString(12, desc);
        }
 
        String director = entity.getDirector();
        if (director != null) {
            stmt.bindString(13, director);
        }
 
        String player = entity.getPlayer();
        if (player != null) {
            stmt.bindString(14, player);
        }
 
        String location = entity.getLocation();
        if (location != null) {
            stmt.bindString(15, location);
        }
 
        String outFilePath = entity.getOutFilePath();
        if (outFilePath != null) {
            stmt.bindString(16, outFilePath);
        }
        stmt.bindLong(17, entity.getHasAddHeaderInfo() ? 1L: 0L);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public UploadTaskCache readEntity(Cursor cursor, int offset) {
        UploadTaskCache entity = new UploadTaskCache( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // fileId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // filePath
            cursor.getInt(offset + 3), // type
            cursor.getLong(offset + 4), // uid
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // title
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // coverUrl
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // uploadName
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // uploadUrl
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // uploadDir
            cursor.getShort(offset + 10) != 0, // hasWaterMark
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // desc
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // director
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // player
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // location
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // outFilePath
            cursor.getShort(offset + 16) != 0 // hasAddHeaderInfo
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, UploadTaskCache entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setFileId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setFilePath(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setType(cursor.getInt(offset + 3));
        entity.setUid(cursor.getLong(offset + 4));
        entity.setTitle(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setCoverUrl(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setUploadName(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setUploadUrl(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setUploadDir(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setHasWaterMark(cursor.getShort(offset + 10) != 0);
        entity.setDesc(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setDirector(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setPlayer(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setLocation(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setOutFilePath(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setHasAddHeaderInfo(cursor.getShort(offset + 16) != 0);
     }
    
    @Override
    protected final Long updateKeyAfterInsert(UploadTaskCache entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(UploadTaskCache entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(UploadTaskCache entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}