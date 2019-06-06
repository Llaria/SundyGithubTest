package sun.sundy.sundygithubtest.sql.utils;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import sun.sundy.sundygithubtest.sql.gen.CodeEntityDao;
import sun.sundy.sundygithubtest.sql.gen.DaoMaster;
import sun.sundy.sundygithubtest.sql.gen.UserEntityDao;


public class BizDaoHelper extends DaoMaster.OpenHelper {

    public static final String DB_NAME = "sundy.db";

    public BizDaoHelper(Context context) {
        super(context, DB_NAME, null);
    }

    public BizDaoHelper(Context context, String dbPath) {
        super(context, dbPath, null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        MigrationHelper.migrate(db,
                UserEntityDao.class, CodeEntityDao.class);
    }

}
