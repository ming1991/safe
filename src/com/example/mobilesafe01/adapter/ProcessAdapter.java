  
package com.example.mobilesafe01.adapter;

import java.util.List;

import com.example.mobilesafe01.R;
import com.example.mobilesafe01.bean.ProcessBean;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/** 
 * ClassName:ProcessAdapter <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Date:     2016年9月14日 下午11:15:26 <br/> 
 * @author   ming001 
 * @version       
 */
public class ProcessAdapter extends BaseAdapter implements StickyListHeadersAdapter {
    private Context context;
    private List<ProcessBean> list;
    private List<ProcessBean> useList;
    private List<ProcessBean> sysList;

    public ProcessAdapter(Context context,List<ProcessBean> list,List<ProcessBean> useList,List<ProcessBean> sysList ) {
        this.context = context;
        this.list = list;
        this.useList = useList;
        this.sysList = sysList;
        
    }

    @Override
    public int getCount() {
        return list==null?0:list.size();
    }

    @Override
    public ProcessBean getItem(int position) {
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
            convertView=View.inflate(context, R.layout.item_act_process_manager_lv, null);
            
            holder.icon = (ImageView) convertView.findViewById(R.id.item_act_process_manager_iv_icon);
            holder.label = (TextView) convertView.findViewById(R.id.item_act_process_manager_tv_label);
            holder.ram = (TextView) convertView.findViewById(R.id.item_act_process_manager_tv_ram);
            holder.cb = (CheckBox) convertView.findViewById(R.id.item_act_process_manager_cb);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        
        //将集合中数据复制给控件
        final ProcessBean processBean = list.get(position);
        
        holder.icon.setImageDrawable(processBean.icon);
        holder.label.setText(processBean.label);
        holder.ram.setText("占用内存:"+Formatter.formatFileSize(context, processBean.ram));
        
        //隐藏自己应用的checkbox框
        if (context.getPackageName().equals(processBean.packageName)) {//屏蔽掉自己的应用
            holder.cb.setVisibility(View.GONE);
        }else{
            holder.cb.setVisibility(View.VISIBLE);//防止convertView复用时出现问题
        }
        
        //给checkbox设置点击事件
        holder.cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //将checkbox的状态保存在bean中isCheck中,并通知listView更新内容
                processBean.isCkecked = isChecked; 
                notifyDataSetChanged();
            }
        });
        
        //在setOnCheckedChangeListener之后,才能监听到上面方法
        
         holder.cb.setChecked(processBean.isCkecked);//processBean.isCkecked之前没有赋值,默认为false;
        
        return convertView;
    }
    
    public class ViewHolder{
        ImageView icon;
        TextView label;
        TextView ram;
        CheckBox cb;
    }

    //设置分类的头标题
    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView==null) {
            holder = new HeaderViewHolder();
            convertView = View.inflate(context, R.layout.view_app_title, null);
            holder.title = (TextView) convertView.findViewById(R.id.view_app_title_tv);
            convertView.setTag(holder);
            
        }else{
            holder = (HeaderViewHolder) convertView.getTag();
        }
        
        //为每一个条目加上一个头标题,系统会自动去掉相同的头标题
        ProcessBean processBean = list.get(position);
        if (processBean.isSystem) {
            holder.title.setText("系统进程(" + sysList.size() + "个)");
        }else{
            holder.title.setText("用户进程(" + useList.size() + "个)");
        }
        return convertView;
    }

    //还回每一个条目头标题的类型
    @Override
    public long getHeaderId(int position) {
        ProcessBean processBean = list.get(position);
        if (processBean.isSystem) {
            return 1;
        }else{
            return 2;
        }
    }
    
    public class HeaderViewHolder{
        TextView title;
    }

}
  