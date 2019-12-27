package sun.sundy.sundygithubtest.autoclick;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.File;

import sun.sundy.sundygithubtest.R;
import sun.sundy.sundygithubtest.utils.ToastUtils;

public class AutoClickActivity extends AppCompatActivity {

    private Button btnTest, btnStart;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_click);
        btnTest = findViewById(R.id.btn_test);
        btnStart = findViewById(R.id.btn_start);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                ToastUtils.showLazzToast("点击了第" + count + "次");
            }
        });

        adb();


        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = 0;
                for (int i = 0; i < 10; i++) {
                    AutoTouch autoTouch = new AutoTouch();
                    autoTouch.autoClick(AutoClickActivity.this, 300, 300);
                }
            }
        });

    }

    public void adb() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    String[] order1 = { "export TMPDIR='/sdcard'"};
//                    new ProcessBuilder(order1).start();
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("========" + e.getMessage());
                }
                String[] order = {"connect", "10.67.151.119",};
                try {
                    new ProcessBuilder(order).start();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("========" + e.getMessage());
                }
            }
        }).start();
    }

    public static void chmod(File file) {
        try {
            String command = "chmod 777 " + file.getAbsolutePath();
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(command);
            process.destroy();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void chmodBest(File file) {
        try {
            String command = "chmod 777 " + file.getAbsolutePath();
            ProcessBuilder process = new ProcessBuilder(command);
            process.start();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        System.out.println("============" + ev.toString());
        return super.dispatchTouchEvent(ev);
    }
}
