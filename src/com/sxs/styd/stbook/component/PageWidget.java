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
 * ����ҳ�滭������������
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
    public void setRightToLeft(boolean rightToLeft) {
        this.rightToLeft = rightToLeft;
    }

    /**
     * ���췽��
     * @param context ������
     */
    public PageWidget(Context context){
        super(context);
        touchPt = new PointF(-1, -1);
        //ARGB A(0-͸��,255-��͸��)
        int[] color = { 0xb0c6a87a, 0x00c6a87a};
        shadowDrawableRL = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, color);
        shadowDrawableRL.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        shadowDrawableLR = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, color);
        shadowDrawableLR.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        
        float[] array = { 0.55f, 0, 0, 0, 80.0f, 0, 0.55f, 0, 0, 80.0f, 0, 0,
            0.55f, 0, 80.0f, 0, 0, 0, 0.2f, 0 };
        ColorMatrix cm = new ColorMatrix();
        cm.set(array);
        /*
         * |A*0.55 + 80|
         * |R*0.55 + 80|
         * |G*0.55 + 80|
         * |B*0.2|
         */
//      cm.setSaturation(0);
        mColorMatrixFilter = new ColorMatrixColorFilter(cm);
        //���ù�������ʵ�ֽӴ���ſ���Ķ���Ч��
        mScroller = new Scroller(context);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            touchPt.x = mScroller.getCurrX();
            touchPt.y = mScroller.getCurrY();
            postInvalidate();
        } else {
//            touchPt.x = -1;
//            touchPt.y = -1;            
        }
        super.computeScroll();
    }
    /**
     * ������Ļ���
     * @param screenWd ���
     * @param screenHg �߶�
     */
    public void setScreen(int screenWd, int screenHg){
        this.screenWidth = screenWd;
        this.screenHeight = screenHg;
    }
    /**
     * ���ñ���
     * @param foreImg ǰ��
     * @param bgImg ����
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
     * ��ǰ��ͼƬ
     * @param canvas canvas���
     */
    private void drawForceImage(Canvas canvas) {
        Paint mPaint = new Paint();

        if (foreImage!=null) {
            canvas.drawBitmap(foreImage, 0, 0, mPaint);
        }        
    }
    /**
     * ������ͼƬ
     * @param canvas canvas���
     * @param path ·��
     */
    private void drawBgImage(Canvas canvas, Path path) {
        Paint mPaint = new Paint();

        if (bgImage!=null) {
            canvas.save();
            //ֻ����·���ཻ����ͼ
            canvas.clipPath(path, Region.Op.INTERSECT);
            canvas.drawBitmap(bgImage, 0, 0, mPaint);
            canvas.restore();
        }
    }


    /**
     * ����ҳЧ��
     * @param canvas ���
     */
    private void drawPageEffect(Canvas canvas) {
        drawForceImage(canvas);
        Paint mPaint = new Paint();
        if (touchPt.x!=-1 && touchPt.y!=-1) {
            //��ҳ������
            canvas.drawLine(touchPt.x, 0, touchPt.x, screenHeight, mPaint);
            //�����߻���Ӱ
            shadowDrawableRL.setBounds((int)touchPt.x - 40, 0 , (int)touchPt.x, screenHeight);
            shadowDrawableRL.draw(canvas);
            //��ҳ���۴�
            float halfCut = touchPt.x + (rightToLeft ? (screenWidth - touchPt.x) : touchPt.x)/2;
            Log.i("hck", "touchPt.x:" + touchPt.x + " -- halfCut -- :" + halfCut);
            canvas.drawLine(halfCut, 0, halfCut, screenHeight, mPaint);
            //���۴���໭��ҳҳͼƬ����
            Rect backArea = new Rect((int)touchPt.x, 0, (int)halfCut, screenHeight);
            Paint backPaint = new Paint();
            backPaint.setColor(0xffdacab0);
            canvas.drawRect(backArea, backPaint);

            //����ҳͼƬ������д���ˮƽ��ת��ƽ�Ƶ�touchPt.x��
            Paint fbPaint = new Paint();
            fbPaint.setColorFilter(mColorMatrixFilter);
            Matrix matrix = new Matrix();

            matrix.preScale(-1, 1);
            matrix.postTranslate(foreImage.getWidth() + touchPt.x, 0);

            canvas.save();
            canvas.clipRect(backArea);
            canvas.drawBitmap(foreImage, matrix, fbPaint);
            canvas.restore();

            //���۴��������Ӱ
            shadowDrawableRL.setBounds((int)halfCut - 50, 0, (int)halfCut, screenHeight);
            shadowDrawableRL.draw(canvas);

            Path bgPath = new Path();

            //������ʾ����ͼ������
            bgPath.addRect(new RectF(halfCut, 0, screenWidth, screenHeight), Direction.CW);

            //���۳��Ҳ໭����
            drawBgImage(canvas, bgPath);

            //���۴����Ҳ���Ӱ
            shadowDrawableLR.setBounds((int)halfCut, 0, (int)halfCut + 50, screenHeight);
            shadowDrawableLR.draw(canvas);
        }
    }
    /**
     * �¼�������
     * @param event �¼�
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
            //���һ���
            if (lastTouchX < touchPt.x) {
                dx = foreImage.getWidth() - (int)touchPt.x + 30;
            } else { 
            //���󻬶�
                dx = -(int)touchPt.x - foreImage.getWidth();
            }
            mScroller.startScroll((int)touchPt.x, (int)touchPt.y, dx, dy, 1000);
            postInvalidate();
        }

        //����Ϊtrue�������޷���ȡACTION_MOVE��ACTION_UP�¼�
        return true;
    }
}
