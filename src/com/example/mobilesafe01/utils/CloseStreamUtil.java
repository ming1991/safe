
package com.example.mobilesafe01.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * ClassName:StreamUtils <br/>
 * Function: 流的工具类. <br/>
 * Date: 2016年9月2日 下午2:16:02 <br/>
 * 
 * @author Alpha
 * @version
 */
public class CloseStreamUtil {

    public static void closeStream(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
