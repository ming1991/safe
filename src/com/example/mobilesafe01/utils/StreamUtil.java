  
package com.example.mobilesafe01.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/** 
 * ClassName:StreamUtil <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Date:     2016年9月2日 下午8:49:41 <br/> 
 * @author   ming001 
 * @version       
 */
public class StreamUtil {

    public static String stream2Sting(InputStream inputStream) {
        String result="";
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        try {
            while((len=inputStream.read(buffer))!=-1){
                bos.write(buffer, 0, len);
            }
            result=bos.toString();
            inputStream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
        
    }

}
  