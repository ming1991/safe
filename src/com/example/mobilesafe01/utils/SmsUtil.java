
package com.example.mobilesafe01.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.example.mobilesafe01.bean.SmsBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.SystemClock;
import android.widget.Toast;

/**
 * ClassName:SmsUtil <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Date: 2016年9月17日 下午8:26:33 <br/>
 * 
 * @author ming001
 * @version
 */
public class SmsUtil {

    // 为了避免多次修改代码.我们可以将业务和UI进行剥离,减少因为需求的变化而导致大量的去修改代码.
    // •实现 : 增加一个接口,将UI的展现通过接口中的方法来实现

    /**
     * DESC : 短信的备份
     * 
     * @param context
     * @param listener
     */
    public static void smsBackup(final Context context, final OnProgressDialogListener listener) {

        new AsyncTask<Void, Integer, Exception>() {

            private List<SmsBean> list;

            // 显示进度圈
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                listener.onPre();
            }

            // 子线程后台执行数据库耗时操作
            @Override
            protected Exception doInBackground(Void... params) {

                list = new ArrayList<SmsBean>();

                ContentResolver contentResolver = context.getContentResolver();

                Uri uri = Uri.parse("content://sms");
                String[] projection = new String[] {"address", "date", "read", "type", "body"};
                Cursor cursor = contentResolver.query(uri, projection, null, null, null);

                int i = 0;
                int count = cursor.getCount();
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        SmsBean smsBean = new SmsBean();

                        smsBean.address = cursor.getString(0);
                        smsBean.date = cursor.getString(1);
                        smsBean.read = cursor.getString(2);
                        smsBean.type = cursor.getString(3);
                        smsBean.body = cursor.getString(4);

                        list.add(smsBean);

                        publishProgress(++i, count);

                        SystemClock.sleep(500);
                    }
                    cursor.close();

                    Gson gson = new Gson();

                    String json = gson.toJson(list);// 用gson将对象转换成json字符串

                    // LogUtils.e(json);

                    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                        // 保存到sd卡上
                        File file = new File(Environment.getExternalStorageDirectory(), "sms.json");
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(file);
                            fos.write(json.getBytes());
                            return null;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return e;
                        } finally {
                            CloseStreamUtil.closeStream(fos);
                        }
                    }
                }
                return new Exception("没有SD卡");

            }

            // 更新进度条
            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);

                listener.onProgressUpdate(values);
            }

            // 更新UI
            @Override
            protected void onPostExecute(Exception result) {
                super.onPostExecute(result);
                if (result == null) {
                    listener.onSuccess();
                } else {
                    listener.onFail();
                }

            }
        }.execute();
    }

    public interface OnProgressDialogListener {

        /**
         * DESC : 短信备份之前,
         */
        public void onPre();

        /**
         * DESC : 短信备份失败时
         */
        public void onFail();

        /**
         * DESC : 短信备份成功时
         */
        public void onSuccess();

        /**
         * DESC : 短信备份过程中
         * 
         * @param values
         */
        public void onProgressUpdate(Integer... values);

    }

    /**
     * DESC : 短信的还原
     */
    public static void smsRestore(final Context context, final OnProgressDialogListener listener) {

        new AsyncTask<Void, Integer, Exception>() {

            @Override
            protected void onPreExecute() {
                if (listener != null) {
                    listener.onPre();
                }

            }

            @Override
            protected Exception doInBackground(Void... params) {
                File file = new File(Environment.getExternalStorageDirectory(), "sms.json");
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new FileReader(file));
                    String json = br.readLine(); // 保存到本地的只有一行
                    Gson gson = new Gson();

                    ArrayList<SmsBean> list =
                            gson.fromJson(json, new TypeToken<ArrayList<SmsBean>>() {}.getType());// 模板代码,将json字符串转换成list对象
                    ContentResolver contentResolver = context.getContentResolver();
                    Uri uri = Uri.parse("content://sms");

                    int max = list.size();
                    int progress = 0;
                    for (SmsBean smsBean : list) {

                        ContentValues values = new ContentValues();
                        values.put("address", smsBean.address);
                        values.put("date", smsBean.date);
                        values.put("read", smsBean.read);
                        values.put("type", smsBean.type);
                        values.put("body", smsBean.body);
                        contentResolver.insert(uri, values);

                        publishProgress(++progress, max);
                        
                        SystemClock.sleep(500);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    return e;
                } finally {
                    CloseStreamUtil.closeStream(br);
                }
                return null;

            }

            protected void onProgressUpdate(Integer... values) {
                if (listener != null) {
                    listener.onProgressUpdate(values);
                }

            }

            @Override
            protected void onPostExecute(Exception result) {
                if (listener != null) {
                    if (result == null) {
                        listener.onSuccess();

                    } else {
                        listener.onFail();
                    }
                }
            };
        }.execute();

    }
}
