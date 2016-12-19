package com.example.mobilesafe01.activity;

import com.example.mobilesafe01.R;
import com.example.mobilesafe01.R.id;
import com.example.mobilesafe01.R.layout;
import com.example.mobilesafe01.bean.BlackBean;
import com.example.mobilesafe01.db.BlackDB;
import com.example.mobilesafe01.db.BlackDBDao;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class UpdateBlackActivity extends Activity {
    private Context mContext;
    private EditText et_num;
    private RadioGroup rg_type;
    private RadioButton rb_num;
    private RadioButton rb_sms;
    private RadioButton rb_all;
    private String num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_black);
        mContext = this;
        initUI();
        initDate();
        
    }
    //初始化数据
    private void initDate() {
        Intent intent = getIntent();
        num = intent.getStringExtra(BlackDB.BlackTable.C_NUM);
        int type = intent.getIntExtra(BlackDB.BlackTable.C_TYPE, -1);
        et_num.setText(num);
        switch (type) {
            case BlackBean.TYPE_NUM:
                rb_num.setChecked(true);
                break;
            case BlackBean.TYPE_SMS:
                rb_sms.setChecked(true);
                break;
            case BlackBean.TYPE_ALL:
                rb_all.setChecked(true);
                break;

            default:
                break;
        }
    }

    private void initUI() {
        et_num = (EditText) findViewById(R.id.act_update_black_et_num);
        rg_type = (RadioGroup) findViewById(R.id.act_update_black_rg_type);
        rb_num = (RadioButton) findViewById(R.id.act_update_black_rb_num);
        rb_sms = (RadioButton) findViewById(R.id.act_update_black_rb_sms);
        rb_all = (RadioButton) findViewById(R.id.act_update_black_rb_all);
        
    }
    
    public void update(View v){
        int id = rg_type.getCheckedRadioButtonId();
        int type=-1;
        switch (id) {
            case R.id.act_update_black_rb_num:
                type=BlackBean.TYPE_NUM;
                break;
            case R.id.act_update_black_rb_sms:
                type=BlackBean.TYPE_SMS;
                break;
            case R.id.act_update_black_rb_all:
                type=BlackBean.TYPE_ALL;
                break;

            default:
                break;
        }
        if (type!=-1) {
            boolean update = BlackDBDao.update(mContext, num, type);
            if (update) {
                Toast.makeText(mContext, "更新成功", Toast.LENGTH_SHORT).show();
            }
        }
        
        finish();
    }
    
    public void cancel(View v){
        finish();
    }

   
}
