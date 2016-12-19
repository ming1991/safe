package com.example.mobilesafe01;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.example.mobilesafe01.adapter.VirusAdapter;
import com.example.mobilesafe01.bean.VirusBean;
import com.example.mobilesafe01.db.VirusDBDao;
import com.example.mobilesafe01.utils.LogUtils;
import com.example.mobilesafe01.utils.MD5Util;
import com.github.lzyzsd.circleprogress.ArcProgress;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class AntiVirusActivity extends Activity implements OnClickListener {

    private LinearLayout ll_run;
    private TextView tv_name;
    private LinearLayout ll_finish;
    private TextView tv_result;
    private Button btn_restart;
    private List<VirusBean> list;
    private ListView lv;
    private VirusAdapter adapter;
    private ArcProgress pb;
    private LinearLayout ll_anim;
    private ImageView anim_left;
    private ImageView anim_right;
    private Context context;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_virus);
        context = this;
        initView();
        initData();
        initEvent();
        
    }

    private void initEvent() {
        btn_restart.setOnClickListener(this);
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_anti_virus_btn_restart://重新扫描
                
                showPreAnim();//设置监听,动画是一个异步操作,防止还没执行动画,就在initData()方法中影藏了视图
                //initData();
                break;

            default:
                break;
        }
    }
    
    private void showPreAnim() {
        int leftWidth = anim_left.getMeasuredWidth();
        int rightWidth = anim_right.getMeasuredWidth();
        
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(anim_left, "translationX", -leftWidth,0);
        ObjectAnimator animator2= ObjectAnimator.ofFloat(anim_left, "alpha", 0f,1f);
        
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(anim_right, "translationX", rightWidth,0);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(anim_right, "alpha", 0f,1f);
        
        ObjectAnimator animator5 = ObjectAnimator.ofFloat(ll_finish, "alpha", 1f,0f);
        
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator1,animator2,animator3,animator4,animator5);
        animatorSet.setDuration(2000);
        animatorSet.addListener(new AnimatorListener() {
            
            @Override
            public void onAnimationStart(Animator animation) {
                //禁用扫面按钮
                btn_restart.setEnabled(false);
            }
            
            @Override
            public void onAnimationRepeat(Animator animation) {
                
            }
            
            @Override
            public void onAnimationEnd(Animator animation) {
                btn_restart.setEnabled(true);
                initData();//重新扫描
            }
            
            @Override
            public void onAnimationCancel(Animator animation) {
                
            }
        });
        animatorSet.start();
        
    }

    private void initData() {
        Task task = new Task();
        task.execute();
    }
    
    //标志位是否在扫描,解决AsyncTask的单线程执行问题
    boolean isFocus  = true;
    
    @Override
    protected void onStart() {
        super.onStart();
        isFocus  = true;
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        isFocus  = false;
    }
    
    class Task extends AsyncTask<Void, VirusBean, Void>{
        private int virusCount ;
        private int progress;
        private int max;
        

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            
            virusCount = 0;
            progress = 0;
            max = 0;
            
            ll_run.setVisibility(View.VISIBLE);
            ll_finish.setVisibility(View.GONE);
            ll_anim.setVisibility(View.GONE);
            
            list = new ArrayList<VirusBean>();
            adapter = new VirusAdapter(AntiVirusActivity.this, list);
            lv.setAdapter(adapter);
        }

        @Override
        protected Void doInBackground(Void... params) {
            
            PackageManager pm = getPackageManager();
            List<ApplicationInfo> applications = pm.getInstalledApplications(0);
            
            max = applications.size();
            
            for (ApplicationInfo info : applications) {
                String packageName = info.packageName;
                
                VirusBean bean = new VirusBean();
                
                String sourceDir = info.sourceDir;//文件安装路径
                File file = new File(sourceDir);
                
                //LogUtils.e(sourceDir);
                
                try {
                    FileInputStream fis = new FileInputStream(file);
                    String md5 = MD5Util.encode(fis);           //计算文件的MD5值
                    
                   // [AntiVirusActivity.java--195] /data/app/org.itheima15.safe-1.apk
                    
                    //模拟数据
                    if (sourceDir.equals("/data/app/org.itheima15.safe-1.apk")) {
                        md5 = "a5f02bc7f0a92d2e9627ce543ce147d8";
                    }
                    
                    boolean queryIsVirus = VirusDBDao.queryIsVirus(context, md5);//查询数据库比对数据
                    bean.isVirus = queryIsVirus;
                    
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    bean.isVirus = false;
                }
                
                        
                
                bean.packageName = packageName;
                
                Drawable icon = pm.getApplicationIcon(info);
                bean.icon = icon;
                
                String label = pm.getApplicationLabel(info).toString();
                bean.label = label;
                
                progress++;
                
                publishProgress(bean);
                
                SystemClock.sleep(100);
                
                //关闭界面时,跳出循环,解决AsyncTask单线程问题
                if (!isFocus ) {
                    break;
                }
            }
            return null;
        }
        
        @Override
        protected void onProgressUpdate(VirusBean... values) {
            super.onProgressUpdate(values);
            VirusBean bean = values[0];
            
            if (bean.isVirus) {
                list.add(0,bean);
                virusCount++;
            }else{
                list.add(bean);
            }
            
            //设置进度条的加载颜色
            if (virusCount < 1) {
                pb.setFinishedStrokeColor(Color.parseColor("#FFFFFF"));
            } else if (1 <= virusCount && virusCount < 20) {
                pb.setFinishedStrokeColor(Color.parseColor("#FF6347"));
            } else if (10 <= virusCount && virusCount < 40) {
                pb.setFinishedStrokeColor(Color.parseColor("#FF34B3"));
            } else {
                pb.setFinishedStrokeColor(Color.parseColor("#FF0000"));
            }

            tv_name.setText(bean.packageName);
            
            adapter.notifyDataSetChanged();
            lv.setSelection(list.size()-1);
            
            pb.setProgress((int)(progress*100/max));//只能通过这种方式设置进度值
        }
        
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            
            if (virusCount > 0) {
                
                //设置特殊文本样式
                String string = "您的手机有" + virusCount + "个病毒,请立即查杀";
                SpannableString ss = new SpannableString(string);
                int end = string.indexOf("个");
                ss.setSpan(new ForegroundColorSpan(Color.RED), 5, end,  Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//字体颜色
                ss.setSpan(new AbsoluteSizeSpan(25), 5, end,  Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//字体大小
                ss.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 5, end,  Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//粗体
                tv_result.setText(ss);
                
            } else {
                tv_result.setText("您的手机非常安全");
            }

            ll_run.setVisibility(View.GONE);
            ll_finish.setVisibility(View.VISIBLE);
            ll_anim.setVisibility(View.VISIBLE);
            
            //执行动画
            showAnim();
            
            lv.smoothScrollToPosition(0);
            
        }
    }

    private void initView() {
        //扫描中控件
        ll_run = (LinearLayout) findViewById(R.id.activity_anti_virus_scan_run);
        pb = (ArcProgress) findViewById(R.id.activity_anti_virus_pb);
        tv_name = (TextView) findViewById(R.id.activity_anti_virus_tv_name);
        
        //扫描结束的控件
        ll_finish = (LinearLayout) findViewById(R.id.activity_anti_virus_scan_finish);
        tv_result = (TextView) findViewById(R.id.activity_anti_virus_tv_result);
        btn_restart = (Button) findViewById(R.id.activity_anti_virus_btn_restart);
        
        lv = (ListView) findViewById(R.id.activity_anti_virus_lv);
        
        //动画的控件
        ll_anim = (LinearLayout) findViewById(R.id.activity_anti_virus_ll_anim);
        anim_left = (ImageView) findViewById(R.id.activity_anti_virus_anim_left);
        anim_right = (ImageView) findViewById(R.id.activity_anti_virus_anim_right);
        
    }

    public void showAnim() {
        //允许绘制缓存,和设置缓存图片
        ll_run.setDrawingCacheEnabled(true);
        ll_run.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap drawingCache = ll_run.getDrawingCache();
        
        //截取缓存图片
        anim_left.setImageBitmap(getLeftBitmap(drawingCache));  //图片要设置成src
        anim_right.setImageBitmap(getRightBitmap(drawingCache));
        
        // 主动请求让视图测量自己的尺寸,跟权重有关
        anim_left.measure(0, 0);
        anim_right.measure(0, 0);
        
        int leftWidth = anim_left.getMeasuredWidth();
        int rightWidth = anim_right.getMeasuredWidth();
        
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(anim_left, "translationX", 0,-leftWidth);
        ObjectAnimator animator2= ObjectAnimator.ofFloat(anim_left, "alpha", 1f,0f);
        
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(anim_right, "translationX", 0,rightWidth);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(anim_right, "alpha", 1f,0f);
        
        ObjectAnimator animator5 = ObjectAnimator.ofFloat(ll_finish, "alpha", 0f,1f);
        
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator1,animator2,animator3,animator4,animator5);
        animatorSet.setDuration(2000);
        animatorSet.start();
        
        
    }

    //截取右边的图
    private Bitmap getRightBitmap(Bitmap drawingCache) {
        //创建画纸
        Bitmap createBitmap = Bitmap.createBitmap(drawingCache.getWidth()/2, drawingCache.getHeight(), drawingCache.getConfig());
        //创建画板
        Canvas canvas = new Canvas(createBitmap);
        Matrix matrix = new Matrix();
        matrix.setTranslate(-drawingCache.getWidth()/2, 0);
        Paint paint = new Paint();
        //在画纸上画图
        canvas.drawBitmap(drawingCache, matrix , paint);
        return createBitmap;
    }

    //截取左边的图
    private Bitmap getLeftBitmap(Bitmap drawingCache) {
        
        //创建画纸
        Bitmap createBitmap = Bitmap.createBitmap(drawingCache.getWidth()/2, drawingCache.getHeight(), drawingCache.getConfig());
        //创建画板
        Canvas canvas = new Canvas(createBitmap);
        Matrix matrix = new Matrix();
        Paint paint = new Paint();
        //在画纸上画图
        canvas.drawBitmap(drawingCache, matrix , paint);
        return createBitmap;
    }

    
}
