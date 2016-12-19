  
package com.example.mobilesafe01.receiver;

import com.example.mobilesafe01.service.UpdateAppWidgetService;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

/** 
 * ClassName:MyAppWidgetProvider <br/> 
 * Function: 创建桌面小控件 
 * Date:     2016年9月16日 下午7:42:42 <br/> 
 * @author   ming001 
 * @version  
 * 
 *实施更新状态,是通过服务,死循环来实现的
 *◦一键清理是通过后台服务来完成
 */
public class MyAppWidgetProvider extends AppWidgetProvider {
    
    // Widget在固定的时间里更新时调用的方法
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        
        //开启服务,更新widget的内容
        Intent service = new Intent(context, UpdateAppWidgetService.class);
        context.startService(service );
        
        
    }
}
  