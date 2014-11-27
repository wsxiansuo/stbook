package com.sxs.styd.stbook.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

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
    
    
    
    
    
    
    
    
    
  //操作数据库MARK表的操作
}
