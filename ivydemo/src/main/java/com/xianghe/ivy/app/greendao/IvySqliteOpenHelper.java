package com.xianghe.ivy.app.greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.xianghe.ivy.entity.dao.DaoMaster;
import com.xianghe.ivy.entity.dao.MovieTaskCacheDao;
import com.xianghe.ivy.entity.dao.SearchHistoryDao;
import com.xianghe.ivy.entity.dao.TagsCategoryDao;
import com.xianghe.ivy.entity.dao.UploadTaskCacheDao;
import com.xianghe.ivy.utils.KLog;

import org.greenrobot.greendao.database.Database;

import static com.xianghe.ivy.entity.dao.DaoMaster.dropAllTables;

public class IvySqliteOpenHelper extends DaoMaster.OpenHelper {

    public IvySqliteOpenHelper(Context context, String name) {
        super(context, name);
    }

    public IvySqliteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onCreate(Database db) {
        super.onCreate(db);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        KLog.e("IvySqliteOpenHelper", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");

//        if (newVersion > oldVersion) {
//            // 升级、数据库迁移操作
//            MigrationHelper.getInstance().migrate(db,
//                    MovieTaskCacheDao.class,
//                    TagsCategoryDao.class,
//                    UploadTaskCacheDao.class,
//                    SearchHistoryDao.class);
//        }else {
//            // 默认操作
//            dropAllTables(db, true);
//            onCreate(db);
//        }
        dropAllTables(db, true);
        onCreate(db);
    }
}
