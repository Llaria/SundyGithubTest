package sun.sundy.sundygithubtest;

import android.app.Application;

import com.alipay.iot.sdk.APIManager;
import com.alipay.iot.sdk.InitFinishCallback;

import sun.sundy.sundygithubtest.sql.utils.BizDaoManager;
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
        BizDaoManager.getInstance().init();
    }

    private void initApp(SundyApplication application) {
        app = application;
    }

    public static SundyApplication getInstance() {
        return app;
    }
}
