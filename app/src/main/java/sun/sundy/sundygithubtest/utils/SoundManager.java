package sun.sundy.sundygithubtest.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.SparseArray;

import sun.sundy.sundygithubtest.R;
import sun.sundy.sundygithubtest.SundyApplication;

/**
 * @author Sundy
 * 声音管理
 * create at 2019-10-29 15:51
 */
public class SoundManager {
    
    public static final int SCAN_ABNORMAL = 1;
    public static final int SCAN_NORMAL = 2;
    public static final int PHOTO = 3;
    public static final int WRONG = 4;
    public static final int WARN = 5;
    public static final int UPLOAD_FAILURE = 6;
    public static final int PIC_UPLOAD_FAILURE = 7;
    public static final int TOUCH_KEY = 8;
    
    private Context context;
    private static SoundManager soundManager;
    private SoundPool sp;

    @SuppressLint("UseSparseArrays")
    private SparseArray<Integer> spMap = new SparseArray<Integer>();

    private SoundManager(Context context) {
        this.context = context;
        initSoundPool();
    }

    public synchronized static SoundManager getInstance() {
        if (soundManager == null) {
            soundManager = new SoundManager(SundyApplication.getInstance());
        }
        return soundManager;
    }

    /**
     * 初始化声音池
     */
    public void initSoundPool() {
        try {
            sp = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
            spMap.put(SCAN_NORMAL, sp.load(context, R.raw.beep, 1));
            spMap.put(SCAN_ABNORMAL, sp.load(context, R.raw.scan_abnormal, 1));
            spMap.put(PHOTO, sp.load(context, R.raw.photo, 1));
            spMap.put(WRONG, sp.load(context, R.raw.repeat_scan_abnormal, 1));
            spMap.put(WARN, sp.load(context, R.raw.warn_sound, 1));
            spMap.put(UPLOAD_FAILURE, sp.load(context, R.raw.upload_failure, 1));
            spMap.put(PIC_UPLOAD_FAILURE, sp.load(context, R.raw.pic_upload_failure, 1));
            spMap.put(TOUCH_KEY, sp.load(context, R.raw.key, 1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放扫描成功声音
     */
    public void success() {
        playSound(SCAN_NORMAL, 1);
    }
    
    /**
     * 播放扫描失败声音
     */
    public void failure() {
        playSound(SCAN_ABNORMAL, 1);
    }

    /**
     * 拍照声音
     */
    public void takePhoto() {
        playSound(PHOTO, 1);
    }

    /**
     * 异常提示音
     */
    public void warn() {
        playSound(WARN, 1);
    }

    /**
     * 报错提示音
     */
    public void wrong() {
        playSound(WRONG, 1);
    }

    /**
     * 数据上传失败提示音
     */
    public void uploadFailure() {
        playSound(UPLOAD_FAILURE, 1);
    }
    
    /**
     * 数据上传失败提示音
     * @param count 播放次数
     */
    public void uploadFailure(int count) {
        playSound(UPLOAD_FAILURE, count);
    }

    /**
     * 图片上传失败提示音
     */
    public void picUploadFailure() {
        playSound(PIC_UPLOAD_FAILURE, 1);
    }
    
    /**
     * 图片上传失败提示音
     * @param count 播放次数
     */
    public void picUploadFailure(int count) {
        playSound(PIC_UPLOAD_FAILURE, count);
    }

    /**
     * 触摸屏声音
     */
    public void playKeyAndTouchSound() {
        playSound(TOUCH_KEY, 1); // 播放声音
    }

    /**
     * @param rawId 在raw文件里面的声音Id(R.raw.id)
     *              播放raw目录下的自定义声音文件
     *              注意：需要先调用initCustom方法
     */
    public void playCustomSound(int rawId, int playCount) {
        playSound(rawId, playCount);
    }

    public void initCustomSound(int rawId) {
        if (spMap.get(rawId) == null) {
            spMap.put(rawId, sp.load(context, rawId, 1));
        }
    }

    /**
     * 播放声音,调用该方法前,需要先调用initSoundPool()方法,否则会异常
     * 
     * @param soundKey
     *            ：保存声音ID的key,这里为SCAN_ABNORMAL(扫描失败)、SCAN_NORMAL(扫描成功)
     * @param playCount 播放次数
     */
    private void playSound(int soundKey, int playCount) {
        if (playCount < 1)
            playCount = 1;
        AudioManager am = (AudioManager) context
                .getSystemService(Context.AUDIO_SERVICE);
        float maxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float currentVolumn = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        float ratioVolumn = currentVolumn / maxVolumn;
        // 第一个参数:待播放的音频ID,二三参数：左右声道,第四个参数：优先级,第五个：循环次数,第六个：回放速度,值在0.5-2.0之间,1为正常速度
        sp.play(spMap.get(soundKey), ratioVolumn, ratioVolumn, 1, playCount - 1, 1f);
    }

    /**
     * 释放资源
     */
    public void release() {
        if (sp != null) {
            sp.release();
        }
    }
}
