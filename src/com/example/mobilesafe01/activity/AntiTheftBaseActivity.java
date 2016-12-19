  
package com.example.mobilesafe01.activity;

import com.example.mobilesafe01.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View;

/** 
 * ClassName:AntiTheftBaseActivity <br/> 
 * Function: 基类的抽取   <br/> 
 * Date:     2016年9月4日 下午3:47:27 <br/> 
 * @author   ming001 
 * @version       
 */

/**
 * 原则 ,相同的功能交给父类完成,不同的功能交给子类去完成,
 * 通过抽象方法强制子类去完成
 * 
 * */
public abstract class AntiTheftBaseActivity extends Activity {
    private GestureDetector gestureDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //设置手势滑动事件
        gestureDetector = new GestureDetector(this, new SimpleOnGestureListener(){
            
            //e1 The first down motion event that started the fling.
            //e2 The move motion event that triggered the current onFling.
            
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,//一次滑动事件
                    float velocityY) {
                //在监听中实现我们的方法  ,通过x轴坐标的变化来判断用户的意图
                //特殊注意:比较x变化跟y变化的大小
                float rawX = e1.getRawX();
                float rawY = e1.getRawY();
                float rawX2 = e2.getRawX();
                float rawY2 = e2.getRawY();
                
                //为防止用户的意外滑动,//特殊注意:比较x变化跟y变化的大小,当x轴的变化大于y轴时才跳转
                float dx = Math.abs((rawX2-rawX));
                float dy = Math.abs((rawY2-rawY));
                
                if (dx>dy) {
                    if (rawX<rawX2) {
                        //跳转到前一页
                        gusture_goPre();
                        
                     }else if (rawX>rawX2) {
                         //跳转到下一个页面
                         gusture_goNext();
                     }
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }
    
    //实现手指滑动的功能,必须重写此方法
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //接管屏幕的滑动事件
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
    
    
    public void pre_page(View v){
        //监听有子类去继承的得来,子类可以直接调用
        gusture_goPre();
    }


    public void gusture_goPre() {
        
        //屏蔽界面1向右滑动的事件     // 解决滑动时上一步退出的问题
        boolean setGoPerName = setGoPerName();//添加还回值,判断是否应该跳转
        if (setGoPerName) {
            //实现页面的切换功能,动画放在跳转页面直面之后
            overridePendingTransition(R.anim.pre_enter_translate, R.anim.pre_exit_translate);
            finish();
        }
    }
    
    //跳转下一页监听
    public void next_page(View v){
        gusture_goNext();
    }


    public void gusture_goNext() {
        boolean setGoNextName = setGoNextName();
        if (setGoNextName) {
          //动画放在跳转页面直面之后
            overridePendingTransition(R.anim.next_enter_translate, R.anim.next_exit_translate);
            finish();
        }
      
    }
    
    //设置跳转页面的名字交给子类去赋值
    public abstract boolean setGoPerName();
    public abstract boolean setGoNextName();
    
}
  