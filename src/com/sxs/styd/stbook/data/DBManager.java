package com.sxs.styd.stbook.data;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sxs.styd.stbook.vo.BookVO;
import com.sxs.styd.stbook.vo.SectionVO;

/**.
 *  数据库操作类
 * @author user
 *
 */
public class DBManager {
    public static DBManager dbManager;
    
    private BookDB bookDB;
    
    private SQLiteDatabase db;
    /**.
     * 构造
     */
    public DBManager(){
    }
    /**.
     * 单例 获取对象
     * @return db实例
     */
    public static DBManager getInstance(){
        if (dbManager == null){
            dbManager = new DBManager();
        }
        return dbManager;
    }
    /**.
     * 设置山下文
     * @param context 上下文显示
     */
    public void setContext(Context context){
        bookDB = new BookDB(context);
        db = bookDB.getWritableDatabase();
    }
    
    //操作数据库Book表的操作
    /**.
     * 根据typeId获取列表 或 获取收藏列表
     * @return 返回book列表
     */
    public ArrayList<BookVO> getBookList() {   
        String sql = "SELECT * FROM " + BookDB.TABLE_BOOK + " ORDER BY "+BookDB.LAST_TIME+" DESC";
        return getBookListBySql(sql, null);
    } 
    public ArrayList<BookVO> getBookListByState(){
        String sql = "SELECT * FROM " + BookDB.TABLE_BOOK + " where "+BookDB.STATE+"=?";
        return getBookListBySql(sql, new String[]{"0"});
    }
    private ArrayList<BookVO> getBookListBySql(String sql, String[] selectionArgs){
        ArrayList<BookVO> listData = new ArrayList<BookVO>();
        Cursor c = db.rawQuery(sql, selectionArgs);
        while (c.moveToNext()) {
            BookVO map = new BookVO();  
            map.id = c.getString(c.getColumnIndex(BookDB.ID)); 
            map.name = c.getString(c.getColumnIndex(BookDB.NAME)); 
            map.path = c.getString(c.getColumnIndex(BookDB.PATH)); 
            map.parent = c.getString(c.getColumnIndex(BookDB.PARENT));
            map.lastPostion = c.getInt(c.getColumnIndex(BookDB.LAST_POSTION));
            map.lastTime = c.getLong(c.getColumnIndex(BookDB.LAST_TIME));
            map.recordState = c.getString(c.getColumnIndex(BookDB.STATE));
            listData.add(map);  
        }  
        c.close();
        return listData;  
    }
    /**.
     * 删除图书目录
     * @param id 图书编号
     */
    public void deleteBookById(String id){
        //更新数据  
        db.delete(BookDB.TABLE_BOOK, BookDB.ID + " = ?", new String[]{id});
        db.delete(BookDB.TABLE_SECTION, BookDB.BOOK_ID + " = ?", new String[]{id});
    }
    /**.
     * 更新图书的修改时间点和进度
     * @param id 图书编号
     * @param lastPostion 阅读进度
     * @param lastTime 更新时间
     */
    public void updateBookById(String id, String lastPostion, String lastTime, String state){
        ContentValues cv = new ContentValues();
        cv.put(BookDB.LAST_POSTION, lastPostion);
        cv.put(BookDB.LAST_TIME, lastTime);
        cv.put(BookDB.STATE, state);
        db.update(BookDB.TABLE_BOOK, cv, BookDB.ID + " = ?", new String[]{id});
    }
    /**.
     * 插入数据
     * @param name 图书名称
     * @param path 图书路径
     * @param parent 图书的父目录
     * @param lastPostion 图书的最新进度
     * @param lastTime  图书的最新操作时间
     */
    public void insertBookToDB(String name, String path, String parent, String lastPostion, String lastTime, String state){
        ContentValues cv = new ContentValues();
        cv.put(BookDB.NAME, name);
        cv.put(BookDB.PATH, path);
        cv.put(BookDB.PARENT, parent);
        cv.put(BookDB.LAST_POSTION, lastPostion);
        cv.put(BookDB.LAST_TIME, lastTime);
        cv.put(BookDB.STATE, state);
        db.insert(BookDB.TABLE_BOOK, BookDB.NAME , cv);
    }
    /**
     * 插入章节目录到数据库
     * @param item
     */
    public void insertSectionToDB(SectionVO item){
        ContentValues cv = new ContentValues();
        cv.put(BookDB.NAME, item.name);
        cv.put(BookDB.POSTION, item.postion);
        cv.put(BookDB.BOOK_ID, item.bookId);
        db.insert(BookDB.TABLE_SECTION, BookDB.NAME, cv);
    }
    public ArrayList<SectionVO> getSectionList(String id){
        ArrayList<SectionVO> listData = new ArrayList<SectionVO>();
        Cursor c = db.rawQuery("select * from " + BookDB.TABLE_SECTION + " where " + BookDB.BOOK_ID + "=?", new String[]{id});
        while (c.moveToNext()) {
            SectionVO item = new SectionVO();  
            item.sectionId = c.getString(c.getColumnIndex(BookDB.ID)); 
            item.name = c.getString(c.getColumnIndex(BookDB.NAME)); 
            item.bookId = id; 
            item.postion = c.getInt(c.getColumnIndex(BookDB.POSTION));
            listData.add(item);  
        }  
        c.close();
        return listData;
    }
    
    
    
  //操作数据库MARK表的操作
}
