package com.example.mobilesafe01.activity;

import java.util.List;

import com.example.mobilesafe01.R;
import com.example.mobilesafe01.R.id;
import com.example.mobilesafe01.R.layout;
import com.example.mobilesafe01.adapter.BlackAdapter;
import com.example.mobilesafe01.bean.BlackBean;
import com.example.mobilesafe01.db.BlackDB;
import com.example.mobilesafe01.db.BlackDBDao;
import com.example.mobilesafe01.utils.LogUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
/**
 * 黑名单的添加和删除放在一个界面里面操作
 * */
public class InterceptActivity extends Activity implements OnClickListener, OnItemClickListener, OnScrollListener {
    protected static final int PAGE_ITEM = 5;
    private static final int REQUEST_CODE_ADD = 200;
    private static final int REQUEST_CODE_UPDATE = 201;
    
    //设置标识符,辨别用户的操作
    public static final String BLACK_UPDATE = "black_update";
    public static final String BLACK_ADD = "black_add";
    public static final String POSITION = "position";
    private Context mContext;
    private ImageView iv_add;
    private ListView lv;
    private List<BlackBean> list;
    private ProgressBar pb;
    private ImageView iv_empty;
    private BlackAdapter blackAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intercept);
        mContext = this;
        initUI();
        initDate();
        initEvent();
        
    }
    
    //从数据库查询,并显示到界面上,一般不放在onstart里面
    @Override
    protected void onStart() {
        super.onStart();
        
    }

    private void initEvent() {
        iv_add.setOnClickListener(this);
        lv.setOnItemClickListener(this);
        
        //给listView设置滑动事件
        lv.setOnScrollListener(this);
    }
    
    //设置加载的进度条
    private void initDate() {   //实现分页加载
        //子线程不能更新UI
        pb.setVisibility(View.VISIBLE);
        new Thread(){
            @Override
            public void run() {
                //获取数据库数据放在子线程中
                SystemClock.sleep(1000);
                //一次加载20条数据
                list = BlackDBDao.getPart(mContext, PAGE_ITEM, 0);
                //list = BlackDBDao.getAllInfo(mContext);
                
                runOnUiThread(new Runnable() {
                    
                    @Override
                    public void run() {
                        pb.setVisibility(View.GONE);
                        
                        blackAdapter = new BlackAdapter(mContext, list);
                        lv.setAdapter(blackAdapter);
                        
                        //设置一个空View,当没有条目时显示
                        lv.setEmptyView(iv_empty);
                        //设置listView总是显示到最后的一个条目上
//                        lv.setSelection(list.size()-1);
                    }
                });
                
            };
        }.start();
        
       
    }

    private void initUI() {
        iv_add = (ImageView) findViewById(R.id.act_intercept_iv_add);
        lv = (ListView) findViewById(R.id.act_intercept_lv);
        pb = (ProgressBar) findViewById(R.id.act_intercept_pb);
        iv_empty = (ImageView) findViewById(R.id.act_intercept_iv_empty);
        
    }

    @Override
    public void onClick(View v) {
        //点击进入添加页面
        Intent intent = new Intent(mContext, OperateBlackActivity.class);
        intent.setAction(BLACK_ADD);
        startActivityForResult(intent, REQUEST_CODE_ADD);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_ADD:
                if (resultCode==RESULT_OK) {
                    
                    String num = data.getStringExtra(BlackDB.BlackTable.C_NUM);
                    int type = data.getIntExtra(BlackDB.BlackTable.C_TYPE, -1);
                   BlackBean blackBean = new BlackBean();
                   blackBean.num = num;
                   blackBean.type = type;
                   
                 //获取还回的数据添加到集合中,并通知adapter更新(在还回前已经添加到数据库中了)
                   list.add(blackBean);
                   blackAdapter.notifyDataSetChanged();
                }
                
                break;
            case REQUEST_CODE_UPDATE:
                
                if (resultCode==RESULT_OK) {
                    
                  //获取指定位置的条目的数据,并将list中数据更新
                    int type = data.getIntExtra(BlackDB.BlackTable.C_TYPE, -1);
                    int position = data.getIntExtra(POSITION, -1);
                    BlackBean blackBean = list.get(position);
                    //修改集合中的数据
                    blackBean.type=type;
                    
                  //通知adapter更新(在还回前已经添加到数据库中了)
                    blackAdapter.notifyDataSetChanged(); 
                }
                break;
        }
    }
    

    //跳转到指定的条目界面上
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (list!=null) {
            //从listview的bean对象中获取数据
            BlackBean blackBean = list.get(position);
            String num = blackBean.num;
            int type = blackBean.type;
            
            Intent intent = new Intent(mContext, OperateBlackActivity.class);
            //通过 intent传递数据
            intent.setAction(BLACK_UPDATE);
            intent.putExtra(BlackDB.BlackTable.C_NUM, num);
            intent.putExtra(BlackDB.BlackTable.C_TYPE, type);
            intent.putExtra(POSITION, position);
            startActivityForResult(intent, REQUEST_CODE_UPDATE);
            //实现页面的跳转
            
            
        }
    }

    //•需求 : 分页加载后, 滑动到列表界面的底部时, 自动加载更多数据并显示
    //•实现 : 为listview增加setOnScrollListener监听

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        
    }
    
 // 标志位:记录当前是否正在加载新的数据
    private boolean load = false; 
    
    // 标志位:记录当前是否有新的数据
    private boolean hasNext = true;
    
    //当加载当底部是,自动更新加载下一页内容,并添加到集合中,通知listView更新内容
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, 
            final int totalItemCount) {
        
        if (list==null) {
            return;
        }
        
        if (load) {
            return;
        }
        
        if (!hasNext) {
            return;
        }
        
        //当第一条可见条目加可见的总条目数等于总的条目数时,证明加载到底部
        if ((firstVisibleItem+visibleItemCount)==totalItemCount) {  //当进入界面的时候就会执行多次
            
            LogUtils.e(firstVisibleItem+"="+visibleItemCount);
            
            //进入加载,不让其他人加载
            load = true;
            pb.setVisibility(View.VISIBLE);
            new Thread(){
                @Override
                public void run() {
                    SystemClock.sleep(1000);
                    //把新加载的数据添加到集合中
                    List<BlackBean> newList = BlackDBDao.getPart(mContext, PAGE_ITEM, totalItemCount); 
                    
                    LogUtils.e(newList.size()+"="+totalItemCount);
                    
                    
                    if (newList.size()<PAGE_ITEM) {
                        hasNext = false;
                    }
                    
                    list.addAll(newList);
                    
                    runOnUiThread(new Runnable() {
                        
                        @Override
                        public void run() {
                            
                            //加载完毕,让其他人可以进入加载
                            
                            pb.setVisibility(View.GONE);
                          //通知adapter 更新数据
                            blackAdapter.notifyDataSetChanged();//只能在主线程中执行
                            //lv.setSelection(list.size()-1);  错误处
                            load = false;
                        }
                    });
                };
            }.start();
            
            
            
        }
    }

   
}
