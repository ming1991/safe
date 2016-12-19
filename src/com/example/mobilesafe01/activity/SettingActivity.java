package com.example.mobilesafe01.activity;

import com.example.mobilesafe01.R;
import com.example.mobilesafe01.service.InterceptService;
import com.example.mobilesafe01.service.LocationSerivce;
import com.example.mobilesafe01.utils.Constant;
import com.example.mobilesafe01.utils.SPUtil;
import com.example.mobilesafe01.utils.ServiceUtil;
import com.example.mobilesafe01.view.LocationStyleDialog;
import com.example.mobilesafe01.view.SettingItemView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingActivity extends Activity implements OnClickListener{

    private SettingItemView act_setting_update;
    private Context mContext;
    private SettingItemView act_setting_intercept;
    private SettingItemView act_setting_location;
    private SettingItemView act_setting_style;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mContext = this;
        initUI();
        
        initDate();
        //设置点击事件
        initEvent();
    }

    private void initDate() {
        //更新设置的回显
        boolean openDate = SPUtil.getBoolean(mContext, Constant.OPEN_UPDATE, true);
        act_setting_update.setUpdatePic(openDate);
        
        //骚扰拦截的回显
        boolean activate = ServiceUtil.isActivate(mContext,InterceptService.class);
        act_setting_intercept.setUpdatePic(activate);
        
        //归属地的回显
        boolean showeLocation = ServiceUtil.isActivate(mContext,LocationSerivce.class);
        act_setting_location.setUpdatePic(showeLocation);
    }

    private void initEvent() {
        act_setting_update.setOnClickListener(this);
        act_setting_intercept.setOnClickListener(this);
        act_setting_location.setOnClickListener(this);
        act_setting_style.setOnClickListener(this);
    }

    private void initUI() {
        act_setting_update = (SettingItemView) findViewById(R.id.act_setting_update);
        act_setting_intercept = (SettingItemView) findViewById(R.id.act_setting_intercept);
        act_setting_location = (SettingItemView) findViewById(R.id.act_setting_location);
        act_setting_style = (SettingItemView) findViewById(R.id.act_setting_style);
        
       
       
    }

    //点击之后取相反的状态
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.act_setting_update://设置更新
                //首先获取当前的设置状态
              
                boolean update = SPUtil.getBoolean(mContext, Constant.OPEN_UPDATE,true);
                if (update) {
                  //点击之后,设置发生改变,保存设置,图片发生改变
                    act_setting_update.setUpdatePic(false);
                    //状态切换
                    SPUtil.putBoolean(mContext, Constant.OPEN_UPDATE, false);
                }else{
                    act_setting_update.setUpdatePic(true);
                    //状态切换
                    SPUtil.putBoolean(mContext, Constant.OPEN_UPDATE, true);
                }
                
                break;
                
            case R.id.act_setting_intercept://设置拦截
                //通过服务,开启拦截设置
                //获取当前服务的状态
                Intent service = new Intent(mContext, InterceptService.class);
                boolean activate = ServiceUtil.isActivate(mContext,InterceptService.class);
                if (activate) {
                    act_setting_intercept.setUpdatePic(false);
                    stopService(service );
                }else{
                    act_setting_intercept.setUpdatePic(true);
                    startService(service);
                }
                break;
            case R.id.act_setting_location://设置归属地
                
                Intent intent = new Intent(mContext, LocationSerivce.class);
                
                boolean showLocation = ServiceUtil.isActivate(mContext, LocationSerivce.class);
                if (showLocation) {
                    act_setting_location.setUpdatePic(false);
                    stopService(intent );
                }else{
                    act_setting_location.setUpdatePic(true);
                    startService(intent);
                }
                break;
                
            case R.id.act_setting_style://归属地风格的设置
                
                //弹出一个自定义的对话框
                LocationStyleDialog dialog = new LocationStyleDialog(mContext);
                dialog.show();
                
                break;

            default:
                break;
        }
        
    }

    
}
