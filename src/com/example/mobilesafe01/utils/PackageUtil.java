  
package com.example.mobilesafe01.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/** 
 * ClassName:PackageUtil <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Date:     2016年9月2日 下午8:09:42 <br/> 
 * @author   ming001 
 * @version       
 */
public class PackageUtil {

    public static String getVersionName(Context context) {
        String versionName = "";
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (NameNotFoundException e) {
            versionName="未知";
            e.printStackTrace();
        }
        
       return versionName;
    }

    public static int getVersionCode(Context context) {
        int versionCode = 1;
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
       return versionCode;
        
    }

}
  