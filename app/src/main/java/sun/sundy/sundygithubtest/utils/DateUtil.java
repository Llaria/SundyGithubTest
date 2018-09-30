package sun.sundy.sundygithubtest.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Sundy
 * 日期工具类
 * create at 18-9-29 下午1:44
 */
@SuppressLint("SimpleDateFormat")
public final class DateUtil {

    /** yyyy-MM-dd HH:mm:ss字符串 */
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /** yyyy-MM-dd字符串 */
    public static final String DEFAULT_FORMAT_DATE = "yyyy-MM-dd";

    /** HH:mm:ss字符串 */
    public static final String DEFAULT_FORMAT_TIME = "HH:mm:ss";

    /** yyyy-MM-dd HH:mm:ss格式 */
    public static final ThreadLocal<SimpleDateFormat> defaultDateTimeFormat = new ThreadLocal<SimpleDateFormat>() {

        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT);
        }

    };

    /** yyyy-MM-dd格式 */
    public static final ThreadLocal<SimpleDateFormat> defaultDateFormat = new ThreadLocal<SimpleDateFormat>() {

        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(DEFAULT_FORMAT_DATE);
        }

    };

    /** HH:mm:ss格式 */
    public static final ThreadLocal<SimpleDateFormat> defaultTimeFormat = new ThreadLocal<SimpleDateFormat>() {

        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(DEFAULT_FORMAT_TIME);
        }

    };

    private DateUtil() {
        throw new RuntimeException("￣ 3￣");
    }

    /**
     * 将date转成yyyy-MM-dd HH:mm:ss字符串
     * <br>
     * @param date Date对象
     * @return  yyyy-MM-dd HH:mm:ss
     */
    public static String getDateTimeFormat(Date date) {
        return dateSimpleFormat(date, defaultDateTimeFormat.get());
    }

    /**
     * 将date转成字符串
     * @param date Date
     * @param format SimpleDateFormat
     * <br>
     * 注： SimpleDateFormat为空时，采用默认的yyyy-MM-dd HH:mm:ss格式
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String dateSimpleFormat(Date date, SimpleDateFormat format) {
        if (format == null)
            format = defaultDateTimeFormat.get();
        return (date == null ? "" : format.format(date));
    }

    public static String formatDuring(long mss) {
        long days = mss / (1000 * 60 * 60 * 24);
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60))/(1000 * 60);
        long seconds = (mss  % (1000 * 60)) / 1000;
        return days + " 天 " + hours + " 小时 " + minutes + " 分 " + seconds + " 秒 ";
    }

}
