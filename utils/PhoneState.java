package cn.yunhu.utils;

import android.app.Service;
import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import java.util.InputMismatchException;

/**
 * 手机状态
 */
public class PhoneState {

    /**
     * 监听呼叫状态
     */
    public static void getPhoneStateListener(Context context, final CallCallback callback) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
        tm.listen(new PhoneStateListener() {

            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                //state 当前状态 incomingNumber,貌似没有去电的API
                super.onCallStateChanged(state, incomingNumber);

                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        callback.onHangUp();
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        callback.onTake();
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        callback.onBell(incomingNumber);
                        break;
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);
    }



    /**
     * 手机拨打电话回调
     */
    abstract static public class CallCallback {

        /**
         * 接听电话
         */
        public abstract void onTake();


        /**
         * 挂断电话
         */
        public abstract void onHangUp();


        /**
         * 响铃
         */
        public abstract void onBell(String phone);
    }
}
