package com.example.mobilesafe01.activity;

import com.example.mobilesafe01.R;
import com.example.mobilesafe01.R.layout;
import com.example.mobilesafe01.utils.Constant;
import com.example.mobilesafe01.utils.SPUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AntiTheftSetup3Activity extends AntiTheftBaseActivity implements OnClickListener {
    private static final int REQUEST_CODE_SAFE_NUM = 200;
    private Context mContext;
    private EditText eText;
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_theft_setup3);
        mContext = this;
        initUI();
       
        initEvent();
    }
   
    private void initEvent() {
        button.setOnClickListener(this);
    }
    @Override
    protected void onStart() {
        super.onStart();
        initDate();
        
    }
    private void initDate() {
        // TODO 用于数据的回显        还有一种情况,用户输入了新的号码待完善
        
        //特殊情况,当用户从跳转页面回来获取数据,  先走onactivityResult方法在走onStart方法
        
        String setNum = eText.getText().toString().trim();
        if (TextUtils.isEmpty(setNum)) {
            String safeNum = SPUtil.getString(mContext, Constant.ANTI_THEFT_SAFE_NUM, "");
            if (!TextUtils.isEmpty(safeNum)) {
                eText.setText(safeNum);
                //让光标处于文本的后面
                eText.setSelection(safeNum.length());
            }   
        }
        
    }

    private void initUI() {
        eText = (EditText) findViewById(R.id.act_anti_theft_setup3_et);
        button = (Button) findViewById(R.id.act_anti_theft_setup3_btn);
    }
    
    @Override
    public void onClick(View v) {
      //跳转到选择号码的页面
        Intent intent = new Intent(mContext, SelectContactActivity.class);
        startActivityForResult(intent , REQUEST_CODE_SAFE_NUM);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SAFE_NUM:
                if (resultCode==RESULT_OK) {
                    String num = data.getStringExtra(SelectContactActivity.CONTACT_NUM);
                    eText.setText(num);
                    eText.setSelection(num.length());
                }
                break;

            default:
                break;
        }
    }
       

    @Override
    public boolean setGoPerName() {
        Intent intent = new Intent(this, AntiTheftSetup2Activity.class);
        startActivity(intent );
        return true;
    }
    @Override
    public boolean setGoNextName() {
        // TODO  判断选择安全密码,才能进入下一个页面
        
        //获取用户输入的手机号码,并保存,保证号码的更新
        
        String safeNum = eText.getText().toString().trim();
        if (TextUtils.isEmpty(safeNum)) {
            //待在该页
            Toast.makeText(mContext, "如果要开启防盗功能,必须设置安全号码", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            //保存数据并实现页面的跳转
            SPUtil.putString(mContext, Constant.ANTI_THEFT_SAFE_NUM, safeNum);
            Intent intent = new Intent(this, AntiTheftSetup4Activity.class);
            startActivity(intent );
            return true;
        }
        
       
    }

    
    
}
