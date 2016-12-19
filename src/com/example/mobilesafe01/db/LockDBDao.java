  
package com.example.mobilesafe01.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/** 
 * ClassName:LockDBDao <br/> 
 * Function: 存储加锁的程序 
 * Date:     2016年9月18日 下午9:25:04 <br/> 
 * @author   ming001 
 * @version       
 */
public class LockDBDao {
    //添加数据
    public static boolean insert(Context context,String packageName){
        LockDBHelper lockDBHelper = new LockDBHelper(context);
        SQLiteDatabase db = lockDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LockDB.LockTable.PACKAGE_NAME, packageName);
        long insert = db.insert(LockDB.LockTable.TABLE_NAME, null, values );
        db.close();
        
        // uri : 要通知的URI
        // observer : 要通知的观察者.null,代表通知所有的
        // 通知内容观察者数据发生了变化
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.parse("content://lock");
        contentResolver.notifyChange(uri , null);
        return insert!=-1;
    }
    
    //删除数据
    public static boolean delete(Context context,String packageName){
        LockDBHelper lockDBHelper = new LockDBHelper(context);
        SQLiteDatabase db = lockDBHelper.getWritableDatabase();
        String whereClause = LockDB.LockTable.PACKAGE_NAME +" = ?";
        String[] whereArgs = new String[]{packageName};
        int delete = db.delete(LockDB.LockTable.TABLE_NAME, whereClause , whereArgs );
        db.close();
        
        //通知内容观察者,数据发生改变
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.parse("content://lock");
        contentResolver.notifyChange(uri , null);
        return delete>0;
    }
    
    //判断是否是加锁程序
    public static boolean query(Context context,String packageName){
        boolean flag = false;
        LockDBHelper lockDBHelper = new LockDBHelper(context);
        SQLiteDatabase db = lockDBHelper.getWritableDatabase();
        String[] columns = new String[]{LockDB.LockTable.PACKAGE_NAME};
        String selection = LockDB.LockTable.PACKAGE_NAME +" = ?";
        String[] selectionArgs = new String[]{packageName};
        Cursor cursor = db.query(LockDB.LockTable.TABLE_NAME, columns , selection , selectionArgs, null, null, null);
        if (cursor!=null) {
            if (cursor.moveToFirst()) {
                flag = true; 
            }
            cursor.close();
        }
        db.close();
        return flag;
    }
    
    
    //查询所有加锁的程序
    public static List<String> queryAll(Context context){
        List<String> list = new ArrayList<String>();
        LockDBHelper lockDBHelper = new LockDBHelper(context);
        SQLiteDatabase db = lockDBHelper.getWritableDatabase();
        String[] columns = new String[]{LockDB.LockTable.PACKAGE_NAME};
        Cursor cursor = db.query(LockDB.LockTable.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor!=null) {
            while(cursor.moveToNext()){
                String packageName = cursor.getString(0);
                list.add(packageName);
            }
            cursor.close();
        }
        db.close();
        return list;
    }
}
  