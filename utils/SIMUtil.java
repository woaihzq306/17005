package cn.yunhu.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import java.util.InputMismatchException;

/**
 * SIM卡工具库
 */
public class SIMUtil {

    /**
     * 获取手机卡IMSI码
     */
    @SuppressLint("HardwareIds")
    public static String getImsi(Context context) throws Exception {
        TelephonyManager telephonyManager = null;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            throw new Exception("无权限获取SIM信息");
        }

        if (context != null){
            telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        }
        return telephonyManager.getSubscriberId();
    }


    /**
     * 获取手机卡运营商
     */
    public static String getIsp(Context context) throws Exception {
        return "中国移动";
        // TODO: 2017/11/2  
        /*String imsi    = getImsi(context);
        String ispName = "";
        if (imsi.startsWith("46000") || imsi.startsWith("46002") || imsi.startsWith("46007") || imsi.startsWith("46008")) {
            ispName = "中国移动";
        } else if (imsi.startsWith("46001") || imsi.startsWith("46006") || imsi.startsWith("46009") || imsi.startsWith("46010")) {
            ispName = "中国联通";
        } else if (imsi.startsWith("46003") || imsi.startsWith("46005") || imsi.startsWith("46011")) {
            ispName = "中国电信";
        } else {
            throw new Exception("无法获取SIM运营商");
        }

        return ispName;*/
    }


    /**
     * 获取SIM状态
     */
    public static boolean getState(Context context) throws Exception {
        if (context != null){
            TelephonyManager telMgr   = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            int              simState = telMgr.getSimState();
            switch (simState) {
                case TelephonyManager.SIM_STATE_ABSENT:
                    throw new Exception("手机未插卡");
                case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                    throw new Exception("手机被锁，需要网络的PIN码解锁");
                case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                    throw new Exception("手机被锁，需要用户的PIN码解锁");
                case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                    throw new Exception("手机被锁，需要用户的PUK码解锁");
                case TelephonyManager.SIM_STATE_UNKNOWN:
                    throw new Exception("手机无法拨打电话");
                case TelephonyManager.SIM_STATE_READY:
                    return true;
            }
        }
        throw new Exception("手机无法拨打电话");
    }
}
