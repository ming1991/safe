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
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class OperateBlackActivity extends Activity implements OnClickListener {
    private Context mContext;
    private TextView tv_title;
    private EditText et_num;
    private RadioGroup rg_type;
    private RadioButton rb_num;
    private RadioButton rb_sms;
    private RadioButton rb_all;
    private Button btn_save;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operate_black);
        mContext = this;
        
        intent = getIntent();
        String action = intent.getAction();
        
        initUI();
        
        if (InterceptActivity.BLACK_UPDATE.equals(action)) {
            initUpdateDate();
        }
        
        btn_save.setOnClickListener(this);
       
    }

    

    private void initUI() {
        tv_title = (TextView) findViewById(R.id.act_operate_black_tv_title);
        et_num = (EditText) findViewById(R.id.act_operate_black_et_num);
        rg_type = (RadioGroup) findViewById(R.id.act_operate_black_rg_type);
        rb_num = (RadioButton) findViewById(R.id.act_operate_black_rb_num);
        rb_sms = (RadioButton) findViewById(R.id.act_operate_black_rb_sms);
        rb_all = (RadioButton) findViewById(R.id.act_operate_black_rb_all);
        btn_save = (Button) findViewById(R.id.act_operate_black__btn_save);
        
    }



    private void initUpdateDate() {
        
        
        tv_title.setText("更新黑名单");
        String num = intent.getStringExtra(BlackDB.BlackTable.C_NUM);
        et_num.setText(num);
        et_num.setEnabled(false);
        int type = intent.getIntExtra(BlackDB.BlackTable.C_TYPE,0 );
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
        }
        
        btn_save.setText("更新");
        
    }
    
    public void cancel(View v){
        finish();
    }



    @Override
    public void onClick(View v) {
        String action = intent.getAction();
        
        //添加黑名单的操作
        if (InterceptActivity.BLACK_ADD.equals(action)) {
            String num = et_num.getText().toString().trim();
            if (TextUtils.isEmpty(num)) {
                
                Toast.makeText(mContext, "号码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            int id = rg_type.getCheckedRadioButtonId();
            int type = -1;
            switch (id) {
                case R.id.act_operate_black_rb_num:
                    type = BlackBean.TYPE_NUM;
                    break;
                case R.id.act_operate_black_rb_sms:
                    type = BlackBean.TYPE_SMS;
                    break;
                case R.id.act_operate_black_rb_all:
                    type = BlackBean.TYPE_ALL;
                    break;
            }
            
            if (type==-1) {
                Toast.makeText(mContext, "模式不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            
            //将数据保存到数据库中,并还回数据
            boolean insert = BlackDBDao.insert(mContext, num, type);
            if (insert) {
                Toast.makeText(mContext, "插入成功", Toast.LENGTH_SHORT).show();
              //将数据还回给调用者
                Intent data = new Intent();
                data.putExtra(BlackDB.BlackTable.C_NUM, num);
                data.putExtra(BlackDB.BlackTable.C_TYPE, type);
                setResult(RESULT_OK, data );
                
                //关闭页面
                finish();
            }else{
                Toast.makeText(mContext, "插入失败", Toast.LENGTH_SHORT).show();
              //关闭页面
                finish();
            }
            
            
        }
        
        //更新黑名单的操作
        if (InterceptActivity.BLACK_UPDATE.equals(action)) {
            
            String num = intent.getStringExtra(BlackDB.BlackTable.C_NUM);
            int id = rg_type.getCheckedRadioButtonId();
            int type = -1;
            switch (id) {
                case R.id.act_operate_black_rb_num:
                    type = BlackBean.TYPE_NUM;
                    break;
                case R.id.act_operate_black_rb_sms:
                    type = BlackBean.TYPE_SMS;
                    break;
                case R.id.act_operate_black_rb_all:
                    type = BlackBean.TYPE_ALL;
                    break;
            }
            
            if (type==-1) {
                Toast.makeText(mContext, "模式不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            
            //将数据更新到集合中,并还回
            boolean update = BlackDBDao.update(mContext, num, type);
            if (update) {
                Toast.makeText(mContext, "更新成功", Toast.LENGTH_SHORT).show();
              //将数据还回给调用者
                Intent data = new Intent();
                data.putExtra(BlackDB.BlackTable.C_TYPE, type);
                
                int position = intent.getIntExtra(InterceptActivity.POSITION, -1);
                data.putExtra(InterceptActivity.POSITION, position);
                setResult(RESULT_OK, data );
                
                //关闭页面
                finish();
            }else{
                Toast.makeText(mContext, "更新失败", Toast.LENGTH_SHORT).show();
              //关闭页面
                finish();
            }
            
        }
    }


   

    
}
