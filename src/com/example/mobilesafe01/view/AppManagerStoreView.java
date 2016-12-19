  
package com.example.mobilesafe01.view;

import com.example.mobilesafe01.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/** 
 * ClassName:AppManagerStoreView <br/> 
 * Function: 软件管理的自定义组合控件
 * Date:     2016年9月12日 上午12:32:29 <br/> 
 * @author   ming001 
 * @version       
 */
public class AppManagerStoreView extends LinearLayout {

    private TextView tv_name;
    private TextView tv_free;
    private TextView tv_use;
    private ProgressBar pb;

    public AppManagerStoreView(Context context) {
        this(context,null);
    }

    public AppManagerStoreView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public AppManagerStoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //挂载视图
        View.inflate(context, R.layout.view_app_manager_store, this);
        //获取子控件
        tv_name = (TextView) findViewById(R.id.view_app_manager_store_tv_name);
        tv_free = (TextView) findViewById(R.id.view_app_manager_store_tv_free);
        tv_use = (TextView) findViewById(R.id.view_app_manager_store_tv_use);
        pb = (ProgressBar) findViewById(R.id.view_app_manager_store_pb);
        
        //从父控件获取内容,设置到子控件上,不能设置内容的控件对外提供方法,设置
        
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AppManagerStoreView);
        String name = array.getString(R.styleable.AppManagerStoreView_am_text);
        //释放资源
        array.recycle();
        
        //设置内容
        tv_name.setText(name);
        
    }
    
    //对外提供方法设置子控件的属性
    //可用
    public void setFreeSpace(String free ){
        tv_free.setText(free);
    }
    //已用
    public void setUseSpace(String use ){
        tv_use.setText(use);
    }
    
    public void setPbMax(int max){
        pb.setMax(max);
    }
    
    public void setPbProgress(int progress){
        pb.setProgress(progress);
    }

    

}
  