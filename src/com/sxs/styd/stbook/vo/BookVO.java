package com.sxs.styd.stbook.vo;
import java.io.Serializable;
/**.
 * ����VO
 * @author user
 *
 */
public class BookVO implements Serializable {
      /**
     * ���
     */
    private static final long serialVersionUID = 1L;
    public String id;
    public String name;
    public String parent;
    public String path;
    public int lastPostion; 
    public long lastTime;
}
