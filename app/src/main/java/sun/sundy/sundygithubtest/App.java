package sun.sundy.sundygithubtest;

import com.alipay.iot.sdk.APIManager;
import com.alipay.iot.sdk.InitFinishCallback;
import com.qihoo360.replugin.RePluginApplication;

import sun.sundy.sundygithubtest.utils.SpeechSoundManager;
import sun.sundy.sundygithubtest.utils.ToastUtils;


/**
 * Created by sundi on 2017/11/17.
 */
public class App extends RePluginApplication {

    public static App app;

//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//        RePlugin.App.attachBaseContext(this);
//    }

    @Override
    public void onCreate() {
        super.onCreate();
//        RePlugin.App.onCreate();
        initApp(this);

//        try {
//            SDKManager.initSDK(this);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

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
    }

//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//
//        /* Not need to be called if your application's minSdkVersion > = 14 */
//        RePlugin.App.onLowMemory();
//    }
//
//    @Override
//    public void onTrimMemory(int level) {
//        super.onTrimMemory(level);
//        /* Not need to be called if your application's minSdkVersion > = 14 */
//        RePlugin.App.onTrimMemory(level);
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration config) {
//        super.onConfigurationChanged(config);
//        /* Not need to be called if your application's minSdkVersion > = 14 */
//        RePlugin.App.onConfigurationChanged(config);
//    }

    private void initApp(App application) {
        app = application;
    }

    public static App getInstance() {
        return app;
    }
}
