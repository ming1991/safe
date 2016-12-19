package com.example.mobilesafe01;

import com.example.mobilesafe01.service.AppLockService;
import com.example.mobilesafe01.utils.ToastUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class LockScreenActivity extends Activity implements OnClickListener {
    public static final String INTENT_ACTION_MING = "intent.action.ming";
    public Context context;
    private ImageView iv_icon;
    private TextView tv_name;
    private EditText et_pwd;
    private Button button;
    private String packageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);
        context = this;
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        button.setOnClickListener(this);
    }

    private void initData() {
        Intent intent = getIntent();
        packageName = intent.getStringExtra(AppLockService.PACKAGE_NAME);
        PackageManager pm = getPackageManager();
        try {
            
            Drawable icon = pm.getApplicationIcon(packageName);
            ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
            String label = pm.getApplicationLabel(applicationInfo).toString();
            iv_icon.setImageDrawable(icon);
            tv_name.setText(label);
            
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            
            iv_icon.setImageResource(R.drawable.ic_launcher);
            tv_name.setText(packageName);
        }
    }

    private void initView() {
        iv_icon = (ImageView) findViewById(R.id.act_lock_screen_iv_icon);
        tv_name = (TextView) findViewById(R.id.act_lock_screen_tv_name);
        et_pwd = (EditText) findViewById(R.id.act_lock_screen_et_pwd);
        button = (Button) findViewById(R.id.act_lock_screen_bt);
    }

    @Override
    public void onClick(View v) {
        String pwd = et_pwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            ToastUtil.showToast(context, "密码不能为空");
            return;
        }
        if (pwd.equals("123")) {    //密码正确就发送广播,让服务放行
            
            Intent intent = new Intent();
            intent.putExtra(AppLockService.PACKAGE_NAME, packageName);
            intent.setAction(INTENT_ACTION_MING);
            sendBroadcast(intent );
            
            finish();//结束本界面,防止本界面结束后,再弹出本应用的其他界面,让本界面启动模式改成单例模式singleInstance
        }else{
            ToastUtil.showToast(context, "密码错误"); 
        }
        
    }
    
    @Override
    public void onBackPressed() {//当用户点击还回键时,跳回到桌面
       
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);
        
        super.onBackPressed();
    }

    
}
