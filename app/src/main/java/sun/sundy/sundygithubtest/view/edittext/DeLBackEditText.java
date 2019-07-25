package sun.sundy.sundygithubtest.view.edittext;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import java.util.HashSet;
import java.util.Set;

import sun.sundy.sundygithubtest.R;

import static sun.sundy.sundygithubtest.view.edittext.KeyCodeConstant.KEY_DOWN;
import static sun.sundy.sundygithubtest.view.edittext.KeyCodeConstant.KEY_LEFT;
import static sun.sundy.sundygithubtest.view.edittext.KeyCodeConstant.KEY_RIGHT;
import static sun.sundy.sundygithubtest.view.edittext.KeyCodeConstant.KEY_UP;


/**
 * <h4>小退功能编辑框</h4>
 * <p/>
 * StdEditText的子类, 新增了对小退功能键的实现
 * <br>
 * <li>如果本编辑框中没有内容时，点击小退键光标会自动跳转到上一个编辑框
 * <li>重写defaultOkEvent()方法，当没有未编辑框设置onOkClickListener事件时，点击ok键光标会自动落到下一个编辑框
 * <li>实现了拦截方向键功能，例如：执行向下键前可做单号判断
 *
 * @author scj
 */
public class DeLBackEditText extends StdEditText implements TextWatcher {

    /**
     * 屏蔽编辑框按键事件。默认不屏蔽
     */
    public static boolean keyEventDisabled = false;

    // 当前状态是否需要执行小退（开启小退功能，且编辑框文本为空时）
    private boolean isBackKey = false;
    // 是否开启小退功能
    private boolean backFuntion = true;
    // 是否是第一个框， 第一个框按小退时会弹出对话框提示
    protected boolean isFirst;
    // 需要拦截的按键
    private Set<Integer> mInterceptKeys = new HashSet<Integer>();
    // 开启按键拦截
    private boolean intercept;
    //    private boolean interceptTouch; // 拦截触摸事件
    private Verifierable verify;
    private InputLengthChangeListener lengthChangeListener;

    public DeLBackEditText(Context context) {
        super(context);
    }

    public DeLBackEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
    }

    public DeLBackEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(attrs);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typeArray = context.obtainStyledAttributes(attrs,
                R.styleable.DelBackEditText);

        boolean interceptUp = typeArray.getBoolean(
                R.styleable.DelBackEditText_interceptKeyUp, false);
        if (interceptUp)
            mInterceptKeys.add(KEY_UP);

        boolean interceptDown = typeArray.getBoolean(
                R.styleable.DelBackEditText_interceptKeyDown, false);
        if (interceptDown)
            mInterceptKeys.add(KEY_DOWN);

        boolean interceptLeft = typeArray.getBoolean(
                R.styleable.DelBackEditText_interceptKeyLeft, false);
        if (interceptLeft)
            mInterceptKeys.add(KEY_LEFT);

        boolean interceptRight = typeArray.getBoolean(
                R.styleable.DelBackEditText_interceptKeyRight, false);
        if (interceptRight)
            mInterceptKeys.add(KEY_RIGHT);

        if (mInterceptKeys.size() > 0)
            interceptOn();

        typeArray.recycle();
    }

    @Override
    protected void init() {
        super.init();
        this.addTextChangedListener(this); // 监听小退键的功能变化
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        isBackKey = isEmpty();
    }

    /**
     * 小退功能的实现<br>
     * 该方法用于处理长按虚拟键盘的小退键连续执行onKeyUp的bug
     */
    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyEventDisabled) {
            return false;
        }
        if (isDelClicked(keyCode, event)) {
            if (!isBackKey)
                isBackKey = true;
            else
                onDelClicked();
            return true;
        }
        return super.onKeyPreIme(keyCode, event);
    }

    /**
     * 判断是否执行小退功能<br>
     * 当编辑框中无内容时，点击小退键，弹起时会执行小退功能
     *
     * @param keyCode
     * @param event
     * @return
     */
    protected boolean isDelClicked(int keyCode, KeyEvent event) {
        if (keyCode != KeyEvent.KEYCODE_DEL)
            return false;
        if (event.getAction() != KeyEvent.ACTION_UP)
            return false;
        return isEmpty();
    }

    /**
     * 小退功能处理，焦点落到上一个编辑框
     */
    public void onDelClicked() {
        if (!backFuntion)
            return;

        if (!isFirst)
            focusToUpView();
        else
            alertExit();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER){
            performOKClick();
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    /**
     * 设置是否开启小退功能
     *
     * @param functionOn
     */
    public void setBackFunction(boolean functionOn) {
        backFuntion = functionOn;
    }

    /**
     * <p>提示用户退出
     * <br>注: 需要客户程序实现
     */
    public void alertExit() {

    }

    /**
     * 重写ok键的默认处理, 焦点会落到下一个编辑框
     */
    @Override
    public void defaultOkEvent() {
        focusToDownView();
    }

    /**
     * 被标记为true时，按小退键时会提示提示用户是否退出指定操作
     *
     * @param isFirst
     */
    public void setFirst(boolean isFirst) {
        this.isFirst = isFirst;
        requestFocus();
    }

    /**
     * 焦点落到上一个编辑框
     */
    public void focusToUpView() {
        changeFocus(FOCUS_UP);
    }

    /**
     * 焦点落到下一个编辑框
     */
    public void focusToDownView() {
        changeFocus(FOCUS_DOWN);
    }

    public void setUpFocusId(int upFocusId) {
        setNextFocusUpId(upFocusId);
    }

    public void setDownFocusId(int downFocusId) {
        setNextFocusDownId(downFocusId);
    }

    /**
     * 默认方式改变焦点
     */
    private void changeFocus(int direction) {
        View view = focusSearch(direction);
        if (view != null && view instanceof EditText)
            view.requestFocus();
    }

    /**
     * 添加编辑框获得焦点时显示已输入的字符数
     */
    @Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (lengthChangeListener != null)
            lengthChangeListener.onTextLengthChanged(getContent().length());
    }

    /**
     * 小退键功能变化<br>
     * 注: 当编辑框中还有文字该键执行删除功能<br>
     * 当编辑框没有内容时执行小退功能<br>
     */
    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore,
                              int lengthAfter) {
        int len = text.length();

        if (lengthChangeListener != null)
            lengthChangeListener.onTextLengthChanged(len);

        if (len > 0)
            isBackKey = false;
    }

    public interface InputLengthChangeListener {
        /**
         * 监听文本框中已输入字符数
         *
         * @param length 当前文本框中的长度
         */
        void onTextLengthChanged(int length);
    }

    public void setInputLengthChangeListener(InputLengthChangeListener listener) {
        this.lengthChangeListener = listener;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KEY_UP:
            case KEY_DOWN:
            case KEY_LEFT:
            case KEY_RIGHT:
                if (interceptKeyCode(event.getKeyCode()))
                    return true;
            default:
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    /*
     * 拦截指定按键
     * 1、先判断是否开启按键拦截功能（未开启则跳过）
     * 2、判断是否是要拦截的按键（不是则跳过）
     * 3、是否符合校验规则（不符合则拦截按键，不让焦点跳转）
     */
    private boolean interceptKeyCode(int curKeyCode) {
        if (mInterceptKeys == null || mInterceptKeys.size() == 0)
            return false;
        if (!isInterceptOn())
            return false;

        for (int each : mInterceptKeys)
            if (curKeyCode == each) {
                if (!verify())
                    return true;
            }

        return false;
    }

    /**
     * 是否开启拦截功能
     */
    public boolean isInterceptOn() {
        return intercept;
    }

    /**
     * 校验规则, 由客户程序具体实现
     *
     * @return
     */
    public boolean verify() {
        if (verify != null)
            return verify.verify(this);
        return true;
    }

    public void setVerifyable(Verifierable verify) {
        this.verify = verify;
    }

    /**
     * 开启拦截
     */
    public void interceptOn() {
        intercept = true;
    }

    /**
     * 关闭拦截
     */
    public void interceptOff() {
        intercept = false;
    }

	/*    public boolean interceptTouchOn() {

	    }*/

    /**
     * 设置需要拦截的按键<p>
     * 目前只可拦截上下左右键
     */
    public void setInterceptKeys(Integer... keys) {
        mInterceptKeys.clear();
        if (keys == null || keys.length == 0)
            return;
        for (Integer key : keys) {
            if (key >= KEY_UP && key <= KEY_RIGHT)
                mInterceptKeys.add(key);
        }
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if (!event.getDevice().getName().equals("Virtual")) {
            System.out.println("=====>>>>>拦截了输入法");
            Log.d("=====", "getAction=" + event.getAction()
                    + ";getKeyCode=" + event.getKeyCode()
                    + ";getCharacters=" + event.getCharacters()
                    + ";getScanCode=" + event.getScanCode()
                    + ";getUnicodeChar=" + event.getUnicodeChar()
                    + ";getNumber=" + event.getNumber());

        }
        return super.dispatchKeyEventPreIme(event);

    }
}
