package com.sxs.styd.stbook.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

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
    
    
    
    
    
    
    
    
    
  //�������ݿ�MARK��Ĳ���
}
