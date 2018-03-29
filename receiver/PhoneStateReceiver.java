package cn.yunhu.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;


import cn.yunhu.BaseApplication;
import cn.yunhu.utils.LogToFile;
import cn.yunhu.utils.PhoneUtil;

/**
 * 广播接收器用于获取拨打的电话号码
 */
public class PhoneStateReceiver extends BroadcastReceiver {

    /** 来电号码 */
    private static String           incomingPhone    = "";
    /** 去电号码 */
    private static String           callPhone        = "";
    /** 是否拨打电话中 */
    private static boolean          isCallPending    = false;
    /** 是否来电 */
    private static boolean          isIncoming       = false;
    /** 监听 */
    private static TelephonyManager telephonyManager = null;
    private        Context          context          = null;

    int i;


    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        // 呼出电话的广播
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            callPhone = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            isCallPending = true;
            log("拨打: " + callPhone);
        }

        // 通话状态改变的广播
        else if (intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            // 来电号码
            incomingPhone = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

            // 改变的状态
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            // 闲置状态，也就是挂断
            if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
                isCallPending = false;
                isIncoming = false;
                log("挂断或闲置");
            }

            // 通话状态，对于呼出而言，开始就是这个状态了；对于接听者而言，接起电话就是这个状态了
            else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
                isCallPending = true;
                log("开始呼叫或接起来电话");
            }

            // 响铃状态，即来电
            else if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
                log("响铃, 来电号码: " + incomingPhone);
            //    endCall();
                isIncoming = true;
            }
        }


        // 监听
        if (telephonyManager == null && context != null) {
            telephonyManager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
            telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }


    PhoneStateListener listener = new PhoneStateListener() {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    isCallPending = false;
                    log("监听, 挂断或闲置");
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    isCallPending = true;
                    log("监听, 开始呼叫或接起来电话");

                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    log("监听, 响铃, 来电号码: " + incomingNumber);
                    break;
            }
        }
    };


    /**
     * 是否拨打电话中
     */
    public static boolean isCallPending() {
        return isCallPending;
    }


    /**
     * 获取正在拨打的号码
     */
    public static String getCallPhone() {
        return callPhone;
    }


    /**
     * 来电的号码
     */
    public static String getIncomingPhone() {
        return incomingPhone;
    }

    /**
     * 是否收到来电
     */
    public static boolean isIncoming() {
        return isIncoming;
    }


//    private void endCall() {
//        // 非试用用户，则强制挂断电话
////        if (!BaseApplication.isTrial()) {
////            PhoneUtil.endCall(context);
////        }
//        if (!BaseApplication.isTrial()) {
//            PhoneUtil.endCall(context);
//        }
//    }


    private void log(String log) {
        Log.e("电话", log);
        LogToFile.e("电话", log);
    }

}
