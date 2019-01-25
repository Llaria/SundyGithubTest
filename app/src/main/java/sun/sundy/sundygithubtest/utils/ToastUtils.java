package sun.sundy.sundygithubtest.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import static sun.sundy.sundygithubtest.App.app;

/**
 * 吐司工具类
 * @author Sundy
 * create at 2017/11/17 15:53
 */
public class ToastUtils {

	private static Toast mToast;
	private static Handler mHandler = new Handler(Looper.getMainLooper());
	private static Runnable runner = new Runnable() {
		public void run() {
			mToast.cancel();
		}
	};

	private ToastUtils() {

	}

	/**
	 * 显示Toast <br>
	 * 注： <li>该toast会被后面的覆盖 <li>默认显示时间为LENGTH_SHORT
	 * 
	 * @param resId
	 */
	public static void showToast(int resId) {
		showToast(app.getString(resId));
	}

	/**
	 * 显示Toast <br>
	 * 注： <li>该toast会被后面的覆盖 <li>默认显示时间为LENGTH_SHORT
	 * 
	 * @param content
	 */
	public static void showToast(String content) {
		showToast(content, 1500);
	}

	/**
	 * 显示Toast <br>
	 * 注： <li>该toast会被后面的覆盖
	 * 
	 * @param content
	 * @param duration
	 *            显示时间
	 */
	public static void showToast(String content, int duration) {
		if (mToast == null) {
			mToast = Toast.makeText(app, content, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(content);
		}
		mHandler.postDelayed(runner, duration);
		mToast.show();
	}
	
	/**
	 * 显示消息不会被覆盖的toast
	 */
	public static void showLazzToast(int resId) {
		showLazzToast(app.getString(resId));
	}

	/**
	 * 显示消息不会被覆盖的toast
	 */
	public static void showLazzToast(String content) {
		Toast.makeText(app, content, Toast.LENGTH_SHORT).show();
	}

   
    /**
     * 显示activity 上面的toast.
     * @param activity activity
     * @param messageRes messageRes
     */
    public static void showActivityToast(Activity activity, String messageRes ){
    	Toast toast = Toast.makeText(activity, messageRes, Toast.LENGTH_SHORT);
        toast.show();
    }
    
}
