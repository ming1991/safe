package com.example.mobilesafe01.activity;

import com.example.mobilesafe01.AntiVirusActivity;
import com.example.mobilesafe01.AppManagerActivity;
import com.example.mobilesafe01.CacheCleanActivity;
import com.example.mobilesafe01.ProcessManagerActivity;
import com.example.mobilesafe01.R;
import com.example.mobilesafe01.TrafficActivity;
import com.example.mobilesafe01.utils.Constant;
import com.example.mobilesafe01.utils.SPUtil;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements  OnClickListener,OnItemClickListener{
    private Context mContext;
    private ImageView iv_icon;
    private GridView gridView;
    private ImageView iv_setting;
    private EditText et_setup_pwd;
    private EditText et_confirm_pwd;
    private AlertDialog setupPwdDialog;
    private EditText et_submit_pwd;
    private AlertDialog confirmPwdDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext=this;
        initUI();
        initAnimation();
        initEvent();
        
        
    }

    private void initEvent() {
        iv_setting.setOnClickListener(this);
        gridView.setOnItemClickListener(this);
        
    }
    
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent;
        switch (position) {
            case 0://进入防盗设置页面
                //分两种情况
                //1.当完成设置时,弹出密码确认框
                String anti_theft_setup_pwd = SPUtil.getString(mContext, Constant.ANTI_THEFT_SETUP_PWD, "");
                if (!TextUtils.isEmpty(anti_theft_setup_pwd)) {
                    //1.弹出密码确认框
                    showConfirmPwdDialog();
                }else{
                  //2.弹出设置密码对话框
                    showSetupPwdDialog();
                }
                break; 
                
            case 1://进入骚扰拦截界面
                intent = new Intent(mContext, InterceptActivity.class);
                startActivity(intent );
                break;
                
            case 2://进入软件管家界面
                intent = new Intent(mContext, AppManagerActivity.class);
                startActivity(intent );
                break;
            case 3://进入进程管理界面
                intent = new Intent(mContext, ProcessManagerActivity.class);
                startActivity(intent );
                break;
                
            case 4://进入软件管家界面
                intent = new Intent(mContext, TrafficActivity.class);
                startActivity(intent );
                break;
                
            case 5://进入手机杀毒界面
                intent = new Intent(mContext, AntiVirusActivity.class);
                startActivity(intent );
                break;
                
            case 6://进入缓存清理界面
                intent = new Intent(mContext, CacheCleanActivity.class);
                startActivity(intent );
                break;
                
            case 7://进入常用工具界面
                intent = new Intent(mContext, ToolsActivity.class);
                startActivity(intent);
                break;
                
            
            default:
                break;
        }
    }
    private void showConfirmPwdDialog() {
        Builder builder = new AlertDialog.Builder(mContext);
        confirmPwdDialog = builder.create();
        View view = View.inflate(mContext, R.layout.view_confirm_pwd_dialog, null);
        confirmPwdDialog.setView(view);
        confirmPwdDialog.show();
        
        Button bt_confirm_ok = (Button) view.findViewById(R.id.bt_confirm_ok);
        Button bt_confirm_cancle = (Button) view.findViewById(R.id.bt_confirm_cancle);
        
        et_submit_pwd = (EditText) view.findViewById(R.id.et_submit_pwd);
        
        bt_confirm_ok.setOnClickListener(MainActivity.this);
        bt_confirm_cancle.setOnClickListener(MainActivity.this);
        
    }

    private void showSetupPwdDialog() {
        Builder builder = new AlertDialog.Builder(mContext);
        setupPwdDialog = builder.create();
        View view = View.inflate(mContext, R.layout.view_setup_pwd_dialog, null);
        setupPwdDialog.setView(view);
        setupPwdDialog.show();
        
        Button bt_ok = (Button) view.findViewById(R.id.bt_ok);
        Button bt_cancle = (Button) view.findViewById(R.id.bt_cancle);
        
        et_setup_pwd = (EditText) view.findViewById(R.id.et_setup_pwd);
        et_confirm_pwd = (EditText) view.findViewById(R.id.et_confirm_pwd);
        
        
        bt_ok.setOnClickListener(MainActivity.this);
        bt_cancle.setOnClickListener(MainActivity.this);
    }
    

    

    

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.act_main_setting:
                Intent intent = new Intent(mContext, SettingActivity.class) ;
                //页面跳转到设置页面
                startActivity(intent);
                break;
            case R.id.bt_ok://(手机防盗)弹出初始密码设置对话框
                String setupPwd = et_setup_pwd.getText().toString().trim();
                String confirm_pwd = et_confirm_pwd.getText().toString().trim();
                if (TextUtils.isEmpty(confirm_pwd)||TextUtils.isEmpty(setupPwd)) {
                    Toast.makeText(mContext, "密码不能为空", Toast.LENGTH_SHORT).show();
                    
                }else{
                    if (setupPwd.equals(confirm_pwd)) {
                      //保存密码
                        SPUtil.putString(mContext, Constant.ANTI_THEFT_SETUP_PWD, setupPwd);
                        
                        setupPwdDialog.dismiss();
                        //进入向导界面1
                          Intent intent1 = new Intent(mContext, AntiTheftSetup1Activity.class);
                          startActivity(intent1);
                          
                      }else{
                          Toast.makeText(mContext, "密码错误", Toast.LENGTH_SHORT).show();  
                      }
                }
                
                break;
                
            case R.id.bt_cancle://(手机防盗)弹出初始密码设置对话框
                //返回到界面
                setupPwdDialog.dismiss();
                break;
                
                
            case R.id.bt_confirm_ok://(手机防盗)密码确认框
                
                String submitPwd = et_submit_pwd.getText().toString().trim();
                //验证密码
                String anti_theft_setup_pwd = SPUtil.getString(mContext,Constant.ANTI_THEFT_SETUP_PWD,"");
                if (anti_theft_setup_pwd.equals(submitPwd)) {
                    
                    // TODO 分两种情况需要加判断,是否完成页面向导
                    boolean isFinish = SPUtil.getBoolean(mContext, Constant.ANTI_THEFT_SET_FINISH, false);
                    if (isFinish) {
                      //2.进入设置完成页面
                        
                        confirmPwdDialog.dismiss();
                        Intent intent2 = new Intent(mContext, AntiTheftActivity.class);
                        startActivity(intent2);
                        
                    }else{
                        confirmPwdDialog.dismiss();
                      //1.进入设置页面
                        Intent intent1 = new Intent(mContext, AntiTheftSetup1Activity.class);
                        startActivity(intent1);
                        
                    }
                    
                }else{
                    Toast.makeText(mContext, "密码错误", Toast.LENGTH_SHORT).show();  
                }
                break;
                
            case R.id.bt_confirm_cancle://(手机防盗)密码确认框
              //返回到界面
                confirmPwdDialog.dismiss();
                break;
            default:
                break;
        }
        
    }

    private void initAnimation() {
        //属性动画
        ObjectAnimator animatorx = ObjectAnimator.ofFloat(iv_icon, "rotationX", 0, 45, 90, 135, 180,
               225, 270, 315, 360);
        animatorx.setRepeatCount(ValueAnimator.INFINITE);
        animatorx.setRepeatMode(ValueAnimator.RESTART);
       
        ObjectAnimator animatory = ObjectAnimator.ofFloat(iv_icon, "rotationY", 0, 45, 90, 135, 180,
               225, 270, 315, 360);
        animatory.setRepeatCount(ValueAnimator.INFINITE);
        animatory.setRepeatMode(ValueAnimator.RESTART);
        
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(2000);
        animatorSet.playTogether(animatorx,animatory);
        
        animatorSet.start();
    }

    private void initUI() {
        iv_icon = (ImageView) findViewById(R.id.act_main_icon);
        
        gridView = (GridView) findViewById(R.id.act_main_gv);
        
        GridViewAdapter gridViewAdapter = new GridViewAdapter();
        
        gridView.setAdapter(gridViewAdapter);
        
        iv_setting = (ImageView) findViewById(R.id.act_main_setting);
    }
    class GridViewAdapter extends BaseAdapter{
        
        String[] titles =
                new String[] {"手机防盗", "骚扰拦截", "软件管家", "进程管理", "流量统计", "手机杀毒", "缓存清理", "常用工具"};
         String[] descs = new String[] {"远程定位手机", "全面拦截骚扰", "管理您的软件", "管理运行进程",
                "流量一目了然", "病毒无处藏身", "系统快如火箭", "工具大全"};
        int[] icons = new int[] {R.drawable.sjfd, R.drawable.srlj, R.drawable.rjgj,
                R.drawable.jcgl, R.drawable.lltj, R.drawable.sjsd, R.drawable.hcql, R.drawable.cygj};


        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView==null) {
                convertView = View.inflate(mContext, R.layout.grid_item_view, null);
            }
            
            //初始化内容
            TextView desc = (TextView) convertView.findViewById(R.id.item_main_grid_desc);
            TextView title = (TextView) convertView.findViewById(R.id.item_main_grid_title);
            ImageView icon = (ImageView) convertView.findViewById(R.id.item_main_grid_icon);
            
            desc.setText(descs[position]);
            title.setText(titles[position]);
            icon.setImageResource(icons[position]);
            
            return convertView;
        }
        
    }
   
    
   
}
