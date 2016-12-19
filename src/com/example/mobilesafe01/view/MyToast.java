  
package com.example.mobilesafe01.view;

import com.example.mobilesafe01.R;
import com.example.mobilesafe01.utils.Constant;
import com.example.mobilesafe01.utils.SPUtil;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

/** 
 * ClassName:MyToast <br/> 
 * Function: 自定义Toast 实现自定义布局,可以拖动的效果 
 * Date:     2016年9月10日 下午4:12:42 <br/> 
 * @author   ming001 
 * @version       
 */
public class MyToast implements OnTouchListener {
    private WindowManager mWM;
    private View mView;
    private Context mContext;
    private  WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    private float startX;
    private float startY;
    
   public MyToast(Context context) {
        //初始化mWM
        mWM = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        this.mContext = context;
       
        final WindowManager.LayoutParams params = mParams;//控件属性
        
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        
        //图片格式:透明,系统默认是黑色
        params.format = PixelFormat.TRANSLUCENT;
        
        // TYPE_PRIORITY_PHONE : toast就可以被拖动了
        // TYPE_TOAST ： toast不能被拖动
        
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;//,可以被拖动的样式
        params.setTitle("Toast");
        
        //能力
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        
               // | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;//添加后不可以托动
        
        //需要添加权限
        //<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
    }
    
    //吐司的显示
    public void show(String location){
        //初始化mView
        mView = View.inflate(mContext, R.layout.view_my_toast, null);
        
        TextView tv = (TextView) mView.findViewById(R.id.view_my_toast_tv);
        tv.setText(location);
        
        //获取保存的tost样式
        int type = SPUtil.getInt(mContext, Constant.LOCATION_STYLE, R.drawable.location_style_bg0);
        //设置背景的样式
        mView.setBackgroundResource(type);
        
        //设置触摸事件
        mView.setOnTouchListener(this);
        
        mWM.addView(mView, mParams);
    }
    
    //吐司的隐藏
    public void hide(){
        if (mView != null) {
            if (mView.getParent() != null) {
                mWM.removeView(mView);
            }

            mView = null;
        }
    }

    //监听用户 的动作,获取用户移动的距离
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        
        switch (event.getAction()) {
            
            case MotionEvent.ACTION_DOWN://落下
                
                startX = event.getRawX();
                startY = event.getRawY();
                
                break;
            case MotionEvent.ACTION_MOVE://移动       多次调用
                float endX = event.getRawX();
                float endY = event.getRawY();
                
                float dX = endX - startX;
                float dY = endY - startY;
                
                //更新View的坐标
                mParams.x += dX;
                mParams.y += dY;
                
                mWM.updateViewLayout(mView, mParams); 
                
                //每次更新起始坐标
                startX = endX;
                startY = endY;
                        
                break;
            case MotionEvent.ACTION_UP://起来
                
                break;

            default:
                break;
        }
        
        return false;
    }
    
    
    /*public interface ViewManager
    {
        public void addView(View view, ViewGroup.LayoutParams params);
        public void updateViewLayout(View view, ViewGroup.LayoutParams params);
        public void removeView(View view);
    }*/
}
  