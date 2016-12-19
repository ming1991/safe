package com.example.mobilesafe01.activity;

import com.example.mobilesafe01.R;
import com.example.mobilesafe01.R.layout;
import com.example.mobilesafe01.receiver.LockDeviceAdmin;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class AntiTheftSetup4Activity extends AntiTheftBaseActivity implements OnClickListener {
    private Context mContext;
    private ImageView iv;
    private RelativeLayout rl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_theft_setup4);
        mContext = this;
        initUI();
        initEvent();
    }
    
    private void initEvent() {
        rl.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initDate();
    }
    //数据的回显
    private void initDate() {
        
        DevicePolicyManager mDPM =
                (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName admin = new ComponentName(mContext, LockDeviceAdmin.class);
        boolean adminActive = mDPM.isAdminActive(admin );
        if (adminActive) {
            iv.setImageResource(R.drawable.admin_activated);
        }else{
            iv.setImageResource(R.drawable.admin_inactivated);
        }
    }

    private void initUI() {
        iv = (ImageView) findViewById(R.id.act_anti_theft_setup4_iv_icon);
        rl = (RelativeLayout) findViewById(R.id.act_anti_theft_setup4_rl_activate);
    }

    @Override
    public boolean setGoPerName() {
        Intent intent = new Intent(this, AntiTheftSetup3Activity.class);
        startActivity(intent );
        return true;
    }
    @Override
    public boolean setGoNextName() {
        //判断是否激活
        DevicePolicyManager mDPM =
                (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName admin = new ComponentName(mContext, LockDeviceAdmin.class);
        boolean adminActive = mDPM.isAdminActive(admin );
        if (adminActive) {
            Intent intent = new Intent(this, AntiTheftSetup5Activity.class);
            startActivity(intent );
            return true;
        }else{
            Toast.makeText(mContext, "需要激活设备管理员", Toast.LENGTH_LONG).show();
            return false;
        }
       
    }
    
    //点击时改变图片和状态
    @Override
    public void onClick(View v) {
        //获取当前的激活状态
        DevicePolicyManager mDPM =
                (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName admin = new ComponentName(mContext, LockDeviceAdmin.class);
        boolean adminActive = mDPM.isAdminActive(admin );
        if (adminActive) {
          //取消激活
            iv.setImageResource(R.drawable.admin_inactivated);
            mDPM.removeActiveAdmin(admin);
            
        }else{
          //跳转到系统激活界面
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, admin);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "360安全卫士保证你的手机安全");
            startActivity(intent);

        }
    }
  
}
