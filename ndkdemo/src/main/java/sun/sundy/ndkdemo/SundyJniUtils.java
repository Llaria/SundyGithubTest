package sun.sundy.ndkdemo;

/**
 * @author Sundy
 * JNI工具类Jar包
 * create at 2021/9/17 10:21
 */
public class SundyJniUtils {

    static {
        System.loadLibrary("native-lib");
    }

    public native static String stringFromJNI();
}
