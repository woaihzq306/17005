package cn.yunhu.task;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.io.DataOutputStream;
import java.io.File;
import java.lang.reflect.Method;
import cn.yunhu.api.AppInfoApi;
import cn.yunhu.dialog.ModelDialog;
import cn.yunhu.phonenetwork.NetEvent;
import cn.yunhu.phonenetwork.NetUtil;


/**
 * 呼叫流量测试任务
 */
public class GPRSTaskTest implements NetEvent {

    private static Context context;
    /**
     * 回调
     */
    private GPRSTaskTestCallback callback;

    static WifiManager mWm;
    boolean isWifiConnected;
    boolean isGprsConnected;
    ConnectivityManager cm;

    private final static String COMMAND_L_ON = "svc data enable\n";
    private final static String COMMAND_L_OFF = "svc data disable\n";
    private final static String COMMAND_SU = "su";

    /**
     * 网络状态
     */
    private int netMobile;
    /**
     * 监控网络的广播
     */
    private static NetBroadcastReceiver netBroadcastReceiver;
    /**
     * dialog
     */
    private static ModelDialog dialog1 = null;

    String message;

    private static boolean iswifiable = false;

    private static boolean issendmessage = true;

    private static boolean isOpwifi = false;

    /**
     * 任务配置主要获取验证失败的提示信息
     */
    private AppInfoApi.TakeConfig takeConfig;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Log.e("tag", "++++++++++++++++++++++++++++++++++++++++++");
            //        isOpenGPRS();
                    break;
                case 1:
                    if (isMobileConnected(context)) {
                        //流量可用 验证成功  打开WiFi 关闭数据流量
                        Log.e("tag", "流量可用 验证成功-------------------------");
                        successfull();
                    } else {
                        // 流量不可用 验证失败
                        //关闭数据流量打开WiFi
                    //    closeGPRS();
//                        if (!mWm.isWifiEnabled()) {
//                            mWm.setWifiEnabled(true);
//                            Log.e("tag", "*****************WiFi再次打开成功********************************");
//                        }
                        callback.onError(message);
                    }
                    break;
                case 2:
//                    issendmessage = true;
//                    isGPRS = false;
                    callback.onSuccess();
                    break;
                case 3:
//                    Log.e("tag", "isMobileConnected(context)*************: " + isMobileConnected(context));
//                    if (isMobileConnected(context)) {
//                        handler.removeMessages(3);
//                    } else {
//                        Log.e("tag", "99999999999999999999**********************: " + dialog1);
//                        if (dialog1 != null) {
//                            dialog1.dismiss();
//                            dialog1 = null;
//                        }
//                        showDiagle(context);
//                    }
                    break;

                case 4:
//                    if (mWm.isWifiEnabled()) {
//                        showWiFiDiagle(context);
//                    } else {
//                        handler.removeMessages(4);
//                    }
                    break;
            }
        }
    };


    public GPRSTaskTest(Context con, GPRSTaskTestCallback takeTaskTestCallback) {
        this.context = con;
        this.callback = takeTaskTestCallback;
        this.takeConfig = AppInfoApi.getTakeConfig();

        message = takeConfig.getGPRSFailTip();

        if (mWm == null) {
            mWm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        }
        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public void onNetChange(int netMobile) {
        this.netMobile = netMobile;
        isNetConnect();
    }

    /**
     * 监听WiFi的开关状态
     */
    @Override
    public void onWifiChange(int wifistate) {
        switch (wifistate) {
            case WifiManager.WIFI_STATE_ENABLED:
                //已打开
                Log.e("tag", "wifi已打开**********************");
                break;
            case WifiManager.WIFI_STATE_ENABLING:
                //打开中
                Log.e("tag", "wifi打开中**********************");
                break;
            case WifiManager.WIFI_STATE_DISABLED:
                //已关闭

//                handler.removeMessages(4);
//                Log.e("tag", "wifi已关闭**********************: " + dialog1);
//                if (dialog1 != null) {
//                    dialog1.dismiss();
//                    dialog1 = null;
//                }

                Log.e("tag", "wifi已关闭**********************: " + iswifiable);
//                if (iswifiable) {
//                    Message message = new Message();
//                    message.what = 0;
//                    handler.sendMessageDelayed(message, 1000);
//                    iswifiable = false;
//
//                }
                break;
            case WifiManager.WIFI_STATE_DISABLING:
                //关闭中
                Log.e("tag", "wifi关闭中**********************");
                break;
            case WifiManager.WIFI_STATE_UNKNOWN:
                //未知
                Log.e("tag", "wifi未知**********************");
                break;
        }
    }

    /**
     * 监听手机网络的状态
     */
    private void isNetConnect() {

        switch (netMobile) {
            case 1://wifi
                Log.e("tag", "当前网络类型:wifi");
            //    handler.removeMessages(3);
                break;
            case 0://移动数据
                Log.e("tag", "当前网络类型:移动数据");
//                handler.removeMessages(4);
//                Log.e("tag", "当前网络类型:移动数据**********************: " + dialog1);
//                if (dialog1 != null) {
//                    dialog1.dismiss();
//                    dialog1 = null;
//                }
//
//                if (issendmessage && isGPRS) {
//                    issendmessage = false;
//                    Message msg = new Message();
//                    msg.what = 1;
//                    handler.sendMessageDelayed(msg, 3000);
//                }
                break;
            case -1://没有网络
                Log.e("tag", "当前无网络");
//                handler.removeMessages(4);
//                Log.e("tag", "当前无网络**********************: " + dialog1);
//                if (dialog1 != null) {
//                    dialog1.dismiss();
//                    dialog1 = null;
//                }
                break;
        }
    }

    /**
     * 开始测试
     */
    public void test() {

        callback.onLoad();

        //注册广播
        if (netBroadcastReceiver == null) {
            netBroadcastReceiver = new NetBroadcastReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            context.registerReceiver(netBroadcastReceiver, filter);
            /**
             * 设置监听
             */
            netBroadcastReceiver.setNetEvent(this);
        }

        /**
         * 1,判断WiFi是否开启
         * 2,如果关闭则验证成功，如果开启则先关闭WiFi再判断是否开启GPRS
         * 3,判断手机系统是否为5.0以上，不是这直接开启数据流量，
         *      如果是5.0以上则判断是否有root权限，如果有root权限则直接打开数据流量，
         *      没有root则提示用户手动打开数据流量
         * 4,判断GPRS是否可用
         * 5,判断出数据流量是否可用以后，关闭数据流量，打开WiFi。
         */
        if (isWifiConnected()) {
            Log.e("tag", "wifi开启状态*************************");
            iswifiable = true;

            mWm.setWifiEnabled(false);
            isOpwifi = true;
            handler.sendEmptyMessageDelayed(1, 5000);

        } else {
            isOpwifi = false;
            handler.sendEmptyMessageDelayed(1, 0);
        //    callback.onSuccess();
        //    isOpenGPRS();
        }
    }

    /**
     * 判断数据流量是否打开，没有则打开
     */
//    private void isOpenGPRS() {
//        isGPRS = true;
//        Log.e("tag", "88888888888888888888888888888888888888888888888888");
//        //判断流量是否开启
//        if (!isGprsConnected()) {
//            //开启流量
//            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                //判断是否root
//                if (isRoot()) {
//                    Log.e("tag", "有root权限*************************************");
//                    setGprsEnabled(true, context);
//                    isGPRSopen();
//                } else {
//
//                    showDiagle(context);
//                }
//            } else {
//                ConnectivityManager mCM = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//                setGprsEnabled(mCM, true);
//                isGPRSopen();
//            }
//        } else {
//            isGPRSopen();
//        }
//    }

    //开启/关闭GPRS
    private static void setGprsEnabled(ConnectivityManager mCM, boolean isEnable) {
        Class cmClass = mCM.getClass();
        Class[] argClasses = new Class[1];
        argClasses[0] = boolean.class;

        try {
            Method method = cmClass.getMethod("setMobileDataEnabled", argClasses);
            method.invoke(mCM, isEnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断MOBILE网络是否可用
     */
    public boolean isMobileConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isConnectedOrConnecting();
            }
        }
        return false;
    }

    /**
     * 判断手机是否ROOT
     */
    public static boolean isRoot() {

        boolean root = false;

        try {
            if ((!new File("/system/bin/su").exists())
                    && (!new File("/system/xbin/su").exists())) {
                root = false;
            } else {
                root = true;
            }

        } catch (Exception e) {
        }

        return root;
    }

    /**
     * 5.0以上有root权限的手机开启数据流量
     */
    public static void setGprsEnabled(boolean enable, Context context) {

        String command;
        if (enable) {
            command = COMMAND_L_ON;
        } else {
            command = COMMAND_L_OFF;
        }

        try {
            Process su = Runtime.getRuntime().exec(COMMAND_SU);
            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());

            outputStream.writeBytes(command);
            outputStream.flush();

            outputStream.writeBytes("exit\n");
            outputStream.flush();
            try {
                su.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }

            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断数据流量是否可用
     * 并做出相应处理
     */
    private void isGPRSopen() {
        //开启成功
        //判断流量是否可用

        if (isMobileConnected(context)) {
            //流量可用 验证成功  打开WiFi  关闭数据流量
            if (issendmessage) {
                issendmessage = false;
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessageDelayed(msg, 3000);
            }

        } else {
            // 流量不可以 验证失败
            callback.onError(message);
        }
    }

    public abstract static class GPRSTaskTestCallback {

        protected abstract void onError(String message);

        protected abstract void onSuccess();

        protected abstract void onLoad();

        protected abstract void onCancel();

    }

    public class NetBroadcastReceiver extends BroadcastReceiver {

        private NetEvent netEvent;

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                //检查网络状态的类型
                int netWrokState = NetUtil.getNetWorkState(context);
                if (netEvent != null)
                    // 接口回传网络状态的类型
                    netEvent.onNetChange(netWrokState);
            }
            if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                if (netEvent != null) {
                    netEvent.onWifiChange(wifistate);
                }
            }
        }

        public void setNetEvent(NetEvent netEvent) {
            this.netEvent = netEvent;
        }
    }

    public static void unRegisterReceiver() {
        if (netBroadcastReceiver != null) {
            context.unregisterReceiver(netBroadcastReceiver);
        }
    }


//    private void closeGPRS() {
//        //关闭流量
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            //判断是否root
//            if (isRoot()) {
//                setGprsEnabled(false, context);
//            }
//        } else {
//            ConnectivityManager mCM = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//            setGprsEnabled(mCM, false);
//        }
//    }

    /**
     * 测试成功后的操作
     */
    private void successfull() {
    //    closeGPRS();
        if (!mWm.isWifiEnabled() && isOpwifi) {
            mWm.setWifiEnabled(true);
            isOpwifi = false;
            Log.e("tag", "*****************WiFi再次打开成功********************************");
        }

        Message message = new Message();
        message.what = 2;
        handler.sendMessageDelayed(message, 1000);
    }

    private boolean isWifiConnected() {
        return isWifiConnected = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED ? true : false;
    }

    private boolean isGprsConnected() {
        return isGprsConnected = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ? true : false;

    }

    private void showDiagle(Context context) {

        // 弹出一个拨打引导说明
        ModelDialog.confirm(context, "提示", "您的手机无法自动打开数据流量联网，请手动打开数据流量上网!", new ModelDialog.ModelDialogCallBack() {
            @Override
            protected boolean onCallback(DialogInterface dialog, int which) {

                dialog.dismiss();
                handler.sendEmptyMessageDelayed(3, 6000);

                return true;
            }
        }, new ModelDialog.ModelDialogCallBack() {
            @Override
            protected boolean onCallback(DialogInterface dialog, int which) {

                dialog.dismiss();
            //    closeGPRS();
                if (!mWm.isWifiEnabled()) {
                    mWm.setWifiEnabled(true);
                    Log.e("tag", "*****************WiFi再次打开成功********************************");
                }
                callback.onCancel();
                return true;
            }
        }, "我已打开", "取消");
    }


    private void showWiFiDiagle(Context context) {

        // 弹出一个拨打引导说明
        Log.e("tag", "dialog1**********************: " + dialog1);
        if (dialog1 == null) {
            dialog1 = ModelDialog.confirm(context, "提示", "您的手机无法自动关闭WIFi，请手动关闭WiFi!", new ModelDialog.ModelDialogCallBack() {
                @Override
                protected boolean onCallback(DialogInterface dialog, int which) {

                    dialog.dismiss();
                    handler.sendEmptyMessageDelayed(4, 5000);

                    return true;
                }
            }, new ModelDialog.ModelDialogCallBack() {
                @Override
                protected boolean onCallback(DialogInterface dialog, int which) {

                    dialog.dismiss();
                    callback.onCancel();
                    return true;
                }
            }, "我已关闭", "取消");
        } else {
            Log.e("tag", "dialog1****111111111111111111***: " + dialog1);
            dialog1.show();
        }
    }
}
