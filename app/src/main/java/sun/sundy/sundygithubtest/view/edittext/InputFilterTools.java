package sun.sundy.sundygithubtest.view.edittext;

import android.text.InputFilter;
import android.text.TextUtils;
import android.widget.EditText;

/**
 * InputFilter工具类
 * @author scj
 *
 */
public final class InputFilterTools {

    private InputFilterTools() {}
    
    /**
     * 特殊字符过滤<br>
     * 为编辑框添加默认的特殊字符过滤器
     * @param editText
     */
    public static void addSpecialCharacterFilter(EditText editText) {
        addSpecialCharacterFilter(editText, SpecialCharacterFilter.DEFAULT_REG);
    }
    
    /**
     * 特殊字符过滤<br>
     * 可自定义特殊字符
     * @param editText
     * @param regEx 需要过滤的字符
     */
    public static void addSpecialCharacterFilter(EditText editText, String regEx) {
        if (TextUtils.isEmpty(regEx))
            return;
        InputFilter filter = new SpecialCharacterFilter(regEx);
        addFilter(editText, filter);
    }
    
    /**
     * 限制GBK字节长度
     * @param editText
     * @param len
     */
    public static void addGBKByteLengthFilter(EditText editText, int len) {
        addByteLengthFilter(editText, "GBK", len);
    }
    
    /**
     * 限制字节长度
     * @param editText
     * @param encoding 指定字符编码
     * @param len 字节长度
     */
    public static void addByteLengthFilter(EditText editText, String encoding, int len) {
        if (TextUtils.isEmpty(encoding))
            encoding = "utf-8";
        InputFilter filters = new ByteLengthFilter(encoding, len);
        addFilter(editText, filters);
    }
    
    /**
     * 为编辑框添加过滤器
     * @param editText
     * @param filters
     */
    public static void addFilter(EditText editText, InputFilter... filters) {
        if (filters == null || filters.length == 0)
            return;
        
        InputFilter[] oldFilters = editText.getFilters();
        int len = filters.length;
        int oldLen = oldFilters.length;
        
        InputFilter[] newFilters = new InputFilter[oldLen + len] ;
        for (int i = 0; i < oldLen; i++) {
            newFilters[i] = oldFilters[i] ;
        }
        for (int i = len; i > 0; i--) {
            newFilters[newFilters.length - i] = filters[len - i];
        }
        
        editText.setFilters(newFilters);
    }
    
}
