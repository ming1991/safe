package com.example.mobilesafe01;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.example.mobilesafe01.adapter.AppAdapter;
import com.example.mobilesafe01.bean.AppBean;
import com.example.mobilesafe01.utils.LogUtils;
import com.example.mobilesafe01.view.AppManagerStoreView;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
/**
 * 将系统应用和用户程序分开显示,添加两个头
 * */
public class AppManagerActivity extends Activity implements OnScrollListener, OnItemClickListener, OnClickListener {
    private Context mContext;
    private AppManagerStoreView rom;
    private AppManagerStoreView sd;
    private ListView lv_progress;
    private List<AppBean> userList;
    private List<AppBean> systemList;
    private List<AppBean> list;
    private TextView tv_title;
    private AppAdapter appAdapter;
    private LinearLayout pb;
    private AppBean appBean;
    private PopupWindow popupWindow;
    private PackageManager pm;
    private UninstallReceiver uninstallReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        mContext = this;
        initUI();
        initData();
        initEvent();
        
        
    }
    
    private void initEvent() {
        //设置进度条
        
        pb.setVisibility(View.VISIBLE);
        tv_title.setVisibility(View.GONE);
        new Thread(){

            @Override
            public void run() {
                super.run();
                
                SystemClock.sleep(2000);
                
                list = getAllInFo();
                
                runOnUiThread(new Runnable() {
                    
                    @Override
                    public void run() {
                        
                        pb.setVisibility(View.GONE);
                        appAdapter = new AppAdapter(mContext, list,userList,systemList);
                        lv_progress.setAdapter(appAdapter);
                        tv_title.setVisibility(View.VISIBLE);
                    }
                });
            }
        }.start();
        
        //给适配器设置内容
        
        
        
        //固定头标题
        lv_progress.setOnScrollListener(this);
        lv_progress.setOnItemClickListener(this);
        
    }

    //获取所有已安装软件的信息
    private List<AppBean> getAllInFo() {
        List<AppBean> listApp = new ArrayList<AppBean>();
        
        userList = new ArrayList<AppBean>();
        systemList = new ArrayList<AppBean>();
                
        
        pm = getPackageManager();
        List<ApplicationInfo> applications = pm.getInstalledApplications(0);
        
        for (ApplicationInfo info : applications) {
            
            AppBean bean = new AppBean();
            
            //获取应用图标
            Drawable icon = pm.getApplicationIcon(info);
            bean.icon = icon;
            
            //获取应用名称
            String label = pm.getApplicationLabel(info).toString();
            bean.label = label;
            
            //获取应用的包名
            String packageName = info.packageName;
            bean.packageName = packageName;
            
            //软件安装的额全路径名
            String sourceDir = info.sourceDir;
            //bean.size = sourceDir.length();错误
            
            //获取安装文件的大小
            File file = new File(sourceDir);
            long length = file.length();
            bean.size=length;
            
           // LogUtils.e(sourceDir);
            
            //sd卡的安装路径
            if (sourceDir.startsWith("/mnt")) {
              bean.isSD = true;  
            }else{
                bean.isSD = false;
            }
            
            
            //判断是系统应用和用户应用分别装在不同的集合中,在添加在集合中
            if (sourceDir.startsWith("/system")) {
                bean.isSystem=true;
                systemList.add(bean);
            }else{
                bean.isSystem=false;
                userList.add(bean);
            }
            
            
        }
        listApp.addAll(userList);
        listApp.addAll(systemList);
        return listApp;
    }

    //获取内存和sd卡控件大小
    private void initData() {
        //设置sd卡控件大小
        File sdFile = Environment.getExternalStorageDirectory();
        long totalSpace = sdFile.getTotalSpace();
        long freeSpace = sdFile.getFreeSpace();
        long usedSpace = totalSpace-freeSpace;
       String use = Formatter.formatFileSize(mContext, usedSpace);
       String free = Formatter.formatFileSize(mContext, freeSpace);
       
       
        sd.setUseSpace(use+"已用");
        sd.setFreeSpace(free+"可用");
        sd.setPbProgress((int)(usedSpace*100/totalSpace + 0.5f));
        
        //设置rom空间大小
        File romFile = Environment.getDataDirectory();
        long totalRom = romFile.getTotalSpace();
        long freeRom = romFile.getFreeSpace();
        long usedRom = totalRom-freeRom;
       String use_rom = Formatter.formatFileSize(mContext, usedRom);
       String free_rom = Formatter.formatFileSize(mContext, freeRom);
       
        rom.setUseSpace(use_rom+"已用");
        rom.setFreeSpace(free_rom+"可用");
        rom.setPbProgress((int)(usedRom*100/totalRom+0.5f));
        
        
    }

    private void initUI() {
        rom = (AppManagerStoreView) findViewById(R.id.act_app_manager_rom);
        sd = (AppManagerStoreView) findViewById(R.id.act_app_manager_sd);
        lv_progress = (ListView) findViewById(R.id.act_app_manager_lv_progress);
        tv_title = (TextView) findViewById(R.id.act_app_manager_tv_title);
        pb = (LinearLayout) findViewById(R.id.act_app_manager_pb);
      
        
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {
        
        //防止空指针
        if (userList==null) {
            return;
        }
        
        if (firstVisibleItem<userList.size()+1) {
            tv_title.setText("用户程序("+userList.size()+"个)");
        }else{
            tv_title.setText("系统程序("+systemList.size()+"个)");
        }
    }

    //popwindow的使用 ,自定义布局
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        
        //记录被点击的条目并之为全局变量
        appBean = appAdapter.getItem(position);         //注意事项
        if (appBean==null) {
            return;
        }
        
        
        View popview = View.inflate(mContext, R.layout.view_popupwindow, null);
        
//        * @param contentView the popup's content   指定视图
//        * @param width the popup's width
//        * @param height the popup's height
        
        popupWindow = new PopupWindow(popview, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        
        //保证只显示一个
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        
        //设置动画
        popupWindow.setAnimationStyle(R.style.Pop_animation);
        
        TextView tv_uninstall = (TextView) popview.findViewById(R.id.view_popupwindow_tv_uninstall);
        TextView tv_info = (TextView) popview.findViewById(R.id.view_popupwindow_tv_info);
        TextView tv_open = (TextView) popview.findViewById(R.id.view_popupwindow_tv_open);
        TextView tv_share = (TextView) popview.findViewById(R.id.view_popupwindow_tv_share);
        
        //隐藏没有对应功能的条目textView
        //没有打开界面,影藏掉
        Intent intent = pm.getLaunchIntentForPackage(appBean.packageName);
        if (intent==null) {
            tv_open.setVisibility(View.GONE);
        }
        
        //隐藏系统的卸载界面,不能卸载
        if (appBean.isSystem) {
            tv_uninstall.setVisibility(View.GONE);
        }
        
        
        
        //设置点击事件
        tv_uninstall.setOnClickListener(this);
        tv_info.setOnClickListener(this);
        tv_open.setOnClickListener(this);
        tv_share.setOnClickListener(this);
        
       // * @param anchor the view on which to pin the popup window
       // * @param xoff A horizontal offset from the anchor in pixels
       // * @param yoff A vertical offset from the anchor in pixels
        
      //anchor,锚固定在谁身上,被点击的条目,并指定偏移量(想对于被点击的View)
        
        //默认起始坐标是指定视图的左下角 
        popupWindow.showAsDropDown(view, 100, -view.getHeight());//在show之前隐藏不需要的界面
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.view_popupwindow_tv_uninstall://卸载界面
                
                //•需求 : 点击PopupWindow的卸载界面时, 卸载对应的应用. 系统应用要隐藏该按钮
               // 2.卸载成功以后, 要通知Adapter更新界面. 需要使用广播
                
                intent = new Intent();//初始化
                intent.setAction("android.intent.action.DELETE");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse("package:"+appBean.packageName));
                startActivity(intent);
                
                uninstallReceiver = new UninstallReceiver();
                IntentFilter filter = new IntentFilter();
                filter.addAction(Intent.ACTION_PACKAGE_REMOVED);//注册系统删除应用的广播
                filter.setPriority(Integer.MAX_VALUE);//设置优先级
                filter.addDataScheme("package"); //监听卸载,必须加此条件

                registerReceiver(uninstallReceiver, filter);
                popupWindow.dismiss();
                
                break;
            case R.id.view_popupwindow_tv_info://打开该软件的安装信息界面
                
                
                intent = new Intent();
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse("package:"+appBean.packageName));
                startActivity(intent ); 
                
                popupWindow.dismiss();
                
                break;
                
            case R.id.view_popupwindow_tv_open://跳转到该应用界面
                
                intent = pm.getLaunchIntentForPackage(appBean.packageName);
                //对于没有界面的应用,判断intent是否为空
                startActivity(intent);
                
                popupWindow.dismiss();
                break;
                
            case R.id.view_popupwindow_tv_share://打开分享界面
                intent = new Intent();
                intent.setAction("android.intent.action.SEND");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "This is my Share text.");
                startActivity(Intent.createChooser(intent, "分享到"));             //分享文本
                
                popupWindow.dismiss();
                break;

            default:
                break;
        }
    }
    
    //监听系统卸载软件的广播
    class UninstallReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            
            //卸载成功后通知listView更新界面
            
            //改变的参数  list uselist  title
            
            /*list.remove(appBean);   // 做法不准确 ,
            userList.remove(appBean);
            tv_title.setText("用户程序("+userList.size()+"个)");*/
            
            String dataString = intent.getDataString();
            
            LogUtils.e(dataString);
            //[AppManagerActivity.java--354] package:wjq.WidgetDemo   获取的包名需截取

            
            dataString = dataString.replace("package:", ""); //获取卸载的包名
            
            //删除list中的元素
            //循环遍历list中的元素比对到底是哪一个bean对象
            Iterator<AppBean> iterator = list.iterator();   //避免出现并发修改异常
            while(iterator.hasNext()){
                AppBean next = iterator.next();
                if (dataString.equals(next.packageName)) {
                    iterator.remove();
                }
            }
            
            //更新uselist中者的内容
            Iterator<AppBean> iterator2 = userList.iterator();
            while(iterator2.hasNext()){
                AppBean next = iterator2.next();
                if (next.packageName.equals(dataString)) {
                    iterator2.remove();
                }
            }
            
            //更新text的内容
            tv_title.setText("用户程序("+userList.size()+"个)");
            
            //更新标题栏sd和内层的显示大小
            initData();
            
            appAdapter.notifyDataSetChanged();
        }
        
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        //解除注册释放资源
        if (uninstallReceiver!=null) { //防止空指针
            unregisterReceiver(uninstallReceiver);
        }
    }

    
}
