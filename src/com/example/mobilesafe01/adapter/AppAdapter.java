  
package com.example.mobilesafe01.adapter;

import java.util.List;

import org.w3c.dom.Text;

import com.example.mobilesafe01.R;
import com.example.mobilesafe01.bean.AppBean;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/** 
 * ClassName:AppAdapter <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Date:     2016年9月12日 下午4:50:20 <br/> 
 * @author   ming001 
 * @version       
 */
public class AppAdapter extends BaseAdapter {
    
    private Context context;
    private List<AppBean> list;
    private List<AppBean> userList;
    private List<AppBean> systemList;

    public AppAdapter(Context context,List<AppBean> list,List<AppBean> userList,List<AppBean> systemList) {
        this.context = context;
        this.list = list;
        this.userList = userList;
        this.systemList = systemList;
         
    }

    //对应的角标全部要改变
    @Override
    public int getCount() {
        return list==null?0:list.size()+2;//增加俩个条目textView的头
    }

    @Override
    public AppBean getItem(int position) {
        if(position==0){    //还回textView的头
            return null;
        }
        if (position==(userList.size()+1)) {
            return null;    //还回textView的头
        }
        if (position<=userList.size()) {
            return list.get(position-1);
        }
        
        
       return list.get(position-2);
        
        
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        if(position==0){    //还回textView的头
            View view = View.inflate(context, R.layout.view_app_title, null);
            TextView title = (TextView) view.findViewById(R.id.view_app_title_tv);
            
            title.setText("用户程序("+userList.size()+"个)");
            return view;
        }
        if (position==(userList.size()+1)) {
            View view = View.inflate(context, R.layout.view_app_title, null);
            TextView title = (TextView) view.findViewById(R.id.view_app_title_tv);
            
            title.setText("系统程序("+systemList.size()+"个)");   
            return view;
        }
        
        
        ViewHolder holder;
        if (convertView==null||convertView instanceof TextView) {//避免textviwe 被复用
            
            holder =  new ViewHolder();
            convertView = View.inflate(context, R.layout.item_act_app_manager_lv, null);
            holder.icon = (ImageView) convertView.findViewById(R.id.item_act_app_manager_iv_icon);
           holder.label =  (TextView) convertView.findViewById(R.id.item_act_app_manager_tv_label);
           holder.location =  (TextView) convertView.findViewById(R.id.item_act_app_manager_tv_location);
           holder.size =  (TextView) convertView.findViewById(R.id.item_act_app_manager_tv_size);
            
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        
        //设置数据
        //AppBean appBean = list.get(position);//角标不正确
        
        AppBean appBean = getItem(position);
        
        holder.icon.setImageDrawable(appBean.icon);
        holder.label.setText(appBean.label);
        
        boolean isSD = appBean.isSD;
        if (isSD) {
            holder.location.setText("SD卡安装");
        }else{
            holder.location.setText("内存安装");
        }
        //格式化文件的大小
        String size = Formatter.formatFileSize(context, appBean.size);
        holder.size.setText(size);
        
        return convertView;
    }
    
    class ViewHolder{
        ImageView icon;
        TextView label;
        TextView location;
        TextView size;
    }

}
  