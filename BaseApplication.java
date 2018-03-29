package cn.yunhu;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.Log;
import android.webkit.WebView;

import com.tencent.bugly.crashreport.CrashReport;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.store.PersistentCookieStore;
import com.zhy.http.okhttp.https.HttpsUtils;

import java.util.List;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import cn.yunhu.service.CallService;
import cn.yunhu.utils.LogToFile;
import cn.yunhu.utils.SPUtil;
import cn.yunhu.utils.ToastUitil;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;


public class BaseApplication extends Application {

    public static BaseApplication mBaseApplication;
    private static String          TAG             = BaseApplication.class.getName();//初始化TAG
    private static Stack<Activity> activityStack   = new Stack<Activity>();//Activity堆
    private static boolean         isTrial         = false;
    private static long            startTime       = System.currentTimeMillis() / 1000;
    private static long            systemStartTime = System.currentTimeMillis() / 1000;


    @Override
    public void onCreate() {
        super.onCreate();

      //  if (Build.VERSION.SDK_INT >= 8) {
            // 异常捕获
            CrashReport.initCrashReport(getApplicationContext(), "90e5a44997", false);
     //       }


        if (mBaseApplication == null) {
            mBaseApplication = this;
        }


        // 允许 webview 被调试
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }


        //初始化 SharedPreferences
        SPUtil.init(getApplicationContext());

        //初始化 Toast
        ToastUitil.init(getApplicationContext());


        // OkHttpUtils
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .cookieJar(new CookiesManager())
                .build();
        OkHttpUtils.initClient(okHttpClient);

        // 初始化文件LOG
        LogToFile.init();
    }


    public static BaseApplication getApplication() {
        return mBaseApplication;
    }


    /**
     * 获取上下文 Context
     */
    public static Context getContext() {
        return mBaseApplication.getApplicationContext();
    }


    /**
     * 添加Activity到集合中
     */
    public static void addActivity(Activity activity) {
        activityStack.add(activity);
    }


    /**
     * 从集合中移除Activity
     */
    public static void removeActivity(Activity activity) {
        activityStack.remove(activity);
    }


    //获取最后一个Activity
    public static Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }


    //返回寨内Activity的总数
    public int howManyActivities() {
        return activityStack.size();
    }


    /**
     * 结束指定的Activity
     * @param activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            this.activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }


    //关闭所有Activity
    public static void finishAllActivities() {
        for (Activity activity : activityStack) {
            if (null != activity) {
                activity.finish();
            }
        }
        activityStack.clear();
    }


    /**
     * 应用退出，结束所有的activity
     */
    public void exit() {
        // 停止更新服务
    //    AppUpdateService.stopServer(getApplicationContext());

        // 停止呼叫服务
        CallService.stopService(getApplicationContext());

        // 结束所有的activity
        finishAllActivities();

        // 杀死进程
        android.os.Process.killProcess(android.os.Process.myPid());

        // 结束系统
        System.exit(0);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


    @Override
    public void onTerminate() {
        super.onTerminate();


        Log.e("程序被终止", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        Log.e("程序被终止", "!!!!!!kkkkkkkkkkkkkkkkkkkkkkkkk!!!!!");
        Log.e("程序被终止", "!!!!!!kkk!!!!!!!!!!!!!!!!!!!kkk!!!!!");
        Log.e("程序被终止", "!!!!!!kkk!!!!!!!!!!!!!!!!!!!kkk!!!!!");
        Log.e("程序被终止", "!!!!!!kkk!!!!!!!!!!!!!!!!!!!kkk!!!!!");
        Log.e("程序被终止", "!!!!!!kkkkkkkkkkkkkkkkkkkkkkkkk!!!!!");
        Log.e("程序被终止", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }


    private class CookiesManager implements CookieJar {

        private final PersistentCookieStore cookieStore = new PersistentCookieStore(getApplicationContext());


        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            if (cookies != null && cookies.size() > 0) {
                cookieStore.add(url, cookies);
            }
        }


        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            List<Cookie> cookies = cookieStore.get(url);
            return cookies;
        }
    }


    public static String getApiRoot() {
        return SPUtil.getString("apiRoot");
    }


    public static void setApiRoot(String apiRoot) {
        SPUtil.putString("apiRoot", apiRoot);
    }


    /**
     * 是否试用账号
     */
    public static boolean isTrial() {
        return isTrial;
    }


    /**
     * 设置是否试用账号
     */
    public static void setIsTrial(boolean trial) {
        isTrial = trial;
    }


    public static long getStartTime() {
        return startTime;
    }


    public static void setStartTime(long startTime) {
        BaseApplication.startTime = startTime;
        systemStartTime = System.currentTimeMillis() / 1000;
    }


    public static long getSystemStartTime() {
        return systemStartTime;
    }

    public static void getapp(){

    }
}
