  
package com.example.mobilesafe01.adapter;

import java.util.List;

import com.example.mobilesafe01.R;
import com.example.mobilesafe01.bean.CacheBean;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/** 
 * ClassName:CacheAdapter <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Date:     2016年9月20日 下午7:49:13 <br/> 
 * @author   ming001 
 * @version       
 */
public class CacheAdapter extends BaseAdapter {
     private Context context;
    private List<CacheBean> list;

    public CacheAdapter(Context context,List<CacheBean> list) {
        this.context = context;
        this.list = list;
         
    }

    @Override
    public int getCount() {
        return list==null?0:list.size();
    }

    @Override
    public CacheBean getItem(int position) {
        return list==null?null:list.get(position);
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
            
            convertView = View.inflate(context, R.layout.item_activity_cache_clean_lv, null);
            
            holder.icon = (ImageView) convertView.findViewById(R.id.item_cache_clean_icon);
            holder.btn = (ImageView) convertView.findViewById(R.id.item_cache_clean_btn);
            holder.size = (TextView) convertView.findViewById(R.id.item_cache_clean_size);
            holder.name = (TextView) convertView.findViewById(R.id.item_cache_clean_name);
            
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        
        final CacheBean bean = getItem(position);
        
        holder.icon.setImageDrawable(bean.icon);
        holder.name.setText(bean.label);
        
        holder.size.setText("缓存大小:"+Formatter.formatFileSize(context, bean.size));
        
        if (bean.size>0) {
            holder.btn.setVisibility(View.VISIBLE);
        }else{
            holder.btn.setVisibility(View.GONE);  
        }
        
        //条目的清理的点击事件
        holder.btn.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {//跳转到系统设置的详情页面,用户手动删除缓存
                Intent intent = new Intent();
                intent.setAction(
                        "android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse("package:" + bean.packageName));
                context.startActivity(intent);
                //从新加载数据
                //notifyDataSetChanged();
            }
        });
        
        return convertView;
    }
    
    class ViewHolder{
        ImageView icon;
        ImageView btn;
        TextView name;
        TextView size;
        
    }

}
  