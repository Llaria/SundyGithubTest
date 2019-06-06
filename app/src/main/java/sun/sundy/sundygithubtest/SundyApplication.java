package sun.sundy.sundygithubtest;

import android.app.Application;

import com.alipay.iot.sdk.APIManager;
import com.alipay.iot.sdk.InitFinishCallback;

import org.greenrobot.greendao.database.Database;

import java.util.List;

import sun.sundy.sundygithubtest.sql.entity.UserEntity;
import sun.sundy.sundygithubtest.sql.gen.DaoMaster;
import sun.sundy.sundygithubtest.sql.gen.DaoSession;
import sun.sundy.sundygithubtest.sql.gen.UserEntityDao;
import sun.sundy.sundygithubtest.sql.utils.BizDaoHelper;
import sun.sundy.sundygithubtest.sql.utils.BizDaoManager;
import sun.sundy.sundygithubtest.utils.FileUtil;
import sun.sundy.sundygithubtest.utils.SpeechSoundManager;
import sun.sundy.sundygithubtest.utils.ToastUtils;
import sun.sundy.sundygithubtest.utils.WeightPreference;


/**
 * Created by sundi on 2017/11/17.
 */

public class SundyApplication extends Application {

    public static SundyApplication app;

    @Override
    public void onCreate() {
        super.onCreate();
        initApp(this);

        if (!SpeechSoundManager.getInstance().initSpeechService())
            ToastUtils.showLazzToast("请确认是否安装讯飞语音+");

        try {
            APIManager.getInstance().initialize(app, "2088511271580123", new InitFinishCallback() {
                @Override
                public void initFinished(boolean b) {
                    if (b){
                        ToastUtils.showToast("成功");
                    }else {
                        ToastUtils.showToast("失败");
                    }
                }
            });
        } catch (APIManager.APIInitException e) {
            e.printStackTrace();
        }
        if (WeightPreference.getInstance(app).isFirstIn()){
            String dbPath = FileUtil.SDPATH + BizDaoHelper.DB_NAME;
            Database database = new BizDaoHelper(app,dbPath).getWritableDb();
            DaoMaster daoMaster = new DaoMaster(database);
            DaoSession daoSession = daoMaster.newSession();
            UserEntityDao userEntityDao = daoSession.getUserEntityDao();
            List<UserEntity> datas = userEntityDao.loadAll();
            daoSession.clear();
            database.close();
            FileUtil.deleteFile(dbPath);
            BizDaoManager.getInstance().init();
            for (int i = 0; i < datas.size(); i++) {
                UserEntity userEntity = new UserEntity();
                userEntity.setName(datas.get(i).getName());
                userEntity.setAge(datas.get(i).getAge());
                UserEntityDao userEntityDao1 = BizDaoManager.getInstance().getDaoSession().getUserEntityDao();
                userEntityDao1.insert(userEntity);
            }
        }else {
            BizDaoManager.getInstance().init();
        }
    }

    private void initApp(SundyApplication application) {
        app = application;
    }

    public static SundyApplication getInstance() {
        return app;
    }
}
