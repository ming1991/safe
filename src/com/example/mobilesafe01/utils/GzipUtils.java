
package com.example.mobilesafe01.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * ClassName:GzipUtils <br/>
 * Function: 压缩和解压数据的工具类. <br/>
 * Date: 2016年9月8日 下午4:15:24 <br/>
 * 
 * 需求 : 查询归属地数据库体积过大, 压缩后存储在本地,
 *  然后再进行解压操作实现 : 通过GZip来实现
 * @author Alpha
 * @version
 */
public class GzipUtils {
    
    //压缩文件
    public static void zip(String in, String out) throws Exception {
        zip(new File(in), new File(out));
    }

    public static void zip(File in, File out) throws Exception {
        zip(new FileInputStream(in), new FileOutputStream(out));
    }

    public static void zip(InputStream in, OutputStream out) throws Exception {
        GZIPOutputStream gos = null;
        try {
            gos = new GZIPOutputStream(out);
            int len;
            byte[] buffer = new byte[1024];
            while ((len = in.read(buffer)) != -1) {
                gos.write(buffer, 0, len);
            }
        } finally {
            CloseStreamUtil.closeStream(gos);
            CloseStreamUtil.closeStream(in);

        }
    }

    //解压文件
    public static void unzip(String in, String out) throws Exception {
        unzip(new File(in), new File(out));
    }

    public static void unzip(File in, File out) throws Exception {
        unzip(new FileInputStream(in), new FileOutputStream(out));
    }

    public static void unzip(InputStream in, OutputStream out)
            throws Exception {

        GZIPInputStream gis = null;
        try {
            gis = new GZIPInputStream(in);
            int len;
            byte[] buffer = new byte[1024];
            while ((len = gis.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        } finally {
            CloseStreamUtil.closeStream(out);
            CloseStreamUtil.closeStream(gis);
        }
    }
}
