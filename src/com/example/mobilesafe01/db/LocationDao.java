  
package com.example.mobilesafe01.db;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/** 
 * ClassName:LocationDao <br/> 
 * Function: 同过访问本地的已有数据库,查询号码归属地
 * Date:     2016年9月9日 下午7:14:09 <br/> 
 * @author   ming001 
 * @version       
 */
public class LocationDao {
    //查询条件
 // 手机号码 ^1[34578]\\d{5,9}$
    // 固定号码
    // 3 警报号码
    // 456 服务号码 短号
    //
    // 010 8 11
    // 0755 78 11 12
    //
    //
    public static String queryLocation(Context context,String num) {
        String city = "未知号码";
        File file = new File(context.getFilesDir(), "address.db");
        //文件的路径
        String path = file.getAbsolutePath();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        
        String regex = "^1[34578]\\d{5,9}$";
        if (num.matches(regex )) {
            String subNum = num.substring(0, 7);
            String sql = "select city from info where mobileprefix = ?";
            Cursor cursor = db.rawQuery(sql , new String[]{subNum});
            if (cursor!=null) {
                if (cursor.moveToFirst()) {
                     city = cursor.getString(0);
                }
            }
            cursor.close();
        }
        
        if (num.length()==3) {
            city = "报警电话";
        }
        
        if (num.length()==4||num.length()==5||num.length()==6) {
            city = "服务号码";
        }
        
        db.close();
        return city;
        
    }

}
  