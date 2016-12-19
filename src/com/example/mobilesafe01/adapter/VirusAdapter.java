  
package com.example.mobilesafe01.adapter;

import java.util.List;

import com.example.mobilesafe01.R;
import com.example.mobilesafe01.bean.VirusBean;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/** 
 * ClassName:VirusAdapter <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Date:     2016年9月21日 下午7:35:25 <br/> 
 * @author   ming001 
 * @version       
 */
public class VirusAdapter extends BaseAdapter {

    private Context context;
    private List<VirusBean> list;

    public VirusAdapter(Context context,List<VirusBean> list) {
        this.context = context;
        this.list = list;
         
    }
    @Override
    public int getCount() {
        return list==null?0:list.size();
    }

    @Override
    public VirusBean getItem(int position) {
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
            convertView =View.inflate(context, R.layout.item_activity_anti_virus_lv, null);
            
            holder.icon = (ImageView) convertView.findViewById(R.id.item_anti_virus_icon);
            holder.btn = (ImageView) convertView.findViewById(R.id.item_anti_virus_btn);
            holder.label = (TextView) convertView.findViewById(R.id.item_anti_virus_name);
            holder.status = (TextView) convertView.findViewById(R.id.item_anti_virus_status);
            
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        
        final VirusBean bean =getItem(position);
        
        holder.icon.setImageDrawable(bean.icon);
        holder.label.setText(bean.label);
        
        if (bean.isVirus) {
            holder.btn.setVisibility(View.VISIBLE);
            holder.status.setText("高威病毒");
            holder.status.setTextColor(Color.RED);
        }else{
            holder.btn.setVisibility(View.GONE);
            holder.status.setText("安全");
            holder.status.setTextColor(Color.GREEN);
        }
        
        //设置清理病毒的点击事件
        holder.btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {//跳转到应用信息界面让用户卸载
                Intent intent = new Intent();
                intent.setAction(
                        "android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse("package:" + bean.packageName));
                context.startActivity(intent);
            }
        });
        
        return convertView;
    }

    class ViewHolder{
        ImageView icon;
        ImageView btn;
        TextView label;
        TextView status;
        
    }
}
  