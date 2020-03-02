package sun.sundy.sundygithubtest.view.edittext;

import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import java.io.UnsupportedEncodingException;

/**
 * 字节长度过滤
 * @author scj
 *
 */
public class ByteLengthFilter implements InputFilter {

    private int maxByteLength = 1000;
    private String encode = "GBK";
    
    public ByteLengthFilter(String encoding, int maxLength) {
        maxByteLength = maxLength;
        encode = encoding;
    }
    
    @Override
    public CharSequence filter(CharSequence source, int start, int end,
            Spanned dest, int dstart, int dend) {
        try {
            int len = 0;
            boolean more = false;
            do {
                SpannableStringBuilder builder = new SpannableStringBuilder(
                        dest).replace(dstart, dend,
                        source.subSequence(start, end));
                len = builder.toString().getBytes(encode).length;
                more = len > maxByteLength;
                if (more && maxByteLength > 0) {
                    end--;
                    source = source.subSequence(start, end);
                }
            } while (more);
            return source;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return "";
    }

    public int getMaxByteLength() {
        return maxByteLength;
    }

    public void setMaxByteLength(int maxByteLength) {
        this.maxByteLength = maxByteLength;
    }

    public String getEncode() {
        return encode;
    }

    public void setEncode(String encode) {
        this.encode = encode;
    }

}
