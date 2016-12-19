  
package com.example.mobilesafe01.service;

import java.util.ArrayList;
import java.util.List;

import com.example.mobilesafe01.LockScreenActivity;
import com.example.mobilesafe01.db.LockDBDao;
import com.example.mobilesafe01.service.AppLockService.LockObserver;
import com.example.mobilesafe01.utils.LogUtils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

/** 
 * ClassName:AppLockService <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Date:     2016年9月19日 下午2:40:16 <br/> 
 * @author   ming001 
 * @version  
 * 
 *      优化,开启服务是就读取所有锁屏的应用到内存中,通过内容观察者,监视自己创的数据库数据的变化,并让list重新获取数据库中的所有数据
 *      
 *      
 *      ◦锁屏时,清空放行集合
 *      ◦锁屏时,停止服务,解锁时,开启服务
 *      ◦通过内容观察者,监听数据集合的变化
 */

public class AppLockService extends Service {

    public static final String PACKAGE_NAME = "packagename";
    
    //标志位监视循环是否继续执行
    private boolean isWatch = true;

    private FreeReceiver receiver;
    
    //存储放行的应用到集合中
    private List<String> freeList = new ArrayList<String>();//输如密码正确的应用

    private List<String> lockList = new ArrayList<String>();

    private ContentResolver contentResolver;

    private LockObserver lockObserver;

    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public void onCreate() {//死循环,不断监视启动的前端应用,并启动锁屏界面
        super.onCreate();
        
        initReceiver();
        
        initObserver();
        
        //一次加载所有数据到内存中
        lockList = LockDBDao.queryAll(this);//获取所有加锁应用的广播到内存中, 
        //问题:初次加载的时候有数据,之后添加加锁应用时,list不能更新数据,通过内容观察着,监视数据内容的改变,来重新加载数据
        
        new Thread(new LockRunnable()).start();
        
        
        
    }

    //数据库数据发生改变时,重新获取数据库中所有数据
    private void initObserver() {
        contentResolver = getContentResolver();
        
        Uri uri = Uri.parse("content://lock");
        
        lockObserver = new LockObserver(new Handler());
        contentResolver.registerContentObserver(uri , true, lockObserver);
           
    }
    
    //内容观察着
    class LockObserver extends ContentObserver{

        public LockObserver(Handler handler) {
            super(handler);
            
          //数据库数据发生改变时,重新获取数据库中所有数据
            lockList.clear();
            lockList = LockDBDao.queryAll(AppLockService.this);
        }
        
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
        }
        
    }

    public void initReceiver() {
        receiver = new FreeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(LockScreenActivity.INTENT_ACTION_MING);  //锁屏界面发过来发行的广播
        
        filter.addAction(Intent.ACTION_SCREEN_OFF);//开锁屏广播
        filter.addAction(Intent.ACTION_SCREEN_ON);
        
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(receiver, filter );
    }
    
    //子线程中执行耗时操作
    public class LockRunnable implements Runnable{

        @Override
        public void run() {
            
            while (isWatch) {
                
                LogUtils.e("ming");
                
                SystemClock.sleep(1000);
                
                ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                List<RunningTaskInfo> runningTasks = am.getRunningTasks(1);//获取最前的一个任务栈
                RunningTaskInfo runningTaskInfo = runningTasks.get(0);
                ComponentName topActivity = runningTaskInfo.topActivity;//获取任务栈最顶端的activity
                String packageName = topActivity.getPackageName();
                
                //判断,放行的应用(密码输如正确的)
                if (freeList.contains(packageName)) {
                    continue;
                }
                
                //查询数据库是否是加锁应用
                //boolean query = LockDBDao.query(AppLockService.this, packageName);  优化
                
                
                if (lockList.contains(packageName)) {//如果是加锁程序,跳转到加锁页面
                    
                    Intent intent = new Intent(AppLockService.this, LockScreenActivity.class);
                    
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //服务中跳转到activity界面需要添加一个任务栈,时set而不是add
                    
                    intent.putExtra(PACKAGE_NAME, packageName);
                    
                    startActivity(intent);
                }
                
            }
            
        }
        
    }
    
    public class FreeReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {//锁屏广播,停止循环
                isWatch = false;
                freeList.clear();  //清空集合,锁屏再开屏之后需再次输入密码
            }
            if (Intent.ACTION_SCREEN_ON.equals(action)) {//开屏广播,开启循环
                
                isWatch = true;
                new Thread(new LockRunnable()).start();//再次开启循环
            }
            if (LockScreenActivity.INTENT_ACTION_MING.equals(action) ) {//锁屏界面发过来发行的广播
                
            }
            String packageName = intent.getStringExtra(PACKAGE_NAME);
            freeList.add(packageName);  //添加放行的应用到集合中
            
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        //服务结束的时候因为停掉循环
        isWatch = false;
        
        //释放资源,解除注册
        if (receiver!=null) {
            unregisterReceiver(receiver);
        }
        if (lockObserver!=null) {
            contentResolver.unregisterContentObserver(lockObserver);
        }
        
        
    }

}
  