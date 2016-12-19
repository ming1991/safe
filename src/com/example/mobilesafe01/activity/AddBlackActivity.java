package com.example.mobilesafe01.activity;

import com.example.mobilesafe01.R;
import com.example.mobilesafe01.R.id;
import com.example.mobilesafe01.R.layout;
import com.example.mobilesafe01.bean.BlackBean;
import com.example.mobilesafe01.db.BlackDBDao;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class AddBlackActivity extends Activity {
    private Context mContext;
    private EditText et_num;
    private RadioGroup rg_type;
    private RadioButton rb_num;
    private RadioButton rb_sms;
    private RadioButton rb_all;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_black);
        mContext = this;
        initUI();
        
    }

    private void initUI() {
        et_num = (EditText) findViewById(R.id.act_add_black_et_num);
        rg_type = (RadioGroup) findViewById(R.id.act_add_black_rg_type);
        rb_num = (RadioButton) findViewById(R.id.act_add_black_rb_num);
        rb_sms = (RadioButton) findViewById(R.id.act_add_black_rb_sms);
        rb_all = (RadioButton) findViewById(R.id.act_add_black_rb_all);
    }
    
    public void save(View v){
        //获取用户属于的数据
        
        String num = et_num.getText().toString().trim();
        if (TextUtils.isEmpty(num)) {
            Toast.makeText(mContext, "号码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        
        //把输入的拦截类型 用int值表示 ,并保存在数据库中;
        int type=-1;
        int id = rg_type.getCheckedRadioButtonId();
        switch (id) {
            case R.id.act_add_black_rb_num:
                type=BlackBean.TYPE_NUM;        //定义成常量
                break;
            case R.id.act_add_black_rb_sms:
                type=BlackBean.TYPE_SMS;
                break;
            case R.id.act_add_black_rb_all:
                type=BlackBean.TYPE_ALL;
                break;
        }
        if (type==-1) {
            Toast.makeText(mContext, "请选择拦截类型", Toast.LENGTH_SHORT).show();
            return;
        }
        
        boolean insert = BlackDBDao.insert(mContext, num, type);
        if (insert) {
            Toast.makeText(mContext, "添加成功", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(mContext, "添加失败", Toast.LENGTH_SHORT).show(); 
        }
        
        finish();
        
    }
    
    public void cancel(View v){
        finish();
    }

}
