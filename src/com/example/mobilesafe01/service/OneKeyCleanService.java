  
package com.example.mobilesafe01.service;

import com.example.mobilesafe01.utils.ProcessUtil;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.Toast;

/** 
 * ClassName:OneKeyCleanService <br/> 
 * Function: 实现一键清理 后台进程的服务,
 * Date:     2016年9月16日 下午11:26:26 <br/> 
 * @author   ming001 
 * @version       
 */
public class OneKeyCleanService extends Service {

    private Context context;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    //实现一键清理的功能,并提示用户清理的数据
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        //获取清理前的数据
        int preRunningProcessCount = ProcessUtil.getRunningProcessCount(context);
        long preAvailMem = ProcessUtil.getRamData(context)[1];//可用内存
        
        ProcessUtil.killAllProcess(OneKeyCleanService.this);
        
        //获取清理之后的数据
        int NowRunningProcessCount = ProcessUtil.getRunningProcessCount(context);
        long NowAvailMem = ProcessUtil.getRamData(context)[1];
        
        //提示用户
        int killProcess = preRunningProcessCount-NowRunningProcessCount;//杀死线程
        
        //
        long cleanMem = Math.abs(NowAvailMem - preAvailMem);//清理内存
        
        String fileSize = Formatter.formatFileSize(context, cleanMem);
        
        if (killProcess>0) {
            Toast.makeText(context, "清理了" + killProcess + "个进程,释放了" + fileSize + "内存", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "手机很干净,无需清理", Toast.LENGTH_SHORT).show();
        }
        
        // 执行完毕之后,停掉自己
        stopSelf();
    }

}
  