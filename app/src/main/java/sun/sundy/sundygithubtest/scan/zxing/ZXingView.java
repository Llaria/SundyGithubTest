package sun.sundy.sundygithubtest.scan.zxing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.hardware.Camera;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import com.best.android.bscan.core.decoder.BDecoder;
import com.best.android.bscan.core.decoder.DecodeResult;
import com.best.android.bscan.core.util.DL;
import com.best.android.bscan.core.util.SL;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import sun.sundy.sundygithubtest.scan.core.BarcodeType;
import sun.sundy.sundygithubtest.scan.core.QRCodeView;
import sun.sundy.sundygithubtest.scan.core.ScanResult;
import sun.sundy.sundygithubtest.scan.core.SundyLogUtil;


public class ZXingView extends QRCodeView {
    private MultiFormatReader mMultiFormatReader;
    private Map<DecodeHintType, Object> mHintMap;

    public ZXingView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ZXingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void setupReader() {
        mMultiFormatReader = new MultiFormatReader();

        if (mBarcodeType == BarcodeType.ONE_DIMENSION) {
            mMultiFormatReader.setHints(QRCodeDecoder.ONE_DIMENSION_HINT_MAP);
        } else if (mBarcodeType == BarcodeType.TWO_DIMENSION) {
            mMultiFormatReader.setHints(QRCodeDecoder.TWO_DIMENSION_HINT_MAP);
        } else if (mBarcodeType == BarcodeType.ONLY_QR_CODE) {
            mMultiFormatReader.setHints(QRCodeDecoder.QR_CODE_HINT_MAP);
        } else if (mBarcodeType == BarcodeType.ONLY_CODE_128) {
            mMultiFormatReader.setHints(QRCodeDecoder.CODE_128_HINT_MAP);
        } else if (mBarcodeType == BarcodeType.ONLY_EAN_13) {
            mMultiFormatReader.setHints(QRCodeDecoder.EAN_13_HINT_MAP);
        } else if (mBarcodeType == BarcodeType.HIGH_FREQUENCY) {
            mMultiFormatReader.setHints(QRCodeDecoder.HIGH_FREQUENCY_HINT_MAP);
        } else if (mBarcodeType == BarcodeType.CUSTOM) {
            mMultiFormatReader.setHints(mHintMap);
        } else {
            mMultiFormatReader.setHints(QRCodeDecoder.ALL_HINT_MAP);
        }
    }

    /**
     * 设置识别的格式
     *
     * @param barcodeType 识别的格式
     * @param hintMap     barcodeType 为 BarcodeType.CUSTOM 时，必须指定该值
     */
    public void setType(BarcodeType barcodeType, Map<DecodeHintType, Object> hintMap) {
        mBarcodeType = barcodeType;
        mHintMap = hintMap;

        if (mBarcodeType == BarcodeType.CUSTOM && (mHintMap == null || mHintMap.isEmpty())) {
            throw new RuntimeException("barcodeType 为 BarcodeType.CUSTOM 时 hintMap 不能为空");
        }
        setupReader();
    }

    @Override
    protected ScanResult processBitmapData(Bitmap bitmap) {
        return new ScanResult(QRCodeDecoder.syncDecodeQRCode(bitmap));
    }

    private BDecoder bDecoder = new BDecoder();
    private DecodeResult decodeCell(final Camera camera, final byte[] data, int left, int top, int capturewidth, int captureheight) {
        try {
            Date d1 = new Date();
            Camera.Size previewSize = null;
            DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
            if(previewSize == null){
                previewSize = camera.getParameters().getPreviewSize();
            }

            //进行屏幕到preview的投影换算, 默认竖屏
            int capLeft = left * previewSize.height / displayMetrics.widthPixels ;
            int capTop = top * previewSize.width / displayMetrics.heightPixels ;
            int capWidth = capturewidth * previewSize.height /displayMetrics.widthPixels ;
            int capHeight = captureheight * previewSize.width / displayMetrics.heightPixels ;

            byte[] subRect = YUV420ToGray(data, previewSize.width, previewSize.height, capLeft, capTop, capWidth, capHeight);
//            byte[] subRect = YUV420ToGray(data, previewSize.width, previewSize.height, left, top, capturewidth, captureheight);
            Date d2 = new Date();
            DL.w("","===========YUV420ToGray use time:" + (d2.getTime() - d1.getTime()));

            DecodeResult decodeResult = bDecoder.decode(subRect,capWidth,capHeight);
            Mat showMat = decodeResult.souceMat;
            if (null != showMat){
                Bitmap bitmap = Bitmap.createBitmap(decodeResult.souceMat.width(), decodeResult.souceMat.height(), Bitmap.Config.RGB_565);
                Utils.matToBitmap(decodeResult.souceMat, bitmap);
                imageView.setImageBitmap(bitmap);
            }else {
                System.out.println("============无show");
            }


            Date df = new Date();
            SL.w("","========decodeCell total use time:" + (df.getTime() - d1.getTime()));
            return decodeResult;
        } catch (Exception e) {
            SL.e("", "======decodeCell error", e);
            return new DecodeResult();
        }
    }

    @Override
    protected ScanResult processData(byte[] data, int width, int height, boolean isRetry) {
        SundyLogUtil.d("每一帧的字节数组：" + Arrays.toString(data));
        Result rawResult = null;
        Rect scanBoxAreaRect = null;

        try {
            PlanarYUVLuminanceSource source;
            scanBoxAreaRect = mScanBoxView.getScanBoxAreaRect(height);

            if (scanBoxAreaRect != null) {
                SundyLogUtil.d("==========带框裁剪Zxing算法" + scanBoxAreaRect.left + "========"  + scanBoxAreaRect.top + "========"  + scanBoxAreaRect.width() + "========"  + scanBoxAreaRect.height());
                DecodeResult decodeResult = decodeCell(mCamera, data, scanBoxAreaRect.left, scanBoxAreaRect.top, scanBoxAreaRect.width(), scanBoxAreaRect.height());


//                source = new PlanarYUVLuminanceSource(data, width, height, scanBoxAreaRect.left, scanBoxAreaRect.top, scanBoxAreaRect.width(), scanBoxAreaRect.height(), false);

                if (decodeResult.zxingResult == null || TextUtils.isEmpty(decodeResult.zxingResult.getText())) {
                    SundyLogUtil.d("========带框裁剪Zxing算法识别失败");
                    return null;
                }else {
                    SundyLogUtil.d("========带框裁剪Zxing算法识别成功");
                    return new ScanResult(decodeResult.zxingResult.getText());
                }

            } else {
                source = new PlanarYUVLuminanceSource(data, width, height, 0, 0, width, height, false);
            }

            rawResult = mMultiFormatReader.decodeWithState(new BinaryBitmap(new GlobalHistogramBinarizer(source)));
            if (rawResult == null) {
                rawResult = mMultiFormatReader.decodeWithState(new BinaryBitmap(new HybridBinarizer(source)));
                if (rawResult != null) {
                    SundyLogUtil.d("GlobalHistogramBinarizer 没识别到，HybridBinarizer 能识别到");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mMultiFormatReader.reset();
        }

        if (rawResult == null) {
            SundyLogUtil.d("完全没识别到============》》》》》》");
            return null;
        }

        String result = rawResult.getText();
        if (TextUtils.isEmpty(result)) {
            return null;
        }

        BarcodeFormat barcodeFormat = rawResult.getBarcodeFormat();
        SundyLogUtil.d("格式为：" + barcodeFormat.name());

        // 处理自动缩放和定位点
        boolean isNeedAutoZoom = isNeedAutoZoom(barcodeFormat);
        if (isShowLocationPoint() || isNeedAutoZoom) {
            ResultPoint[] resultPoints = rawResult.getResultPoints();
            final PointF[] pointArr = new PointF[resultPoints.length];
            int pointIndex = 0;
            for (ResultPoint resultPoint : resultPoints) {
                pointArr[pointIndex] = new PointF(resultPoint.getX(), resultPoint.getY());
                pointIndex++;
            }

            if (transformToViewCoordinates(pointArr, scanBoxAreaRect, isNeedAutoZoom, result)) {
                return null;
            }
        }
        return new ScanResult(result);
    }

    private boolean isNeedAutoZoom(BarcodeFormat barcodeFormat) {
        return isAutoZoom() && barcodeFormat == BarcodeFormat.QR_CODE;
    }


    private static byte[] YUV420ToGray(byte[] yuv420sp, int width, int height, int offsetLeft, int offsetTop, int rectWidth, int rectHeight) {
        byte[] subGray = new byte[rectHeight * rectWidth];

        //提取灰度信息
        for( int a=0; a< rectWidth ; a++) {
            for (int b = 0; b < rectHeight; b++) {
                int x = offsetTop + b;
                int y = height - offsetLeft- a - 1;
                int oriindex = y * width + x;
                int nIndex = b * rectWidth + a;
                subGray[nIndex] = yuv420sp[oriindex];
            }
        }
        return subGray;
    }
}