
package com.example.mobilesafe01.service;

import com.example.mobilesafe01.db.LocationDao;
import com.example.mobilesafe01.utils.LogUtils;
import com.example.mobilesafe01.view.MyToast;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

/**
 * ClassName:LocationSerivce <br/>
 * Function: 来电和打电话显示号码的归属地 Date: 2016年9月9日 下午8:03:28 <br/>
 * ◾监听呼入的号码, 需要使用TelephonyManager       监听呼出的号码, 需要使用系统广播
 * @author ming001
 * @version
 */
public class LocationSerivce extends Service {

    private CallOut callOut;
    private PhoneListener listener;
    private TelephonyManager tm;
    private MyToast toast;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        
        //创建吐司对象
        toast = new MyToast(LocationSerivce.this);
        
        //打入电话的监听
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        listener = new PhoneListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

        // 注册监听呼出状态的广播
        IntentFilter filter = new IntentFilter();
        filter.setPriority(Integer.MAX_VALUE);
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        callOut = new CallOut();
        registerReceiver(callOut, filter);
    }
    
    //打入电话的监听
    class PhoneListener extends PhoneStateListener{
       

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            
            if (state==TelephonyManager.CALL_STATE_RINGING) {//响铃
                String location = LocationDao.queryLocation(getApplicationContext(), incomingNumber);
                
                toast.show(location);
            }
            
            if (state==TelephonyManager.CALL_STATE_IDLE) {//挂断
                toast.hide();
            }
            
        }
    }

    
    //呼出的广播
    class CallOut extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取拨打的号码
            String num = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            String location = LocationDao.queryLocation(getApplication(), num);
            
            toast.show(location);

        }
    }
    
    //解除注册,释放资源
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (callOut!=null) {
            unregisterReceiver(callOut);
        }
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);
        
    }

}
