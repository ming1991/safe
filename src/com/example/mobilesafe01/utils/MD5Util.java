  
package com.example.mobilesafe01.utils;

import java.io.InputStream;
import java.security.MessageDigest;

/** 
 * ClassName:MD5Util <br/> 
 * Function: 计算文件和字符串的MD5值(加密)
 * Date:     2016年9月22日 下午2:50:28 <br/> 
 * @author   ming001 
 * @version       
 */
public class MD5Util {
 // 获取字符的MD5值
    public static String encode(String in) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(in.getBytes());

            for (byte b : digest) {
                int c = b & 0xff;
                String s = Integer.toHexString(c);
                if (s.length() == 1) {
                    s = "0" + s;
                }
                result += s;
            }

        } catch (Exception cnse) {
        }
        return result;
    }

    // 获取文件的MD5值
    public static String encode(InputStream in) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = new byte[8192];
            int len;
            while ((len = in.read(bytes)) > 0) {
                md.update(bytes, 0, len);
            }
            byte[] digest = md.digest();

            for (byte b : digest) {
                int c = b & 0xff;
                String s = Integer.toHexString(c);
                if (s.length() == 1) {
                    s = "0" + s;
                }
                result += s;
            }
        } catch (Exception cnse) {
        } finally {
            CloseStreamUtil.closeStream(in);
        }
        return result;
    }
}
  