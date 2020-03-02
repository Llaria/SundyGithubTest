package sun.sundy.sundygithubtest.service;


import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import sun.sundy.sundygithubtest.autoclick.AutoClickActivity;
import sun.sundy.sundygithubtest.autoclick.AutoTouch;
import sun.sundy.sundygithubtest.utils.ToastUtils;


public class AutoClickService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(1, new Notification());
        ToastUtils.showLazzToast("服务开启");
                        for (int i = 0; i < 100; i++) {
//                    AutoTouch autoTouch = new AutoTouch();
//                    autoTouch.autoClick(300, 300);
                }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new ChuangXinServiceBinder();
    }

    public class ChuangXinServiceBinder extends Binder {
        public AutoClickService getService() {
            return AutoClickService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }
}
