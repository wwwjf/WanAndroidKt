package com.xianghe.ivy.entity.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.xianghe.ivy.entity.db.MovieItemDb;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "MOVIE_ITEM_DB".
*/
public class MovieItemDbDao extends AbstractDao<MovieItemDb, Long> {

    public static final String TABLENAME = "MOVIE_ITEM_DB";

    /**
     * Properties of entity MovieItemDb.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Uid = new Property(1, String.class, "uid", false, "UID");
        public final static Property Key = new Property(2, long.class, "key", false, "KEY");
        public final static Property FilePath = new Property(3, String.class, "filePath", false, "FILE_PATH");
        public final static Property VideoTime = new Property(4, Double.class, "videoTime", false, "VIDEO_TIME");
        public final static Property FilPicPath = new Property(5, String.class, "filPicPath", false, "FIL_PIC_PATH");
        public final static Property From = new Property(6, int.class, "from", false, "FROM");
        public final static Property MusicPath = new Property(7, String.class, "musicPath", false, "MUSIC_PATH");
        public final static Property VoicePath = new Property(8, String.class, "voicePath", false, "VOICE_PATH");
        public final static Property Video_from = new Property(9, boolean.class, "video_from", false, "VIDEO_FROM");
        public final static Property Video_name = new Property(10, String.class, "video_name", false, "VIDEO_NAME");
        public final static Property Video_desc = new Property(11, String.class, "video_desc", false, "VIDEO_DESC");
        public final static Property Video_types = new Property(12, String.class, "video_types", false, "VIDEO_TYPES");
        public final static Property Video_director = new Property(13, String.class, "video_director", false, "VIDEO_DIRECTOR");
        public final static Property Video_actor = new Property(14, String.class, "video_actor", false, "VIDEO_ACTOR");
        public final static Property Location = new Property(15, String.class, "location", false, "LOCATION");
        public final static Property MusicName = new Property(16, String.class, "musicName", false, "MUSIC_NAME");
        public final static Property MusicId = new Property(17, String.class, "musicId", false, "MUSIC_ID");
        public final static Property Push_type = new Property(18, int.class, "push_type", false, "PUSH_TYPE");
        public final static Property Rotate = new Property(19, int.class, "rotate", false, "ROTATE");
        public final static Property PicList = new Property(20, String.class, "picList", false, "PIC_LIST");
        public final static Property PicToMovie = new Property(21, boolean.class, "picToMovie", false, "PIC_TO_MOVIE");
        public final static Property TempletType = new Property(22, int.class, "templetType", false, "TEMPLET_TYPE");
        public final static Property IsScreenRecord = new Property(23, int.class, "isScreenRecord", false, "IS_SCREEN_RECORD");
    }


    public MovieItemDbDao(DaoConfig config) {
        super(config);
    }
    
    public MovieItemDbDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"MOVIE_ITEM_DB\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"UID\" TEXT," + // 1: uid
                "\"KEY\" INTEGER NOT NULL ," + // 2: key
                "\"FILE_PATH\" TEXT," + // 3: filePath
                "\"VIDEO_TIME\" REAL," + // 4: videoTime
                "\"FIL_PIC_PATH\" TEXT," + // 5: filPicPath
                "\"FROM\" INTEGER NOT NULL ," + // 6: from
                "\"MUSIC_PATH\" TEXT," + // 7: musicPath
                "\"VOICE_PATH\" TEXT," + // 8: voicePath
                "\"VIDEO_FROM\" INTEGER NOT NULL ," + // 9: video_from
                "\"VIDEO_NAME\" TEXT," + // 10: video_name
                "\"VIDEO_DESC\" TEXT," + // 11: video_desc
                "\"VIDEO_TYPES\" TEXT," + // 12: video_types
                "\"VIDEO_DIRECTOR\" TEXT," + // 13: video_director
                "\"VIDEO_ACTOR\" TEXT," + // 14: video_actor
                "\"LOCATION\" TEXT," + // 15: location
                "\"MUSIC_NAME\" TEXT," + // 16: musicName
                "\"MUSIC_ID\" TEXT," + // 17: musicId
                "\"PUSH_TYPE\" INTEGER NOT NULL ," + // 18: push_type
                "\"ROTATE\" INTEGER NOT NULL ," + // 19: rotate
                "\"PIC_LIST\" TEXT," + // 20: picList
                "\"PIC_TO_MOVIE\" INTEGER NOT NULL ," + // 21: picToMovie
                "\"TEMPLET_TYPE\" INTEGER NOT NULL ," + // 22: templetType
                "\"IS_SCREEN_RECORD\" INTEGER NOT NULL );"); // 23: isScreenRecord
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"MOVIE_ITEM_DB\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, MovieItemDb entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String uid = entity.getUid();
        if (uid != null) {
            stmt.bindString(2, uid);
        }
        stmt.bindLong(3, entity.getKey());
 
        String filePath = entity.getFilePath();
        if (filePath != null) {
            stmt.bindString(4, filePath);
        }
 
        Double videoTime = entity.getVideoTime();
        if (videoTime != null) {
            stmt.bindDouble(5, videoTime);
        }
 
        String filPicPath = entity.getFilPicPath();
        if (filPicPath != null) {
            stmt.bindString(6, filPicPath);
        }
        stmt.bindLong(7, entity.getFrom());
 
        String musicPath = entity.getMusicPath();
        if (musicPath != null) {
            stmt.bindString(8, musicPath);
        }
 
        String voicePath = entity.getVoicePath();
        if (voicePath != null) {
            stmt.bindString(9, voicePath);
        }
        stmt.bindLong(10, entity.getVideo_from() ? 1L: 0L);
 
        String video_name = entity.getVideo_name();
        if (video_name != null) {
            stmt.bindString(11, video_name);
        }
 
        String video_desc = entity.getVideo_desc();
        if (video_desc != null) {
            stmt.bindString(12, video_desc);
        }
 
        String video_types = entity.getVideo_types();
        if (video_types != null) {
            stmt.bindString(13, video_types);
        }
 
        String video_director = entity.getVideo_director();
        if (video_director != null) {
            stmt.bindString(14, video_director);
        }
 
        String video_actor = entity.getVideo_actor();
        if (video_actor != null) {
            stmt.bindString(15, video_actor);
        }
 
        String location = entity.getLocation();
        if (location != null) {
            stmt.bindString(16, location);
        }
 
        String musicName = entity.getMusicName();
        if (musicName != null) {
            stmt.bindString(17, musicName);
        }
 
        String musicId = entity.getMusicId();
        if (musicId != null) {
            stmt.bindString(18, musicId);
        }
        stmt.bindLong(19, entity.getPush_type());
        stmt.bindLong(20, entity.getRotate());
 
        String picList = entity.getPicList();
        if (picList != null) {
            stmt.bindString(21, picList);
        }
        stmt.bindLong(22, entity.getPicToMovie() ? 1L: 0L);
        stmt.bindLong(23, entity.getTempletType());
        stmt.bindLong(24, entity.getIsScreenRecord());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, MovieItemDb entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String uid = entity.getUid();
        if (uid != null) {
            stmt.bindString(2, uid);
        }
        stmt.bindLong(3, entity.getKey());
 
        String filePath = entity.getFilePath();
        if (filePath != null) {
            stmt.bindString(4, filePath);
        }
 
        Double videoTime = entity.getVideoTime();
        if (videoTime != null) {
            stmt.bindDouble(5, videoTime);
        }
 
        String filPicPath = entity.getFilPicPath();
        if (filPicPath != null) {
            stmt.bindString(6, filPicPath);
        }
        stmt.bindLong(7, entity.getFrom());
 
        String musicPath = entity.getMusicPath();
        if (musicPath != null) {
            stmt.bindString(8, musicPath);
        }
 
        String voicePath = entity.getVoicePath();
        if (voicePath != null) {
            stmt.bindString(9, voicePath);
        }
        stmt.bindLong(10, entity.getVideo_from() ? 1L: 0L);
 
        String video_name = entity.getVideo_name();
        if (video_name != null) {
            stmt.bindString(11, video_name);
        }
 
        String video_desc = entity.getVideo_desc();
        if (video_desc != null) {
            stmt.bindString(12, video_desc);
        }
 
        String video_types = entity.getVideo_types();
        if (video_types != null) {
            stmt.bindString(13, video_types);
        }
 
        String video_director = entity.getVideo_director();
        if (video_director != null) {
            stmt.bindString(14, video_director);
        }
 
        String video_actor = entity.getVideo_actor();
        if (video_actor != null) {
            stmt.bindString(15, video_actor);
        }
 
        String location = entity.getLocation();
        if (location != null) {
            stmt.bindString(16, location);
        }
 
        String musicName = entity.getMusicName();
        if (musicName != null) {
            stmt.bindString(17, musicName);
        }
 
        String musicId = entity.getMusicId();
        if (musicId != null) {
            stmt.bindString(18, musicId);
        }
        stmt.bindLong(19, entity.getPush_type());
        stmt.bindLong(20, entity.getRotate());
 
        String picList = entity.getPicList();
        if (picList != null) {
            stmt.bindString(21, picList);
        }
        stmt.bindLong(22, entity.getPicToMovie() ? 1L: 0L);
        stmt.bindLong(23, entity.getTempletType());
        stmt.bindLong(24, entity.getIsScreenRecord());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public MovieItemDb readEntity(Cursor cursor, int offset) {
        MovieItemDb entity = new MovieItemDb( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // uid
            cursor.getLong(offset + 2), // key
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // filePath
            cursor.isNull(offset + 4) ? null : cursor.getDouble(offset + 4), // videoTime
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // filPicPath
            cursor.getInt(offset + 6), // from
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // musicPath
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // voicePath
            cursor.getShort(offset + 9) != 0, // video_from
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // video_name
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // video_desc
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // video_types
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // video_director
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // video_actor
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // location
            cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16), // musicName
            cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17), // musicId
            cursor.getInt(offset + 18), // push_type
            cursor.getInt(offset + 19), // rotate
            cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20), // picList
            cursor.getShort(offset + 21) != 0, // picToMovie
            cursor.getInt(offset + 22), // templetType
            cursor.getInt(offset + 23) // isScreenRecord
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, MovieItemDb entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUid(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setKey(cursor.getLong(offset + 2));
        entity.setFilePath(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setVideoTime(cursor.isNull(offset + 4) ? null : cursor.getDouble(offset + 4));
        entity.setFilPicPath(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setFrom(cursor.getInt(offset + 6));
        entity.setMusicPath(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setVoicePath(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setVideo_from(cursor.getShort(offset + 9) != 0);
        entity.setVideo_name(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setVideo_desc(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setVideo_types(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setVideo_director(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setVideo_actor(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setLocation(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setMusicName(cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16));
        entity.setMusicId(cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17));
        entity.setPush_type(cursor.getInt(offset + 18));
        entity.setRotate(cursor.getInt(offset + 19));
        entity.setPicList(cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20));
        entity.setPicToMovie(cursor.getShort(offset + 21) != 0);
        entity.setTempletType(cursor.getInt(offset + 22));
        entity.setIsScreenRecord(cursor.getInt(offset + 23));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(MovieItemDb entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(MovieItemDb entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(MovieItemDb entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
