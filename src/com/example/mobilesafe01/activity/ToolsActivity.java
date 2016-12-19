package com.example.mobilesafe01.activity;

import com.example.mobilesafe01.AppLockActivity;
import com.example.mobilesafe01.R;
import com.example.mobilesafe01.service.AppLockService;
import com.example.mobilesafe01.service.AppLockService2;
import com.example.mobilesafe01.utils.ServiceUtil;
import com.example.mobilesafe01.utils.SmsUtil;
import com.example.mobilesafe01.utils.SmsUtil.OnProgressDialogListener;
import com.example.mobilesafe01.view.SettingItemView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class ToolsActivity extends Activity implements OnClickListener {
    private Context mContext;
    private SettingItemView query_location;
    private SettingItemView query_num;
    private SettingItemView sms_backup;
    private SettingItemView sms_restore;
    private ProgressDialog pd;
    private SettingItemView app_lock;
    private SettingItemView app_lock1;
    private SettingItemView app_lock2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools);
        mContext = this;
        initUI();
        iniEvent();
    }
    
    //数据回显
    @Override
    protected void onStart() {
        super.onStart();
        boolean activate1= ServiceUtil.isActivate(mContext, AppLockService.class);
        app_lock1.setUpdatePic(activate1);
        //程序锁2的数据回显
        boolean activate2 = ServiceUtil.isActivate(mContext, AppLockService2.class);
        app_lock2.setUpdatePic(activate2);
        
    }

    private void iniEvent() {
        query_location.setOnClickListener(this);
        query_num.setOnClickListener(this);
        sms_backup.setOnClickListener(this);
        sms_restore.setOnClickListener(this);
        app_lock.setOnClickListener(this);
        app_lock1.setOnClickListener(this);
        app_lock2.setOnClickListener(this);
    }

    private void initUI() {
        query_location = (SettingItemView) findViewById(R.id.act_tools_query_location);
        query_num = (SettingItemView) findViewById(R.id.act_tools_query_num);
        sms_backup = (SettingItemView) findViewById(R.id.act_tools_sms_backup);
        sms_restore = (SettingItemView) findViewById(R.id.act_tools_sms_restore);
        app_lock = (SettingItemView) findViewById(R.id.act_tools_app_lock);
        app_lock1 = (SettingItemView) findViewById(R.id.act_tools_app_lock1);
        app_lock2 = (SettingItemView) findViewById(R.id.act_tools_app_lock2);
        
        
        //显示进度条
        pd = new ProgressDialog(mContext);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
      
        
        
    }

    @Override
    public void onClick(View v) {
        Intent intent = null ;
        
        switch (v.getId()) {
            case R.id.act_tools_query_location:// 号码归属地查询
                 intent = new Intent(mContext, QueryNumLocationActivity.class);
                startActivity(intent );
                break;
                
            case R.id.act_tools_query_num:// 号码归属地查询
                 intent = new Intent(mContext, QueryCommonNumActivity.class);
                startActivity(intent );
                break;
                
            case R.id.act_tools_sms_backup:// 短信备份
                
                SmsUtil.smsBackup(mContext, new OnProgressDialogListener() {
                    
                    @Override
                    public void onSuccess() {
                        pd.dismiss();
                        Toast.makeText(mContext, "保存成功", Toast.LENGTH_SHORT).show();
                    }
                    
                    @Override
                    public void onProgressUpdate(Integer... values) {
                        pd.setMax(values[1]);
                        pd.setProgress(values[0]);
                    }
                    
                    @Override
                    public void onPre() {
                        pd.show();
                    }
                    
                    @Override
                    public void onFail() {
                        pd.dismiss();
                        Toast.makeText(mContext, "保存失败", Toast.LENGTH_SHORT).show();
                        
                    }
                });
                
                break;
                
            case R.id.act_tools_sms_restore:// 短信还原
                
                SmsUtil.smsRestore(mContext, new OnProgressDialogListener() {
                    
                    @Override
                    public void onSuccess() {
                        pd.dismiss();
                        Toast.makeText(mContext, "还原成功", Toast.LENGTH_SHORT).show();
                        
                    }
                    
                    @Override
                    public void onProgressUpdate(Integer... values) {
                        pd.setMax(values[1]);
                        pd.setProgress(values[0]);
                        
                    }
                    
                    @Override
                    public void onPre() {
                        pd.show();
                        
                    }
                    
                    @Override
                    public void onFail() {
                        pd.dismiss();
                        Toast.makeText(mContext, "还原失败", Toast.LENGTH_SHORT).show();
                        
                    }
                });
                
                break;
                
            case R.id.act_tools_app_lock:// 程序锁管理
                
                intent = new Intent(mContext, AppLockActivity.class);
                startActivity(intent);
                break;
            case R.id.act_tools_app_lock1:// 程序锁服务1
                
                intent = new Intent(mContext,  AppLockService.class);
                boolean activate = ServiceUtil.isActivate(mContext, AppLockService.class);
                if (activate) {
                    stopService(intent);
                    app_lock1.setUpdatePic(false);
                }else{
                    startService(intent);
                    app_lock1.setUpdatePic(true);
                }
                
                break;
                
            case R.id.act_tools_app_lock2:// 程序锁服务2
                //跳转到设置界面的Accessibility界面
                intent = new Intent();
                intent.setAction("android.intent.action.MAIN");
                intent.setAction("android.settings.ACCESSIBILITY_SETTINGS");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.addCategory("android.intent.category.VOICE_LAUNCH");
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    
}
