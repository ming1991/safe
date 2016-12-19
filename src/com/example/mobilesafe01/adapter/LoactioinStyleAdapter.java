  
package com.example.mobilesafe01.adapter;

import java.util.List;

import com.example.mobilesafe01.R;
import com.example.mobilesafe01.bean.LoactioinStyleBean;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/** 
 * ClassName:LoactioinStyleAdapter <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Date:     2016年9月11日 下午6:40:56 <br/> 
 * @author   ming001 
 * @version       
 */
public class LoactioinStyleAdapter extends BaseAdapter {
    private Context context;
    private List<LoactioinStyleBean> list;
    public LoactioinStyleAdapter(Context context,List<LoactioinStyleBean> list) {
        this.context =context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list==null?0:list.size();
    }

    @Override
    public LoactioinStyleBean getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null) {
            holder = new ViewHolder();
            
            convertView  = View.inflate(context, R.layout.item_view_location_style_dialog_lv, null);
            
            holder.iv_bg = (ImageView) convertView.findViewById(R.id.item_view_location_style_dialog_iv_bg);
            holder.iv_selected = (ImageView) convertView.findViewById(R.id.item_view_location_style_dialog_iv_selected);
            holder.tv_name = (TextView) convertView.findViewById(R.id.item_view_location_style_dialog_tv_name);
            
            convertView.setTag(holder);
            
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        
        //将listView中的数据赋值给控件
        LoactioinStyleBean bean = list.get(position);
        holder.iv_bg.setImageResource(bean.icon);
        holder.tv_name.setText(bean.name);
        
        //设置图片可不可见
        if (bean.selected) {
            holder.iv_selected.setVisibility(View.VISIBLE);
        }else{
            holder.iv_selected.setVisibility(View.GONE);
        }
        
        return convertView;
    }
    
    public class ViewHolder{
        ImageView iv_bg;
        TextView tv_name;
        ImageView iv_selected;
    }

}
  