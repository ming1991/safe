  
package com.example.mobilesafe01.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.SyncStateContract.Constants;

/** 
 * ClassName:SPUtil <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Date:     2016年9月3日 下午10:53:49 <br/> 
 * @author   ming001 
 * @version       
 */
public class SPUtil {

    public static boolean getBoolean(Context context,String key,Boolean deValue) {
        SharedPreferences sp = context.getSharedPreferences(Constant.SP_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, deValue);
    }
    
    public static void putBoolean(Context context,String key,Boolean value) {
        SharedPreferences sp = context.getSharedPreferences(Constant.SP_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).commit();
    }
    
    
    
    //获取密码
    public static String getString(Context context,String key,String deValue) {
        SharedPreferences sp = context.getSharedPreferences(Constant.SP_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, deValue);
    }
    
    
    //保存密码
    public static void putString(Context context,String key,String value) {
        SharedPreferences sp = context.getSharedPreferences(Constant.SP_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();
    }
    
    
    public static int getInt(Context context,String key,int deValue) {
        SharedPreferences sp = context.getSharedPreferences(Constant.SP_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getInt(key, deValue);
    }
    
    
    public static void putInt(Context context,String key,int value) {
        SharedPreferences sp = context.getSharedPreferences(Constant.SP_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().putInt(key, value).commit();
    }
}
  