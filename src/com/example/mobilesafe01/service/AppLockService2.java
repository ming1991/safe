  
package com.example.mobilesafe01.service;

import java.util.ArrayList;
import java.util.List;

import com.example.mobilesafe01.LockScreenActivity;
import com.example.mobilesafe01.db.LockDBDao;
import com.example.mobilesafe01.utils.LogUtils;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

/** 
 * ClassName:AppLockService2 <br/> 
 * Function: 程序锁服务2
 * Date:     2016年9月20日 下午3:58:14 <br/> 
 * @author   ming001 
 * @version       
 */
public class AppLockService2 extends AccessibilityService {//关机自动停止,开机启动
    
    private List<String> lockList;
    private List<String> unLockList;
    private FreeRecevier receiver;
    private ContentResolver contentResolver;
    private LockObserver observer;

    @Override
    public void onCreate() {
        super.onCreate();
        //加载锁屏的所有数据
        lockList = LockDBDao.queryAll(AppLockService2.this);
        unLockList = new ArrayList<String>();
        //获取释放程序的数据
        initReceiver();
        //获取内容观察者,监视数据库变化
        initObserver();
        
        
    }

    public void initObserver() {
        contentResolver = getContentResolver();
        Uri uri = Uri.parse("content://lock");
        observer = new LockObserver(new Handler());
        contentResolver.registerContentObserver(uri, true, observer);
    }
    
    class LockObserver extends ContentObserver{

        public LockObserver(Handler handler) {
            super(handler);
        }
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            //数据数据发生改变时,更新集合中数据
            lockList.clear();
            lockList = LockDBDao.queryAll(AppLockService2.this);
            
        }
    }

    private void initReceiver() {
        receiver = new FreeRecevier();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(Integer.MAX_VALUE);
        filter.addAction(LockScreenActivity.INTENT_ACTION_MING);//
        //关机广播
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver, filter );
    }
    
    //将放行的包名存放到集合中
    class FreeRecevier extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {//关屏清空集合,开屏再次输入密码
                unLockList.clear();
            }
            if (LockScreenActivity.INTENT_ACTION_MING.equals(action)) {
                String packageName = intent.getStringExtra(AppLockService.PACKAGE_NAME);
                unLockList.add(packageName);
            }
            
        }
        
    }

    //当窗口呢界面发生改变时,调用此方法
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        
        LogUtils.e("onAccessibilityEvent");
        
        //获取包名
        String packageName = event.getPackageName().toString();
        
        if (lockList.contains(packageName)&&!unLockList.contains(packageName)) {
            
            Intent intent = new Intent(AppLockService2.this, LockScreenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(AppLockService.PACKAGE_NAME, packageName);//发送包名
            startActivity(intent );
        }
    }

    @Override
    public void onInterrupt() {

    }
    
    //释放资源
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver!=null) {
            unregisterReceiver(receiver);
        }
        
        if (observer!=null) {
            contentResolver.unregisterContentObserver(observer);
        }
    }

}
  