package sun.sundy.sundygithubtest.autoclick;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import sun.sundy.sundygithubtest.R;
import sun.sundy.sundygithubtest.utils.SDUtils;
import sun.sundy.sundygithubtest.utils.ToastUtils;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class OcrActivity extends AppCompatActivity implements View.OnClickListener {
    //TessBaseAPI初始化用到的第一个参数，是个目录
    private static final String DATAPATH = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + File.separator;
    //在DATAPATH中新建这个目录，TessBaseAPI初始化要求必须有这个目录
    private static final String tessdata = DATAPATH + File.separator + "tessdata";
    //TessBaseAPI初始化测第二个参数，就是识别库的名字不要后缀名。
    private static String DEFAULT_LANGUAGE = "chi_sim";
    //assets中的文件名
    private static String DEFAULT_LANGUAGE_NAME = DEFAULT_LANGUAGE + ".traineddata";
    //保存到SD卡中的完整文件名
    private static String LANGUAGE_PATH = tessdata + File.separator + DEFAULT_LANGUAGE_NAME;

    private static final int PICK_PHOTO = 1;
    private static final int TAKE_PHOTO = 2;
    private static final int CROP_PHOTO = 3;

//    private OpenCVNativeLoader loader = new OpenCVNativeLoader();

    private Button recBtn;
    private TextView resultTv;
    private TextView txtFinal;
    private Button pickBtn;
    private Button takePhoto;
    private ImageView imgView;
    private Spinner spinner;

    private String mFilePath;
    private Uri imageUri;

    private Bitmap srcBitmap;
    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        recBtn = (Button) findViewById(R.id.btn_rec);
        pickBtn = (Button) findViewById(R.id.btn_pick);
        takePhoto = (Button) findViewById(R.id.btn_take);
        resultTv = (TextView) findViewById(R.id.result);
        txtFinal = (TextView) findViewById(R.id.finalResult);
        imgView = (ImageView) findViewById(R.id.img);
        spinner = (Spinner) findViewById(R.id.spinner);

        recBtn.setOnClickListener(this);
        pickBtn.setOnClickListener(this);
        takePhoto.setOnClickListener(this);
        imgView.setVisibility(View.INVISIBLE);

        mFilePath = DATAPATH + "/DCIM/Camera/" + "photo.jpg";

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String array[] = getResources().getStringArray(R.array.trainedData);
                if (position == 0) {
                    DEFAULT_LANGUAGE = array[0];
                } else {
                    DEFAULT_LANGUAGE = array[position];
                }
                DEFAULT_LANGUAGE_NAME = DEFAULT_LANGUAGE + ".traineddata";
                LANGUAGE_PATH = tessdata + File.separator + DEFAULT_LANGUAGE_NAME;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//        loader.init();
        requestPermissions();
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
            }
        }
    }

    //打开相册
    private void openAlbum() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO);
    }

    //启动相机
    private void openCamera() {
        imageUri = Uri.fromFile(new File(mFilePath));
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        Intent intent = new Intent(this, Camera2Activity.class);
        //传递你要保存的图片的路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    //图像处理
    public void proSrc2Gray() {
        Mat rgbMat = new Mat();
        Mat grayMat = new Mat();
        Mat binaryMat = new Mat();
        Mat cannyMat = new Mat();


//        Mat canny = new Mat();

        //获取彩色图像所对应的像素数据
        Utils.bitmapToMat(srcBitmap, rgbMat);
        //图像灰度化,将彩色图像数据转换为灰度图像数据并存储到grayMat中
        Imgproc.cvtColor(rgbMat, grayMat, Imgproc.COLOR_RGB2GRAY);
        //得到边缘图,这里最后两个参数控制着选择边缘的阀值上限和下限
//        Imgproc.Canny(grayMat, cannyMat, 50, 300);
        //二值化
//        Imgproc.threshold(grayMat, binaryMat, 100, 255, Imgproc.THRESH_BINARY);
        //获取自定义核,参数MORPH_RECT表示矩形的卷积核，当然还可以选择椭圆形的、交叉型的
//        Mat strElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
//                new Size(2, 2));
//        //腐蚀
//        Imgproc.dilate(binaryMat,cannyMat,strElement);

//        Imgproc.HoughLinesP(binaryMat,cannyMat,1,);
        //创建一个图像
        mBitmap = Bitmap.createBitmap(grayMat.cols(), grayMat.rows(),
                Bitmap.Config.RGB_565);
        //将矩阵binaryMat转换为图像

        Utils.matToBitmap(grayMat, mBitmap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_PHOTO) {
                imageUri = data.getData();
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(imageUri, "image/*");
                intent.putExtra("crop", true);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, CROP_PHOTO); // 启动裁剪程序
            } else if (requestCode == TAKE_PHOTO) {
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(imageUri, "image/*");
                intent.putExtra("crop", true);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, CROP_PHOTO); // 启动裁剪程序
            } else if (requestCode == CROP_PHOTO) {
                try {
                    srcBitmap = BitmapFactory.decodeStream(getContentResolver().
                            openInputStream(imageUri));
                    proSrc2Gray();
                    saveImage(mBitmap, "photo.jpg");
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                            Uri.fromFile(new File(mFilePath))));
                    if (mBitmap != null) {
                        showPicFileByLuban(mFilePath);
                        imgView.setImageBitmap(mBitmap); // 将裁剪后的照片显示出来
                        imgView.setVisibility(View.VISIBLE);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showPicFileByLuban(String path) {
        Luban.with(this)
                .load(new File(path))
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        // TODO 压缩开始前调用，可以在方法内启动 loading UI
                    }

                    @Override
                    public void onSuccess(File file) {
                        // TODO 压缩成功后调用，返回压缩后的图片文件
                        ToastUtils.showToast("hah");
                        mBitmap = BitmapFactory.decodeFile(file.getPath());
//                        imgUri=Uri.fromFile(file);
//                        txtSize2.setText(file.length() / 1024 + "K");
                        ToastUtils.showToast(file.length() / 1024 + "K");
                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO 当压缩过去出现问题时调用
                    }
                }).launch();//启动压缩
    }

    public void saveImage(Bitmap bitmap, String fileName) {
        File appDir = new File(DATAPATH + "/DCIM/Camera/");
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        File file = new File(DATAPATH + "/DCIM/Camera/", fileName);
        try {
            // 创建一个向指定 File 对象表示的文件中写入数据的文件输出流
            FileOutputStream fos = new FileOutputStream(file);
            //压缩图片,按指定的图片格式以及画质，将图片转换为输出流。
            //quality：画质，0-100.0表示最低画质压缩，100以最高画质压缩,不压缩。
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            //flush()强制将缓冲区中的数据发送出去,不必等到缓冲区满
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_take:
                openCamera();
                break;
            case R.id.btn_pick:
                openAlbum();
                break;
            case R.id.btn_rec:
                if (imgView.getVisibility() != View.VISIBLE) {
                    Toast.makeText(getApplicationContext(), "请先拍照或者选一张图片", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    resultTv.setText("");
                    txtFinal.setText("");
                    try {
                        mBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    recognition(mBitmap);
                }
                break;
        }
    }

    //权限请求返回
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[]
            permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PICK_PHOTO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "你拒绝了权限!", Toast.LENGTH_SHORT).show();
                }
                break;
            case TAKE_PHOTO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Toast.makeText(this, "你拒绝了权限!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private boolean checkTrainedDataExists() {
        File file = new File(LANGUAGE_PATH);
        return file.exists();
    }

    //识别图像
    private void recognition(final Bitmap bitmap) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!checkTrainedDataExists()) {
                    SDUtils.assets2SD(getApplicationContext(), LANGUAGE_PATH, DEFAULT_LANGUAGE_NAME);
                }
                TessBaseAPI tessBaseAPI = new TessBaseAPI();
                tessBaseAPI.setDebug(true);
                tessBaseAPI.init(DATAPATH, DEFAULT_LANGUAGE);
                //识别的图片
                tessBaseAPI.setImage(bitmap);
                //获得识别后的字符串
                String text = "";
                text = "识别结果：" + "\n" + tessBaseAPI.getUTF8Text();
                final String finalText = text;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultTv.setText(finalText);
                        String str = "";
                        for (int i = 0; i < finalText.length(); i++) {
                            if ((finalText.charAt(i) >= 48 && finalText.charAt(i) <= 57) ||
                                    (finalText.charAt(i) >= 65 && finalText.charAt(i) <= 90)) {
                                str += finalText.charAt(i);
                            }
                        }
                        txtFinal.setText(str);
                    }
                });
                tessBaseAPI.end();
            }
        }).start();
    }
}
