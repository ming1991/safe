  
package com.example.mobilesafe01.view;

import com.example.mobilesafe01.R;
import com.lidroid.xutils.http.client.multipart.content.ContentBody;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/** 
 * ClassName:SettingItemView <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Date:     2016年9月3日 下午6:41:14 <br/> 
 * @author   ming001 
 * @version       
 */
public class SettingItemView extends RelativeLayout {

    private static final int BG_TOP = 0;
    private static final int BG_MID = 1;
    private static final int BG_BOTTOM = 2;
    private ImageView iv;

    public SettingItemView(Context context) {
        this(context,null);
        // TODO Auto-generated constructor stub
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
        // TODO Auto-generated constructor stub
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //写自定义布局的,将布局文件挂载在SettingItemView
        View.inflate(context, R.layout.setting_item_view, this);
        //找到控件
        TextView tv = (TextView) findViewById(R.id.setting_item_tv);
        iv = (ImageView) findViewById(R.id.setting_item_iv);
        
        //读出自定义的属性,并完成赋值
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingItemView);
        String tv_value = typedArray.getString(R.styleable.SettingItemView_siv_text);
        boolean iv_value = typedArray.getBoolean(R.styleable.SettingItemView_siv_iv, false);
        int bg_value = typedArray.getInt(R.styleable.SettingItemView_siv_bg, 0);
        //关流释放资源
        typedArray.recycle();
        
        tv.setText(tv_value);
        //按钮图片设置是否可见
        iv.setVisibility(iv_value?View.VISIBLE:View.INVISIBLE);
        //选择背景图片
        switch (bg_value) {
            case BG_TOP:
                setBackgroundResource(R.drawable.top_normal);
                break;
            case BG_MID:
                setBackgroundResource(R.drawable.middle_normal);
                break;
            case BG_BOTTOM:
                setBackgroundResource(R.drawable.bottom_normal);
                break;

            default:
                break;
        }
    }
    
    public void setUpdatePic(boolean flag){
        if (flag) {
            iv.setBackgroundResource(R.drawable.on); 
        }else{
            iv.setBackgroundResource(R.drawable.off); 
        }
    }
    
    

   

}
  