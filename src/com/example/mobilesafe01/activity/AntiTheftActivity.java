package com.example.mobilesafe01.activity;

import com.example.mobilesafe01.R;
import com.example.mobilesafe01.R.id;
import com.example.mobilesafe01.R.layout;
import com.example.mobilesafe01.R.menu;
import com.example.mobilesafe01.utils.Constant;
import com.example.mobilesafe01.utils.SPUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AntiTheftActivity extends Activity implements OnClickListener {
    private Context mContext;
    private RelativeLayout rl;
    private TextView tv;
    private ImageView iv;
    private TextView tv_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_theft);
        mContext = this;
        initUI();

        initEvent();
    }

    private void initEvent() {
        rl.setOnClickListener(this);
        tv.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initDate();
    }

    private void initDate() {
        // 数据的显示
        String num = SPUtil.getString(mContext, Constant.ANTI_THEFT_SAFE_NUM, "");
        tv_num.setText(num);
        boolean protect = SPUtil.getBoolean(mContext, Constant.ANTI_THEFT_OPEN_PROTECT, false);
        if (protect) {
            iv.setImageResource(R.drawable.lock);
        } else {
            iv.setImageResource(R.drawable.unlock);
        }
    }

    private void initUI() {
        rl = (RelativeLayout) findViewById(R.id.activity_anti_theft_rl_open);
        tv = (TextView) findViewById(R.id.activity_anti_theft_tv_reset);
        iv = (ImageView) findViewById(R.id.activity_anti_theft_iv_lock);
        tv_num = (TextView) findViewById(R.id.activity_anti_theft_tv_num);
        
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_anti_theft_rl_open:// 防盗保护开启的点击事件
                //获取当前的状态
                boolean protect = SPUtil.getBoolean(mContext, Constant.ANTI_THEFT_OPEN_PROTECT, false);
                if (protect) {
                    //点击之后状态取反
                    iv.setImageResource(R.drawable.unlock);
                    SPUtil.putBoolean(mContext, Constant.ANTI_THEFT_OPEN_PROTECT,false);
                } else {
                    iv.setImageResource(R.drawable.lock);
                    SPUtil.putBoolean(mContext, Constant.ANTI_THEFT_OPEN_PROTECT,true);
                }
                break;
            case R.id.activity_anti_theft_tv_reset:// 重新设置的点击事件
                Intent intent = new Intent(mContext, AntiTheftSetup1Activity.class);
                //跳转到第一个页面
                startActivity(intent );
                finish();
                break;

            default:
                break;
        }

    }

}
