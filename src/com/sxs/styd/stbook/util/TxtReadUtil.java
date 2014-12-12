package com.sxs.styd.stbook.util;

import java.nio.MappedByteBuffer;

public class TxtReadUtil {
    public static final String STR_CHAREST_NAME = "GBK";
    
    /**
     * 读取指定位置的下一个段落
     * 
     * @param nFromPos 位置
     * @return byte[] 字节流
     */
    public static byte[] readParagraphForward(int nFromPos, MappedByteBuffer mMbBuf, int mMbBufLen){
        int nStart = nFromPos;
        int i = nStart;
        byte b0;
        byte b1;
        // 根据编码格式判断换行
        if ("UTF-16LE".equals(STR_CHAREST_NAME)){
            while (i < mMbBufLen - 1) {
                b0 = mMbBuf.get(i++);
                b1 = mMbBuf.get(i++);
                if (b0 == 0x0a && b1 == 0x00) {
                    break;
                }
            }
        } else if ("UTF-16BE".equals(STR_CHAREST_NAME)){
            while (i < mMbBufLen - 1) {
                b0 = mMbBuf.get(i++);
                b1 = mMbBuf.get(i++);
                if (b0 == 0x00 && b1 == 0x0a) {
                    break;
                }
            }
        } else {
            while (i < mMbBufLen) {
                b0 = mMbBuf.get(i++);
                if (b0 == 0x0a) {
                    break;
                }
            }
        }
        int nParaSize = i - nStart;
        byte[] buf = new byte[nParaSize];
        for (i = 0; i < nParaSize; i++) {
            buf[i] = mMbBuf.get(nFromPos + i);
        }
        return buf;
    }

}
