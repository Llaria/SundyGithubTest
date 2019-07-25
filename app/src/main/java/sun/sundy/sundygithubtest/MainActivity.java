package sun.sundy.sundygithubtest;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.alipay.iot.sdk.APIManager;
import com.alipay.iot.sdk.sound.FetchSoundBankCallback;
import com.alipay.iot.sdk.sound.SoundBank;

import sun.sundy.sundygithubtest.network.OkHttp3Activity;
import sun.sundy.sundygithubtest.utils.SpeechSoundManager;
import sun.sundy.sundygithubtest.utils.ToastUtils;
import sun.sundy.sundygithubtest.view.edittext.DeLBackEditText;

public class MainActivity extends AppCompatActivity {

    private DeLBackEditText editText;
    private TextView tvTest;
    private String barcode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.et_speak);
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.d("onKey", "getAction=" + event.getAction()
                        + ";getKeyCode=" + event.getKeyCode()
                        + ";getCharacters=" + event.getCharacters()
                        + ";getScanCode=" + event.getScanCode()
                        + ";getUnicodeChar=" + event.getUnicodeChar()
                        + ";getNumber=" + event.getNumber());
                return false;
            }
        });
        tvTest = findViewById(R.id.tv_test);
    }

//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event){
//        if (event.getAction() == KeyEvent.ACTION_DOWN) {
//            Log.d("MainActivity", "getAction=" + event.getAction()
//                    + ";getKeyCode=" + event.getKeyCode()
//                    + ";getCharacters=" + event.getCharacters()
//                    + ";getScanCode=" + event.getScanCode()
//                    + ";getUnicodeChar=" + event.getUnicodeChar()
//                    + ";getNumber=" + event.getNumber());
//            if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
//                int c = event.getUnicodeChar();
//                if ((c >= 35 && c <= 64) || (c >= 65 && c <=92) ||  (c >= 91 && c <= 127)) {
//                    barcode += (char) c;
//                }
//                ToastUtils.showLazzToast(barcode);
//                editText.setText(barcode);
//                editText.setSelection(barcode.length());
//                barcode = "";
//            } else {
//                int c = event.getUnicodeChar();
//                if ((c >= 35 && c <= 64) || (c >= 65 && c <=92) ||  (c >= 91 && c <= 127)) {
//                    barcode += (char) c;
//                }
//                Log.d("MainActivity", "dispatchKeyEvent: " + barcode + "字符值：" + c);
//            }
//        }
//        return super.dispatchKeyEvent(event);
//    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d("MainActivity", "getAction=" + event.getAction()
                + ";getKeyCode=" + event.getKeyCode()
                + ";getCharacters=" + event.getCharacters()
                + ";getScanCode=" + event.getScanCode()
                + ";getUnicodeChar=" + event.getUnicodeChar()
                + ";getNumber=" + event.getNumber() + "设备类型：" + event.getDevice().getName());
        return super.dispatchKeyEvent(event);
    }

    // 顶尖秤测试
    public void aclas_test(View view) {
//        startActivity(new Intent(this, AclasTestActivity.class));
        if (!SpeechSoundManager.getInstance().initSpeechService())
            ToastUtils.showLazzToast("请确认是否安装讯飞语音+");
    }

    public void speak_test(View view) {
        SpeechSoundManager.getInstance().startSpeech(editText.getText().toString());
    }

    @SuppressLint("SetTextI18n")
    public void alipay_test(View view) {
        APIManager.getInstance().getSoundAPI().fetchSoundBank("default", "isv_pos", new FetchSoundBankCallback() {
            @Override
            public void Finished(SoundBank soundBank) {
                Log.e("TAG", "Finished: " + Thread.currentThread().getName() + soundBank.getEventList().toString());
                soundBank.play("scan_failed");
            }
        });
        tvTest.setText("状态：" + APIManager.getInstance().getDeviceAPI().getDeviceStatus() +
                "id:" + APIManager.getInstance().getDeviceAPI().getDeviceId() +
                "sn:" + APIManager.getInstance().getDeviceAPI().getDeviceSn() + "品牌：" + Build.BRAND + Build.PRODUCT + Build.DEVICE + Build.MODEL);
    }

    public void speaker_download(View view) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        ComponentName componentName = new ComponentName("com.iflytek.speechcloud", "com.iflytek.speechcloud.activity.setting.speaker.SpeakerSetting");
        intent.setComponent(componentName);
        startActivity(intent);
    }

    public void dahua_test(View view) {
        startActivity(new Intent(this, DahuaActivity.class));
    }

    public void okhttp_test(View view) {
        startActivity(new Intent(this, OkHttp3Activity.class));
    }

    public void greendao_test(View view) {
        startActivity(new Intent(this,SqlActivity.class));
    }
}
