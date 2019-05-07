package sun.sundy.sundygithubtest;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alipay.iot.sdk.APIManager;
import com.alipay.iot.sdk.sound.FetchSoundBankCallback;
import com.alipay.iot.sdk.sound.SoundBank;

import sun.sundy.sundygithubtest.network.OkHttp3Activity;
import sun.sundy.sundygithubtest.utils.SpeechSoundManager;
import sun.sundy.sundygithubtest.utils.ToastUtils;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private TextView tvTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.et_speak);
        tvTest = findViewById(R.id.tv_test);
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
}
