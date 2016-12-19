package com.example.mobilesafe01;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.example.mobilesafe01.adapter.CacheAdapter;
import com.example.mobilesafe01.bean.CacheBean;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CacheCleanActivity extends Activity implements OnClickListener {
    private Context mContext;
    private RelativeLayout rl_scan;
    private ImageView iv_icon;
    private ImageView iv_line;
    private ProgressBar pb;
    private TextView tv_name;
    private TextView tv_size;
    private RelativeLayout rl_finish;
    private Button bt_scan;
    private TextView tv_result;
    private ListView lv_scan;
    private List<CacheBean> list;
    private CacheAdapter cacheAdapter;
    private PackageManager pm;
    private Task task;
    private Button bt_one_clean;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_clean);
        mContext = this;
        initView();
        initData();
        
        initEvent();
    }

    private void initEvent() {
        bt_one_clean.setOnClickListener(this);
        bt_scan.setOnClickListener(this);
        
    }
    
    private ClearCacheObserver mClearCacheObserver;

    class ClearCacheObserver extends IPackageDataObserver.Stub {
        public void onRemoveCompleted(final String packageName,
                final boolean succeeded) {

        }
    }

    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_cache_clean_bt_scan://快速扫描,重新扫描一次
                initData();
                break;
                
            case R.id.activity_cache_clean_bt_one_clean://一键清理
                
                if (mClearCacheObserver == null) {
                    mClearCacheObserver = new ClearCacheObserver();
                }

                try {
                    Method method =
                            pm.getClass().getMethod("freeStorageAndNotify",
                                    long.class, IPackageDataObserver.class);
                    method.invoke(pm, Long.MAX_VALUE, mClearCacheObserver);

                    // 释放完缓存, 重新加载一次数据
                    initData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;
        }
    }

    private void initData() {
        
        task = new Task();
        task.execute();
    }
    
    
    // 扫描是否完成的标志位,用来解决异步任务导致ListView无法回到第一个条目的问题,mStatsObserver响应获取数据需要时间
    private boolean isScanFinished = false;

    
    //获取缓存数据的回调对象
    final IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {
        
        public void onGetStatsCompleted(PackageStats stats, boolean succeeded) {
            
            CacheBean cacheBean = new CacheBean();
            
            long cacheSize = stats.cacheSize;//获取缓存数据
            cacheBean.size = cacheSize;
            
            //Log.e("ming", Formatter.formatFileSize(mContext, cacheSize));
            
            String packageName = stats.packageName;//获取包名
            cacheBean.packageName = packageName;
            
            try {
                cacheBean.icon = pm.getApplicationIcon(packageName);
                
                ApplicationInfo info = pm.getApplicationInfo(packageName, 0);
                
                cacheBean.label = pm.getApplicationLabel(info).toString();
                
            } catch (NameNotFoundException e) {
                e.printStackTrace();
                
                //名字找不着时显示的数据
                cacheBean.icon  = mContext.getResources().getDrawable(R.drawable.ic_launcher);
                cacheBean.label = packageName;
            }
            
            //LogUtils.e("notifyChange");
            
            //通知onProgressUpdate()更新数据,将封装数据的bean还回
            task.notifyChange(cacheBean);
        }
    };
    
    
    
    //增加标志位判断当前Activity是否失去焦点
    private boolean isActivityFocus = false;
    @Override
    protected void onStart() {
        super.onStart();
        isActivityFocus = true;
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        isActivityFocus = false;
    }
    
    //•需求 : 在高版本系统上AsyncTask的执行是单线程的,如果多次打开清理页面,再退出,会导致页面数据展示出现异常.
    //•实现 : 增加标志位,如果当前Activity失去焦点,中断for循环

    class Task extends AsyncTask<Void, CacheBean, Void>{
        //进度值
        int max = 0;
        int progress = 0;
        int cacheCount = 0;
        int cacheSize = 0;
        private TranslateAnimation animation;
        
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //在加载数据时禁用一键清理的按钮,AsyncTask的执行是单线程的,加载数据时出现错乱
            bt_one_clean.setEnabled(false);
            
            isScanFinished = false;
            
            pm = getPackageManager();
            
            //重置数据
            cacheCount = 0;
            cacheSize = 0;
            progress = 0; 
            
            list = new ArrayList<CacheBean>();
            cacheAdapter = new CacheAdapter(mContext, list);
            lv_scan.setAdapter(cacheAdapter);
            
            rl_scan.setVisibility(View.VISIBLE);
            rl_finish.setVisibility(View.GONE);
            
            animation = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_PARENT, 0f, 
                    TranslateAnimation.RELATIVE_TO_PARENT, 0f, 
                    TranslateAnimation.RELATIVE_TO_PARENT, 0f, 
                    TranslateAnimation.RELATIVE_TO_PARENT, 1f);
            animation.setDuration(500);
            animation.setRepeatCount(TranslateAnimation.INFINITE);
            animation.setRepeatMode(TranslateAnimation.REVERSE);
            iv_line.setAnimation(animation);
            animation.start();
            
        }

      //通知onProgressUpdate()更新数据
        public void notifyChange(CacheBean cacheBean) {
            
            publishProgress(cacheBean);
            
            //LogUtils.e("publishProgress");
        }


        @Override
        protected Void doInBackground(Void... params) {
            
            List<ApplicationInfo> installedApplications = pm.getInstalledApplications(0);
            
            max = installedApplications.size();
            
            for (ApplicationInfo info : installedApplications) {
                
                //pm.getPackageSizeInfo(packageName, mStatsObserver);   反射获取该方法,来获取缓存数据
                try {
                    Method method = pm.getClass().getMethod("getPackageSizeInfo", String.class,
                            IPackageStatsObserver.class);
                    method.invoke(pm, info.packageName, mStatsObserver);
                    
                } catch (Exception e) {
                }
                
                SystemClock.sleep(100);
                
                progress++;
                
                if (!isActivityFocus) { //当activity失去焦点时,结束循环
                    break;
                }
                
            }
            
            return null;
        }
        
        
        @Override
        protected void onProgressUpdate(CacheBean... values) {
            super.onProgressUpdate(values);
            //跟新标题的内容
            CacheBean cacheBean = values[0];
            
            if (cacheBean.size>0) {
                list.add(0, cacheBean);//将有缓存的放到集合顶部
                cacheCount++;
                cacheSize += cacheBean.size;
            }else{
                list.add(cacheBean);//添加数据到集合
            }
            
            iv_icon.setImageDrawable(cacheBean.icon);
            tv_name.setText(cacheBean.label);
            tv_size.setText("缓存大小:"+Formatter.formatFileSize(mContext, cacheBean.size));
            
            pb.setMax(max);
            pb.setProgress(progress);
            
            cacheAdapter.notifyDataSetChanged();//通知listview更新数据
            
            lv_scan.setSelection(list.size()-1);
            
            
            // 如果扫描已经结束,直接调用结束方法.刷新一下数据
         // 如果任务已经执行完毕,就手动调用一次onPostExecute()方法,避免ListView无法回到第一个条目的问题
            if (isScanFinished) {
                onPostExecute(null);
            }

        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //数据加载完,启用一键清理
            bt_one_clean.setEnabled(true);
            
            // 扫描结束
            isScanFinished = true;

            rl_scan.setVisibility(View.GONE);
            rl_finish.setVisibility(View.VISIBLE);
            
            
            if (cacheSize>0) {
                
                tv_result. setText("总共有"+cacheCount+"个缓存"+",大小是"+Formatter.formatFileSize(mContext, cacheSize));
            }else{
                tv_result.setText("您的手机非常干净");

            }
            
            //取消动画
            animation.cancel();

            lv_scan.smoothScrollToPosition(0);
            
        }
        
    }

    private void initView() {
        //1扫描过程中控件
        rl_scan = (RelativeLayout) findViewById(R.id.activity_cache_clean_rl_scan);
        iv_icon = (ImageView) findViewById(R.id.activity_cache_clean_iv_icon);
        iv_line = (ImageView) findViewById(R.id.activity_cache_clean_iv_line);
        pb = (ProgressBar) findViewById(R.id.activity_cache_clean_pb);
        tv_name = (TextView) findViewById(R.id.activity_cache_clean_tv_name);
        tv_size = (TextView) findViewById(R.id.activity_cache_clean_tv_size);
        
        //2扫面完成控件
        rl_finish = (RelativeLayout) findViewById(R.id.activity_cache_clean_rl_finish);
        bt_scan = (Button) findViewById(R.id.activity_cache_clean_bt_scan);
        tv_result = (TextView) findViewById(R.id.activity_cache_clean_tv_result);
        
        lv_scan = (ListView) findViewById(R.id.activity_cache_clean_lv_scan);
       
        bt_one_clean = (Button) findViewById(R.id.activity_cache_clean_bt_one_clean);
    
    }

    
}
