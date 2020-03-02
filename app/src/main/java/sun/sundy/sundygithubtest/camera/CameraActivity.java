package sun.sundy.sundygithubtest.camera;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sun.sundy.sundygithubtest.R;
import sun.sundy.sundygithubtest.utils.ToastUtils;

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback{

    private SurfaceView surfaceView;
    private Button btnTake,btnFoucs;
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private int useWidth;
    private int useHeight;
    private static int mOrientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        surfaceView = findViewById(R.id.sv_show);
        btnTake = findViewById(R.id.btn_take);
        btnFoucs = findViewById(R.id.btn_focus);

        // 普通使用
//        startActivity(new Intent(MediaStore.ACTION_IMAGE_CAPTURE));

        System.out.println("====是否拥有摄像头" + getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA));
        System.out.println("====摄像头数目"  + Camera.getNumberOfCameras());


        initSurfaceView();
        initCamera();

        btnFoucs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        if (success){
                            ToastUtils.showLazzToast("对焦成功");
                        }
                    }
                });
            }
        });

        btnTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        // 获取Jpeg图片，并保存在sd卡上
                        String path = Environment.getExternalStorageDirectory()
                                .getPath()  +"/focus/";
                        File pathDir = new File(path);
                        if (!pathDir.exists()){
                            pathDir.mkdir();
                        }
                        File pictureFile = new File(path+ "focusdemo.jpg");
                        if (pictureFile.exists()){
                            pictureFile.delete();
                        }
                        try {
                            FileOutputStream fos = new FileOutputStream(pictureFile);
                            fos.write(data);
                            fos.close();
                        } catch (Exception e) {

                        }
                        camera.startPreview();
                    }
                });
            }
        });


    }

    private void initSurfaceView() {
        surfaceView.setKeepScreenOn(true);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) surfaceView.getLayoutParams();
        layoutParams.width = displayMetrics.widthPixels;
        layoutParams.height = displayMetrics.heightPixels * 4 / 3;
        useWidth = layoutParams.width;
        useHeight = layoutParams.height;
        surfaceView.setLayoutParams(layoutParams);
    }

    private void initCamera() {

        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);

        if (null != camera){
            try {
                camera.setPreviewDisplay(surfaceHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            camera.setOneShotPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {

                }
            });

            Camera.Parameters parameters = camera.getParameters();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            camera.setParameters(parameters);
            calculateCameraPreviewOrientation(this);
            Camera.Size tempSize = setPreviewSize(camera, useHeight,useWidth);
            {
                //此处可以处理，获取到tempSize，如果tempSize和设置的SurfaceView的宽高冲突，重新设置SurfaceView的宽高
            }

            setPictureSize(camera,  useHeight,useWidth);
            camera.setDisplayOrientation(mOrientation);
            int degree = calculateCameraPreviewOrientation(CameraActivity.this);
            camera.setDisplayOrientation(degree);
            camera.startPreview();


        }
    }


    private void setPictureSize(Camera camera ,int expectWidth,int expectHeight){
        Camera.Parameters parameters = camera.getParameters();
        Point point = new Point(expectWidth, expectHeight);
        Camera.Size size = findProperSize(point,parameters.getSupportedPreviewSizes());
        parameters.setPictureSize(size.width, size.height);
        camera.setParameters(parameters);
    }

    private Camera.Size setPreviewSize(Camera camera, int expectWidth, int expectHeight) {
        Camera.Parameters parameters = camera.getParameters();
        Point point = new Point(expectWidth, expectHeight);
        Camera.Size size = findProperSize(point,parameters.getSupportedPictureSizes());
        parameters.setPictureSize(size.width, size.height);
        camera.setParameters(parameters);
        return size;
    }


    /**
     * 找出最合适的尺寸，规则如下：
     * 1.将尺寸按比例分组，找出比例最接近屏幕比例的尺寸组
     * 2.在比例最接近的尺寸组中找出最接近屏幕尺寸且大于屏幕尺寸的尺寸
     * 3.如果没有找到，则忽略2中第二个条件再找一遍，应该是最合适的尺寸了
     */
    private static Camera.Size findProperSize(Point surfaceSize, List<Camera.Size> sizeList) {
        if (surfaceSize.x <= 0 || surfaceSize.y <= 0 || sizeList == null) {
            return null;
        }

        int surfaceWidth = surfaceSize.x;
        int surfaceHeight = surfaceSize.y;

        List<List<Camera.Size>> ratioListList = new ArrayList<>();
        for (Camera.Size size : sizeList) {
            addRatioList(ratioListList, size);
        }

        final float surfaceRatio = (float) surfaceWidth / surfaceHeight;
        List<Camera.Size> bestRatioList = null;
        float ratioDiff = Float.MAX_VALUE;
        for (List<Camera.Size> ratioList : ratioListList) {
            float ratio = (float) ratioList.get(0).width / ratioList.get(0).height;
            float newRatioDiff = Math.abs(ratio - surfaceRatio);
            if (newRatioDiff < ratioDiff) {
                bestRatioList = ratioList;
                ratioDiff = newRatioDiff;
            }
        }

        Camera.Size bestSize = null;
        int diff = Integer.MAX_VALUE;
        assert bestRatioList != null;
        for (Camera.Size size : bestRatioList) {
            int newDiff = Math.abs(size.width - surfaceWidth) + Math.abs(size.height - surfaceHeight);
            if (size.height >= surfaceHeight && newDiff < diff) {
                bestSize = size;
                diff = newDiff;
            }
        }

        if (bestSize != null) {
            return bestSize;
        }

        diff = Integer.MAX_VALUE;
        for (Camera.Size size : bestRatioList) {
            int newDiff = Math.abs(size.width - surfaceWidth) + Math.abs(size.height - surfaceHeight);
            if (newDiff < diff) {
                bestSize = size;
                diff = newDiff;
            }
        }

        return bestSize;
    }

    private static void addRatioList(List<List<Camera.Size>> ratioListList, Camera.Size size) {
        float ratio = (float) size.width / size.height;
        for (List<Camera.Size> ratioList : ratioListList) {
            float mine = (float) ratioList.get(0).width / ratioList.get(0).height;
            if (ratio == mine) {
                ratioList.add(size);
                return;
            }
        }

        List<Camera.Size> ratioList = new ArrayList<>();
        ratioList.add(size);
        ratioListList.add(ratioList);
    }
    /**
     * 排序
     * @param list
     */
    private static void sortList(List<Camera.Size> list) {
        Collections.sort(list, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size pre, Camera.Size after) {
                if (pre.width > after.width) {
                    return 1;
                } else if (pre.width < after.width) {
                    return -1;
                }
                return 0;
            }
        });
    }

    /**
     * 设置预览角度，setDisplayOrientation本身只能改变预览的角度
     * previewFrameCallback以及拍摄出来的照片是不会发生改变的，拍摄出来的照片角度依旧不正常的
     * 拍摄的照片需要自行处理
     * 这里Nexus5X的相机简直没法吐槽，后置摄像头倒置了，切换摄像头之后就出现问题了。
     * @param activity
     */
    public static int calculateCameraPreviewOrientation(Activity activity) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        mOrientation = result;
        System.out.println("=========orienttaion============="+result);
        return result;
    }


    @Override
    protected void onResume() {
        super.onResume();
        camera.startPreview();
    }

    @Override
    protected void onStop() {
        super.onStop();
        camera.stopPreview();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
    }

    private  void handleFocus(MotionEvent event, Camera camera) {
        int viewWidth = useWidth;
        int viewHeight = useHeight;
        Rect focusRect = calculateTapArea(event.getX(), event.getY(),  viewWidth, viewHeight,1.0f);

        //一定要首先取消
        camera.cancelAutoFocus();
        Camera.Parameters params = camera.getParameters();
        if (params.getMaxNumFocusAreas() > 0) {
            List<Camera.Area> focusAreas = new ArrayList<>();
            focusAreas.add(new Camera.Area(focusRect, 800));
            params.setFocusAreas(focusAreas);
        } else {
            //focus areas not supported
        }
        //首先保存原来的对焦模式，然后设置为macro，对焦回调后设置为保存的对焦模式
        final String currentFocusMode = params.getFocusMode();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
        camera.setParameters(params);

        camera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                //回调后 还原模式
                Camera.Parameters params = camera.getParameters();
                params.setFocusMode(currentFocusMode);
                camera.setParameters(params);
                if(success){
                    Toast.makeText(CameraActivity.this,"对焦区域对焦成功",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * 转换对焦区域
     * 范围(-1000, -1000, 1000, 1000)
     */
    private Rect calculateTapArea(float x, float y, int width, int height, float coefficient) {
        float focusAreaSize = 200;
        int areaSize = (int) (focusAreaSize * coefficient);
        int surfaceWidth = width;
        int surfaceHeight = height;
        int centerX = (int) (x / surfaceHeight * 2000 - 1000);
        int centerY = (int) (y / surfaceWidth * 2000 - 1000);
        int left = clamp(centerX - (areaSize / 2), -1000, 1000);
        int top = clamp(centerY - (areaSize / 2), -1000, 1000);
        int right = clamp(left + areaSize, -1000, 1000);
        int bottom = clamp(top + areaSize, -1000, 1000);
        return new Rect(left, top, right, bottom);
    }

    //不大于最大值，不小于最小值
    private  int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

}
