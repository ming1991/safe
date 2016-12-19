  
package com.example.mobilesafe01.db;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IInterface;

/** 
 * ClassName:VirusDBDao <br/> 
 * Function: 从数据库查询数据,文件是否是病毒
 * Date:     2016年9月22日 下午3:13:18 <br/> 
 * @author   ming001 
 * @version       
 */
public class VirusDBDao {
    public static boolean queryIsVirus(Context context, String md5){
        boolean flag = false;
        File path = new File(context.getFilesDir(), "antivirus.db");
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
        String sql = "select _id from datable where md5 = ?";
        Cursor cursor = db.rawQuery(sql , new String[]{md5});
        if (cursor!=null) {
            if (cursor.moveToFirst()) {
                flag = true;
            }
            cursor.close();
        }
        db.close();
        return flag;
        
    }
}
  