
package com.example.mobilesafe01.receiver;

import com.example.mobilesafe01.R;
import com.example.mobilesafe01.service.GPSService;
import com.example.mobilesafe01.utils.Constant;
import com.example.mobilesafe01.utils.LogUtils;
import com.example.mobilesafe01.utils.SPUtil;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

/**
 * ClassName:SmsReceiver <br/>
 * Function: TODO 接受来自安全号码的特殊指令,并执行对应的操作 Date: 2016年9月6日 下午1:57:07 <br/>
 * 
 * @author ming001
 * @version
 */
public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // 获取短信内容
        Bundle bundle = intent.getExtras();
        Object[] objs = (Object[]) bundle.get("pdus");
        for (Object object : objs) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) object);
            String senderNum = smsMessage.getOriginatingAddress();
            String messageBody = smsMessage.getMessageBody();

            // 判断是否开启防盗保护
            boolean protect = SPUtil.getBoolean(context, Constant.ANTI_THEFT_OPEN_PROTECT, false);
            if (protect) {
                // 获取本地保存的安全号码
                String safeNum = SPUtil.getString(context, Constant.ANTI_THEFT_SAFE_NUM, "");
                if (TextUtils.isEmpty(safeNum)) {
                    LogUtils.e("没有保存安全号码");
                    return;
                }

                if (senderNum.equals(safeNum)) {
                    // 通过短信的内容来执行各种操作
                    if (messageBody.contains("#*location*#")) {
                        // 获取当前手机的地理位置
                        getLocation(context);
                        // 拦截掉广播不让用户看到
                        abortBroadcast();
                    }
                    if (messageBody.contains("#*alarm*#")) {
                        // 播放报警音乐1
                        playMusic(context);
                        abortBroadcast();
                    }
                    // 远程销毁数据
                    if (messageBody.contains("#*wipedata*#")) {
                        wipeDate(context);
                    }
                    // 远程,重设密码,锁屏
                    if (messageBody.contains("#*lockscreen*#")) {
                        lockScreen(context);
                    }
                }
            }
        }

    }
    //重设密码和锁屏
    private void lockScreen(Context context) {
        DevicePolicyManager mDPM =
                (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDPM.resetPassword("111", DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
        mDPM.lockNow();
        
    }
    
    //销毁数据
    private void wipeDate(Context context) {
        DevicePolicyManager mDPM =
                (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDPM.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
        

    }

    // 播放报警音乐
    private void playMusic(Context context) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.alarm);
        // 循环播放,音量最大
        mediaPlayer.setLooping(true);
        mediaPlayer.setVolume(1.0f, 1.0f);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you

    }

    // 获取当前手机的地理位置
    private void getLocation(Context context) {
        Intent service = new Intent(context, GPSService.class);
        context.startService(service );
    }

}
