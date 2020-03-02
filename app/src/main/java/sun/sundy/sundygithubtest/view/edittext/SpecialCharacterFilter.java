package sun.sundy.sundygithubtest.view.edittext;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Pattern;

/**
 * 特殊字符过滤
 * @author scj
 *
 */
public class SpecialCharacterFilter implements InputFilter {

    public static final String DEFAULT_REG = "[`~!@#$%^&*()+=|{}';'"
            + "//[//]<>/?~！#￥%……&*（）——+|{}【】‘；：”“’。，、？\"]";

    private String regEx = DEFAULT_REG;

    public SpecialCharacterFilter(String characters) {
        regEx = characters;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end,
            Spanned dest, int dstart, int dend) {
        if (Pattern.matches(regEx, source))
            return "";
        return null;
    }

    public String getRegEx() {
        return regEx;
    }

    public void setRegEx(String regEx) {
        this.regEx = regEx;
    }

}
