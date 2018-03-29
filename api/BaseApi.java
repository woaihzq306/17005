package cn.yunhu.api;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import cn.yunhu.BaseApplication;
import cn.yunhu.activity.BaseActivity;
import cn.yunhu.activity.BuyRechargeActivity;
import cn.yunhu.activity.BuyUnlockActivity;
import cn.yunhu.activity.LoginActivity;
import cn.yunhu.dialog.ModelDialog;
import cn.yunhu.dialog.Pending;
import cn.yunhu.http.OkRequest;
import cn.yunhu.http.OkRequestCallback;
import cn.yunhu.utils.Common;
import cn.yunhu.utils.Constants;
import cn.yunhu.utils.SPUtil;
import cn.yunhu.utils.ToastUitil;

import static cn.yunhu.http.Rest.*;


/**
 * 接口基本类
 */
public class BaseApi {

    //+--------------------------------------
    //| 错误代码
    //+--------------------------------------
    /**
     * 需要登录
     */
    final public static int CODE_LOGIN = 3010000;
    /**
     * 需要购买
     */
    final public static int CODE_NEED_BUY_GROUP = 3010001;
    /**
     * 需要购买
     */
    final public static int CODE_NEED_BUY_UNLOCK = 3010002;

    /**
     * APP ID
     */
    public static String APP_ID;


    /**
     * 上下文
     */
    protected Context context;
    /**
     * activity
     */
    protected BaseActivity activity;
    /**
     * 是否POST提交
     */
    private boolean isPost = true;
    /**
     * 是否POST JSON数据
     */
    private boolean isPostJson = false;
    /**
     * 是否加载中
     */
    private boolean isPending = false;
    private boolean parseError = true;


    //+--------------------------------------
    //| 加载等待提示
    //+--------------------------------------
    /**
     * 加载等待提示框
     */
    private Pending pending = null;
    /**
     * 是否显示加载提示框
     */
    private boolean pendingStatus = true;
    /**
     * 加载等待提示文字
     */
    private String pendingMessage = "请稍候...";


    //+--------------------------------------
    //| 请求参数
    //+--------------------------------------
    /**
     * 字符串参数
     */
    protected HashMap<String, String> stringParams = new HashMap<>();
    /**
     * 附件参数
     */
    protected HashMap<String, File> fileParams = new HashMap<>();
    /**
     * 请求路径
     */
    private String apiPath = "";


    //+--------------------------------------
    //| 回调
    //+--------------------------------------
    /**
     * 解析回调
     */
    private RestCallback restCallback = new RestCallback() {

        @Override
        public void onSuccess(String message, JSONObject data) {

        }


        @Override
        public void onError(String message, String field, int code) {

        }
    };

    /**
     * 回调处理
     */
    private OkRequestCallback okRequestCallback = new OkRequestCallback() {

        @Override
        public void onSuccess(final String result, int id) {
            isPending = false;

            parse(result, new RestCallback() {

                @Override
                public void onSuccess(String message, JSONObject data) {
                    restCallback.onSuccess(message, data);
                }


                @Override
                public void onError(String message, String field, int code) {
                    if (parseError && executeError(message, field, code)) {
                        return;
                    }

                    restCallback.onError(message, field, code);
                }
            });
        }


        @Override
        public boolean onError(String message, int id) {
            isPending = false;
            restCallback.onError(message, "", id);
            return super.onError(message, id);
        }


        @Override
        public void onComplete(int id) {
            super.onComplete(id);
            if (pending != null && activity != null && !activity.isFinishing()) {
                pending.dismiss();
            }
            isPending = false;
        }
    };

    /**
     * 构造
     */
    BaseApi(Context context) {

        if (context != null) {
            this.context = context;
            this.activity = (BaseActivity) context;

            // APP ID设置
            try {
                ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                APP_ID = String.valueOf(applicationInfo.metaData.get("BUGLY_APP_CHANNEL"));
            } catch (PackageManager.NameNotFoundException e) {
                APP_ID = "1";
            }
        }

        this.setAppId(APP_ID);

        this.setProxyId(AppInfoApi.getProxyId());


        // 登录密钥
        this.setUserToken(LoginApi.getUserToken());


        // 接口密钥
//        long   sysStartTime = BaseApplication.getSystemStartTime();
//        long   startTime    = BaseApplication.getStartTime();
        long sysStartTime = BaseApplication.getSystemStartTime();
        long startTime = BaseApplication.getStartTime();
        long currentTime = System.currentTimeMillis() / 1000;
        String requestTime = (startTime + (currentTime - sysStartTime)) + "";
        String requestStr = getRandomString(16);
        String temp = "app_id=" + APP_ID;
        temp += "&proxy_id=" + AppInfoApi.getProxyId();
        temp += "&request_str=" + requestStr;
        temp += "&request_time=" + requestTime;
        temp += "&sign_key=" + Constants.SIGN_KEY;
        stringParams.put("request_str", requestStr);
        stringParams.put("request_time", requestTime);
        stringParams.put("sign", Common.md5(temp).toUpperCase());
    }


    /**
     * 生成随机字符串
     */
    public static String getRandomString(int length) {
        String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }


    /**
     * 执行错误
     */
    public boolean executeError(String message, String field, int code) {
        switch (code) {
            // 跳转到登录界面
            case CODE_LOGIN:
                // 弹出消息
                ToastUitil.showLong(context, message);

                // 退出登录
                LoginApi.outLogin();

                // 跳转界面
                activity.goTopActivity(LoginActivity.class);
                return true;

            // 跳转到购买解锁码界面
            case CODE_NEED_BUY_UNLOCK:
                // 弹出消息
                ModelDialog.alert(context, message, new ModelDialog.ModelDialogCallBack() {

                    @Override
                    protected boolean onCallback(DialogInterface dialog, int which) {
                        // 退出登录
                        LoginApi.outLogin();

                        // 跳转界面
                        activity.startActivity(BuyUnlockActivity.class);

                        return true;
                    }
                });

                return true;


            // 跳转到购买充值卡界面
            case CODE_NEED_BUY_GROUP:

                ModelDialog.alert(context, message, new ModelDialog.ModelDialogCallBack() {

                    @Override
                    protected boolean onCallback(DialogInterface dialog, int which) {
                        // 跳转界面
                        activity.startActivity(BuyRechargeActivity.class);

                        return true;
                    }
                });

                return true;
        }

        return false;
    }


    /**
     * 设置代理ID
     */
    protected void setProxyId(String proxyId) {
        stringParams.put("proxy_id", proxyId);
    }


    /**
     * 设置会员登录密钥
     */
    protected void setUserToken(String userToken) {
        stringParams.put("user_token", userToken);
    }


    /**
     * 设置APP编号
     */
    protected void setAppId(String appId) {
        stringParams.put("app_id", appId);
    }


    /**
     * 设置加载等待提示文字
     */
    public void setPendingMessage(String pendingMessage) {
        this.pendingMessage = pendingMessage;
    }


    /**
     * 设置是否显示加载提示框，默认显示
     */
    public void setPendingStatus(boolean pendingStatus) {
        this.pendingStatus = pendingStatus;
    }


    /**
     * 设置是否解析错误
     */
    public void setParseError(boolean parseError) {
        this.parseError = parseError;
    }


    /**
     * 设置是否POST提交JSON数据
     */
    protected void setIsPostJson(boolean isPostJson) {
        this.isPostJson = isPostJson;
        if (isPostJson) {
            this.isPost = false;
        }
    }


    /**
     * 设置是否POST提交方式
     */
    protected void setIsPost(boolean isPost) {
        this.isPost = isPost;
        this.isPostJson = false;
    }


    /**
     * 设置请求路径
     */
    protected void setApiPath(String apiPath) {
        this.apiPath = apiPath;
    }


    /**
     * 执行请求
     *
     * @param callback 回调
     */
    public void request(final RestCallback callback) {

        if (apiPath.equals("")) {
            ToastUitil.showShort("请先通过 setApiPath() 方法设置请求路径");
            return;
        }

        this.restCallback = callback;

        // 显示提示
        if (pendingStatus) {
            pending = new Pending(context);
            pending.setCanceledOnTouchOutside(false);
            if (activity != null && !activity.isFinishing()) {
                pending.show();
                pending.setMessage(pendingMessage);
            }
        }

        isPending = true;
        OkRequest res = new OkRequest(context);

        // 设置UA
        res.addHeader("User-Agent", AppInfoApi.getUserAgent());

        // JSON提交
        if (isPostJson) {
            JSONObject jsonObject = new JSONObject();
            for (Object o : stringParams.entrySet()) {
                Map.Entry entry = (Map.Entry) o;
                String key = (String) entry.getKey();
                String val = (String) entry.getValue();
                try {
                    jsonObject.put(Common.string(key), Common.string(val));
                } catch (JSONException ignored) {
                }
            }
            res.postString(apiPath, jsonObject.toString(), okRequestCallback);
        } else {
            for (Object o : stringParams.entrySet()) {
                Map.Entry entry = (Map.Entry) o;
                String key = (String) entry.getKey();
                String val = (String) entry.getValue();
                res.addParam(Common.string(key), Common.string(val));
            }

            // 封装file
            if (isPost) {
                for (Object o : fileParams.entrySet()) {
                    Map.Entry entry = (Map.Entry) o;
                    String key = (String) entry.getKey();
                    File val = (File) entry.getValue();
                    res.addFile(Common.string(key), val);
                }
            }

            if (isPost) {
                //    res.post(BaseApplication.getApiRoot() + apiPath, okRequestCallback);
                res.post(BaseApplication.getApiRoot() + apiPath, okRequestCallback);
            } else {
                //    res.get(BaseApplication.getApiRoot() + apiPath, okRequestCallback);
                res.get(BaseApplication.getApiRoot() + apiPath, okRequestCallback);
            }
        }
    }


    public boolean isPending() {
        return isPending;
    }


    /**
     * 销毁
     */
    public void destroy() {
        if (pending != null) {
            pending.dismiss();
            pending = null;
        }
    }


    /**
     * 快速设置数据
     */
    public static void setData(String prefix, String name, String value) {
        SPUtil.putString(prefix + name, value);
    }


    /**
     * 快速设置数据
     */
    public static void setData(String prefix, String name, boolean value) {
        SPUtil.putboolean(prefix + name, value);
    }


    /**
     * 快速获取数据
     */
    public static String getData(String prefix, String name) {
        return Common.string(SPUtil.getString(prefix + name));
    }


    /**
     * 快速获取数据
     */
    public static boolean getBoolean(String prefix, String name) {
        return SPUtil.getboolean(prefix + name);
    }
}

