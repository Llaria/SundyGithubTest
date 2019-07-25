package sun.sundy.sundygithubtest.view.edittext;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import sun.sundy.sundygithubtest.R;
import sun.sundy.sundygithubtest.utils.SpeechSoundManager;
import sun.sundy.sundygithubtest.utils.ToastUtils;


/**
 * <h4>自定义标准编辑框</h4>
 * 
 * <h6>1、输入法控制</h6>
 * 该类中已经提供了软键盘的打开、关闭、默认数字键盘等方法
 * 
 * <h6>2、默认过滤器设置</h6>
 * 已实现过滤特殊字符和字节长度限制
 * 
 * <h6>3、ok键的监听事件</h6>
 * 客户程序在处理编辑框的点击事件时, 需要添加onOKClickListener（触摸屏点击不实现， 不要使用onClickListener）
 * {@link OnOKClickListener#onOkClicked(View)}
 * <br>
 * 具体子类也可以重写defaultOkEvent来处理ok键的点击事件
 * 
 * <h6>4、其他常用方法</h6>
 * <li>获取编辑框中的内容{@link #getContent()}
 * <li>判断编辑框中的内容是否为空{@link #isEmpty()}, {@link #isNotEmpty()}
 * <li>显示密文
 * <li>错误提醒,setError和toast两种
 * 
 * @author scj
 */
public class StdEditText extends android.support.v7.widget.AppCompatEditText {

    protected Context context;
    // OK键监听
    protected OnOKClickListener okListener;
    // 是否弹出软件盘
    private boolean showSoftInput = true;
    private int defaultInputType = InputType.TYPE_CLASS_PHONE;
    private int defaultRawInputType = InputType.TYPE_CLASS_PHONE;
    private int maxByteLength = -1;
    private int maxLength = -1;
    // 控件失去焦点时，是否隐藏软键盘
    private boolean hideImeWithLostFocus = false;

    public StdEditText(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public StdEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initAttributeSet(attrs);
        init();
    }

    public StdEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initAttributeSet(attrs);
        init();
    }

    private void initAttributeSet(AttributeSet attrs) {
        TypedArray typeArray = context.obtainStyledAttributes(attrs,
                R.styleable.StdEditText);
        maxByteLength = typeArray.getInt(R.styleable.StdEditText_maxByteLength,
                -1);

        defaultRawInputType = typeArray.getInt(
                R.styleable.StdEditText_rawInputType,
                InputType.TYPE_CLASS_PHONE);

        showSoftInput = typeArray.getBoolean(
                R.styleable.StdEditText_showSoftKeyboard, true);

        hideImeWithLostFocus = typeArray.getBoolean(
                R.styleable.StdEditText_hideImeWithLostFocus, false);

        maxLength = attrs.getAttributeIntValue(
                "http://schemas.android.com/apk/res/android", "maxLength", -1);

        if (maxLength < 0)
            setMaxLength(20); // 默认长度
        
        typeArray.recycle();
    }

    protected void init() {
        shieldSoftKeyboard();
        defaultInputType = getInputType();
        initSoftInputNumber();
        setSelectAllOnFocus(true);
        filter();
    }

    // 是否需要屏蔽软键盘
    private void shieldSoftKeyboard() {
        if (!showSoftInput)
            SoftInputTools.shieldSoftKeyboard(this);
    }

    private void initSoftInputNumber() {
        setRawInputType(defaultRawInputType);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) { // 点击ok
            onOkKeyClick();
            cancelLongPress();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            SoftInputTools.hideSoftInput(this);
            return true;
        } else if (keyCode == KeyCodeConstant.KEY_JING) {
            setInputType(defaultInputType);
        } else if (keyCode == KeyCodeConstant.KEY_XING) {
            getText().insert(getSelectionEnd(), ".");
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction,
            Rect previouslyFocusedRect) {
        if (focused) {
            initSoftInputNumber();
        } else {
            if (hideImeWithLostFocus)
                SoftInputTools.hideSoftInput(this);
        }
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    /** 点击ok键处理 */
    private void onOkKeyClick() {
        try {
            if (okListener != null)
                okListener.onOkClicked(this);
            else
                defaultOkEvent();
        } catch (Exception e) {

        }
    }

    /**
     * 默认ok键的处理, StdEditText不做处理
     */
    public void defaultOkEvent() {
        changeFocus(FOCUS_DOWN);
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
     * 设置可输入的最大的字节数(默认GBK编码)
     * @param maxLen
     */
    public void setByteMaxLength(int maxLen) {
        setByteMaxLength(maxLen, "GBK");
    }

    /**
     * 设置可输入的最大的字节数
     * @param maxLen
     * @param encoding
     */
    public void setByteMaxLength(int maxLen, String encoding) {
        if (maxLen <= 0)
            return;
        if (maxByteLength > 0)
            return;
        maxByteLength = maxLen;
        // 第一次设置
        InputFilterTools.addByteLengthFilter(this, encoding, maxLen);
    }

    public int getMaxLength() {
        return maxLength;
    }

    /**
     * 设置最大可输入的字符个数
     * @param len
     */
    public void setMaxLength(int len) {
        maxLength = len;
        InputFilterTools.addFilter(this,
                new InputFilter[] { new InputFilter.LengthFilter(maxLength) });
    }

    // 默认过滤特殊字符
    private void filter() {
        InputFilter specialFilter = new SpecialCharacterFilter(
                SpecialCharacterFilter.DEFAULT_REG);
        if (maxByteLength <= 0) {
            InputFilterTools.addFilter(this, specialFilter);
        } else {
            InputFilter lengthFilter = new ByteLengthFilter("GBK",
                    maxByteLength);
            InputFilterTools.addFilter(this, specialFilter, lengthFilter);
        }
    }

    /**
     * 获得输入框中的内容 
     * @return 返回编辑框中的内容, 如果编辑框中的内容为空则返回""
     */
    public String getContent() {
        return getText() == null ? "" : getText().toString();
    }

    /**
     * 判断输入框是否不为空
     * @return 不为空返回true
     */
    public boolean isNotEmpty() {
        return !isEmpty();
    }

    /**
     * 判断输入框是否为空
     * @return 为空返回true
     */
    public boolean isEmpty() {
        return TextUtils.isEmpty(getContent());
    }

    /** 设置是否弹出软件盘,默认为true */
    public void setShowSoftInput(boolean showSoftInput) {
        this.showSoftInput = showSoftInput;
        shieldSoftKeyboard();
    }
    
    @Override
    public void setInputType(int type) {
    	super.setInputType(type);
    	defaultInputType = type;
    }

    /** 
     * 设置ok键监听
     * 直接用setOnClickListener就好
     */
    @Deprecated
    public void setOnOkClickListener(OnOKClickListener okListener) {
        this.okListener = okListener;
    }
    
    @Override
    public void setOnClickListener(OnClickListener listener) {
    	setOnOkClickListener(new ClickListenerWrapper(listener));
    }

    /**
     * 显示密文
     * @return
     */
    public void showPassword() {
        this.setTransformationMethod(PasswordTransformationMethod.getInstance());
        this.postInvalidate();
    }
    
    /**
     * 重新设置初始文本,并将焦点落到本文后面
     */
    public void resetText(String text) {
    	setText(text);
    	requestFocus();
        setSelection(getContent().length());
    }

    /**
     * 使用setError提示, 播放错误提示音并获得焦点
     * @param message
     */
    @Deprecated
    public void alertError(String message) {
        this.setError(message);
        requestFocus();
        String content = getContent();
        setSelection(0, content.length());
    }

    /**
     * 使用Toast提示, 播放错误提示音并获得焦点
     * @param message
     */
    public void alertErrorToast(String message) {
        ToastUtils.showToast(message);
        requestFocus();
        String content = getContent();
        setSelection(0, content.length());
    }
    
    /**
     * 使用Toast提示, 播放错误提示音并获得焦点, 并播放语音
     * @param message
     */
    public void alertErrorToastAndSpeech(String message) {
    	alertErrorToast(message);
    	try {
			SpeechSoundManager.getInstance().startSpeech(message);
		} catch (Exception e) {
		}
    }

    /**
     * 是否允许弹出软键盘
     * @return true:允许弹出软键盘
     */
    public boolean isShowSoftInput() {
        return showSoftInput;
    }

    /**
     * ok键监听事件
     * @author scj
     */
    public interface OnOKClickListener {

        /** ok键功能处理 */
        public void onOkClicked(View view);

    }
    
    public void performOKClick() {
    	if (okListener != null) {
    		okListener.onOkClicked(this);
    	} else {
    		defaultOkEvent();
    	}
    }
    
    /**
     * 封装点击事件，将原本的OnClickListener封装成OnOkClickListener事件<br>
     * 只有点击OK键才会执行事件，触摸不执行
     * @author scj
     *
     */
    private static class ClickListenerWrapper implements OnOKClickListener {
    	OnClickListener clicklistener;

    	ClickListenerWrapper(OnClickListener listener) {
    		this.clicklistener = listener;
    	}
    	
		@Override
		public void onOkClicked(View view) {
			try {
				clicklistener.onClick(view);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    	
    }

}
