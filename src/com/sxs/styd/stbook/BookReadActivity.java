package com.sxs.styd.stbook;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.sxs.styd.stbook.base.BaseActivity;
import com.sxs.styd.stbook.component.BookPageFactory;
import com.sxs.styd.stbook.component.PageWidget;
import com.sxs.styd.stbook.config.Constants;
import com.sxs.styd.stbook.data.DBManager;
import com.sxs.styd.stbook.vo.BookVO;
/**
 * �Ķ���
 * @author user
 *
 */
public class BookReadActivity extends BaseActivity implements View.OnClickListener{
    
    @ViewInject(R.id.rl_read_layout)  RelativeLayout  rlLayout;
    private BookVO        currItem;
    private Context mContext;
    
    private int defaultSize = 0;
    private int readHeight = 0; //��������ʾ�߶�
    private Bitmap mCurPageBitmap;
    private Bitmap mNextPageBitmap;
    private Canvas mCurPageCanvas;
    private Canvas mNextPageCanvas;
    private PageWidget mPageWidget;
    private Boolean show = false; //popwindow�Ƿ���ʾ
    private BookPageFactory pageFactory;
    private int begin;
    private SharedPreferences sp;
    private Editor editor;
    private int size;
    private int light;
    private boolean isNight;
    private LayoutParams lp;
    private boolean isFirstMove;
    
    public String word;
    
    private PopupWindow mPopupWindow; //�������Ŀ¼
    private View mPopupView;
    private TextView bookBtn1;
    private TextView bookBtn2;
    private TextView bookBtn3;
    private TextView bookBtn4;
    private TextView speakBtn;
    private LinearLayout layout;
    
    /* (non-Javadoc)
     * @see com.sxs.styd.stbook.base.BaseActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        defaultSize = (Constants.SCREEN_WIDTH * 20) / 320;
        readHeight = Constants.SCREEN_HEIGHT - Constants.SCREEN_WIDTH / 320;
        mCurPageBitmap = Bitmap.createBitmap(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT, Bitmap.Config.ARGB_8888);
        mNextPageBitmap = Bitmap.createBitmap(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT, Bitmap.Config.ARGB_8888);
        mCurPageCanvas = new Canvas(mCurPageBitmap);
        mNextPageCanvas = new Canvas(mNextPageBitmap);
        mPageWidget = new PageWidget(this); //ҳ��
        mPageWidget.setScreen(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        setContentView(R.layout.activity_book_read);
        ViewUtils.inject(this);
        rlLayout.addView(mPageWidget);
        mPageWidget.setBitmaps(mCurPageBitmap, mCurPageBitmap);
        mPageWidget.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                boolean ret = false;
                if (v == mPageWidget && !show) {
                    if (e.getAction() == MotionEvent.ACTION_DOWN) {
                        if (e.getY() > readHeight) {// ������Χ�ˣ���ʾ�������������������ҳ
                            return false;
                        }
                        isFirstMove = true;
                    }
                    if (e.getAction() == MotionEvent.ACTION_MOVE && isFirstMove) {
                        isFirstMove = false;
                        mPageWidget.abortAnimation();
                        mPageWidget.setRightToLeft(e.getX());
                        if (!handlerEvent()){
                            return false;
                        }
                    }
                    ret = mPageWidget.doTouchEvent(e);
                    return ret;
                }
                return false;
            }
        });
        setPop();
        sp = getSharedPreferences(Constants.CONFIG, MODE_PRIVATE);
        editor = sp.edit();
        getSize();
        getLight();
        //count = sp.getLong(bookPath + "count", 1);
        lp = getWindow().getAttributes();
        lp.screenBrightness = light / 10.0f < 0.01f ? 0.01f :light / 10.0f;
        getWindow().setAttributes(lp);
        pageFactory = new BookPageFactory(Constants.SCREEN_WIDTH, readHeight); //�鹤��
        if (isNight){
            pageFactory.setBgBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.main_bg));
            pageFactory.setMTextColor(Color.rgb(128, 128, 128));
        } else {
            pageFactory.setBgBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.bg));
            pageFactory.setMTextColor(Color.rgb(28, 28, 28));
        }
    }
    
    private boolean handlerEvent(){
        pageFactory.onDraw(mCurPageCanvas);
        if (mPageWidget.getRightToLeft()) { // ��
            try {
                pageFactory.prePage();
                begin = pageFactory.getMMbBufBegin(); // ��ȡ��ǰ�Ķ�λ��
                word = pageFactory.getFirstLineText(); // ��ȡ��ǰ�Ķ�λ�õ���������
            } catch (IOException e1) {
                Log.e(TAG, "onTouch->prePage error", e1);
            }
            if (pageFactory.isfirstPage()) {
                showToast("��ǰ�ǵ�һҳ");
                return false;
            }
            pageFactory.onDraw(mNextPageCanvas);
        } else { // �ҷ�
            try {
                pageFactory.nextPage();
                begin = pageFactory.getMMbBufBegin(); // ��ȡ��ǰ�Ķ�λ��
                word = pageFactory.getFirstLineText(); // ��ȡ��ǰ�Ķ�λ�õ���������
            } catch (IOException e1) {
                Log.e(TAG, "onTouch->nextPage error", e1);
            }
            if (pageFactory.islastPage()) {
                showToast("�Ѿ������һҳ��");
                return false;
            }
            pageFactory.onDraw(mNextPageCanvas);
        }
        mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap);
        return true;
    }
    
    /**
     * ���õ�������
     */
    private void setPop(){
        mPopupView = LayoutInflater.from(this).inflate(R.layout.pop_style_setting_layout, null);
        mPopupWindow = new PopupWindow(mPopupView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        
    }
    /**
     * ��ȡ�����ļ��������С
     */
    private void getSize(){
        size = sp.getInt("size", defaultSize);
    }
    /**
     * ��ȡ�����ļ�������ֵ
     */
    private void getLight(){
        light = sp.getInt("light", 5);
        isNight = sp.getBoolean("night", false);
    }
    /**
     * �򿪵�����
     */
    private void openBook(){
        begin = currItem.lastPostion;
        try {
            pageFactory.openbook(currItem.path, begin); //��ָ��λ�ô��鼮 Ĭ�ϴӿ�ʼ��
            pageFactory.setMFontSize(size);
            pageFactory.onDraw(mCurPageCanvas);
        } catch (IOException e1){
            Log.e(TAG, "�򿪵�����ʧ��", e1);
            showToast("�򿪵�����ʧ��");
        }
    }
    
    
    /**
     * ��������
     */
    private void pop(){
        mPopupWindow.showAtLocation(mPageWidget, Gravity.BOTTOM, 0, 0);
        bookBtn1 = (TextView) mPopupView.findViewById(R.id.bookBtn1);
        bookBtn2 = (TextView) mPopupView.findViewById(R.id.bookBtn2);
        bookBtn3 = (TextView) mPopupView.findViewById(R.id.bookBtn3);
        bookBtn4 = (TextView) mPopupView.findViewById(R.id.bookBtn4);
        speakBtn = (TextView) mPopupView.findViewById(R.id.text_speak);
        layout = (LinearLayout) mPopupView.findViewById(R.id.book_pop);
        getLight();
        if (isNight){
            layout.setBackgroundResource(R.drawable.tmall_bar_bg);
        } else {
            layout.setBackgroundResource(R.drawable.titlebar_big);
        }
        bookBtn1.setOnClickListener(this);
        bookBtn2.setOnClickListener(this);
        bookBtn3.setOnClickListener(this);
        bookBtn4.setOnClickListener(this);
        speakBtn.setOnClickListener(this);
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (show){
                show = false;
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                mPopupWindow.dismiss();
            } else {
                finish();
            }
        }
        return true;
    }
    /* (non-Javadoc)
     * @see android.app.Activity#onKeyUp(int, android.view.KeyEvent)
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU){
            if (show){
                mPopupWindow.dismiss();
            } else {
                pop();
            }
            show = !show;
        }
        return super.onKeyUp(keyCode, event);
    }
    /* (non-Javadoc)
     * @see com.sxs.styd.stbook.base.BaseActivity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (intent.hasExtra("itemVO")){
            currItem = (BookVO) intent.getExtras().getSerializable("itemVO");
            intent.removeExtra("itemVO");
            openBook();
        }
    }
    
    /* (non-Javadoc)
     * @see com.sxs.styd.stbook.base.BaseActivity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (currItem != null){ //��ҳ����ͣ����Ҫ����ʱ ִ�д˷��� ���µ����ݿ�
            DBManager.getInstance().updateBookById(currItem.id, begin+"", System.currentTimeMillis() + "");
        }
    }
    /* (non-Javadoc)
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        pageFactory = null;
        mPageWidget = null;
        
        finish();
    }
    @Override
    public void onClick(View v) {
        int tag = v.getId();
        switch (tag) {
            case R.id.bookBtn1:
              
                break;
    
            default:
                break;
        }
    }
    
}
