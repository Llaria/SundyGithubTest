package sun.sundy.sundygithubtest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alipay.iot.sdk.APIManager;
import com.alipay.iot.sdk.sound.FetchSoundBankCallback;
import com.alipay.iot.sdk.sound.SoundBank;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import sun.sundy.sundygithubtest.autoclick.AutoClickActivity;
import sun.sundy.sundygithubtest.camera.CameraActivity;
import sun.sundy.sundygithubtest.design.DesignActivity;
import sun.sundy.sundygithubtest.network.OkHttp3Activity;
import sun.sundy.sundygithubtest.scan.ZbarScanActivity;
import sun.sundy.sundygithubtest.scan.ZxingScanActivity;
import sun.sundy.sundygithubtest.utils.SpeechSoundManager;
import sun.sundy.sundygithubtest.utils.ToastUtils;
import sun.sundy.sundygithubtest.view.edittext.DeLBackEditText;

public class MainActivity extends AppCompatActivity {

    private DeLBackEditText editText;
    private TextView tvTest;
    private ImageView ivTest;
    private String barcode = "";
    private EditText myEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.et_speak);
        ivTest = findViewById(R.id.iv_test);
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

        myEditText = findViewById(R.id.my_edit);
        myEditText.setInputType(InputType.TYPE_NULL);


//        Glide.with(this).load("https://gss0.bdstatic.com/5bVWsj_p_tVS5dKfpU_Y_D3/res/r/image/2017-09-26/352f1d243122cf52462a2e6cdcb5ed6d.png").into(ivTest);


        PermissionGen
                .with(this)
                .addRequestCode(0x13)
                .permissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.RECEIVE_BOOT_COMPLETED,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.MODIFY_AUDIO_SETTINGS,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.CHANGE_WIFI_STATE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.VIBRATE
                )
                .request();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    //权限申请成功
    @PermissionSuccess(requestCode = 0x13)
    public void doSomething() {

    }

    //申请失败
    @PermissionFail(requestCode = 0x13)
    public void doFailSomething() {

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

    public void md_test(View view) {
        startActivity(new Intent(this, DesignActivity.class));
    }

    public void zbar_test(View view) {
        startActivity(new Intent(this, ZbarScanActivity.class));
    }

    public void zxing_test(View view) {
        startActivity(new Intent(this, ZxingScanActivity.class));
    }

    public void camera_test(View view) {
        startActivity(new Intent(this, CameraActivity.class));
    }

    public void auto_click_test(View view) {
        startActivity(new Intent(this, AutoClickActivity.class));
    }
}
