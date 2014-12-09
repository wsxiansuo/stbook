package com.sxs.styd.stbook.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

/**
 * 控制页面画出贝塞尔曲线
 * 
 * @author yuanhang
 * 
 */
public class PageWidget extends View {

    private Bitmap foreImage;
    private Bitmap bgImage;
    private PointF touchPt;
    private int screenWidth;
    private int screenHeight;
    private GradientDrawable shadowDrawableRL;
    private GradientDrawable shadowDrawableLR;
    private ColorMatrixColorFilter mColorMatrixFilter;
    private Scroller mScroller;
    private int lastTouchX;
    private boolean rightToLeft = false;
    /**
     * @param rightToLeft the rightToLeft to set
     */
    public void setRightToLeft(float x) {
        this.rightToLeft = x > lastTouchX;
    }
    public boolean getRightToLeft(){
        return rightToLeft;
    }

    /**
     * 构造方法
     * @param context 上下文
     */
    public PageWidget(Context context){
        super(context);
        touchPt = new PointF(-1, -1);
        //ARGB A(0-透明,255-不透明)
        int[] color = { 0xb0c6a87a, 0x00c6a87a};
        shadowDrawableRL = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, color);
        shadowDrawableRL.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        shadowDrawableLR = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, color);
        shadowDrawableLR.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        
        float[] array = { 0.55f, 0, 0, 0, 80.0f, 0, 0.55f, 0, 0, 80.0f, 0, 0,
            0.55f, 0, 80.0f, 0, 0, 0, 0.2f, 0 };
        ColorMatrix cm = new ColorMatrix();
        cm.set(array);
//      cm.setSaturation(0);
        mColorMatrixFilter = new ColorMatrixColorFilter(cm);
        //利用滚动条来实现接触点放开后的动画效果
        mScroller = new Scroller(context);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            touchPt.x = mScroller.getCurrX();
            touchPt.y = mScroller.getCurrY();
            postInvalidate();
        }
        super.computeScroll();
    }
    /**
     * 设置屏幕宽高
     * @param screenWd 宽度
     * @param screenHg 高度
     */
    public void setScreen(int screenWd, int screenHg){
        this.screenWidth = screenWd;
        this.screenHeight = screenHg;
    }
    /**
     * 设置背景
     * @param foreImg 前景
     * @param bgImg 背景
     */
    public void setBitmaps(Bitmap foreImg, Bitmap bgImg){
        this.foreImage = foreImg;
        this.bgImage = bgImg;
    }
    

    @Override
    protected void onDraw(Canvas canvas) {
        drawPageEffect(canvas);
        super.onDraw(canvas);
    }


    /**
     * 画前景图片
     * @param canvas canvas面板
     */
    private void drawForceImage(Canvas canvas) {
        Paint mPaint = new Paint();

        if (foreImage!=null) {
            canvas.drawBitmap(foreImage, 0, 0, mPaint);
        }        
    }
    /**
     * 画背景图片
     * @param canvas canvas面板
     * @param path 路径
     */
    private void drawBgImage(Canvas canvas, Path path) {
        Paint mPaint = new Paint();

        if (bgImage!=null) {
            canvas.save();
            //只在与路径相交处画图
            canvas.clipPath(path, Region.Op.INTERSECT);
            canvas.drawBitmap(bgImage, 0, 0, mPaint);
            canvas.restore();
        }
    }


    /**
     * 画翻页效果
     * @param canvas 面板
     */
    private void drawPageEffect(Canvas canvas) {
        drawForceImage(canvas);
        Paint mPaint = new Paint();
        if (touchPt.x!=-1 && touchPt.y!=-1) {
            //翻页对折处
            float halfCut = rightToLeft ? (touchPt.x + (screenWidth - touchPt.x)/2) : touchPt.x / 2;
            canvas.drawLine(halfCut, 0, halfCut, screenHeight, mPaint);
            
            //对折处左侧画翻页页图片背面
            Rect backArea = new Rect(rightToLeft ? (int)touchPt.x : (int)halfCut, 
                0, rightToLeft ? (int)halfCut : (int)touchPt.x , screenHeight);
            Paint backPaint = new Paint();
            backPaint.setColor(0xffdacab0);
            canvas.drawRect(backArea, backPaint);

            //将翻页图片正面进行处理水平翻转并平移到touchPt.x点
            Paint fbPaint = new Paint();
            fbPaint.setColorFilter(mColorMatrixFilter);
            Matrix matrix = new Matrix();

            matrix.preScale(-1, 1);
            matrix.postTranslate(rightToLeft?(foreImage.getWidth() + touchPt.x):touchPt.x, 0);

            canvas.save();
            canvas.clipRect(backArea);
            canvas.drawBitmap(foreImage, matrix, fbPaint);
            canvas.restore();
            if (rightToLeft){
                drawRightToLeftEffect(canvas, mPaint, halfCut);
            } else {
                drawLeftToRightEffect(canvas, mPaint, halfCut);
            }
        }
    }
    
    public void drawLeftToRightEffect(Canvas canvas, Paint mPaint, float halfCut){
        //翻页左侧书边
        canvas.drawLine(touchPt.x, 0, touchPt.x, screenHeight, mPaint);
        //左侧书边画阴影
        shadowDrawableLR.setBounds((int)touchPt.x, 0, (int)touchPt.x + 40, screenHeight);
        shadowDrawableLR.draw(canvas);
        Path bgPath = new Path();
        //可以显示背景图的区域
        bgPath.addRect(new RectF(0, 0, halfCut, screenHeight), Direction.CW);
        //对折出右侧画背景
        drawBgImage(canvas, bgPath);
        //对折处画左侧阴影
        shadowDrawableRL.setBounds((int)halfCut - 50, 0, (int)halfCut, screenHeight);
        shadowDrawableRL.draw(canvas);
        //对折处画右侧阴影
        shadowDrawableLR.setBounds((int)halfCut, 0, (int)halfCut + 50, screenHeight);
        shadowDrawableLR.draw(canvas);
    }
    public void drawRightToLeftEffect(Canvas canvas, Paint mPaint, float halfCut){
        //翻页左侧书边
        canvas.drawLine(touchPt.x, 0, touchPt.x, screenHeight, mPaint);
        //左侧书边画阴影
        shadowDrawableRL.setBounds((int)touchPt.x - 40, 0, (int)touchPt.x, screenHeight);
        shadowDrawableRL.draw(canvas);
        //对折处画左侧阴影
        shadowDrawableRL.setBounds((int)halfCut - 50, 0, (int)halfCut, screenHeight);
        shadowDrawableRL.draw(canvas);
        Path bgPath = new Path();
        //可以显示背景图的区域
        bgPath.addRect(new RectF(halfCut, 0, screenWidth, screenHeight), Direction.CW);
        //对折出右侧画背景
        drawBgImage(canvas, bgPath);
        //对折处画右侧阴影
        shadowDrawableLR.setBounds((int)halfCut, 0, (int)halfCut + 50, screenHeight);
        shadowDrawableLR.draw(canvas);
    }
    
    public void abortAnimation() {
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
        }
    }
    /**
     * 事件处理类
     * @param event 事件
     * @return bool
     */
    public boolean doTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            touchPt.x = event.getX();
            touchPt.y = event.getY();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE){
            lastTouchX = (int)touchPt.x;
            touchPt.x = event.getX();
            touchPt.y = event.getY();
            postInvalidate();
        } else if (event.getAction() == MotionEvent.ACTION_UP){
            int dx;
            int dy = 0;
            //向右滑动
            if (lastTouchX < touchPt.x) {
                dx = foreImage.getWidth() + screenWidth;
            } else { 
            //向左滑动
                dx = -(int)touchPt.x - foreImage.getWidth();
            }
            mScroller.startScroll((int)touchPt.x, (int)touchPt.y, dx, dy, 1000);
            postInvalidate();
        }

        //必须为true，否则无法获取ACTION_MOVE及ACTION_UP事件
        return true;
    }
}
