
package com.example.mobilesafe01.service;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.example.mobilesafe01.bean.BlackBean;
import com.example.mobilesafe01.db.BlackDBDao;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

/**
 * ClassName:InterceptService <br/>
 * Function: 拦截短信和电话的服务
 * Date: 2016年9月8日 下午9:26:32 <br/>
 * 
 * @author ming001
 * @version
 */
public class InterceptService extends Service {

    private SmsReceiver smsReceiver;
    private TelephonyManager tm;
    private PhoneListener phoneListener;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        interceptPhone();
        interceptSms();
    }

    //监听外来电话
    private void interceptPhone() {
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        phoneListener = new PhoneListener();
        tm.listen(phoneListener , PhoneStateListener.LISTEN_CALL_STATE);
        
    }
    
   /* *//** @hide *//*
    @SystemApi
    public boolean endCall() {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null)
                return telephony.endCall();
        } catch (RemoteException e) {
            Log.e(TAG, "Error calling ITelephony#endCall", e);
        }
        return false;
    }*/
    
    //需要添加权限 call phone
    //<uses-permission android:name="android.permission.CALL_PHONE"/>
    // <uses-permission android:name="android.permission.READ_CALL_LOG"/>
   // <uses-permission android:name="android.permission.WRITE_CALL_LOG"/>
    //电话拦截(来电)
    
    class PhoneListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, final String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            //响铃状态
            if (state==TelephonyManager.CALL_STATE_RINGING) {
                
                //从数据库获取号码的拦截类型
                int type = BlackDBDao.queryType(InterceptService.this, incomingNumber);
                if (type == BlackBean.TYPE_NUM || type == BlackBean.TYPE_ALL) {
                    // 挂断电话
                 // 获取远程服务
                    try {

                        // ServiceManager
                        // .getService(Context.TELEPHONY_SERVICE)
                        
                        // 1.拿字节码文件
                        Class<?> clazz =
                                Class.forName("android.os.ServiceManager");
                        // 2. 获取方法
                        // name: 方法名
                        // parameterTypes: 参数列表对应的字节码文件
                        Method method =
                                clazz.getMethod("getService", String.class);
                        // 3.执行方法
                        // method: 对象
                        // args :具体的参数
                        IBinder iBinder = (IBinder) method.invoke(null,
                                Context.TELEPHONY_SERVICE);
                        ITelephony telephony =
                                ITelephony.Stub.asInterface(iBinder);
                        // 挂电话
                        telephony.endCall();
                        
                        
                        //删除手机通讯录里面的通话记录,通过内容观察者,时时监听状态,有数据时,通知并删除
                        final ContentResolver contentResolver = getContentResolver();
                        
                        final Uri uri = CallLog.Calls.CONTENT_URI;
                        //通过内容观察者
                        contentResolver.registerContentObserver(uri , true, new ContentObserver(new Handler()) {
                            
                            @Override
                            public void onChange(boolean selfChange) {
                                super.onChange(selfChange);
                                String where = Calls.NUMBER +" = ?";
                                contentResolver.delete(uri, where , new String[]{incomingNumber});
                                
                                //释放资源
                                contentResolver.unregisterContentObserver(this);
                            }
                        });
                       
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

 
                }
                    
            }
            
        }
        
    }
    // 动态注册广播,监听短信
    private void interceptSms() {

        smsReceiver = new SmsReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(Integer.MAX_VALUE);// 短信属于有序广播,设置优先级
        registerReceiver(smsReceiver, filter);
    }

    class SmsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 获取短信内容
            SmsMessage msg = null;
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (Object p : pdusObj) {
                    msg = SmsMessage.createFromPdu((byte[]) p);

                    String senderNumber = msg.getOriginatingAddress();

                    int type = BlackDBDao.queryType(context, senderNumber);
                    if (type == BlackBean.TYPE_SMS || type == BlackBean.TYPE_ALL) {
                        // 终止广播
                        abortBroadcast();
                    }

                }
            }

        }
    }

    // 解除绑定释放资源
    @Override
    public void onDestroy() {
        super.onDestroy();
        //短信解除绑定
        if (smsReceiver != null) {
            unregisterReceiver(smsReceiver);
        }
        //电话解除绑定
        if (tm!=null) {
            
            tm.listen(phoneListener, PhoneStateListener.LISTEN_NONE);
        }
    }

}
