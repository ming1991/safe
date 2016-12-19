
package com.example.mobilesafe01.receiver;

import com.example.mobilesafe01.service.ProtectedService;
import com.example.mobilesafe01.utils.Constant;
import com.example.mobilesafe01.utils.LogUtils;
import com.example.mobilesafe01.utils.SPUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * ClassName:BootReceiver <br/>
 * Function: 开机广播
 * Date: 2016年9月6日 上午12:14:53 <br/>
 * 
 * @author ming001
 * @version
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        
        //在开机广播中启动我们程序的服务,保证我们的服务不被杀死
        Intent service = new Intent(context, ProtectedService.class);
        context.startService(service);
        
        // 通过手机开关机,判断sim卡信息是否改变 ,
        // 判断是否开启防盗保护
        boolean protect = SPUtil.getBoolean(context, Constant.ANTI_THEFT_OPEN_PROTECT, false);
        if (protect) {
            // 获取本地的sim卡系列号和保存的序列号是否相同
            TelephonyManager tm =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String local_simSerialNumber = tm.getSimSerialNumber();
            if (TextUtils.isEmpty(local_simSerialNumber)) {
                LogUtils.e("手机没插卡");
                return;
            }
            
            String save_simSerialNumber = SPUtil.getString(context, Constant.SIM_INFO, "");
            if (TextUtils.isEmpty(save_simSerialNumber)) {
                LogUtils.e("没保存sim序列号");
                return;
            }
            if (!save_simSerialNumber.equals(local_simSerialNumber+123)) {
                //发送预警信息给安全号码
                String num = SPUtil.getString(context, Constant.ANTI_THEFT_SAFE_NUM, "");
                
                LogUtils.e("num"+num);
                
                if (TextUtils.isEmpty(num)) {
                    LogUtils.e("没保存安全号码");
                    return;
                }
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(num, null, "手机被盗了", null, null);
                // TODO 收不到短信
                LogUtils.e(num);//
                
            }else{
                LogUtils.e("手机安全");
            }
        }

    }

}
