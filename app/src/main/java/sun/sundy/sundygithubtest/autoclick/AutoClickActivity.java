package sun.sundy.sundygithubtest.autoclick;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;

import sun.sundy.sundygithubtest.R;

public class AutoClickActivity extends AppCompatActivity {

    private Button btnTest, btnStart;
    private int count;
    private ImageView ivPrint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_click);
        btnTest = findViewById(R.id.btn_test);
        btnStart = findViewById(R.id.btn_start);
        ivPrint = findViewById(R.id.iv_print);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                count++;
//                ToastUtils.showLazzToast("点击了第" + count + "次");
                startActivity(new Intent(AutoClickActivity.this,OcrActivity.class));
            }
        });

//        adb();


        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View decorView = getWindow().getDecorView();
                decorView.setDrawingCacheEnabled(true);
                v.buildDrawingCache();//获取屏幕整张图片
                Bitmap bitmap = decorView.getDrawingCache();
                System.out.println("====》》宽度：" + bitmap.getWidth() + "，高度：" +  bitmap.getHeight());

                TessBaseAPI tessBaseAPI = new TessBaseAPI();
//                tessBaseAPI.init(FileUtil.SDPATH, DEFAULT_LANGUAGE);
                tessBaseAPI.setImage(bitmap);
                btnTest.setText("识别结果：" + "\n" + tessBaseAPI.getUTF8Text());




                ivPrint.setImageBitmap(bitmap);

//                count = 0;
//                for (int i = 0; i < 10; i++) {
//                    AutoTouch autoTouch = new AutoTouch();
//                    autoTouch.autoClick(AutoClickActivity.this, 300, 300);
//                }

//                ivPrint.setImageBitmap(getBitmap(v));
            }
        });

    }

    /**
     *
     * @param view 需要截取图片的view
     * @return 截图
     */
    private Bitmap getBitmap(View view) {

        View screenView = getWindow().getDecorView();
        screenView.setDrawingCacheEnabled(true);
        screenView.buildDrawingCache();

        //获取屏幕整张图片
        Bitmap bitmap = screenView.getDrawingCache();

        if (bitmap != null) {

            //需要截取的长和宽
            int outWidth = view.getWidth();
            int outHeight = view.getHeight();

            //获取需要截图部分的在屏幕上的坐标(view的左上角坐标）
            int[] viewLocationArray = new int[2];
            view.getLocationOnScreen(viewLocationArray);

            //从屏幕整张图片中截取指定区域
            bitmap = Bitmap.createBitmap(bitmap, viewLocationArray[0], viewLocationArray[1], outWidth, outHeight);

        }

        return bitmap;
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
