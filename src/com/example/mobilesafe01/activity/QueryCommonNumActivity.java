package com.example.mobilesafe01.activity;

import java.util.List;

import com.example.mobilesafe01.R;
import com.example.mobilesafe01.R.id;
import com.example.mobilesafe01.R.layout;
import com.example.mobilesafe01.adapter.CommonNumAdapter;
import com.example.mobilesafe01.bean.CommonNumChildBean;
import com.example.mobilesafe01.bean.CommonNumGroupBean;
import com.example.mobilesafe01.db.CommonNumDao;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

public class QueryCommonNumActivity extends Activity implements OnGroupClickListener, OnChildClickListener {
    private Context mContext;
    private ExpandableListView elv;
    private List<CommonNumGroupBean> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_common_num);
        mContext = this;
        initUI();
        
        //获取数据list
        
        list = CommonNumDao.getAll(mContext);
        
        CommonNumAdapter commonNumAdapter = new CommonNumAdapter(mContext, list );
        
        elv.setAdapter(commonNumAdapter);
        
        elv.setOnGroupClickListener(this);
        
        elv.setOnChildClickListener(this);
       
    }

    private void initUI() {
        elv = (ExpandableListView) findViewById(R.id.act_query_common_num_elv);
    }

 // 实现仅有一个Group可被点击展开
    
    int select = -1;//标记被打开的条目
    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        //一个也没有被打开
        if (select==-1) {
            elv.expandGroup(groupPosition);//展开
            select = groupPosition;
        }else{
            //点击关闭已关闭的条目
            if (select ==groupPosition) {
                elv.collapseGroup(groupPosition);//关闭
            }else{
                //有一个被打开,点击打开另一个
                elv.collapseGroup(select);
                elv.expandGroup(groupPosition);
                select = groupPosition;
            }
        }
        
        
        
        return true;//只有返回true,才能实现该功能
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
            int childPosition, long id) {
        //获取子条目的电话号码
        CommonNumChildBean childBean = list.get(groupPosition).child.get(childPosition);
        String num = childBean.num;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+num));
        startActivity(intent );
        return false;
    }

    

   
}
