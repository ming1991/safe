package com.example.mobilesafe01;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.mobilesafe01.adapter.TrafficAdapter;
import com.example.mobilesafe01.bean.TrafficBean;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class TrafficActivity extends Activity {
    private Context mContext;
    private ListView lv;
    private LinearLayout pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic);
        mContext = this;
        
        initUI();
        
        initEvent();
        
    }

    private void initUI() {
        lv = (ListView) findViewById(R.id.act_traffic_lv);
        pb = (LinearLayout) findViewById(R.id.act_traffic_pb);
    }

    private void initEvent() {
        // 获取条目的数据
        
        pb.setVisibility(View.VISIBLE);
        
        new Thread(){
            @Override
            public void run() {
                super.run();
                SystemClock.sleep(1500);
                
                final List<TrafficBean> list = getAll();
                
                runOnUiThread(new Runnable() {
                    
                    @Override
                    public void run() {
                        pb.setVisibility(View.GONE);
                        TrafficAdapter adapter = new TrafficAdapter(mContext, list);
                        lv.setAdapter(adapter);
                    }
                });
            }
        }.start();
        
        
       
    }

    // 获取所有安装软件的信息
    private List<TrafficBean> getAll() {
        ArrayList<TrafficBean> list = new ArrayList<TrafficBean>();

        PackageManager pm = getPackageManager();
        List<ApplicationInfo> installedApplications = pm.getInstalledApplications(0);
        for (ApplicationInfo info : installedApplications) {

            // 把获取的数据封装到集合中
            TrafficBean bean = new TrafficBean();
            Drawable icon = pm.getApplicationIcon(info);
            bean.icon = icon;
            String label = pm.getApplicationLabel(info).toString();
            bean.label = label;

            // 获取应用的流量
            // ◦通过TrafficStats.getUidRxBytes获取收到的流量,
            // 通过TrafficStats.getUidTxBytes获取发送的流量,但是这种方法获取的数据并不准确

            int uid = info.uid;
            //long rxBytes = TrafficStats.getUidRxBytes(uid); // 接受的流量
            //long txBytes = TrafficStats.getUidTxBytes(uid);// 获取发送的流量

            long receive = getReceive(uid);
            long send = getSend(uid);
            bean.receive = receive;
            bean.send = send;

            list.add(bean);
        }
        return list;
    }
    

 // 此方法比较准确,但是在高版本手机上无法使用.
    // 在改版的手机上因为目录可能被更改,也会导致无法使用

    private long getSend(long uid) {
        //本地记录发送流量的文件
        long send = 0;
        File file = new File("/proc/uid_stat/" + uid + "/tcp_snd");
        BufferedReader br = null;
        try {
            
            br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            Long valueOf = Long.valueOf(line);
            send = valueOf;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (br!=null) {     //防止空指针
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
        }
        return send;
    }

    private long getReceive(long uid) {
        //本地记录接受流量的文件
        long receive = 0;
        File file = new File("/proc/uid_stat/" + uid + "/tcp_rcv");
        BufferedReader br = null;
        try {
            
            br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            Long valueOf = Long.valueOf(line);
            receive = valueOf;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            
            if (br!=null) { //防止空指针
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } 
            }
        }
        return receive;
    }

    
}
