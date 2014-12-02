package com.sxs.styd.stbook;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.sxs.styd.stbook.base.BaseActivity;
import com.sxs.styd.stbook.component.BookPageFactory;
import com.sxs.styd.stbook.component.PageWidget;
import com.sxs.styd.stbook.config.Constants;
import com.sxs.styd.stbook.vo.BookVO;
/**
 * �Ķ���
 * @author user
 *
 */
public class BookReadActivity extends BaseActivity {
    
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
    public String word;
    
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
        mPageWidget = new PageWidget(this, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT); //ҳ��
        setContentView(R.layout.activity_book_read);
        ViewUtils.inject(this);
        mPageWidget.setBitmaps(mCurPageBitmap, mCurPageBitmap);
        mPageWidget.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean ret = false;
                if (v == mPageWidget){
                    if (!show){
                        if (event.getAction() == MotionEvent.ACTION_DOWN){
                            if (event.getY() > readHeight){ //��������ʾ ��Χ �� ��ʾ������������������ҳ
                                return false;
                            }
                            mPageWidget.abortAnimation();
                            mPageWidget.calcCornerXY(event.getX(), event.getY());
                            pageFactory.onDraw(mCurPageCanvas);
                            if (mPageWidget.dragToRight()){
                                try {
                                    pageFactory.prePage();
                                    begin = pageFactory.getMMbBufBegin(); //��ȡ��ǰ�Ķ�λ��
                                    word = pageFactory.getFirstLineText(); //��ȡ��ǰ�Ķ�λ�õ���������
                                } catch (IOException el){
                                    Log.e(TAG, "onTouch->prePage error", el);
                                }
                                if (pageFactory.isfirstPage()){
                                    showToast("��ǰ�ǵ�һҳ");
                                    return false;
                                }
                                pageFactory.onDraw(mNextPageCanvas);
                            } else { //�ҷ�
                                try {
                                    pageFactory.nextPage();
                                    begin = pageFactory.getMMbBufBegin();
                                    word = pageFactory.getFirstLineText();
                                } catch (IOException e1){
                                    Log.e(TAG, "onTouch->nextPage error", e1);
                                }
                                if (pageFactory.islastPage()){
                                    showToast("�Ѿ������һҳ��");
                                }
                                pageFactory.onDraw(mNextPageCanvas);
                            }
                            mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap);
                        }
                        
                        ret = mPageWidget.doTouchEvent(event);
                        return ret;
                    }
                }
                return false;
            }
        });
        
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

            
            
        }
    }
    
}
