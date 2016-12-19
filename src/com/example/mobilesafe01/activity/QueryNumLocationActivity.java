package com.example.mobilesafe01.activity;

import com.example.mobilesafe01.R;
import com.example.mobilesafe01.R.id;
import com.example.mobilesafe01.R.layout;
import com.example.mobilesafe01.db.LocationDao;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 在应用启动时加载并解压归属地数据zip
 * */
public class QueryNumLocationActivity extends Activity implements TextWatcher {
    private Context mContext;
    private EditText et_input;
    private TextView tv_show;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_num_location);
        mContext = this;
        initUI();
        initDate();
        initEvent();
        
    }

    private void initEvent() {
        //•需求 : 动态的根据用户输入的内容进行查询
        et_input.addTextChangedListener(this);
        
    }
    private void initDate() {
        
    }

    private void initUI() {
        et_input = (EditText) findViewById(R.id.act_query_num_location_et_input);
        tv_show = (TextView) findViewById(R.id.act_query_num_location_tv_show);
        
    }
    //获取用户输入的数据
    public void query(View v){
        String num = et_input.getText().toString().trim();
        if (TextUtils.isEmpty(num)) {
            Toast.makeText(mContext, "号码不能为空", Toast.LENGTH_SHORT).show();
            
            
            //•需求 : 当查询号码的时候输入框如果没有内容, 添加动画效果(动画插入器)interpolator
            /* <translate xmlns:android="http://schemas.android.com/apk/res/android" 
                     android:fromXDelta="0" android:toXDelta="10" android:duration="1000" 
                     android:interpolator="@anim/cycle_7" />
                     
             Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
             findViewById(R.id.pw).startAnimation(shake);*/
             
            TranslateAnimation animation = new TranslateAnimation(0, 10, 0, 0);
            //设置时长
            animation.setDuration(1000);
            //设置动画插入器
            animation.setInterpolator(new CycleInterpolator(7));
            et_input.startAnimation(animation);
            return;
        }
        
        String location = LocationDao.queryLocation(mContext,num);
        tv_show.setText(location);
        
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        
    }

    //CharSequence s 用户输入的内容
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // 动态的根据用户输入的内容进行查询
        String location = LocationDao.queryLocation(mContext,s.toString());
        tv_show.setText(location);
    }

    @Override
    public void afterTextChanged(Editable s) {
        
    }
   
}
