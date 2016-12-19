  
package com.example.mobilesafe01.service;

import com.example.mobilesafe01.utils.Constant;
import com.example.mobilesafe01.utils.LogUtils;
import com.example.mobilesafe01.utils.SPUtil;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.text.TextUtils;

/** 
 * ClassName:LocationService <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Date:     2016年9月6日 下午7:05:07 <br/> 
 * @author   ming001 
 * @version  
 *需要加权限   
 *gsp时很耗电耗时的操作  
 */
public class GPSService extends Service implements LocationListener {

    private LocationManager locationManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = LocationManager.GPS_PROVIDER;
        locationManager.requestLocationUpdates(provider, 0, 0, this);
    }
    
    
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        
        //取消注册
        if (locationManager!=null) {
            locationManager.removeUpdates(this);
            locationManager=null;
        }
        
    }
    
    //当地理位置发生改变时,此方法被调用
    @Override
    public void onLocationChanged(Location location) {
        //获取地理位置,经纬度
        double latitude = location.getLatitude();//纬度
        double longitude = location.getLongitude();//经度
        
        //获取保存的安全号码
        String safeNum = SPUtil.getString(getApplication(), Constant.ANTI_THEFT_SAFE_NUM, "");
        if (!TextUtils.isEmpty(safeNum)) {
          //发送短信,并结束掉服务
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(safeNum, null, "当前手机的位置:纬度:"+latitude+"经度:"+longitude, null, null);
            
            LogUtils.e("当前手机的位置:纬度:"+latitude+"经度:"+longitude);
        }
        
        //停止广播
        stopSelf();
        
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        
    }

    @Override
    public void onProviderEnabled(String provider) {
        
    }

    @Override
    public void onProviderDisabled(String provider) {
        
    }

}
  