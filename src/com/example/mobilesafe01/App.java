  
package com.example.mobilesafe01;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.content.Context;

/** 
 * ClassName:App <br/> 
 * Function: 程序的入口
 * Date:     2016年9月23日 上午12:19:19 <br/> 
 * @author   ming001 
 * @version       
 */

/*@ReportsCrashes(
        formUri = "http://192.168.241.2:8080/ACRABugTest/BugServlet"(第一种方式)
    )*/

/*@ReportsCrashes(formUri = "http://192.168.241.2:8080/ACRABugTest/BugServlet",弹出吐司(第二种方式)
mode = ReportingInteractionMode.TOAST,
resToastText = R.string.crash_toast_text)*/

@ReportsCrashes(formUri = "http://192.168.241.2:8080/ACRABugTest/BugServlet",//自己服务器的地址,弹出对话框(第三种方式)
mode = ReportingInteractionMode.DIALOG,
resToastText = R.string.crash_toast_text, // optional, displayed as soon as the crash occurs, before collecting data which can take a few seconds
resDialogText = R.string.crash_dialog_text,
resDialogIcon = android.R.drawable.ic_dialog_info, //optional. default is a warning sign
resDialogTitle = R.string.crash_dialog_title, // optional. default is your application name
resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, // optional. When defined, adds a user text field input with this text resource as a label
resDialogEmailPrompt = R.string.crash_user_email_label, // optional. When defined, adds a user email text entry with this text resource as label. The email address will be populated from SharedPreferences and will be provided as an ACRA field if configured.
resDialogOkToast = R.string.crash_dialog_ok_toast // optional. displays a Toast message when the user accepts to send a report.
//resDialogTheme = R.style.AppTheme_Dialog //optional. default is Theme.Dialog
)

public class App extends Application {//在Application 节点下 设置名字 android:name="com.example.mobilesafe01.App"
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        
        // The following line triggers the initialization of ACRA
        ACRA.init(this);
    }
}
  