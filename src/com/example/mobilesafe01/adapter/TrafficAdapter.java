  
package com.example.mobilesafe01.adapter;

import java.util.List;

import com.example.mobilesafe01.R;
import com.example.mobilesafe01.R.id;
import com.example.mobilesafe01.bean.TrafficBean;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/** 
 * ClassName:TrafficAdapter <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Date:     2016年9月13日 上午1:26:35 <br/> 
 * @author   ming001 
 * @version       
 */
public class TrafficAdapter extends BaseAdapter {
    
    private Context context;
    private List<TrafficBean> list;

    public TrafficAdapter(Context context,List<TrafficBean> list){
        this.context = context;
        this.list = list;
        
    }

    @Override
    public int getCount() {
        return list==null?0:list.size();
    }

    @Override
    public TrafficBean getItem(int position) {
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
           holder =  new ViewHolder();
            convertView = View.inflate(context, R.layout.item_activity_traffic_lv, null);
            
            holder.icon = (ImageView) convertView.findViewById(R.id.item_activity_traffic_iv_icon);
            holder.label = (TextView) convertView.findViewById(R.id.item_activity_traffic_tv_label);
            holder.receive = (TextView) convertView.findViewById(R.id.item_activity_traffic_tv_receive);
            holder.send = (TextView) convertView.findViewById(R.id.item_activity_traffic_tv_send);
            
            convertView.setTag(holder);
        }else{
            holder =  (ViewHolder) convertView.getTag();
        }
        
        //给子控件赋值
        TrafficBean bean = list.get(position);
        holder.icon.setImageDrawable(bean.icon);
        holder.label.setText(bean.label);
        String receive = Formatter.formatFileSize(context, bean.receive);
        holder.receive.setText(receive);
        String send = Formatter.formatFileSize(context, bean.send);
        holder.send.setText(send);
        
        return convertView;
    }
    
   class ViewHolder {
       ImageView icon;
       TextView label;
       TextView receive;
       TextView send;
   }

}
  