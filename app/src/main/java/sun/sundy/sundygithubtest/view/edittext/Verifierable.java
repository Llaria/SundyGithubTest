package sun.sundy.sundygithubtest.view.edittext;

/**
 * 编辑框内容校验
 * @author scj
 *
 */
public interface Verifierable {

    /**
     * 校验并做错误处理
     * @param editText
     * @return
     */
    public boolean verify(DeLBackEditText editText);
    
}
