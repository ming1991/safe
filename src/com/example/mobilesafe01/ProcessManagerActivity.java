package com.example.mobilesafe01;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.example.mobilesafe01.adapter.ProcessAdapter;
import com.example.mobilesafe01.bean.ProcessBean;
import com.example.mobilesafe01.service.LockScreenCleanService;
import com.example.mobilesafe01.utils.Constant;
import com.example.mobilesafe01.utils.ProcessUtil;
import com.example.mobilesafe01.utils.SPUtil;
import com.example.mobilesafe01.utils.ServiceUtil;
import com.example.mobilesafe01.view.AppManagerStoreView;
import com.example.mobilesafe01.view.SettingItemView;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class ProcessManagerActivity extends Activity implements OnItemClickListener, OnClickListener {

    private AppManagerStoreView process;
    private AppManagerStoreView ram;
    private LinearLayout pb;
    private Context context;
    private ActivityManager am;
    private List<ProcessBean> list;
    private ProcessAdapter processAdapter;
    private PackageManager pm;
    private StickyListHeadersListView lv_process;
    private List<ProcessBean> useList;
    private List<ProcessBean> sysList;
    private SlidingDrawer drawer;
    private ImageView arrow1;
    private ImageView arrow2;
    private SettingItemView show_sys;
    private SettingItemView auto_clean;
    private ImageButton clean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_manager);
        context = this;
        initUI();
        initProcessData();
        initRamData();
        initLvData();
        initAnim();
        initEvent();

    }
    
    @Override
    protected void onStart() {
        super.onStart();
        
      //显示系统进程数据的回显
        boolean show_system = SPUtil.getBoolean(context, Constant.SHOW_SYSTEM, true);
        show_sys.setUpdatePic(show_system);
        
        /*if (!show_system) {
            list.removeAll(sysList);
        }*/
        
      //锁屏自动清理数据的回显(在服务里注册一个广播,来监听开机启动)
        boolean activate = ServiceUtil.isActivate(context, LockScreenCleanService.class);
        auto_clean.setUpdatePic(activate);
        
    }

    private void initAnim() {
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(arrow1, "alpha", 0f,1f);
        animator1.setDuration(800);
        animator1.setRepeatCount(ObjectAnimator.INFINITE);
        animator1.setRepeatMode(ObjectAnimator.RESTART);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(arrow2, "alpha", 1f,0f);
        animator2.setDuration(800);
        animator2.setRepeatCount(ObjectAnimator.INFINITE);
        animator2.setRepeatMode(ObjectAnimator.RESTART);
        
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator1,animator2);
        
        animatorSet.start();
        
    }

    private void initEvent() {
        lv_process.setOnItemClickListener(this);
        show_sys.setOnClickListener(this);
        clean.setOnClickListener(this);
        auto_clean.setOnClickListener(this);
        
        //当点击打开抽屉时显示的动画
        drawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
            
            @Override
            public void onDrawerOpened() {
                arrow1.setImageResource(R.drawable.drawer_arrow_down);
                arrow2.setImageResource(R.drawable.drawer_arrow_down);
            }
        });
        
      //当点击关闭抽屉时显示的动画
        drawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
            
            @Override
            public void onDrawerClosed() {
                arrow1.setImageResource(R.drawable.drawer_arrow_up);
                arrow2.setImageResource(R.drawable.drawer_arrow_up);
            }
        });
    }

    // 初始化listView的数据
    private void initLvData() {
        //隐藏标题,显示进度条
        process.setVisibility(View.GONE);
        ram.setVisibility(View.GONE);
        pb.setVisibility(View.VISIBLE);
        
        new Thread(){
            @Override
            public void run() {
                
                SystemClock.sleep(1500);
                list = getAllProceessData(); 
                
                runOnUiThread(new Runnable() {
                    
                    @Override
                    public void run() {
                        
                        process.setVisibility(View.VISIBLE);
                        ram.setVisibility(View.VISIBLE);
                        pb.setVisibility(View.GONE);
                        processAdapter = new ProcessAdapter(context, list, useList, sysList);
                        lv_process.setAdapter(processAdapter); 
                    }
                });
            };
        }.start();
        
        
    }

    // 获取lv的数据,经数据分类进行存放
    private List<ProcessBean> getAllProceessData() {

        List<ProcessBean> list = new ArrayList<ProcessBean>();
        useList = new ArrayList<ProcessBean>();
        sysList = new ArrayList<ProcessBean>();
        
        pm = context.getPackageManager();
        am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        
        List<RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();

        for (RunningAppProcessInfo info : appProcesses) {
            
            ProcessBean bean = new ProcessBean();
            // 获取包名(默认情况,进程名就是包名)
            String packageName = info.processName;
            bean.packageName = packageName;
            
            try {
                // 获取图标
                Drawable icon = pm.getApplicationIcon(packageName);
                bean.icon = icon;
            } catch (NameNotFoundException e) {
                e.printStackTrace();
                Drawable drawable = getResources().getDrawable(R.drawable.ic_launcher);
                bean.icon = drawable;
            }

            // 获取应用名称
            ApplicationInfo applicationInfo;
            try {
                applicationInfo = pm.getApplicationInfo(packageName, 0);
                String label = pm.getApplicationLabel(applicationInfo).toString();
                bean.label = label;
            } catch (NameNotFoundException e) {
                e.printStackTrace();
                bean.label = packageName;
            }

            // 获取应用所占的内存
            int pid = info.pid;
            int[] pids = new int[] {pid};
            android.os.Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(pids);
            android.os.Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
            int totalPss = memoryInfo.getTotalPss();
            // 单位换算成b
            bean.ram = totalPss * 1024;

            // 判断是用户程序还是系统程序
            try {
                applicationInfo = pm.getApplicationInfo(packageName, 0);
                if ((applicationInfo.flags
                        & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                    bean.isSystem = true;
                } else {
                    bean.isSystem = false;
                }
            } catch (NameNotFoundException e) {
                e.printStackTrace();
                bean.isSystem = true;
            }

            list.add(bean);
        }

        // 将集合中的元素分类存放
        for (ProcessBean processBean : list) {
            boolean isSystem = processBean.isSystem;
            if (isSystem) {
                sysList.add(processBean);
            } else {
                useList.add(processBean);
            }
        }
        
        // 将集合中数据清空后重新添加数据
        list.clear();
        
        boolean show_system = SPUtil.getBoolean(context, Constant.SHOW_SYSTEM, true); 
        if (show_system) {
            list.addAll(useList);
            list.addAll(sysList);
        }else{
            list.addAll(useList);  
        }
        
        return list;

    }

    // 获取程序所占内存空间
    private void initRamData() {
        long[] ramData = ProcessUtil.getRamData(context);
        ram.setUseSpace("占用内存:" + Formatter.formatFileSize(context, ramData[2]));
        ram.setFreeSpace("可用的内存:" + Formatter.formatFileSize(context, ramData[1]));
        
        ram.setPbProgress((int) (ramData[2] * 100f / ramData[0] + 0.5f));

    }

    // 获取进程数
    private void initProcessData() {

        // 获取正在运行的进程数
        process.setUseSpace("正在运行" + ProcessUtil.getRunningProcessCount(context) + "个");

        // 统计总的process数目
        process.setFreeSpace("可运行" + ProcessUtil.getAllProcessCount(context) + "个");
        
        process.setPbMax(ProcessUtil.getAllProcessCount(context));
        
        process.setPbProgress(ProcessUtil.getRunningProcessCount(context));
    }

    private void initUI() {
        process = (AppManagerStoreView) findViewById(R.id.act_process_manager_process);
        ram = (AppManagerStoreView) findViewById(R.id.act_process_manager_ram);
        lv_process = (StickyListHeadersListView) findViewById(R.id.act_process_manager_lv_process);
        pb = (LinearLayout) findViewById(R.id.act_process_manager_pb);
        drawer = (SlidingDrawer) findViewById(R.id.drawer);
        arrow1 = (ImageView) findViewById(R.id.act_process_manager_arrow1);
        arrow2 = (ImageView) findViewById(R.id.act_process_manager_arrow2);
        show_sys = (SettingItemView) findViewById(R.id.act_process_manager_show_sys);
        auto_clean = (SettingItemView) findViewById(R.id.act_process_manager_auto_clean);
        clean = (ImageButton) findViewById(R.id.act_process_manager_ib_clean);
        
    }

    // 全选按钮,屏蔽掉自己的应用
    public void select_all(View v) {
        // 将集合中所有bean中isCkecked全部改成true,并通知listView更新内容
        for (ProcessBean bean : list) {
            if (getPackageName().equals(bean.packageName)) {//屏蔽掉自己的应用
                continue;
            }
            bean.isCkecked = true;
        }
        processAdapter.notifyDataSetChanged();

    }

    // 反选按钮
    public void select_reverse(View v) {
        // 将集合中所有bean中isChecked全部改成相反的状态,并通知listView更新内容
        for (ProcessBean bean : list) {
            if (getPackageName().equals(bean.packageName)) {//屏蔽掉自己的应用,不改变自己应用的状态
                continue;
            }
            bean.isCkecked = !bean.isCkecked;
        }
        processAdapter.notifyDataSetChanged();
    }

    //改变点击条目的状态,并通知listView更新内容
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ProcessBean processBean = processAdapter.getItem(position);
        if (!getPackageName().equals(processBean.packageName)) {//屏蔽掉自己的应用,不改变自己应用的状态
            processBean.isCkecked = !processBean.isCkecked;
            processAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.act_process_manager_show_sys://显示系统进程,默认为true
                //获取之前的保存的状态
                boolean show_system = SPUtil.getBoolean(context, Constant.SHOW_SYSTEM, true); 
                if (show_system) {
                    
                    //更新图片,更新结合中的数据,并通知listView更新
                    show_sys.setUpdatePic(false);
                    SPUtil.putBoolean(context, Constant.SHOW_SYSTEM, false);
                    list.removeAll(sysList);
                    processAdapter.notifyDataSetChanged();
                    
                }else{
                    show_sys.setUpdatePic(true);
                    SPUtil.putBoolean(context, Constant.SHOW_SYSTEM, true);
                    list.addAll(sysList);
                    processAdapter.notifyDataSetChanged();
                }
                break;
                
            case R.id.act_process_manager_ib_clean://清理用户选中后台进程
                
                //防止并发修改异常
                Iterator<ProcessBean> iterator = list.iterator();
                
                while(iterator.hasNext()){
                    ProcessBean next = iterator.next();
                    //清理用户选中的进程
                    if (next.isCkecked) {
                        am.killBackgroundProcesses(next.packageName);
                    }
                }
                processAdapter.notifyDataSetChanged();
                
                //清理完后重新加载数据
                list.clear();
                useList.clear();
                sysList.clear();
                
                initProcessData();
                initRamData();
                initLvData();
                
                break;
                
            case R.id.act_process_manager_auto_clean://锁屏自动清理
                // TODO
                Intent name = new Intent(context, LockScreenCleanService.class);
                boolean activate = ServiceUtil.isActivate(context, LockScreenCleanService.class);
                if (activate) {
                    //关闭服务
                    stopService(name);
                    auto_clean.setUpdatePic(false);
                }else{
                    //开启服务
                    startService(name);
                    auto_clean.setUpdatePic(true);
                }
                break;

            default:
                break;
        }
    }

   
   
        

}
