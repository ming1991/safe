package com.example.mobilesafe01;

import java.util.ArrayList;
import java.util.List;

import com.example.mobilesafe01.adapter.AppLockAdapter;
import com.example.mobilesafe01.bean.LockBean;
import com.example.mobilesafe01.db.LockDBDao;
import com.example.mobilesafe01.view.SegamentView;
import com.example.mobilesafe01.view.SegamentView.OnSegamentSelectListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.media.MediaBrowserCompat.MediaItem.Flags;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class AppLockActivity extends Activity implements OnSegamentSelectListener {

    private SegamentView segament;
    private TextView tv_title;
    private ListView lv_left;
    private ListView lv_right;
    private LinearLayout load;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);
        context = this;
        initView();
        initData();
        initEvent();
    }

    //标志位,判断当前按钮是显示未加锁状态还是已加锁状态
    boolean shwoUnlock = true;
    
    private List<LockBean> unlockList;
    private List<LockBean> list;
    private List<LockBean> lockList;
    
    //初始化listView的数据
    private void initData() {
        new Task().execute();
    }
    
    public class Task extends AsyncTask<Void, Void, Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            load.setVisibility(View.VISIBLE);
            tv_title.setVisibility(View.GONE);
        }
        @Override
        protected Void doInBackground(Void... params) {
            
            list = new ArrayList<LockBean>();
            unlockList = new ArrayList<LockBean>();
            lockList = new ArrayList<LockBean>();
            
            PackageManager pm = getPackageManager();
            List<ApplicationInfo> applications = pm.getInstalledApplications(0);
            
            for (ApplicationInfo info : applications) {
                String packageName = info.packageName;
                
                
                Intent intent = pm.getLaunchIntentForPackage(packageName); // 如果没有界面,就跳过
                // 如果没有界面,就跳过
                if (intent == null) {
                    continue;
                }
                
                LockBean lockBean = new LockBean();
                lockBean.packageName = packageName;
                
                Drawable icon = pm.getApplicationIcon(info);
                lockBean.icon = icon;
                
                String label = pm.getApplicationLabel(info).toString();
                lockBean.label = label;
                list.add(lockBean);
                
            }
            for (LockBean lockBean : list) {
                boolean lock = LockDBDao.query(context, lockBean.packageName);
                if (lock) {
                    lockList.add(lockBean);
                }else{
                    unlockList.add(lockBean);
                }
            }
            
            SystemClock.sleep(1000);
            
            return null;
        }
        
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            
            load.setVisibility(View.GONE);
            tv_title.setVisibility(View.VISIBLE);
            
            AppLockAdapter unLockAdapter = new AppLockAdapter(context, unlockList,lockList,true,tv_title); 
            lv_left.setAdapter(unLockAdapter);
            
            AppLockAdapter lockAdapter = new AppLockAdapter(context, unlockList,lockList,false,tv_title); 
            lv_right.setAdapter(lockAdapter);
            
            //判断当前按钮是未加锁状态还是已加锁状态,加载数据
            if (shwoUnlock) {
                tv_title.setText("未加锁(" + unlockList.size() + ")个");
            }else{
                tv_title.setText("已加锁(" + lockList.size() + ")个");
            }
        }
        
    }
    
    private void initEvent() {
        segament.setOnSegamentSelectListener(this);
    }

    private void initView() {
        segament = (SegamentView) findViewById(R.id.act_app_lock_segament);
        tv_title = (TextView) findViewById(R.id.act_app_lock_tv_title);
        lv_left = (ListView) findViewById(R.id.act_app_lock_lv_left);
        lv_right = (ListView) findViewById(R.id.act_app_lock_lv_right);
        load = (LinearLayout) findViewById(R.id.act_app_lock_load);
    }

    //当点击左边的时候
    //更改标题内容
    @Override
    public void onLeftSelected() {
        shwoUnlock = true;
        
       /* list.clear(); 性能不好
        unlockList.clear();
        lockList.clear();
        initData();*/
        
        tv_title.setText("未加锁(" + unlockList.size() + ")个");
        
        lv_left.setVisibility(View.VISIBLE);
        lv_right.setVisibility(View.GONE);
    }

    //当点击右边按钮的时候
    @Override
    public void onRightSelected() {
        shwoUnlock = false;
        //重新加载数据
        /*list.clear();
        unlockList.clear();
        lockList.clear();
        initData();*/
        tv_title.setText("已加锁(" + lockList.size() + ")个");
        
        lv_left.setVisibility(View.GONE);
        lv_right.setVisibility(View.VISIBLE);
    }

    
}
