package com.example.mobilesafe01.activity;

import com.example.mobilesafe01.R;
import com.example.mobilesafe01.R.layout;

import android.R.bool;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class AntiTheftSetup1Activity extends AntiTheftBaseActivity {
/**
 * 监听由父类继承过来,可以直接调用
 * 
 * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_theft_setup1);
    }
        
    @Override
    public boolean setGoPerName() {
        //不发生跳转
        return false;
    }
    
    //调用父类的监听方法
    @Override
    public boolean setGoNextName() {
        Intent intent = new Intent(this, AntiTheftSetup2Activity.class);
        startActivity(intent );
        return true;
        
    }
   
}
