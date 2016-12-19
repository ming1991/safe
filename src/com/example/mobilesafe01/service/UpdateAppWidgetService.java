  
package com.example.mobilesafe01.service;

import com.example.mobilesafe01.R;
import com.example.mobilesafe01.receiver.MyAppWidgetProvider;
import com.example.mobilesafe01.utils.ProcessUtil;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.View;
import android.widget.RemoteViews;

/** 
 * ClassName:UpdateAppWidget <br/> 
 * Function: 实时更新桌面控件的内容
 * Date:     2016年9月16日 下午8:55:44 <br/> 
 * @author   ming001 
 * 
 * ◦实施更新状态,是通过死循环来实现的
 * ◦一键清理是通过后台服务来完成

 * @version       
 */
public class UpdateAppWidgetService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        //@param provider      The {@link ComponentName} for the {@link
        //    * android.content.BroadcastReceiver BroadcastReceiver} provider
        //    *                      for your AppWidget.
        
        new Thread(){
            @Override
            public void run() {
                super.run();
                
                while (true) {
                  //要监听的广播
                    ComponentName provider = new ComponentName(UpdateAppWidgetService.this, MyAppWidgetProvider.class);
                    //远程视图 ,要展示的视图The RemoteViews object to show.
                    RemoteViews views = new RemoteViews(UpdateAppWidgetService.this.getPackageName(), R.layout.process_widget);
                    
                    //更改远程视图内TextView控件的内容
                    int runningProcessCount = ProcessUtil.getRunningProcessCount(UpdateAppWidgetService.this);
                    views.setTextViewText(R.id.process_count, "正在运行的进程:"+runningProcessCount+"个");//显示正在运行的进程时
                    
                    long[] ramData = ProcessUtil.getRamData(UpdateAppWidgetService.this);
                    views.setTextViewText(R.id.process_memory, "可用内存:"+Formatter.formatFileSize(UpdateAppWidgetService.this, ramData[1]));//显示可用的内存大小
                    
                    //开启后台服务一键清理
                    Intent intent = new Intent(UpdateAppWidgetService.this, OneKeyCleanService.class);
                    //开启延时意图
                    PendingIntent pendingIntent = PendingIntent.getService(UpdateAppWidgetService.this, 101, intent , PendingIntent.FLAG_UPDATE_CURRENT);
                    
                    views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent );
                    
                    
                    //获取AppWidgetManager实例
                    AppWidgetManager.getInstance(UpdateAppWidgetService.this).updateAppWidget(provider, views);
                    
                    // 休眠2秒,节省系统资源,否则手机会很卡
                    SystemClock.sleep(1000);

                }
            }
        }.start();
        
        
    }
   

}
  