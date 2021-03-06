package com.sxs.styd.stbook.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**.
 * 数据库类
 * @author user
 *
 */
public class BookDB extends SQLiteOpenHelper{
    
    private static final String DATABASE_NAME = "stbook.db";
    private static int DATABASE_VERSION = 1;
    
    public static String ID = "_id";
    public static String LAST_TIME = "lastTime";
    //book表格
    public static String TABLE_BOOK = "table_books";
    public static String PARENT = "parent";
    public static String PATH = "path";
    public static String NAME = "name";
    public static String LAST_POSTION = "lastPostion";
    public static String STATE = "state";
    
    //标签表格
    public static String TABLE_MARK = "table_mark";
    public static String BOOK_ID = "book_id";
    public static String POSTION = "postion";
    
    //图书目录
    public static final String TABLE_SECTION = "table_section";
    
    /**.
     * 构造方法
     * @param context 上下文参数
     */
    public BookDB(Context context) {
          super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlBook = "CREATE TABLE " + TABLE_BOOK + 
            "( "+ ID + " integer primary key autoincrement, " + PARENT + 
            " varchar(200), " + PATH + " varchar(200), " + NAME + 
            " varchar(200), " + LAST_POSTION + " float, " + STATE + " integer, " + LAST_TIME + " double)";
        String sqlMark = "CREATE TABLE " + TABLE_MARK + 
            "(" + ID + " integer primary key autoincrement, " + BOOK_ID + 
            " integer," + POSTION + " text, " + LAST_TIME + " text)";
        String sqlMenu = "CREATE TABLE " + TABLE_SECTION + 
            "(" + ID + " integer primary key autoincrement, " + BOOK_ID + 
            " integer," + POSTION + " text, " + NAME + " text)";
        db.execSQL(sqlBook);
        db.execSQL(sqlMark);
        db.execSQL(sqlMenu);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        
    }

}
