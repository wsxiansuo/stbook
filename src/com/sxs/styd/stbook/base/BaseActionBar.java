package com.sxs.styd.stbook.base;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sxs.styd.stbook.R;
/**.
 * actionBar �Զ������
 * @author user
 *
 */
public class BaseActionBar extends RelativeLayout {
    
    TextView  titleTV;
    Button    leftBtn;
    Button    rightBtn;
    private OnClickListener leftBtnClickListener = null;
    /**
     * @param leftBtnClickListener the leftBtnClickListener to set
     */
    public void setLeftBtnClickListener(OnClickListener leftBtnClickListener) {
        this.leftBtnClickListener = leftBtnClickListener;
        if (leftBtn != null){
            leftBtn.setOnClickListener(leftBtnClickListener);
        }
    }
    private OnClickListener rightBtnClickListener = null;
    /**
     * @param rightBtnClickListener the rightBtnClickListener to set
     */
    public void setRightBtnClickListener(OnClickListener rightBtnClickListener) {
        this.rightBtnClickListener = rightBtnClickListener;
        if (rightBtn != null){
            rightBtn.setOnClickListener(rightBtnClickListener);
        }
    }
    /**.
     * ��������
     * @param title ����
     */
    public void setTitle(String title){
        if (titleTV != null){
            titleTV.setText(title);
        }
    }
    /**.
     * �����Ҳఴť
     * @param title ����
     */
    public void setRightTitle(String title){
        if (rightBtn != null){
            rightBtn.setText(title);
        }
    }
    /**.
     * ���ð�ť����ʾ
     * @param value 0 1
     */
    public void setRightBtnVisible(int value){
        if (rightBtn != null){
            rightBtn.setVisibility(value);
        }
    }
    /**.
     * ���ð�ť����ʾ
     * @param value 0 1
     */
    public void setLeftBtnVisible(int value){
        if (leftBtn != null){
            leftBtn.setVisibility(value);
        }
    }
    
    /* (non-Javadoc)
     * @see android.widget.RelativeLayout#onLayout(boolean, int, int, int, int)
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        for (int i = 0; i < getChildCount(); i++){
            getChildAt(i).layout(l, t, r, b);
        }
    }
    /**.
     * ���췽��
     * @param context ������
     */
    public BaseActionBar(Context context) {
        super(context);
    }
    /**.
     * ���췽��
     * @param context ������
     * @param attrs ����
     */
    public BaseActionBar(Context context, AttributeSet attrs){
        super(context, attrs);
        initComponent(context, attrs);
    }
    /**.
     * ��ʼ�����
     * @param context ������
     * @param attrs ����
     */
    private void initComponent(Context context, AttributeSet attrs){
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.common_attrs);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.top_actionbar_layout , this);
        
        titleTV = (TextView) findViewById(R.id.topBar_title);
        leftBtn = (Button) findViewById(R.id.topBar_left_button);
        rightBtn = (Button) findViewById(R.id.topBar_right_button);
        String title = attr.getString(R.styleable.common_attrs_title);
        titleTV.setText(title);
        
        boolean showLeft = attr.getBoolean(R.styleable.common_attrs_show_left_btn, true);
        leftBtn.setVisibility(showLeft ? View.VISIBLE : View.INVISIBLE);
        if (showLeft){
            if (leftBtnClickListener == null){
                leftBtnClickListener = new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((Activity)getContext()).onBackPressed();
                    }
                };
            }
            leftBtn.setOnClickListener(leftBtnClickListener);
        }
        
        boolean showRight = attr.getBoolean(R.styleable.common_attrs_show_right_btn, true);
        rightBtn.setVisibility(showRight ? View.VISIBLE : View.INVISIBLE);
        Drawable rightBtnDrawble = attr.getDrawable(R.styleable.common_attrs_right_btn_selector);
        String rightTitle = attr.getString(R.styleable.common_attrs_right_title);
        if (showRight){
            if (rightBtnDrawble != null){
                rightBtn.setBackground(rightBtnDrawble);
            } else {
                rightBtn.setBackgroundResource(R.drawable.topbar_right_btn);
            }
            rightBtn.setText(rightTitle);
            if (rightBtnClickListener != null){
                rightBtn.setOnClickListener(rightBtnClickListener);
            }
        }
        attr.recycle();
    }
    /* (non-Javadoc)
     * @see android.view.View#onFinishInflate()
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (isInEditMode()){
            return;
        }
    }
    
    

}
