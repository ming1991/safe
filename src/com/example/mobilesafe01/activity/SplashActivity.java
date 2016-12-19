package com.example.mobilesafe01.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import com.example.mobilesafe01.R;
import com.example.mobilesafe01.service.ProtectedService;
import com.example.mobilesafe01.utils.CloseStreamUtil;
import com.example.mobilesafe01.utils.Constant;
import com.example.mobilesafe01.utils.GzipUtils;
import com.example.mobilesafe01.utils.PackageUtil;
import com.example.mobilesafe01.utils.SPUtil;
import com.example.mobilesafe01.utils.StreamUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

public class SplashActivity extends Activity {
    protected static final int UPDATE_APK = 200;
    protected static final int GOHOME = 201;
    protected static final int ERROR = 202;
    private static final int REQUEST_INSTALL = 100;
    private Context mContext;
    private TextView tv_version_name;
    private int versionCode_local;
    private String download_uil;
    private String desc;
    
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_APK:
                    shwoUpdteDialog();
                    break;
                case GOHOME:
                    goHomeActivity();
                    break;
                case ERROR:
                    Toast.makeText(mContext, "出错了", Toast.LENGTH_SHORT).show();
                    //同样还回主界面
                    goHomeActivity();
                    break;

                default:
                    break;
            }
        };
    };
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        //int a = 10/0;     //测试ACRA  bug收集
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mContext = this;
        initUI();
        versionCode_local = PackageUtil.getVersionCode(mContext);
        // 检查更新
        
        //根据本地的设置检查是否更新
        boolean openUpdate = SPUtil.getBoolean(mContext, Constant.OPEN_UPDATE, true);
        if (openUpdate) {
            checkUpdate();
        }else{
            //进入主界面
            goHomeActivity();
        }
        
        
      //加载并解压号码归属地查询的数据,保存到本地date/date目录下
        loadAddressDB();
        
        //加载,解压常用号码数据库的文件
        loadCommonNumDB();
        
        //启动本程序的保护服务,
        Intent service = new Intent(mContext, ProtectedService.class);
        startService(service );
        
        //加载解压病毒的数据库
        UnZipVirusDB();
        
        //安装桌面快捷方式
        installShortcut();

    }

    //需要加权限     android:permission="com.android.launcher.permission.INSTALL_SHORTCUT
   // <uses-permission android:name="com.android.launcher.permission.INSTALL_SHORTCUT"/>
    
    
    private void installShortcut() {//通过向系统发送广播的方式实现的
        boolean isInstall = SPUtil.getBoolean(mContext, Constant.INSTALL_SHORTCUT, false);//检查是否安装桌面快捷方式,防止重复创建
        if (!isInstall) {
            Intent intent = new Intent();
            intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            
            // String name = data.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "360手机卫士");//设置快捷方式的名字
            
            //Intent intent = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);
            Intent clickIntent = new Intent(mContext, SplashActivity.class);
            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, clickIntent);//点击快捷方式,跳转的界面
            
            //boolean duplicate = data.getBooleanExtra(Launcher.EXTRA_SHORTCUT_DUPLICATE, true); //是否重复创建图标
            //intent.putExtra(Launcher.EXTRA_SHORTCUT_DUPLICATE, value);
            intent.putExtra("duplicate", false);  //此设置无效
            
            //Parcelable bitmap = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap);//设置快捷图标
            
            sendBroadcast(intent);
            
            SPUtil.putBoolean(mContext, Constant.INSTALL_SHORTCUT, true);
            
        }
        
        
    }

    private void UnZipVirusDB() {//保存到本地保存到本地date/date目录下
        
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                File file = new File(getFilesDir(), "antivirus.db");
                
                if (!file.exists()) {//文件不存在时解压文件
                    InputStream inputStream = null;
                    FileOutputStream fos = null;
                    try {
                        inputStream = getAssets().open("antivirus.zip");
                        fos = new FileOutputStream(file);
                        GzipUtils.unzip(inputStream, fos);
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        
    }

//加载,解压常用号码数据库的文件
    private void loadCommonNumDB() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                
                InputStream inputStream;
                try {
                    
                    inputStream = getAssets().open("commonnum.zip");
                    FileOutputStream output = openFileOutput("commonnum.db", Context.MODE_PRIVATE);
                    GzipUtils.unzip(inputStream, output);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
            }
        }.start();
    }

    private void loadAddressDB() {
      //加载并解压号码归属地查询的数据,保存到本地date/date目录下
        //从assets目录下加载数据
        new Thread(){
            @Override
            public void run() {
                 File file = new File(getFilesDir(), "address.db");
                 
                 //避免文件的重复加载
                 if (file.exists()) {
                     return ;
                 }
                 
                 try {
                    InputStream inputStream = getAssets().open("address.zip");
                    FileOutputStream fos = new FileOutputStream(file);
                    GzipUtils.unzip(inputStream, fos);
                    
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
            };
        }.start();;
        
    }

    protected void shwoUpdteDialog() {
        Builder builder = new AlertDialog.Builder(mContext);
        builder.setIcon(R.drawable.hcql);
        builder.setTitle("提示");
        builder.setMessage(desc);
        builder.setPositiveButton("马上升级", new OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //现在新软件到本地
                downloadNewVersion();
            }
        });
        
        builder.setNegativeButton("稍后再说", new OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                 //还回到主界面
                goHomeActivity();
            }
        });
       //当点击取消时;
        builder.setOnCancelListener( new OnCancelListener() {
            
            @Override
            public void onCancel(DialogInterface dialog) {
               //跳转到主界面
                goHomeActivity();
                
            }
        });
        builder.create().show();
        
    }

    protected void downloadNewVersion() {
        //判断本地sd卡是否挂载
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            
            final ProgressDialog progressDialog = new ProgressDialog(mContext);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(100);
            //防止用户按返回键
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            
            progressDialog.show();
            
            final File file = new File(Environment.getExternalStorageDirectory(), "mobilesafe.apk");
            HttpUtils httpUtils = new HttpUtils();
            httpUtils.download(download_uil, file.getAbsolutePath(), new RequestCallBack<File>() {
                
                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    // TODO Auto-generated method stub
                    //下载后跳转到系统安装界面
                    progressDialog.dismiss();
                    installNewversion(file);
                };
                
                @Override
                public void onFailure(HttpException error, String msg) {
                    // TODO Auto-generated method stub
                    
                }
                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    // TODO Auto-generated method stub
                    super.onLoading(total, current, isUploading);
                    //显示更新进度条
                    progressDialog.setProgress((int)(current*100/total));
                    
                }
            });
        }
        
        
        
    }

    protected void installNewversion(File file) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        //跳转安装界面
        startActivityForResult(intent, REQUEST_INSTALL);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
            if (requestCode==REQUEST_INSTALL) {
                if (resultCode==RESULT_OK) {
                    System.out.println("安装成功");
                }else{
                    goHomeActivity(); 
                }
            }
    }

    /**
     * DESC : 跳转到主界面
     */
    protected void goHomeActivity() {
        //防止跳转过快,延迟一下时间
        handler.postDelayed(new Runnable() {
            
            @Override
            public void run() {
                Intent intent = new Intent(mContext, MainActivity.class);
                // TODO Auto-generated method stub
                startActivity(intent);
                //并关闭本界面
                finish();
            }
        }, 2000);
    }

    

    private void checkUpdate() {
        // 请求网络,获取服务端的数据
        new Thread() {
           

            @Override
            public void run() {

                String spec = "http://192.168.56.1:8080/mobileupdate.json";

                try {
                    URL url = new URL(spec);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    int code = connection.getResponseCode();
                    if (code == 200) {
                        InputStream inputStream = connection.getInputStream();
                        String reslut = StreamUtil.stream2Sting(inputStream);

                        System.out.println(reslut);

                        // 解析json
                        JSONObject jsonObject = new JSONObject(reslut);
                        String version_name_service = jsonObject.getString("version_name");
                        int version_code_service = jsonObject.getInt("version_code");
                        desc = jsonObject.getString("description");
                        download_uil = jsonObject.getString("download_uil");

                        if (version_code_service > versionCode_local) {
                            // TODO 提示用户更新
                            handler.sendEmptyMessage(UPDATE_APK);
                        } else {
                            // TODO 直接跳转到主界面
                            handler.sendEmptyMessage(GOHOME);
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(ERROR);
                }

            }
        }.start();

    }

    private void initUI() {
        tv_version_name = (TextView) findViewById(R.id.act_splash_tv_version_name);
        String versionName = PackageUtil.getVersionName(mContext);
        tv_version_name.setText("版本:" + versionName);
    }

}
