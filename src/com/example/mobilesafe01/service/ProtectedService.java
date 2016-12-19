  
package com.example.mobilesafe01.service;

import com.example.mobilesafe01.R;
import com.example.mobilesafe01.activity.SplashActivity;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/** 
 * ClassName:ProtectedService <br/> 
 * Function: 保护自己的进程不被杀死 
 * 1.在开机时启动本服务,变成前台进程
 * 2.在杀死本服务的,再起启动本服务,
 * 3.多启动几个本程序的服务,在杀死一个的同时,启动另外几个服务
 * Date:     2016年9月16日 下午6:33:17 <br/> 
 * @author   ming001 
 * @version       
 */
public class ProtectedService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    //让我们的服务变成前台进程
    @Override
    public void onCreate() {
        super.onCreate();
        
        //真实意图,启动自己的程序
        Intent intent = new Intent(this, SplashActivity.class);
        //延时意图,通知系统的意图
        PendingIntent contentIntent = PendingIntent.getActivity(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                //设置图片
                .setSmallIcon(R.drawable.ic_launcher)  // the status icon
                .setTicker("ming黑马手机卫士")  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle("ming黑马手机卫士")  // the label
                .setContentText("正在保护你的安全")  // the contents of the entry
                .setContentIntent(contentIntent )  // The intent to send when clicked
                .build();
        startForeground(200, notification );
    }
    
    //在杀死本服务的时候,再次开启本服务
    @Override
    public void onDestroy() {
        super.onDestroy();
        
        Intent service = new Intent(this, ProtectedService.class);
        startService(service );
        
    }
    //// 当检测到自己被杀死的时候,重启本服务
    // 如果希望自己永远不被杀死,可以创建多个类似本服务的服务.
    // 当某一个服务被杀死时,启动其他几个同类服务.这样可以保证总有一个服务被开启,永远都不被杀死

}
  