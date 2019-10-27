package sun.sundy.sundygithubtest.view.edittext;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.lang.reflect.Method;

/**
 * 软件盘控制工具类
 * @author scj
 *
 */
public final class SoftInputTools {

    private SoftInputTools() {
    }
    
    /**
     * 打开or关闭软键盘
     * @param context
     */
    public static void showOrHideIme(Context context) {
        InputMethodManager imm = getInputMethodManager(context);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
    
    /**
     * 显示软键盘
     * @param view
     */
    public static void showSoftInput(View view) {
        InputMethodManager imm = getInputMethodManager(view.getContext());
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }
    
    /**
     * 隐藏软键盘
     * @param view
     */
    public static void hideSoftInput(View view) {
        InputMethodManager imm = getInputMethodManager(view.getContext());
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    
    /**
     * 屏蔽软件盘
     * @param editText
     */
    public static void shieldSoftKeyboard(EditText editText) {
        if (android.os.Build.VERSION.SDK_INT <= 10) {
            editText.setInputType(InputType.TYPE_NULL);
        } else {
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod(
                        "setShowSoftInputOnFocus", boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(editText, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static InputMethodManager getInputMethodManager(Context context) {
        return (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
    }
    
}
