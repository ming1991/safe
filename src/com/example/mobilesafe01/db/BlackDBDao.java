  
package com.example.mobilesafe01.db;

import java.util.ArrayList;
import java.util.List;

import com.example.mobilesafe01.bean.BlackBean;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/** 
 * 电话号码的唯一性
 * ClassName:BlackDBDao <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Date:     2016年9月7日 上午1:06:41 <br/> 
 * @author   ming001 
 * @version       
 */
public class BlackDBDao {
    
    
    /**
     * DESC :  插入数据   true 代表插入成功  false代表插入失败
     * @param context
     * @param num
     * @param type
     */
    public  static boolean insert(Context context ,String num,int type){
        BlackDBHelper blackDBHelper = new BlackDBHelper(context);
        SQLiteDatabase db = blackDBHelper.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(BlackDB.BlackTable.C_NUM, num);
        values.put(BlackDB.BlackTable.C_TYPE, type);
        long insert = db.insert(BlackDB.BlackTable.TABLE_NAME, null, values );
        db.close();
        
        return insert!=-1;
    }
    
    /**
     * DESC :  . 删除数据,true 代表删除成功  false代表删除失败
     * @param context
     * @param num
     * @return
     */
    public static boolean delete(Context context,String num){
        BlackDBHelper blackDBHelper = new BlackDBHelper(context);
        SQLiteDatabase db = blackDBHelper.getWritableDatabase();
        
        String whereClause = BlackDB.BlackTable.C_NUM+" =?";
        int delete = db.delete(BlackDB.BlackTable.TABLE_NAME, whereClause , new String[]{num});
        db.close();
        return delete>0;
    }
    
    /**
     * DESC :  . 更新数据,true 代表更新成功  false代表更新失败
     * @param context
     * @param num
     * @param type
     * @return
     */
    public static boolean update(Context context,String num,int type){
        BlackDBHelper blackDBHelper = new BlackDBHelper(context);
        SQLiteDatabase db = blackDBHelper.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(BlackDB.BlackTable.C_TYPE, type);
        String whereClause = BlackDB.BlackTable.C_NUM+" =?";
        int update = db.update(BlackDB.BlackTable.TABLE_NAME, values , whereClause , new String[]{num});
        db.close();
        return update>0;
    }
    
    /**
     * DESC :  . 查询拦截的类型  -1代表查询失败
     * @param context
     * @param num
     * @return
     */
    public static int queryType(Context context,String num){
        int type = -1;
        BlackDBHelper blackDBHelper = new BlackDBHelper(context);
        SQLiteDatabase db = blackDBHelper.getReadableDatabase();
        String[] columns =new String[]{BlackDB.BlackTable.C_TYPE};
        String selection = BlackDB.BlackTable.C_NUM+" =?";
        String[] selectionArgs = new String[]{num};
        
        Cursor cursor = db.query(BlackDB.BlackTable.TABLE_NAME, columns , selection, selectionArgs, null, null, null);
        if (cursor!=null) {
            if (cursor.moveToFirst()) {
                type = cursor.getInt(0);
            }
        }
        cursor.close();
        db.close();
        return type;
    } 
    
    
    /**
     * DESC :  . 查询所有结果
     * @param context
     * @return
     */
    public static List<BlackBean> getAllInfo(Context context){
        
        List<BlackBean> list = new ArrayList<BlackBean>();
        
        BlackDBHelper blackDBHelper = new BlackDBHelper(context);
        SQLiteDatabase db = blackDBHelper.getReadableDatabase();
        
        String table = BlackDB.BlackTable.TABLE_NAME;
        String[] columns = new String[]{BlackDB.BlackTable.C_NUM,BlackDB.BlackTable.C_TYPE};
        
        Cursor cursor = db.query(table , columns, null, null, null, null, null);
        if (cursor!=null) {
            
            while(cursor.moveToNext()){
                //把数据封装在list集合中
                BlackBean blackBean = new BlackBean();
                String num = cursor.getString(0);
                int type = cursor.getInt(1);
                
                blackBean.num = num;
                blackBean.type = type;
                
                list.add(blackBean);
            }
        }
        cursor.close();
        db.close();
        
        return list;
        
    }
    
    //•需求 : 一次查询数据库中的一部分数据,而不是全部数据
    //•实现 : SQL语句 : select * from blacklist limit 20 offset 1

    public static List<BlackBean> getPart(Context context,int pageItem,int offset ){
        ArrayList<BlackBean> list = new ArrayList<BlackBean>();
        BlackDBHelper blackDBHelper = new BlackDBHelper(context);
        SQLiteDatabase db = blackDBHelper.getReadableDatabase();
        
        //String sql = "select num,type from blacklist limit 20 offset 1";
        
        String sql = "select "+BlackDB.BlackTable.C_NUM
                + ", "+BlackDB.BlackTable.C_TYPE+" from "
                +BlackDB.BlackTable.TABLE_NAME+" limit "+pageItem+" offset "+offset;
        Cursor cursor = db.rawQuery(sql , null);
        if (cursor!=null) {
            while(cursor.moveToNext()){
                BlackBean blackBean = new BlackBean();
                blackBean.num = cursor.getString(0);
                blackBean.type = cursor.getInt(1);
                
                list.add(blackBean);
            }
            cursor.close();
        }
        db.close();
        return list;
    }
}
  