  
package com.example.mobilesafe01.adapter;

import java.util.List;

import com.example.mobilesafe01.R;
import com.example.mobilesafe01.bean.CommonNumChildBean;
import com.example.mobilesafe01.bean.CommonNumGroupBean;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

/** 
 * ClassName:CommonNumAdapter <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Date:     2016年9月11日 下午8:38:59 <br/> 
 * @author   ming001 
 * @version       
 */
public class CommonNumAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<CommonNumGroupBean> list;

    public CommonNumAdapter(Context context,List<CommonNumGroupBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getGroupCount() {
        return list==null?0:list.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        
        return list.get(groupPosition).child.size();
    }

    @Override
    public CommonNumGroupBean getGroup(int groupPosition) {
        return list.get(groupPosition);
    }

    @Override
    public CommonNumChildBean getChild(int groupPosition, int childPosition) {
        return list.get(groupPosition).child.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
            ViewGroup parent) {
        GroupViewHolder holder;
        if (convertView==null) {
            holder = new GroupViewHolder();
            
            convertView = View.inflate(context, R.layout.item_common_num_group, null);
            
            holder.tv_title = (TextView) convertView.findViewById(R.id.item_common_num_group_tv_title);
            
            convertView.setTag(holder);
        }else {
            holder = (GroupViewHolder) convertView.getTag();
        }
        
        CommonNumGroupBean group = getGroup(groupPosition);
            
        //设置数据
        holder.tv_title.setText(group.title);

        
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
            View convertView, ViewGroup parent) {
        ChildViewHolder holder;
        if (convertView==null) {
            holder = new ChildViewHolder();
            
            convertView = View.inflate(context, R.layout.item_common_num_child, null);
            
            holder.tv_name = (TextView) convertView.findViewById(R.id.item_common_num_child_tv_name);
            holder.tv_num = (TextView) convertView .findViewById(R.id.item_common_num_child_tv_num);
            
            convertView.setTag(holder);
            
        }else{
            holder = (ChildViewHolder) convertView.getTag();
        }
        
        CommonNumChildBean child = getChild(groupPosition, childPosition);
        //设置数据
        holder.tv_name.setText(child.name);
        holder.tv_num.setText(child.num);
        
        return convertView;
    }

    //用于child是否可以被点击 ,true
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    
    
   public class GroupViewHolder{
       TextView tv_title;
   }
   public class ChildViewHolder{
       TextView tv_name;
       TextView tv_num;
   }

}
  