  
package com.example.mobilesafe01.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.Context;

/** 
 * ClassName:ServiceUtil <br/> 
 * Function: 判断当前服务是否激活 
 * Date:     2016年9月8日 下午9:09:13 <br/> 
 * @author   ming001 
 * @version       
 */
public class ServiceUtil {

    public static boolean isActivate(Context context,Class<? extends Service> clazz) {
        boolean flag = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> runningServices = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (RunningServiceInfo Info : runningServices) {
            String className = Info.service.getClassName();
            if (className.equals(clazz.getName())) {
                flag = true;
            }
        }
        return flag;
    }

}
  