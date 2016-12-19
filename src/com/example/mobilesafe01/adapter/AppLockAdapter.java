
package com.example.mobilesafe01.adapter;

import java.util.List;

import com.example.mobilesafe01.R;
import com.example.mobilesafe01.bean.LockBean;
import com.example.mobilesafe01.db.LockDBDao;
import com.example.mobilesafe01.utils.ToastUtil;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * ClassName:AppLockAdapter <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Date: 2016年9月18日 下午7:54:04 <br/>
 * 
 * notifyDataSetChanged();//能同时通知两个adapter
 * 
 * @author ming001
 * @version
 */
public class AppLockAdapter extends BaseAdapter {
    private Context context;
    //private List<LockBean> list;
    private boolean shwoUnlock;
    private TextView tv_title;
    private List<LockBean> unlockList;
    private List<LockBean> lockList;

    public AppLockAdapter(Context context, List<LockBean> unlockList,List<LockBean> lockList,boolean shwoUnlock,TextView tv_title) {
        this.context = context;
        this.unlockList = unlockList;
        this.lockList = lockList;
        //this.list = list;
        this.shwoUnlock = shwoUnlock;
        this.tv_title = tv_title;

    }

    @Override
    public int getCount() {
        if (shwoUnlock) {
            return unlockList == null ? 0 : unlockList.size();
        }else{
            return lockList == null ? 0 : lockList.size(); 
        }
        
        //return list == null ? 0 : list.size();
    }

    @Override
    public LockBean getItem(int position) {
        //return list.get(position);
        if (shwoUnlock) {
            return unlockList.get(position); 
        }else{
            return lockList.get(position); 
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //标志位,判断动画是否正在加载,是就不加载
    boolean isAnimation = false;//当动画开始的时候,点击再次执行的时候会重新开始执行
    
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            
            convertView = View.inflate(context, R.layout.item_activity_app_lock_lv, null);

            holder.icon = (ImageView) convertView.findViewById(R.id.item_activity_app_lock_iv_icon);
            holder.tv_name =
                    (TextView) convertView.findViewById(R.id.item_activity_app_lock_tv_name);
            holder.iv_lock =
                    (ImageView) convertView.findViewById(R.id.item_activity_app_lock_iv_lock);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final LockBean bean = getItem(position);

        holder.icon.setImageDrawable(bean.icon);
        holder.tv_name.setText(bean.label);

        // 判断左右 加载来显示锁
        if (shwoUnlock) {
            holder.iv_lock.setImageResource(R.drawable.app_lock_selector);
        }else{
            holder.iv_lock.setImageResource(R.drawable.app_unlock_selector);
        }

        //避免convertView加上final不能被复用
        final View view = convertView;
        
        holder.iv_lock.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                final String packageName = bean.packageName;
                
                //未加锁状态,添加到数据库,删除集合数据,改变标题,通知更新
                
                if (shwoUnlock) {
                    
                    if (isAnimation) {//正在执行就return掉
                       return;
                    }
                    
                    //2.问题:快速点击同一条目的右侧按钮, 动画出现紊乱. 解决方法 : 增加标志位
                    TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0f, 
                            Animation.RELATIVE_TO_PARENT, 1.0f, 
                            Animation.RELATIVE_TO_PARENT, 0f,
                            Animation.RELATIVE_TO_PARENT, 0f);
                    translateAnimation.setDuration(200);
                    translateAnimation.setAnimationListener(new AnimationListener() {
                        
                        @Override
                        public void onAnimationStart(Animation animation) {
                            isAnimation = true;//更改标志位
                        }
                        
                        @Override
                        public void onAnimationRepeat(Animation animation) {
                            
                        }
                        
                        @Override
                        public void onAnimationEnd(Animation animation) {           //动画执行是一个异步的操作,会出现紊乱
                            boolean insert = LockDBDao.insert(context, packageName);//问题1.防止动画还没执行完,就将该动画删除掉,而执行下一个动画了
                            if (insert) {
                                ToastUtil.showToast(context, "加锁成功");
                                
                                //移除未加锁界面的数据
                                unlockList.remove(position);
                                
                                //添加已加锁界面的数据
                                lockList.add(bean);
                                
                                tv_title.setText("未加锁(" + unlockList.size() + ")个");
                                
                                notifyDataSetChanged();//能同时通知两个adapter
                                
                            }else{
                                ToastUtil.showToast(context, "加锁失败");
                            }
                            
                            isAnimation = false;//更改标志位
                        }
                    });
                    
                    view.startAnimation(translateAnimation);
                    
                    
                }else{//已加锁状态,删除数据库,删除集合数据,改变标题,通知更新
                    
                    if (isAnimation) {//正在执行就return掉
                        return;
                     }
                    
                    TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0f, 
                            Animation.RELATIVE_TO_PARENT, -1.0f, 
                            Animation.RELATIVE_TO_PARENT, 0f,
                            Animation.RELATIVE_TO_PARENT, 0f);
                    translateAnimation.setDuration(200);
                    
                    translateAnimation.setAnimationListener(new AnimationListener() {
                        
                        @Override
                        public void onAnimationStart(Animation animation) {
                            isAnimation = true;
                        }
                        
                        @Override
                        public void onAnimationRepeat(Animation animation) {
                            
                        }
                        
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            boolean delete = LockDBDao.delete(context, packageName);
                            if (delete) {
                                ToastUtil.showToast(context, "解锁成功");
                                
                                lockList.remove(position);
                                
                                unlockList.add(bean);
                                
                                tv_title.setText("已加锁(" + lockList.size() + ")个");
                                
                                notifyDataSetChanged();
                            }else{
                                ToastUtil.showToast(context, "解锁失败");
                            }
                            
                            isAnimation = false;
                        }
                    });
                    view.startAnimation(translateAnimation);
                }
                
            }
        });
        return convertView;
    }

    public class ViewHolder {
        ImageView icon;
        TextView tv_name;
        ImageView iv_lock;
    }

}
