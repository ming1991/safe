  
package com.example.mobilesafe01.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/** 
 * ClassName:LockDBHelper <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Date:     2016年9月18日 下午9:07:15 <br/> 
 * @author   ming001 
 * @version       
 */
public class LockDBHelper extends SQLiteOpenHelper {

    public LockDBHelper(Context context) {
        super(context, LockDB.NAME, null, LockDB.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        
        db.execSQL(LockDB.LockTable.CREATE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
  