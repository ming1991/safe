package com.example.mobilesafe01.activity;

import org.w3c.dom.Text;

import com.example.mobilesafe01.R;
import com.example.mobilesafe01.utils.Constant;
import com.example.mobilesafe01.utils.SPUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class AntiTheftSetup2Activity extends AntiTheftBaseActivity implements OnClickListener{
    private Context mContext;
    private RelativeLayout rl;
    private ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_theft_setup2);
        mContext = this;
        initUI();
        initDate();
        initEvent();
        
    }
    
    private void initDate() {
        //数据设置和回显
        String sim_info = SPUtil.getString(mContext, Constant.SIM_INFO, "");
        if (!TextUtils.isEmpty(sim_info)) {
            iv.setImageResource(R.drawable.lock);
        }
    }

    private void initEvent() {
        rl.setOnClickListener(this); 
        
    }

    private void initUI() {
        rl = (RelativeLayout) findViewById(R.id.act_anti_theft_setup2_rl);
        iv = (ImageView) findViewById(R.id.act_anti_theft_setup2_iv);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.act_anti_theft_setup2_rl:
                //首先获取保存的数据
                String sim_info = SPUtil.getString(mContext, Constant.SIM_INFO, "");
                if (TextUtils.isEmpty(sim_info)) {
                    //初次绑定sim卡
                    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    String serialNumber = telephonyManager.getSimSerialNumber();//添加权限
                    //保存数据并修改图片
                    SPUtil.putString(mContext, Constant.SIM_INFO, serialNumber);
                    iv.setImageResource(R.drawable.lock);
                    
                }else{
                    //保存改变之后的值,解除绑定,并修改图片
                    SPUtil.putString(mContext, Constant.SIM_INFO, "");
                    iv.setImageResource(R.drawable.unlock);
                }
                break;

            default:
                break;
        }
    }

    //监听的方法有父类继承过来,并调用自己的重写的方法
    @Override
    public boolean setGoPerName() {
        Intent intent = new Intent(this, AntiTheftSetup1Activity.class);
        startActivity(intent );
        return true;
        
    }
    
    //没有绑定sim卡不能进入下一步,需判断是否 绑定sim卡在,选择跳转
    @Override
    public boolean setGoNextName() {
        String simInfo = SPUtil.getString(mContext, Constant.SIM_INFO, "");
        if (TextUtils.isEmpty(simInfo)) {
            //待在该页面,并提示用户
            Toast.makeText(mContext, "如果要开启防盗功能,必须绑定SIM卡", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            //实现页面跳转
            Intent intent = new Intent(this, AntiTheftSetup3Activity.class);
            startActivity(intent );
            return true;
        }
        
        
    }

   
    
}
