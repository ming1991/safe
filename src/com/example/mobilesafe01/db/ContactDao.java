  
package com.example.mobilesafe01.db;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.example.mobilesafe01.bean.ContactBean;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;

/** 
 * ClassName:ContactDao <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Date:     2016年9月5日 下午6:23:24 <br/> 
 * @author   ming001 
 * @version       
 */
public class ContactDao {
    public static List<ContactBean> getContactInfo(Context mContext) {
        //通过内容解析者获取联系人的数据
        List<ContactBean> list = new ArrayList<ContactBean>();
        ContentResolver contentResolver = mContext.getContentResolver();
        
        Uri uri = Phone.CONTENT_URI;
        //查找联系人的姓名和电话两列
        String[] projection = new String[]{Phone.DISPLAY_NAME,Phone.NUMBER,Phone.CONTACT_ID};
        Cursor cursor = contentResolver.query(uri , projection , null, null, null);
        if (cursor!=null) {
            while(cursor.moveToNext()){
                //把查询的数据放到list中
                ContactBean contactBean = new ContactBean();
                contactBean.name = cursor.getString(0);
                contactBean.num = cursor.getString(1);
                contactBean.id= cursor.getString(2);
                
                list.add(contactBean);
            }
        }
        //关流
        cursor.close();
        return list;
    }
    
    //获取每一个联系人的图片Bitmap对象
    public static Bitmap getPic (Context mcontext,String id){
        ContentResolver cr = mcontext.getContentResolver();
        
        //Uri不同ContactsContract.Contacts.CONTENT_URI
        Uri contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, id);
        InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(cr, contactUri ); 
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        
        return bitmap;
        
    }

}
  