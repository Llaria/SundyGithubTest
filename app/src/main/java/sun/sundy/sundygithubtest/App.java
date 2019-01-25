package sun.sundy.sundygithubtest;

import android.app.Application;

import sun.sundy.sundygithubtest.utils.SpeechSoundManager;
import sun.sundy.sundygithubtest.utils.ToastUtils;


/**
 * Created by sundi on 2017/11/17.
 */

public class App extends Application {

    public static App app;

    @Override
    public void onCreate() {
        super.onCreate();
        initApp(this);

        if (!SpeechSoundManager.getInstance().initSpeechService())
            ToastUtils.showLazzToast("请确认是否安装讯飞语音+");

    }

    private void initApp(App application) {
        app = application;
    }

    public static App getInstance() {
        return app;
    }
}
