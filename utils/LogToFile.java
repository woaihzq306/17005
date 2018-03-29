package cn.yunhu.utils;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 将Log日志写入文件中
 * <p>
 * 使用单例模式是因为要初始化文件存放位置
 * <p>
 * Created by waka on 2016/3/14.
 */
public class LogToFile {

    private static String  TAG      = "LogToFile";
    private static boolean IS_DEBUG = true;

    private static String logPath = null;//log日志存放路径

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHH", Locale.US);//日期格式;
    private static SimpleDateFormat logFormat  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);//日期格式;

    private static Date date = new Date();//因为log日志是使用日期命名的，使用静态成员变量主要是为了在整个程序运行期间只存在一个.log文件中;


    /**
     * 初始化，须在使用之前设置，最好在Application创建时调用
     */
    public static void init() {
        if (IS_DEBUG) {
            logPath = Common.getSDCardPath() + Constants.APP_NAME + "/Logs";
            Common.createDir(logPath);
            Log.e(TAG, logPath);
        }
    }


    private static final char VERBOSE = 'V';

    private static final char DEBUG = 'D';

    private static final char INFO = 'I';

    private static final char WARN = 'W';

    private static final char ERROR = 'E';


    public static void v(String tag, String msg) {
        writeToFile(VERBOSE, tag, msg);
    }


    public static void d(String tag, String msg) {
        writeToFile(DEBUG, tag, msg);
    }


    public static void i(String tag, String msg) {
        writeToFile(INFO, tag, msg);
    }


    public static void w(String tag, String msg) {
        writeToFile(WARN, tag, msg);
    }


    public static void e(String tag, String msg) {
        writeToFile(ERROR, tag, msg);
    }


    /**
     * 将log信息写入文件中
     * @param type
     * @param tag
     * @param msg
     */
    private static void writeToFile(char type, String tag, String msg) {

        if (IS_DEBUG) {
            if (null == logPath) {
                Log.e(TAG, "logPath == null ，未初始化LogToFile");
                return;
            }


            String fileName = logPath + "/log_" + dateFormat.format(date) + ".log"; //log日志名，使用时间命名，保证不重复
            String log      = "[ " + logFormat.format(new Date()) + " ] [ " + type + " ] " + tag + ": " + msg + "\n";

            //如果父路径不存在
            File file = new File(logPath);
            if (!file.exists()) {
                file.mkdirs();//创建父路径
            }

            FileOutputStream fos = null;//FileOutputStream会自动调用底层的close()方法，不用关闭
            BufferedWriter   bw  = null;
            try {

                fos = new FileOutputStream(fileName, true);//这里的第二个参数代表追加还是覆盖，true为追加，flase为覆盖
                bw = new BufferedWriter(new OutputStreamWriter(fos));
                bw.write(log);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bw != null) {
                        bw.close();//关闭缓冲流
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}