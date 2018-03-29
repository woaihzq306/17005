package cn.yunhu.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.widget.ImageView;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.yunhu.BaseApplication;
import cn.yunhu.api.AppInfoApi;
import cn.yunhu.dialog.ModelDialog;
import okhttp3.Call;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * 获取数据类
 */
public class Common {

    /**
     * 返回纯字符串
     * @param data 字符串
     */
    public static String string(String data) {
        if (null == data) {
            return "";
        }

        return data;
    }


    /**
     * 获取文字
     */
    public static String getString(Context context, @StringRes int id) {
        return context.getString(id);
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     * @param context 上下文
     * @param dpValue DP值
     */
    public static int dpToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     * @param context 上下文
     * @param pxValue PX值
     */
    public static int pxToDp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * 将sp值转换为px值
     * @param context 上下文
     * @param spValue PX值
     */
    public static int spToPx(Context context, float spValue) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * scale + 0.5f);
    }


    /**
     * 将px值转换为sp值
     * @param context 上下文
     * @param pxValue PX值
     */
    public static int pxToSp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * 合并jsonObject
     */
    public static JSONObject mergeJsonObject(JSONObject oldObject, JSONObject newObject) {
        // 旧的jsonobject不为空则按照旧的来
        if (!oldObject.toString().equals("{}")) {
            Iterator iterator = newObject.keys();
            String   key;
            while (iterator.hasNext()) {
                key = (String) iterator.next();
                try {
                    // JSONObject类型
                    if (oldObject.get(key) instanceof JSONObject) {
                        oldObject.put(key, mergeJsonObject(oldObject.getJSONObject(key), newObject.getJSONObject(key)));
                    }

                    // 其他类型则直接覆盖
                    else {
                        oldObject.put(key, newObject.get(key));
                    }
                } catch (JSONException ignored) {
                }
            }

            return oldObject;
        }

        return newObject;
    }


    /**
     * 合并jsonObject
     */
    public static JSONObject mergeJsonObject(JSONObject oldObject, String jsonString) {
        jsonString = string(jsonString);
        JSONObject newObject = null;
        try {
            newObject = new JSONObject(jsonString);
        } catch (JSONException ignored) {
        }
        if (newObject != null) {
            return mergeJsonObject(oldObject, newObject);
        }
        return oldObject;
    }


    /**
     * 创建文件夹
     */
    public static boolean createDir(String dirPath) {
        File file = new File(dirPath);
        if (file.exists()) {
            return true;
        }

        return file.mkdirs();
    }


    /**
     * 获取SD卡路径
     */
    public static String getSDCardPath() {
        String  cmd = "cat /proc/mounts";
        Runtime run = Runtime.getRuntime();// 返回与当前 Java 应用程序相关的运行时对象
        try {
            Process             p    = run.exec(cmd);// 启动另一个进程来执行命令
            BufferedInputStream in   = new BufferedInputStream(p.getInputStream());
            BufferedReader      inBr = new BufferedReader(new InputStreamReader(in));

            String lineStr;
            while ((lineStr = inBr.readLine()) != null) {
                // 获得命令执行后在控制台的输出信息
                if (lineStr.contains("sdcard") && lineStr.contains(".android_secure")) {
                    String[] strArray = lineStr.split(" ");
                    if (strArray != null && strArray.length >= 5) {
                        String result = strArray[1].replace("/.android_secure", "");
                        return result + "/";
                    }
                }
                // 检查命令是否执行失败。
                if (p.waitFor() != 0 && p.exitValue() == 1) {
                    // p.exitValue()==0表示正常结束，1：非正常结束
                }
            }
            inBr.close();
            in.close();
        } catch (Exception e) {

            return Environment.getExternalStorageDirectory().getPath() + "/";
        }

        return Environment.getExternalStorageDirectory().getPath() + "/";
    }


    /**
     * 保存文件
     * @param uri      资源路径
     * @param folder   保存文件夹
     * @param filename 保存文件名
     */
    public static String saveFile(Uri uri, String folder, String filename) {
        File             file       = new File(folder, filename);
        FileInputStream  inStream   = null;
        FileOutputStream outStream  = null;
        FileChannel      inChannel  = null;
        FileChannel      outChannel = null;
        try {
            inStream = new FileInputStream(new File(uri.getPath()));
            outStream = new FileOutputStream(file);
            inChannel = inStream.getChannel();
            outChannel = outStream.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            return file.getAbsolutePath();
        } catch (Exception e) {
            return null;
        } finally {
            try {
                outChannel.close();
                outStream.close();
                inChannel.close();
                inStream.close();
            } catch (IOException ignored) {
            }
        }
    }


    /**
     * 保存Bitmap到文件夹中
     */
    public static File saveBitmap(List<Bitmap> list, String filename, int quality) {

        File file = new File(filename);

        // 如果文件已存在则删除该文件
//        if (file.exists()) {
//            file.delete();
//        }
        Bitmap bitmap;
        for (int i = 0; i < list.size(); i++) {
            bitmap = list.get(i);
            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(getFileBitmapFormat(filename, Bitmap.CompressFormat.JPEG), quality, out);
                bitmap.recycle();
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }


    public static File writeBitmapToFile(String filePath, Bitmap bitmap, int quality) {
        File desFile = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File dir = new File(Environment.getExternalStorageDirectory(), "photograph");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            try {
                desFile = new File(dir, filePath + ".jpg");

                FileOutputStream     fos = new FileOutputStream(desFile);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                if (bitmap != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
                    bos.flush();
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return desFile;
    }


    /**
     * 获取bitmap类型
     */
    public static Bitmap.CompressFormat getFileBitmapFormat(String filename, Bitmap.CompressFormat defaultFormat) {
        String extension = getFileExtension(filename, "jpeg");
        if (extension == null) {
            return defaultFormat;
        }

        switch (extension.toUpperCase()) {
            case "PNG":
                defaultFormat = Bitmap.CompressFormat.PNG;
                break;
        }

        return defaultFormat;
    }


    /**
     * 获取文件扩展名，不带点
     */
    public static String getFileExtension(String filename, String defaultExtension) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }

        return defaultExtension;
    }


    /**
     * 获取不带扩展名的文件名
     */
    public static String getFileName(String filename) {
        int start = filename.lastIndexOf("/");
        int end   = filename.lastIndexOf(".");
        if (start != -1 && end != -1) {
            return filename.substring(start + 1, end);
        } else {
            return null;
        }
    }


    /**
     * 获取 webView cookie
     */
    public static String getWebViewCookie(WebView webView) {
        return CookieManager.getInstance().getCookie(webView.getUrl());
    }


    /**
     * 获取系统信息
     */
    public static PackageInfo getAppInfo(Context context) {
        if (context != null){
            try {
                PackageManager manager = context.getPackageManager();
                return manager.getPackageInfo(context.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException ignored) {
            }

        }
        return null;
    }


    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo    packageInfo    = packageManager.getPackageInfo(context.getPackageName(), 0);
            int            labelRes       = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取系统信息
     */
    public static JSONObject getAppInfoToJson(Context context) {
        JSONObject  json = new JSONObject();
        PackageInfo info = getAppInfo(context);
        if (info != null) {
            return json;
        }

        try {
            json.put("versionCode", info.versionCode);
            json.put("versionName", info.versionName);
            json.put("packageName", info.packageName);
            return json;
        } catch (Exception e) {
            return json;
        }
    }


    /**
     * 判断网络类型 返回值 -1：没有网络  1：WIFI网络2：wap网络3：net网络
     * @param context
     */
    public static int getNetType(Context context) {
        int                 netType     = -1;
        ConnectivityManager connMgr     = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo         networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            String extraInfo = networkInfo.getExtraInfo();
            if (extraInfo == null) {
                netType = 2;
            } else {
                if (extraInfo.toLowerCase().equals("cmnet")) {
                    netType = 3;
                } else {
                    netType = 2;
                }
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = 1;
        }

        return netType;
    }


    /**
     * 将bitmap转换成base64字符串
     */
    public static String bitmapToBase64(Bitmap bitmap, int bitmapQuality) {
        String                string  = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, bitmapQuality, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;
    }


    /**
     * 将base64转换成bitmap图片
     */
    public static Bitmap base64ToBitmap(String string) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }


    /**
     * 关闭键盘
     */
    public static void closeInputMethod(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean            isOpen             = inputMethodManager.isActive();
        if (isOpen) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    /**
     * 打开键盘
     */
    public static void openInputMethod(final Context context) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                boolean            isOpen             = inputMethodManager.isActive();
                if (!isOpen) {
                    inputMethodManager.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                }
            }

        }, 500);
    }


    /**
     * 背景颜色
     */
    public static void setBackgroundDrawable(View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }


    /**
     * 正则验证
     */
    public static boolean regex(String text, String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }


    /**
     * 验证数组中是否包含某个值
     */
    public static boolean inStringArray(String value, String[] array) {
        Set<String> set = new HashSet<String>(Arrays.asList(array));
        return set.contains(value);
    }


    public static String getURLExtension(String url) {
        if (!TextUtils.isEmpty(url)) {
            int fragment = url.lastIndexOf('#');
            if (fragment > 0) {
                url = url.substring(0, fragment);
            }

            int query = url.lastIndexOf('?');
            if (query > 0) {
                url = url.substring(0, query);
            }

            int    filenamePos = url.lastIndexOf('/');
            String filename    = 0 <= filenamePos ? url.substring(filenamePos + 1) : url;

            // if the filename contains special characters, we don't
            // consider it valid for our matching purposes:
            if (!filename.isEmpty() &&
                    Pattern.matches("[a-zA-Z_0-9\\.\\-\\(\\)\\%]+", filename)) {
                int dotPos = filename.lastIndexOf('.');
                if (0 <= dotPos) {
                    return filename.substring(dotPos + 1);
                }
            }
        }

        return "";
    }


    /**
     * 将字符串转成MD5值
     */
    public static String md5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            return "";
        } catch (UnsupportedEncodingException e) {
            return "";
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }


    public static String inputStream2String(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int                   i    = -1;
        while ((i = is.read()) != -1) {
            baos.write(i);
        }

        return baos.toString();
    }


    /**
     * 判断 用户是否安装微信客户端
     */
    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo>    pinfo          = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 判断 用户是否安装QQ客户端
     */
    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo>    pinfo          = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equalsIgnoreCase("com.tencent.qqlite") || pn.equalsIgnoreCase("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 获取网落图片资源
     * @param url 链接地址
     */
    public static Bitmap getHttpBitmap(String url) {
        URL    myFileURL;
        Bitmap bitmap = null;
        try {
            myFileURL = new URL(url);
            //获得连接
            HttpURLConnection conn = (HttpURLConnection) myFileURL.openConnection();
            //设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
            conn.setConnectTimeout(6000);
            //连接设置获得数据流
            conn.setDoInput(true);
            //不使用缓存
            conn.setUseCaches(false);
            //这句可有可无，没有影响
            //conn.connect();
            //得到数据流
            InputStream is = conn.getInputStream();
            //解析得到图片
            bitmap = BitmapFactory.decodeStream(is);
            //关闭数据流
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }


    /**
     * 设置验证码图片
     * @param imageView ImageView对象
     * @param url       加载的地址
     */
    public static void setVerifyImageUrl(final ImageView imageView, String url) {
        url = string(url);
        if (url.equals("")) {
            return;
        }

        Log.e("图片地址", url);
       Map map = new HashMap<String, String>();
       map.put("User-Agent", AppInfoApi.getUserAgent());
        OkHttpUtils.get().url(url).tag("Common.setVerifyImageUrl")
                .headers(map)
                .build()
                .connTimeOut(20000).readTimeOut(20000).writeTimeOut(20000)
                .execute(new BitmapCallback() {

                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(Bitmap response, int id) {
                        if (response != null){
                            //缩放图片
                            Bitmap zoomBitmap = ImageUtil.zoomBitmap(response, 300, 100);
                            //获取圆角图片
                            Bitmap roundBitmap = ImageUtil.getRoundedCornerBitmap(zoomBitmap, 60.0f);
                            imageView.setImageBitmap(response);
                        }else {

                        }
                    }
                });
    }


    /**
     * 获取一个随机的URL
     * @param url
     * @return
     */
    public static String getRandUrl(String url) {
        url = Common.string(url);
        if (url.equals("")) {
            return "";
        }

        if (url.contains("?")) {
            url += "&____t" + System.currentTimeMillis();
        } else {
            url += "?____t" + System.currentTimeMillis();
        }
        return url;
    }


    /**
     * 获取屏幕的高宽度和高度
     * @param context
     */
    public static int[] getScreenSize(Context context) {
        DisplayMetrics outMetrics = null;
        if (context != null){
            WindowManager  wm         = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            outMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(outMetrics);
        }else {
            return new int[]{0, 0};
        }
        return new int[]{outMetrics.widthPixels, outMetrics.heightPixels};
    }


    /**
     * 复制
     * @param context
     * @param string
     */
    public static void copy(Context context, String string, String tip) {
        tip = tip == null ? "复制成功" : tip;
        ClipData         mClipData         = ClipData.newPlainText("", string(string));
        ClipboardManager mClipboardManager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        mClipboardManager.setPrimaryClip(mClipData);
        ToastUitil.showShort(tip);
    }


    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobiles) {
        String telRegex = "[1][345678]\\d{9}";
        if (TextUtils.isEmpty(mobiles)) {
            return false;
        } else {
            return mobiles.matches(telRegex);
        }
    }


    /**
     * 获取手机IMEI号
     */
    @SuppressLint("HardwareIds")
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = null;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ModelDialog.alert(context, "系统无法获取您的 <b>手机状态</b> 信息，无法为您继续提供服务。如需继续使用请前往设置授权本软件该权限，或者在安全软件中将本软件加为信任！", new ModelDialog.ModelDialogCallBack() {

                @Override
                protected boolean onCallback(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    BaseApplication.getApplication().exit();

                    return false;
                }
            });

            return "";
        }
        if (context != null){
           telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        }
        return telephonyManager.getDeviceId();
    }
}