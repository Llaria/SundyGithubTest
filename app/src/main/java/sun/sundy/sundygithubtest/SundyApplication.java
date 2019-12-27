package sun.sundy.sundygithubtest;

import android.app.Application;
import android.util.Log;

import com.alipay.iot.sdk.APIManager;
import com.alipay.iot.sdk.InitFinishCallback;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;

import sun.sundy.sundygithubtest.utils.SoundManager;
import sun.sundy.sundygithubtest.utils.SpeechSoundManager;
import sun.sundy.sundygithubtest.utils.ToastUtils;


/**
 * Created by sundi on 2017/11/17.
 */

public class SundyApplication extends Application {

    public static SundyApplication app;

    @Override
    public void onCreate() {
        super.onCreate();
        initApp(this);
        SoundManager.getInstance().initSoundPool();
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
            Log.d("dd", "onCreate: ");
        }



        HashMap<DecodeHintType, Object> decodeHints = new HashMap();
        Collection<BarcodeFormat> decodeFormats = EnumSet.noneOf(BarcodeFormat.class);
        decodeFormats.addAll(EnumSet.of(BarcodeFormat.EAN_13));
        decodeFormats.addAll(EnumSet.of(BarcodeFormat.EAN_8));
        decodeFormats.addAll(EnumSet.of(BarcodeFormat.CODE_128));
        decodeFormats.addAll(EnumSet.of(BarcodeFormat.QR_CODE));
        decodeHints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        decodeHints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
        decodeHints.put(DecodeHintType.TRY_HARDER, 1);
        //是否先拿原图直接通过zxing进行识别
//        BScan.TRY_SPEED = false;
//        BScan.decodeHints = decodeHints;
//        BScan.init(this);


//        if (WeightPreference.getInstance(app).isFirstIn()){
//            String dbPath = FileUtil.SDPATH + BizDaoHelper.DB_NAME;
//            Database database = new BizDaoHelper(app,dbPath).getWritableDb();
//            DaoMaster daoMaster = new DaoMaster(database);
//            DaoSession daoSession = daoMaster.newSession();
//            UserEntityDao userEntityDao = daoSession.getUserEntityDao();
//            List<UserEntity> datas = userEntityDao.loadAll();
//            daoSession.clear();
//            database.close();
//            FileUtil.deleteFile(dbPath);
//            BizDaoManager.getInstance().init();
//            for (int i = 0; i < datas.size(); i++) {
//                UserEntity userEntity = new UserEntity();
//                userEntity.setName(datas.get(i).getName());
//                userEntity.setAge(datas.get(i).getAge());
//                UserEntityDao userEntityDao1 = BizDaoManager.getInstance().getDaoSession().getUserEntityDao();
//                userEntityDao1.insert(userEntity);
//            }
//        }else {
//            BizDaoManager.getInstance().init();
//        }
    }

    private void initApp(SundyApplication application) {
        app = application;
    }

    public static SundyApplication getInstance() {
        return app;
    }
}
