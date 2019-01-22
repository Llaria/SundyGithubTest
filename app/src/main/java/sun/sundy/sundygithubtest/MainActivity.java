package sun.sundy.sundygithubtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import sun.sundy.sundygithubtest.utils.SpeechSoundManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // 顶尖秤测试
    public void aclas_test(View view) {
        startActivity(new Intent(this, AclasTestActivity.class));
    }

    public void speak_test(View view) {
        SpeechSoundManager.getInstance().startSpeech("支付宝到账123.11元");
    }
}
