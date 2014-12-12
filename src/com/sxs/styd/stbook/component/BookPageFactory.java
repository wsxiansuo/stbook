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
 * 书页
 * @author user
 *
 */
public class BookPageFactory {
    StringBuilder  word;
    private static final String TAG = "BookPageFactory";
    private File bookFile = null;
    private int mBackColor = 0xffff9e85; // 背景颜色
    private Bitmap mBookBg = null;
    private int mFontSize = 20;
    private boolean mIsfirstPage;
    private boolean mIslastPage;
    private Vector<String> mLines = new Vector<String>();
    private MappedByteBuffer mMbBuf = null; // 内存中的图书字符
    private int mMbBufBegin = 0; // 当前页起始位置
    private int mMbBufEnd = 0; // 当前页终点位置

    private int mMbBufLen = 0; // 图书总长度

    private int mTextColor = Color.rgb(28, 28, 28);

    private int marginHeight = 15; // 上下与边缘的距离
    private int marginWidth = 15; // 左右与边缘的距离
    private int mHeight;
    private int mLineCount; // 每页可以显示的行数
    private Paint mPaint;

    private float mVisibleHeight; // 绘制内容的宽
    private float mVisibleWidth; // 绘制内容的宽
    private int mWidth;
    /**
     * 构造工厂
     * @param w 宽度
     * @param h 高度
     */
    public BookPageFactory(int w, int h) {
        mWidth = w;
        mHeight = h;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // 画笔
        mPaint.setTextAlign(Align.LEFT); // 做对其
        mPaint.setTextSize(mFontSize); // 字体大小
        mPaint.setColor(mTextColor); // 字体颜色
        mVisibleWidth = mWidth - marginWidth * 2;
        mVisibleHeight = mHeight - marginHeight * 2;
        mLineCount = (int) (mVisibleHeight / mFontSize) - 1; // 可显示的行数,-1是因为底部显示进度的位置容易被遮住
    }
    /**
     * 获取文字大小
     * @return 大小
     */
    public int getMFontSize() {
        return mFontSize;
    }
    /**
     * 获取最小行数
     * @return 行数
     */
    public int getmLineCount() {
        return mLineCount;
    }
    /**
     * 是否是第一页
     * @return bool
     */
    public boolean isfirstPage() {
        return mIsfirstPage;
    }
    /**
     * 是否是最后一页
     * @return bool
     */
    public boolean islastPage() {
        return mIslastPage;
    }

    /**
     * 向后翻页
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
        mMbBufBegin = mMbBufEnd; // 下一页页起始位置=当前页结束位置
        mLines = pageDown();
    }
    /**
     * 当前页
     * @throws IOException IOE
     */
    public void currentPage() throws IOException {
        mLines.clear();
        mLines = pageDown();
    }
    /**
     * 绘制操作
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
     * @param strFilePath 路径
     * @param begin 开始
     *            表示书签记录的位置，读取书签时，将begin值给m_mbBufEnd，在读取nextpage，及成功读取到了书签
     *            记录时将m_mbBufBegin开始位置作为书签记录
     * @throws IOException Error
     */
    @SuppressWarnings("resource")
    public void openbook(String strFilePath, int begin) throws IOException {
        bookFile = new File(strFilePath);
        long lLen = bookFile.length();
        mMbBufLen = (int) lLen;
        mMbBuf = new RandomAccessFile(bookFile, "r").getChannel().map(
                FileChannel.MapMode.READ_ONLY, 0, lLen);
        Log.d(TAG, "total lenth：" + mMbBufLen);
        // 设置已读进度
        if (begin >= 0) {
            mMbBufBegin = begin;
            mMbBufEnd = begin;
        }
    }
    
    /**
     * 画指定页的下一页
     * 
     * @return 下一页的内容 Vector<String>
     */
    public Vector<String> pageDown() {
        mPaint.setTextSize(mFontSize);
        mPaint.setColor(mTextColor);
        String strParagraph = "";
        Vector<String> lines = new Vector<String>();
        while (lines.size() < mLineCount && mMbBufEnd < mMbBufLen) {
            byte[] paraBuf = TxtReadUtil.readParagraphForward(mMbBufEnd, mMbBuf, mMbBufLen);
            mMbBufEnd += paraBuf.length; // 每次读取后，记录结束点位置，该位置是段落结束位置
            try {
                strParagraph = new String(paraBuf, TxtReadUtil.STR_CHAREST_NAME); // 转换成制定GBK编码
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "pageDown->转换编码失败", e);
            }
            String strReturn = "";
            // 替换掉回车换行符
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
                // 画一行文字
                int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth,
                        null);
                lines.add(strParagraph.substring(0, nSize));
                strParagraph = strParagraph.substring(nSize); // 得到剩余的文字
                // 超出最大行数则不再画
                if (lines.size() >= mLineCount) {
                    break;
                }
            }
            // 如果该页最后一段只显示了一部分，则从新定位结束点位置
            if (strParagraph.length() != 0) {
                try {
                    mMbBufEnd -= (strParagraph + strReturn).getBytes(TxtReadUtil.STR_CHAREST_NAME).length;
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG, "pageDown->记录结束点位置失败", e);
                }
            }
        }
        return lines;
    }

    /**
     * 得到上上页的结束位置
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
            mMbBufBegin -= paraBuf.length; // 每次读取一段后,记录开始点位置,是段首开始的位置
            try {
                strParagraph = new String(paraBuf, TxtReadUtil.STR_CHAREST_NAME);
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "pageUp->转换编码失败", e);
            }
            strParagraph = strParagraph.replaceAll("\r\n", "");
            strParagraph = strParagraph.replaceAll("\n", "");
            // 如果是空白行，直接添加
            if (strParagraph.length() == 0) {
                paraLines.add(strParagraph);
            }
            while (strParagraph.length() > 0) {
                // 画一行文字
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
                Log.e(TAG, "pageUp->记录起始点位置失败", e);
            }
        }
        mMbBufEnd = mMbBufBegin; // 上上一页的结束点等于上一页的起始点
        return;
    }

    /**
     * 向前翻页
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
     * 读取指定位置的上一个段落
     * 
     * @param nFromPos 位置
     * @return byte[] 字节流
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
                if (b0 == 0x0a && i != nEnd - 1) {// 0x0a表示换行符
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
     * 设置背景图
     * @param bg 背景
     */
    public void setBgBitmap(Bitmap bg){
        mBookBg = bg;
    }
    /**
     * 设置文字大小
     * @param fontSize 文件大小
     */
    public void setMFontSize(int fontSize){
        this.mFontSize = fontSize;
        mLineCount = (int) (mVisibleHeight / fontSize) - 1;
    }

    /**
     *  设置页面起始点
     * @param mbBufBegin 起始位置
     */
    public void setMMbBufBegin(int mbBufBegin) {
        this.mMbBufBegin = mbBufBegin;
    }

    /**
     *  设置页面结束点
     * @param mbBufEnd 结束为止
     */
    public void setMMbBufEnd(int mbBufEnd){
        this.mMbBufEnd = mbBufEnd;
    }
    /**
     *  获取起始位置
     * @return 返回位置
     */
    public int getMMbBufBegin() {
        return mMbBufBegin;
    }
    /**
     * 获取第一行文字内容
     * @return 文字
     */
    public String getFirstLineText(){
        return mLines.size() > 0 ? mLines.get(0) : "";
    }
    /**
     * 获取文本颜色
     * @return 返回色值
     */
    public int getMTextColor(){
        return mTextColor;
    }
    /**
     * 设置文本颜色
     * @param textColor 文字颜色
     */
    public void setMTextColor(int textColor){
        this.mTextColor = textColor;
    }
    /**
     * 获取字节流长度
     * @return 返回长度
     */
    public int getMMbBufLen(){
        return mMbBufLen;
    }
    /**
     * 获取文件流结尾
     * @return 长度
     */
    public int getMMbBufEnd() {
        return mMbBufEnd;
    }

}
