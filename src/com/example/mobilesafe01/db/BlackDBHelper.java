  
package com.example.mobilesafe01.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/** 
 * ClassName:BlackDBHelper <br/> 
 * Function: 创建数据库的类
 * Date:     2016年9月7日 上午12:35:39 <br/> 
 * @author   ming001 
 * @version       
 */
public class BlackDBHelper extends SQLiteOpenHelper {
    
    public BlackDBHelper(Context context) {
        this(context, BlackDB.NAME, null, BlackDB.VERSION);
    }

    public BlackDBHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(BlackDB.BlackTable.CREATE_SQL );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
  