package sun.sundy.sundygithubtest.sql.utils;


import org.greenrobot.greendao.database.Database;

import sun.sundy.sundygithubtest.BuildConfig;
import sun.sundy.sundygithubtest.SundyApplication;
import sun.sundy.sundygithubtest.sql.gen.DaoMaster;
import sun.sundy.sundygithubtest.sql.gen.DaoSession;
import sun.sundy.sundygithubtest.utils.FileUtil;


/**
 * 下载的数据库管理类
 */
public class BizDaoManager {
    private static final String PWD = "dianjia123";

    private DaoMaster master;
    private DaoSession daoSession;

    private static BizDaoManager ourInstance = new BizDaoManager();

    public static BizDaoManager getInstance() {
        return ourInstance;
    }

    private BizDaoManager() {
    }

    public Database init() {
        return getDaoSession().getDatabase();
    }

    public DaoSession getDaoSession() {
        if (daoSession != null) {
            return daoSession;
        }
        if (master == null) {
            master = getDaoMaster();
        }
        daoSession = master.newSession();
        return daoSession;
    }

    private DaoMaster getDaoMaster() {
        if (master != null)
            return master;
        Database db;
        if (BuildConfig.DEBUG) {
            String dbPath = FileUtil.SDPATH + BizDaoHelper.DB_NAME;
            //DEBUG数据库不加密
//                db = new BizDaoHelper(SundyApplication.getInstance(), dbPath).getWritableDb();
            db = new BizDaoHelper(SundyApplication.getInstance(), dbPath).getEncryptedWritableDb(PWD);
        } else {
            //发版本时用下面的方法创建带密码的数据库
            db = new BizDaoHelper(SundyApplication.getInstance()).getEncryptedWritableDb(PWD);
        }
        master = new DaoMaster(db);
        return master;
    }

    private Database getSQLiteDatabase() {
        return daoSession.getDatabase();
    }

    public void close() {
        if (daoSession != null) {
            daoSession.clear();
        }
        Database database = getSQLiteDatabase();
        if (database != null) {
            database.close();
        }
        if (null != daoSession){
            daoSession = null;
        }
        if (null != master){
            master = null;
        }
    }
}
