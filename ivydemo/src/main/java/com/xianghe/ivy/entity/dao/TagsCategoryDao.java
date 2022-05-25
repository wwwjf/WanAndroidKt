package com.xianghe.ivy.entity.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.xianghe.ivy.entity.db.TagsCategory;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "TAGS_CATEGORY".
*/
public class TagsCategoryDao extends AbstractDao<TagsCategory, Long> {

    public static final String TABLENAME = "TAGS_CATEGORY";

    /**
     * Properties of entity TagsCategory.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Lid = new Property(0, Long.class, "lid", true, "_id");
        public final static Property Tid = new Property(1, int.class, "tid", false, "TID");
        public final static Property Type_code = new Property(2, int.class, "type_code", false, "TYPE_CODE");
        public final static Property Tags_name = new Property(3, String.class, "tags_name", false, "TAGS_NAME");
    }


    public TagsCategoryDao(DaoConfig config) {
        super(config);
    }
    
    public TagsCategoryDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"TAGS_CATEGORY\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: lid
                "\"TID\" INTEGER NOT NULL ," + // 1: tid
                "\"TYPE_CODE\" INTEGER NOT NULL ," + // 2: type_code
                "\"TAGS_NAME\" TEXT);"); // 3: tags_name
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"TAGS_CATEGORY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, TagsCategory entity) {
        stmt.clearBindings();
 
        Long lid = entity.getLid();
        if (lid != null) {
            stmt.bindLong(1, lid);
        }
        stmt.bindLong(2, entity.getTid());
        stmt.bindLong(3, entity.getType_code());
 
        String tags_name = entity.getTags_name();
        if (tags_name != null) {
            stmt.bindString(4, tags_name);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, TagsCategory entity) {
        stmt.clearBindings();
 
        Long lid = entity.getLid();
        if (lid != null) {
            stmt.bindLong(1, lid);
        }
        stmt.bindLong(2, entity.getTid());
        stmt.bindLong(3, entity.getType_code());
 
        String tags_name = entity.getTags_name();
        if (tags_name != null) {
            stmt.bindString(4, tags_name);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public TagsCategory readEntity(Cursor cursor, int offset) {
        TagsCategory entity = new TagsCategory( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // lid
            cursor.getInt(offset + 1), // tid
            cursor.getInt(offset + 2), // type_code
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3) // tags_name
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, TagsCategory entity, int offset) {
        entity.setLid(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setTid(cursor.getInt(offset + 1));
        entity.setType_code(cursor.getInt(offset + 2));
        entity.setTags_name(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(TagsCategory entity, long rowId) {
        entity.setLid(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(TagsCategory entity) {
        if(entity != null) {
            return entity.getLid();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(TagsCategory entity) {
        return entity.getLid() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
