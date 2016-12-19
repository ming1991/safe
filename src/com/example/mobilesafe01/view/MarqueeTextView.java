  
package com.example.mobilesafe01.view;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

/** 
 * ClassName:MarqueeTextView <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Date:     2016年9月3日 上午8:25:24 <br/> 
 * @author   ming001 
 * @version       
 */
public class MarqueeTextView extends TextView {

    public MarqueeTextView(Context context) {
        this(context,null);
        // TODO Auto-generated constructor stub
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
        // TODO Auto-generated constructor stub
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setSingleLine();
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setMarqueeRepeatLimit(-1);
    }
    //是否右焦点
    @Override
    public boolean isFocused() {
        // TODO Auto-generated method stub
        return true;
    }
    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        // TODO Auto-generated method stub
        super.onFocusChanged(true, direction, previouslyFocusedRect);
    }
    
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(true);
    }

    
}
  