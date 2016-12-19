package com.example.mobilesafe01.activity;

import java.util.List;

import com.example.mobilesafe01.R;
import com.example.mobilesafe01.adapter.ContactAdapter;
import com.example.mobilesafe01.bean.ContactBean;
import com.example.mobilesafe01.db.ContactDao;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

/**
 * 查找数据库等耗时操作放在子线程中
 * 
 * */
public class SelectContactActivity extends Activity implements OnClickListener, OnItemClickListener{

    public static final String CONTACT_NUM = "contact_num";
    private ListView listView;
    private Button button;
    private Context mContext;
    private List<ContactBean> contactList;
    private ProgressBar pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);
        mContext = this;
        initUI();
        initDate();
        initEvent();
    }

    private void initEvent() {
        //  TODO
        button.setOnClickListener(this);
        listView.setOnItemClickListener(this);
        
    }
    
    private void initDate() {
    //获取封装在集合中的数据  
        new Thread(new ContactRunable()).start();
        
    }
    
    class ContactRunable implements Runnable{

        @Override
        public void run() {
            
            pb.setVisibility(View.VISIBLE);
            
            SystemClock.sleep(2000);
            
            contactList = ContactDao.getContactInfo(mContext);
            
            //更新UI的操作放在主线程中
            runOnUiThread(new Runnable() {
                
                @Override
                public void run() {
                    ContactAdapter contactAdapter = new ContactAdapter(contactList, mContext);
                    listView.setAdapter(contactAdapter);
                    pb.setVisibility(View.INVISIBLE);
                }
            });
        }
        
    }
    

    private void initUI() {
        listView = (ListView) findViewById(R.id.act_select_contact_lv);
        button = (Button) findViewById(R.id.act_select_contact_btn);
        pb = (ProgressBar) findViewById(R.id.act_select_contact_pb);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //把获取的电话号码还回给调用者
        Intent data = new Intent();
        String num = contactList.get(position).num;
        data.putExtra(CONTACT_NUM, num);
        setResult(RESULT_OK, data );;
        finish();
    }

   
}
