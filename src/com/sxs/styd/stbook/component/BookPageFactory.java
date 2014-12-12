package com.sxs.styd.stbook.component;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.Log;

import com.sxs.styd.stbook.util.TxtReadUtil;

/**
 * ��ҳ
 * @author user
 *
 */
public class BookPageFactory {
    StringBuilder  word;
    private static final String TAG = "BookPageFactory";
    private File bookFile = null;
    private int mBackColor = 0xffff9e85; // ������ɫ
    private Bitmap mBookBg = null;
    private int mFontSize = 20;
    private boolean mIsfirstPage;
    private boolean mIslastPage;
    private Vector<String> mLines = new Vector<String>();
    private MappedByteBuffer mMbBuf = null; // �ڴ��е�ͼ���ַ�
    private int mMbBufBegin = 0; // ��ǰҳ��ʼλ��
    private int mMbBufEnd = 0; // ��ǰҳ�յ�λ��

    private int mMbBufLen = 0; // ͼ���ܳ���

    private int mTextColor = Color.rgb(28, 28, 28);

    private int marginHeight = 15; // �������Ե�ľ���
    private int marginWidth = 15; // �������Ե�ľ���
    private int mHeight;
    private int mLineCount; // ÿҳ������ʾ������
    private Paint mPaint;

    private float mVisibleHeight; // �������ݵĿ�
    private float mVisibleWidth; // �������ݵĿ�
    private int mWidth;
    /**
     * ���칤��
     * @param w ���
     * @param h �߶�
     */
    public BookPageFactory(int w, int h) {
        mWidth = w;
        mHeight = h;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // ����
        mPaint.setTextAlign(Align.LEFT); // ������
        mPaint.setTextSize(mFontSize); // �����С
        mPaint.setColor(mTextColor); // ������ɫ
        mVisibleWidth = mWidth - marginWidth * 2;
        mVisibleHeight = mHeight - marginHeight * 2;
        mLineCount = (int) (mVisibleHeight / mFontSize) - 1; // ����ʾ������,-1����Ϊ�ײ���ʾ���ȵ�λ�����ױ���ס
    }
    /**
     * ��ȡ���ִ�С
     * @return ��С
     */
    public int getMFontSize() {
        return mFontSize;
    }
    /**
     * ��ȡ��С����
     * @return ����
     */
    public int getmLineCount() {
        return mLineCount;
    }
    /**
     * �Ƿ��ǵ�һҳ
     * @return bool
     */
    public boolean isfirstPage() {
        return mIsfirstPage;
    }
    /**
     * �Ƿ������һҳ
     * @return bool
     */
    public boolean islastPage() {
        return mIslastPage;
    }

    /**
     * ���ҳ
     * 
     * @throws IOException IOE
     */
    public void nextPage() throws IOException{
        if (mMbBufEnd >= mMbBufLen) {
            mIslastPage = true;
            return;
        } else {
            mIslastPage = false;
        }
        mLines.clear();
        mMbBufBegin = mMbBufEnd; // ��һҳҳ��ʼλ��=��ǰҳ����λ��
        mLines = pageDown();
    }
    /**
     * ��ǰҳ
     * @throws IOException IOE
     */
    public void currentPage() throws IOException {
        mLines.clear();
        mLines = pageDown();
    }
    /**
     * ���Ʋ���
     * @param c Canvas
     */
    public void draw(Canvas c){
        word=new StringBuilder();
        mPaint.setTextSize(mFontSize);
        mPaint.setColor(mTextColor);
        if (mLines.size() == 0){
            mLines = pageDown();
        }
        if (mLines.size() > 0) {
            if (mBookBg == null){
                c.drawColor(mBackColor);
            } else {
                c.drawBitmap(mBookBg, 0, 0, null);
            }
            int y = marginHeight;
            for (String strLine : mLines) {
                y += mFontSize;
                c.drawText(strLine, marginWidth, y, mPaint);
                word.append(strLine);
            }
//            Read.words=word.toString();
            word=null;
        }
        float fPercent = (float) (mMbBufBegin * 1.0 / mMbBufLen);
        DecimalFormat df = new DecimalFormat("#0.0");
        String strPercent = df.format(fPercent * 100) + "%";
        int nPercentWidth = (int) mPaint.measureText("999.9%") + 1;
        c.drawText(strPercent, mWidth - nPercentWidth, mHeight - 5, mPaint);
    }

    /**
     * 
     * @param strFilePath ·��
     * @param begin ��ʼ
     *            ��ʾ��ǩ��¼��λ�ã���ȡ��ǩʱ����beginֵ��m_mbBufEnd���ڶ�ȡnextpage�����ɹ���ȡ������ǩ
     *            ��¼ʱ��m_mbBufBegin��ʼλ����Ϊ��ǩ��¼
     * @throws IOException Error
     */
    @SuppressWarnings("resource")
    public void openbook(String strFilePath, int begin) throws IOException {
        bookFile = new File(strFilePath);
        long lLen = bookFile.length();
        mMbBufLen = (int) lLen;
        mMbBuf = new RandomAccessFile(bookFile, "r").getChannel().map(
                FileChannel.MapMode.READ_ONLY, 0, lLen);
        Log.d(TAG, "total lenth��" + mMbBufLen);
        // �����Ѷ�����
        if (begin >= 0) {
            mMbBufBegin = begin;
            mMbBufEnd = begin;
        }
    }
    
    /**
     * ��ָ��ҳ����һҳ
     * 
     * @return ��һҳ������ Vector<String>
     */
    public Vector<String> pageDown() {
        mPaint.setTextSize(mFontSize);
        mPaint.setColor(mTextColor);
        String strParagraph = "";
        Vector<String> lines = new Vector<String>();
        while (lines.size() < mLineCount && mMbBufEnd < mMbBufLen) {
            byte[] paraBuf = TxtReadUtil.readParagraphForward(mMbBufEnd, mMbBuf, mMbBufLen);
            mMbBufEnd += paraBuf.length; // ÿ�ζ�ȡ�󣬼�¼������λ�ã���λ���Ƕ������λ��
            try {
                strParagraph = new String(paraBuf, TxtReadUtil.STR_CHAREST_NAME); // ת�����ƶ�GBK����
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "pageDown->ת������ʧ��", e);
            }
            String strReturn = "";
            // �滻���س����з�
            if (strParagraph.indexOf("\r\n") != -1) {
                strReturn = "\r\n";
                strParagraph = strParagraph.replaceAll("\r\n", "");
            } else if (strParagraph.indexOf("\n") != -1) {
                strReturn = "\n";
                strParagraph = strParagraph.replaceAll("\n", "");
            }
            if (strParagraph.length() == 0) {
                lines.add(strParagraph);
            }
            while (strParagraph.length() > 0) {
                // ��һ������
                int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth,
                        null);
                lines.add(strParagraph.substring(0, nSize));
                strParagraph = strParagraph.substring(nSize); // �õ�ʣ�������
                // ��������������ٻ�
                if (lines.size() >= mLineCount) {
                    break;
                }
            }
            // �����ҳ���һ��ֻ��ʾ��һ���֣�����¶�λ������λ��
            if (strParagraph.length() != 0) {
                try {
                    mMbBufEnd -= (strParagraph + strReturn).getBytes(TxtReadUtil.STR_CHAREST_NAME).length;
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG, "pageDown->��¼������λ��ʧ��", e);
                }
            }
        }
        return lines;
    }

    /**
     * �õ�����ҳ�Ľ���λ��
     */
    public void pageUp() {
        if (mMbBufBegin < 0){
            mMbBufBegin = 0;
        }
        Vector<String> lines = new Vector<String>();
        String strParagraph = "";
        while (lines.size() < mLineCount && mMbBufBegin > 0) {
            Vector<String> paraLines = new Vector<String>();
            byte[] paraBuf = readParagraphBack(mMbBufBegin);
            mMbBufBegin -= paraBuf.length; // ÿ�ζ�ȡһ�κ�,��¼��ʼ��λ��,�Ƕ��׿�ʼ��λ��
            try {
                strParagraph = new String(paraBuf, TxtReadUtil.STR_CHAREST_NAME);
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "pageUp->ת������ʧ��", e);
            }
            strParagraph = strParagraph.replaceAll("\r\n", "");
            strParagraph = strParagraph.replaceAll("\n", "");
            // ����ǿհ��У�ֱ�����
            if (strParagraph.length() == 0) {
                paraLines.add(strParagraph);
            }
            while (strParagraph.length() > 0) {
                // ��һ������
                int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth,
                        null);
                paraLines.add(strParagraph.substring(0, nSize));
                strParagraph = strParagraph.substring(nSize);
            }
            lines.addAll(0, paraLines);
        }

        while (lines.size() > mLineCount) {
            try {
                mMbBufBegin += lines.get(0).getBytes(TxtReadUtil.STR_CHAREST_NAME).length;
                lines.remove(0);
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "pageUp->��¼��ʼ��λ��ʧ��", e);
            }
        }
        mMbBufEnd = mMbBufBegin; // ����һҳ�Ľ����������һҳ����ʼ��
        return;
    }

    /**
     * ��ǰ��ҳ
     * 
     * @throws IOException error
     */
    public void prePage() throws IOException {
        if (mMbBufBegin <= 0) {
            mMbBufBegin = 0;
            mIsfirstPage = true;
            return;
        } else {
            mIsfirstPage = false;
        }
        mLines.clear();
        pageUp();
        mLines = pageDown();
    }

    /**
     * ��ȡָ��λ�õ���һ������
     * 
     * @param nFromPos λ��
     * @return byte[] �ֽ���
     */
    public byte[] readParagraphBack(int nFromPos) {
        int nEnd = nFromPos;
        int i;
        byte b0;
        byte b1;
        if ("UTF-16LE".equals(TxtReadUtil.STR_CHAREST_NAME)){
            i = nEnd - 2;
            while (i > 0) {
                b0 = mMbBuf.get(i);
                b1 = mMbBuf.get(i + 1);
                if (b0 == 0x0a && b1 == 0x00 && i != nEnd - 2) {
                    i += 2;
                    break;
                }
                i--;
            }

        } else if ("UTF-16BE".equals(TxtReadUtil.STR_CHAREST_NAME)){
            i = nEnd - 2;
            while (i > 0) {
                b0 = mMbBuf.get(i);
                b1 = mMbBuf.get(i + 1);
                if (b0 == 0x00 && b1 == 0x0a && i != nEnd - 2) {
                    i += 2;
                    break;
                }
                i--;
            }
        } else {
            i = nEnd - 1;
            while (i > 0) {
                b0 = mMbBuf.get(i);
                if (b0 == 0x0a && i != nEnd - 1) {// 0x0a��ʾ���з�
                    i++;
                    break;
                }
                i--;
            }
        }
        if (i < 0){
            i = 0;
        }
        int nParaSize = nEnd - i;
        int j;
        byte[] buf = new byte[nParaSize];
        for (j = 0; j < nParaSize; j++) {
            buf[j] = mMbBuf.get(i + j);
        }
        return buf;
    }

    
    /**
     * ���ñ���ͼ
     * @param bg ����
     */
    public void setBgBitmap(Bitmap bg){
        mBookBg = bg;
    }
    /**
     * �������ִ�С
     * @param fontSize �ļ���С
     */
    public void setMFontSize(int fontSize){
        this.mFontSize = fontSize;
        mLineCount = (int) (mVisibleHeight / fontSize) - 1;
    }

    /**
     *  ����ҳ����ʼ��
     * @param mbBufBegin ��ʼλ��
     */
    public void setMMbBufBegin(int mbBufBegin) {
        this.mMbBufBegin = mbBufBegin;
    }

    /**
     *  ����ҳ�������
     * @param mbBufEnd ����Ϊֹ
     */
    public void setMMbBufEnd(int mbBufEnd){
        this.mMbBufEnd = mbBufEnd;
    }
    /**
     *  ��ȡ��ʼλ��
     * @return ����λ��
     */
    public int getMMbBufBegin() {
        return mMbBufBegin;
    }
    /**
     * ��ȡ��һ����������
     * @return ����
     */
    public String getFirstLineText(){
        return mLines.size() > 0 ? mLines.get(0) : "";
    }
    /**
     * ��ȡ�ı���ɫ
     * @return ����ɫֵ
     */
    public int getMTextColor(){
        return mTextColor;
    }
    /**
     * �����ı���ɫ
     * @param textColor ������ɫ
     */
    public void setMTextColor(int textColor){
        this.mTextColor = textColor;
    }
    /**
     * ��ȡ�ֽ�������
     * @return ���س���
     */
    public int getMMbBufLen(){
        return mMbBufLen;
    }
    /**
     * ��ȡ�ļ�����β
     * @return ����
     */
    public int getMMbBufEnd() {
        return mMbBufEnd;
    }

}
