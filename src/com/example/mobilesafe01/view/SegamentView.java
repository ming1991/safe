  
package com.example.mobilesafe01.view;

import com.example.mobilesafe01.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

/** 
 * ClassName:SegamentView <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Date:     2016年9月18日 下午6:05:35 <br/> 
 * @author   ming001 
 * @version       
 */
public class SegamentView extends LinearLayout implements OnClickListener {
    
    private TextView tv_left;
    private TextView tv_right;

    public static final int SELECT_LEFT = 0;
    private static final int SELECT_RIGHT = 1;

    public SegamentView(Context context) {
        this(context,null);
    }

    public SegamentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //挂载视图
        View.inflate(context, R.layout.view_segament, this);
        
        tv_left = (TextView) findViewById(R.id.view_segament_tv_left);
        tv_right = (TextView) findViewById(R.id.view_segament_tv_right);
        
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SegamentView);
        String left_name = typedArray.getString(R.styleable.SegamentView_sv_left_tv);
        String right_name = typedArray.getString(R.styleable.SegamentView_sv_right_tv);
        int select = typedArray.getInt(R.styleable.SegamentView_sv_select, SELECT_LEFT);
        
        tv_left.setText(left_name);
        tv_right.setText(right_name);
        
        switch (select) {
            case SELECT_LEFT:
                tv_left.setSelected(true);
                break;
            case SELECT_RIGHT:
                tv_right.setSelected(true);
                break;
        }
        tv_left.setOnClickListener(this);
        tv_right.setOnClickListener(this);
        
        //释放资源
        typedArray.recycle();
        
        
    }

    //标志位判断已经选中的不能再次选中
    boolean left_select = true;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_segament_tv_left:
                if (!left_select) {
                    tv_left.setSelected(true);
                    tv_right.setSelected(false);
                    left_select = true;
                    
                    //对外提供方法,给主界面操作
                    if (listener!=null) {
                        listener.onLeftSelected();
                    }
                }
                
                break;
            case R.id.view_segament_tv_right:
                if (left_select) {
                    tv_left.setSelected(false);
                    tv_right.setSelected(true);
                    left_select = false; 
                    
                    if (listener!=null) {
                        listener.onRightSelected();
                    }
                    
                }
                break;
        }
    }
    
    
    //•需求 : 将程序所顶部的视图抽取为单独的组合控件, 并通过接口的形式和外部的Activity之间实现通信
    public interface OnSegamentSelectListener{

        void onLeftSelected();

        void onRightSelected();
        
    }
    private OnSegamentSelectListener listener;
    
    
    public void setOnSegamentSelectListener(OnSegamentSelectListener listener){
        this.listener = listener;
    }
    

}
  