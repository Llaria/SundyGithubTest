package sun.sundy.sundygithubtest;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import sun.sundy.sundygithubtest.utils.SpeechSoundManager;

public class MainActivity extends AppCompatActivity {

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.et_speak);
    }

    // 顶尖秤测试
    public void aclas_test(View view) {
//        startActivity(new Intent(this, AclasTestActivity.class));
        Intent intent = new Intent(Intent.ACTION_MAIN);
        //前提：知道要跳转应用的包名、类名
        ComponentName componentName = new ComponentName("com.iflytek.speechcloud", "com.iflytek.speechcloud.activity.setting.speaker.SpeakerSetting");
        intent.setComponent(componentName);
        startActivity(intent);
    }

    public void speak_test(View view) {
        SpeechSoundManager.getInstance().startSpeech(editText.getText().toString());
    }
}
