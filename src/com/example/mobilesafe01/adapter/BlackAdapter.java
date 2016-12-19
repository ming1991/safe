  
package com.example.mobilesafe01.adapter;

import java.util.List;

import com.example.mobilesafe01.R;
import com.example.mobilesafe01.bean.BlackBean;
import com.example.mobilesafe01.db.BlackDBDao;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/** 
 * ClassName:BlackAdapter <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Date:     2016年9月7日 上午11:13:33 <br/> 
 * @author   ming001 
 * @version       
 */
public class BlackAdapter extends BaseAdapter {
    private Context mContext;
    private List<BlackBean> list;
    public BlackAdapter(Context mContext, List<BlackBean> list) {
        super();
        this.mContext = mContext;
        this.list = list;
    }

    
    @Override
    public int getCount() {
        return list==null?0:list.size();
    }

    @Override
    public BlackBean getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null) {
            convertView = View.inflate(mContext, R.layout.view_activity_intercept_lv_item, null);
            
            holder = new ViewHolder();
            holder.tv_num = (TextView) convertView.findViewById(R.id.view_activity_intercept_lv_item_tv_num);
            holder.tv_type = (TextView) convertView.findViewById(R.id.view_activity_intercept_lv_item_tv_type);
            holder.iv_detele = (ImageView) convertView.findViewById(R.id.view_activity_intercept_lv_item_iv_detele);
            
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        
        //找到控件 对象并完成赋值
        final BlackBean blackBean = list.get(position);
        holder.tv_num.setText(blackBean.num);
        
        int type = blackBean.type;
        switch (type) {
            case BlackBean.TYPE_NUM:
                holder.tv_type.setText("电话拦截");
                break;
            case BlackBean.TYPE_SMS:
                holder.tv_type.setText("短信拦截");
                break;
            case BlackBean.TYPE_ALL:
                holder.tv_type.setText("电话+短信拦截");
                break;
        }
        
        //设置删除条目的图片的删除的监听
        holder.iv_detele.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                
                //删除数据库中的数据,删除集合中数据.并更新adapter,通知listView更新界面
                boolean delete = BlackDBDao.delete(mContext, blackBean.num);
                list.remove(position);
                //通知listView更新界面
                notifyDataSetChanged();
                
                if (delete) {
                    Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        return convertView;
    }
    
    //创建视图缓存类
    class ViewHolder{
        TextView tv_num;
        TextView tv_type;
        ImageView iv_detele;
    }

}
  