package com.sxs.styd.stbook.data;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sxs.styd.stbook.vo.BookVO;

/**.
 *  ���ݿ������
 * @author user
 *
 */
public class DBManager {
    public static DBManager dbManager;
    
    private BookDB bookDB;
    
    private SQLiteDatabase db;
    /**.
     * ����
     */
    public DBManager(){
    }
    /**.
     * ���� ��ȡ����
     * @return dbʵ��
     */
    public static DBManager getInstance(){
        if (dbManager == null){
            dbManager = new DBManager();
        }
        return dbManager;
    }
    /**.
     * ����ɽ����
     * @param context ��������ʾ
     */
    public void setContext(Context context){
        bookDB = new BookDB(context);
        db = bookDB.getWritableDatabase();
    }
    
    //�������ݿ�Book��Ĳ���
    /**.
     * ����typeId��ȡ�б� �� ��ȡ�ղ��б�
     * @return ����book�б�
     */
    public ArrayList<BookVO> getBookList() {   
        ArrayList<BookVO> listData = new ArrayList<BookVO>();
        Cursor c = db.rawQuery("SELECT * FROM " + BookDB.TABLE_BOOK + " ORDER BY "+BookDB.LAST_TIME+" DESC", null);  
        while (c.moveToNext()) {
            BookVO map = new BookVO();  
            map.id = c.getString(c.getColumnIndex(BookDB.ID)); 
            map.name = c.getString(c.getColumnIndex(BookDB.NAME)); 
            map.path = c.getString(c.getColumnIndex(BookDB.PATH)); 
            map.parent = c.getString(c.getColumnIndex(BookDB.PARENT));
            map.lastPostion = c.getInt(c.getColumnIndex(BookDB.LAST_POSTION));
            map.lastTime = c.getLong(c.getColumnIndex(BookDB.LAST_TIME));
            listData.add(map);  
        }  
        c.close();
        return listData;  
    } 
    /**.
     * ɾ��ͼ��Ŀ¼
     * @param id ͼ����
     */
    public void deleteBookById(String id){
        //��������  
        db.delete(BookDB.TABLE_BOOK, BookDB.ID + " = ?", new String[]{id});
    }
    /**.
     * ����ͼ����޸�ʱ���ͽ���
     * @param id ͼ����
     * @param lastPostion �Ķ�����
     * @param lastTime ����ʱ��
     */
    public void updateBookById(String id, String lastPostion, String lastTime){
        ContentValues cv = new ContentValues();
        cv.put(BookDB.LAST_POSTION, lastPostion);
        cv.put(BookDB.LAST_TIME, lastTime);
        db.update(BookDB.TABLE_BOOK, cv, BookDB.ID + " = ?", new String[]{id});
    }
    /**.
     * ��������
     * @param name ͼ������
     * @param path ͼ��·��
     * @param parent ͼ��ĸ�Ŀ¼
     * @param lastPostion ͼ������½���
     * @param lastTime  ͼ������²���ʱ��
     */
    public void insertBookToDB(String name, String path, String parent, String lastPostion, String lastTime){
        ContentValues cv = new ContentValues();
        cv.put(BookDB.NAME, name);
        cv.put(BookDB.PATH, path);
        cv.put(BookDB.PARENT, parent);
        cv.put(BookDB.LAST_POSTION, lastPostion);
        cv.put(BookDB.LAST_TIME, lastTime);
        db.insert(BookDB.TABLE_BOOK, BookDB.NAME , cv);
    }
    
    
    
    
    
  //�������ݿ�MARK��Ĳ���
}
