  
package com.example.mobilesafe01.view;

import java.util.ArrayList;
import java.util.List;

import com.example.mobilesafe01.R;
import com.example.mobilesafe01.R.layout;
import com.example.mobilesafe01.adapter.LoactioinStyleAdapter;
import com.example.mobilesafe01.bean.LoactioinStyleBean;
import com.example.mobilesafe01.utils.Constant;
import com.example.mobilesafe01.utils.SPUtil;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/** 
 * ClassName:LocationStyleDialog <br/> 
 * Function: 自定义 对话框
 * Date:     2016年9月11日 下午4:24:21 <br/> 
 * @author   ming001 
 * @version       
 */
public class LocationStyleDialog extends Dialog implements OnItemClickListener {
    private ListView lv;
    private Context context;
    private List<LoactioinStyleBean> list;

    public LocationStyleDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    public LocationStyleDialog(Context context) {
        //加载自定义样式
        this(context,R.style.Location_Style_Dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 去掉标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_location_style_dialog);
        
        //底部垂直居中对齐
        LayoutParams attributes = getWindow().getAttributes();
        attributes.gravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(attributes);
        
        lv = (ListView) findViewById(R.id.view_location_style_dialog_lv);
        //获取数据
        
        initData();
        LoactioinStyleAdapter styleAdapter = new LoactioinStyleAdapter(context, list);
        
        lv.setAdapter(styleAdapter);
        
        lv.setOnItemClickListener(this);
        
        
    }

    private void initData() {
        //初始化list 并添加数据
        String[] titles = new String[] {"半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿",};
        int[] bgs = new int[] {R.drawable.location_style_bg0,
                R.drawable.location_style_bg1, R.drawable.location_style_bg2,
                R.drawable.location_style_bg3, R.drawable.location_style_bg4};
        
        list = new ArrayList<LoactioinStyleBean>();
        
        //获取之前保存的样式的选择;
        int selected = SPUtil.getInt(context, Constant.LOCATION_STYLE, bgs[0]);
        
        for (int i = 0; i < titles.length; i++) {
            LoactioinStyleBean bean = new LoactioinStyleBean();
            bean.icon = bgs[i];
            bean.name = titles[i];
            
            if (selected==bgs[i]) {
                bean.selected = true;
            }else{
                bean.selected = false; 
            }
            
            list.add(bean);
        }
    }

    //保存用户选择的样式
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //保存图片的id
        int icon = list.get(position).icon;
        SPUtil.putInt(context, Constant.LOCATION_STYLE, icon);
        
        dismiss();
        
    }
   
   

   

}
  