  
package com.example.mobilesafe01.db;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.example.mobilesafe01.bean.CommonNumChildBean;
import com.example.mobilesafe01.bean.CommonNumGroupBean;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/** 
 * ClassName:CommonNumDao <br/> 
 * Function: 获取常用号码数据库的数据
 * Date:     2016年9月11日 下午10:39:02 <br/> 
 * @author   ming001 
 * @version       
 */
public class CommonNumDao {

    public static List<CommonNumGroupBean> getAll(Context mContext) {
        List<CommonNumGroupBean> list  = new ArrayList<CommonNumGroupBean>();
        
        File filesDir = mContext.getFilesDir();
        File file = new File(filesDir, "commonnum.db");
        SQLiteDatabase db  =  SQLiteDatabase.openDatabase(file.getAbsolutePath(), null,  SQLiteDatabase.OPEN_READONLY);
        
        String sql = "select name,idx from classlist";
        Cursor cursor = db.rawQuery(sql , null);
        if (cursor!=null) {
            while(cursor.moveToNext()){
                
                String title = cursor.getString(0);
                String idx = cursor.getString(1);
                CommonNumGroupBean groupBean = new CommonNumGroupBean();
                groupBean.title = title;
                
                String sqlChild = "select name,number from table" +idx;
                Cursor cursor2 = db.rawQuery(sqlChild , null);
                
                ArrayList<CommonNumChildBean> arrayList = new ArrayList<CommonNumChildBean>();
                if (cursor2!=null) {
                    
                    while(cursor2.moveToNext()){
                        CommonNumChildBean  childBean = new CommonNumChildBean();
                        childBean.name = cursor2.getString(0);
                        childBean.num = cursor2.getString(1);
                        arrayList.add(childBean);
                    }
                    cursor2.close();
                }
                groupBean.child = arrayList;
                
                list.add(groupBean);
                
            }
            cursor.close();
        }
        
        db.close();
        
        return list;
    }

}
  