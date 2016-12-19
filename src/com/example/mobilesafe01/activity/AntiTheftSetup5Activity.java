package com.example.mobilesafe01.activity;

import com.example.mobilesafe01.R;
import com.example.mobilesafe01.R.layout;
import com.example.mobilesafe01.utils.Constant;
import com.example.mobilesafe01.utils.SPUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

public class AntiTheftSetup5Activity extends AntiTheftBaseActivity implements OnCheckedChangeListener {
    private Context mContext;
    private CheckBox cb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_theft_setup5);
        mContext = this;
        initUI();
        initEvent();
    }
    @Override
    protected void onStart() {
        super.onStart();
        //回显数据
        boolean open_protect = SPUtil.getBoolean(mContext, Constant.ANTI_THEFT_OPEN_PROTECT, false);
        if (open_protect) {
            cb.setChecked(true);
        }
        
    }
    private void initEvent() {
        cb.setOnCheckedChangeListener(this);
    }
    private void initUI() {
      cb = (CheckBox) findViewById(R.id.activity_anti_theft_setup5_cb);
        
    }
    @Override
    public boolean setGoPerName() {
        Intent intent = new Intent(this, AntiTheftSetup4Activity.class);
        startActivity(intent );
        return true;
    }
    @Override
    public boolean setGoNextName() {
        if (cb.isChecked()) {
            
            SPUtil.putBoolean(mContext, Constant.ANTI_THEFT_SET_FINISH,true);
            
            Intent intent = new Intent(this, AntiTheftActivity.class);
            startActivity(intent );
            return true;
        }else{
            Toast.makeText(mContext, "必须开启手机防盗设置", Toast.LENGTH_SHORT).show();
            return false;
        }
       
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //获取当前的状态,并保存数据
        SPUtil.putBoolean(mContext, Constant.ANTI_THEFT_OPEN_PROTECT, isChecked);
    }
    
}
