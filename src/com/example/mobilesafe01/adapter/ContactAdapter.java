  
package com.example.mobilesafe01.adapter;

import java.util.List;

import com.example.mobilesafe01.R;
import com.example.mobilesafe01.bean.ContactBean;
import com.example.mobilesafe01.db.ContactDao;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/** 
 * ClassName:ContactAdapter <br/> 
 * Function: 获取联系人数据的适配器
 * Date:     2016年9月5日 下午5:57:31 <br/> 
 * @author   ming001 
 * @version       
 */
public class ContactAdapter extends BaseAdapter {
    //通过构造方法把需要的数据传递过来
    
    private List<ContactBean> contactList;
    private Context mContext;
    public ContactAdapter(List<ContactBean> contactList, Context mContext) {
        super();
        this.contactList = contactList;
        this.mContext = mContext;
    }

    
    //判断非空,防止空指针
    @Override
    public int getCount() {
        return contactList==null?0:contactList.size();
    }

    @Override
    public ContactBean getItem(int position) {
        return contactList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //使用ViewHolder对Adapter进行优化
        
        ViewHolder holder;
        
        if (convertView==null) {
            
            //加载视图缓存类
            holder = new ViewHolder();
            
            convertView=View.inflate(mContext, R.layout.view_contact_listview_item, null);
            holder.iv = (ImageView) convertView.findViewById(R.id.view_contact_listview_item_icon);
            holder.tv_name = (TextView) convertView.findViewById(R.id.view_contact_listview_item_name);
            holder.tv_num = (TextView) convertView.findViewById(R.id.view_contact_listview_item_num);
            
            convertView.setTag(holder);
        }
        
        holder = (ViewHolder) convertView.getTag();
        
        ContactBean contactBean = contactList.get(position);
        
        holder.tv_name.setText(contactBean.name);
        holder.tv_num.setText(contactBean.num);
        
        String id = contactBean.id;
        // 设置联系人图片
        Bitmap pic = ContactDao.getPic(mContext, id);
        
        //可能图片不存在
        if (pic!=null) {
            holder.iv.setImageBitmap(pic);
        }
       
        
        return convertView;
    }
    
    
    //对adapter的优化
    class ViewHolder {
        ImageView iv;
        TextView tv_name;
        TextView tv_num;
    }

}
  