  
package com.example.mobilesafe01.service;

import com.example.mobilesafe01.utils.LogUtils;
import com.example.mobilesafe01.utils.ProcessUtil;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

/** 
 * ClassName:LockScreenCleanService <br/> 
 * Function: 锁屏自动清理后台程序,注册动态 动态广播
 * Date:     2016年9月17日 下午4:17:46 <br/> 
 * @author   ming001 
 * @version       
 */
public class LockScreenCleanService extends Service {

    private ScreenOffReceiver screenOffReceiver;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        screenOffReceiver = new ScreenOffReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(screenOffReceiver, filter );
    }
    
    class ScreenOffReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            
            ProcessUtil.killAllProcess(context);
            
            LogUtils.e("清理完毕");
        }
        
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //释放资源
        if (screenOffReceiver!=null) {
            unregisterReceiver(screenOffReceiver);
        }
    }

}
  