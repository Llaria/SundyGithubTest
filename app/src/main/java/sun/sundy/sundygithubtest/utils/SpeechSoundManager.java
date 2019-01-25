package sun.sundy.sundygithubtest.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.iflytek.speech.ISpeechModule;
import com.iflytek.speech.InitListener;
import com.iflytek.speech.SpeechConstant;
import com.iflytek.speech.SpeechSynthesizer;
import com.iflytek.speech.SpeechUtility;
import com.iflytek.speech.SynthesizerListener;

import static sun.sundy.sundygithubtest.App.app;

/**
 * @author Sundy
 * 讯飞语音管理类
 * create at 2019/1/22 17:14
 */
public class SpeechSoundManager {
    private static final String PREFER_NAME = "com.iflytek.setting";
    private static String TAG = "SpeechSoundManager";
    private static SpeechSoundManager mSpeechManager;
    // 语音合成对象
    private SpeechSynthesizer mTts;
    private SharedPreferences mSharedPreferences;

    public static SpeechSoundManager getInstance() {
        if (mSpeechManager == null) {
            mSpeechManager = new SpeechSoundManager();
        }
        return mSpeechManager;
    }

    /**
     * 检查是否安装【讯飞语音+】并获取TTS对象
     * @return 没有安装【讯飞语音+】返回false 安装了返回true
     */
    public boolean initSpeechService() {
        if (mSpeechManager != null)
            mSpeechManager.destroy();
        if (checkApkExist()) {
            getTTS();
        } else {
            Log.e(TAG, "initSpeechService: 讯飞语音+ 未安装");
            return false;
        }
        return true;
    }

    private void getTTS() {
        SpeechUtility.getUtility(app).setAppid("");
        mSharedPreferences = app.getSharedPreferences(PREFER_NAME, Activity.MODE_PRIVATE);
        // 初始化合成对象
        mTts = new SpeechSynthesizer(app, mTtsInitListener);
    }

    /**
     * 开始语音播报播报之前必须先调用initSpeechService
     * @param speechStr 播报的内容
     */
    public void startSpeech(String speechStr) {
        if (mTts != null && speechStr != null && speechStr.length() > 0) {
            setParam();
            // 设置参数
            mTts.startSpeaking(speechStr, mTtsListener);
            Toast.makeText(app, speechStr, Toast.LENGTH_LONG).show();
        }else {
            Log.e(TAG, "startSpeech: 未初始化成功或文字为空！");
        }
    }

    /**
     * 语音播报参数设置
     * 可以设置播报人员,播报语速、音调、音量
     */
    private void setParam() {
        mTts.setParameter(SpeechConstant.ENGINE_TYPE,
                mSharedPreferences.getString("engine_preference", "local"));
        // 设置发音人
        mTts.setParameter(SpeechSynthesizer.VOICE_NAME,"xiaoyan");
        // 设置语速
        mTts.setParameter(SpeechSynthesizer.SPEED,
                mSharedPreferences.getString("speed_preference", "40"));
        // 设置音调
        mTts.setParameter(SpeechSynthesizer.PITCH,
                mSharedPreferences.getString("pitch_preference", "50"));
        // 设置音量
        mTts.setParameter(SpeechSynthesizer.VOLUME,
                mSharedPreferences.getString("volume_preference", "100"));
    }

    /**
     * 初始化监听
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(ISpeechModule arg0, int code) {
            Log.d(TAG, "InitListener init() code = " + code);
        }
    };

    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener.Stub() {
        @Override
        public void onBufferProgress(int progress) {
            Log.d(TAG, "onBufferProgress :" + progress);
        }

        @Override
        public void onCompleted(int code) {
            Log.d(TAG, "onCompleted code =" + code);
        }

        @Override
        public void onSpeakBegin() {
            Log.d(TAG, "onSpeakBegin");
        }

        @Override
        public void onSpeakPaused() {
            Log.d(TAG, "onSpeakPaused.");
        }

        @Override
        public void onSpeakProgress(int progress) {
            Log.d(TAG, "onSpeakProgress :" + progress);
        }

        @Override
        public void onSpeakResumed() {
            Log.d(TAG, "onSpeakResumed.");
        }
    };

    /**
     * 判断手机中是否安装了讯飞语音+
     */
    private boolean checkApkExist() {
        try {
            app.getPackageManager().getApplicationInfo("com.iflytek.speechcloud", PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 释放语音播报资源
     */
    private void destroy() {
        if (mTts != null) {
            mTts.stopSpeaking(mTtsListener);
            mTts.destory();
            mTts = null;
        }
    }
}
