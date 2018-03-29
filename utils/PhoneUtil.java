package cn.yunhu.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhoneUtil {

    public static String TAG = PhoneUtil.class.getSimpleName();


    /**
     * 挂断电话
     */
    public static void endCall(Context context) {
        try {
            Object telephonyObject = getTelephonyObject(context);
            if (null != telephonyObject) {
                Class telephonyClass = telephonyObject.getClass();

                Method endCallMethod = telephonyClass.getMethod("endCall");
                endCallMethod.setAccessible(true);

                endCallMethod.invoke(telephonyObject);
            }
        } catch (SecurityException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }


    @SuppressLint("LongLogTag")
    private static Object getTelephonyObject(Context context) {
        Object telephonyObject = null;
        if (context != null){
            try {
                // 初始化iTelephony
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                Class            telManager       = telephonyManager.getClass();
                Method           getITelephony    = telManager.getDeclaredMethod("getITelephony");
                getITelephony.setAccessible(true);
                telephonyObject = getITelephony.invoke(telephonyManager);
            } catch (SecurityException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return telephonyObject;
    }


    /**
     * 拨打电话
     */
    public static void call(Context context, String phone) throws Exception {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            throw new Exception("无权限拨打电话");
        }


        // 飞行模式
        int isAirplaneMode;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            isAirplaneMode = Settings.System.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0);
        } else {
            isAirplaneMode = Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0);
        }
        if (isAirplaneMode > 0) {
            throw new Exception("飞行模式下无法进行呼叫");
        }


        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    /**
     * 获取第一条通讯记录
     */
    public static Record getFirstRecord(Context context) throws Exception {
        // 1.获得ContentResolver
        ContentResolver resolver = context.getContentResolver();

        // 权限检测
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            throw new Exception("无读取通讯录权限");
        }


        // 2.利用ContentResolver的query方法查询通话记录数据库
        Cursor cursor = resolver.query(
                CallLog.Calls.CONTENT_URI,
                new String[]{
                        // 通话记录的联系人
                        CallLog.Calls.CACHED_NAME,
                        // 通话记录的电话号码
                        CallLog.Calls.NUMBER,
                        // 通话记录的日期
                        CallLog.Calls.DATE,
                        // 通话时长
                        CallLog.Calls.DURATION,
                        // 通话类型
                        CallLog.Calls.TYPE}
                ,
                null,
                null,
                // 按照时间逆序排列，最近打的最先显示
                CallLog.Calls.DEFAULT_SORT_ORDER
        );


        if (cursor == null) {
            throw new Exception("没有通话记录");
        }


        // 3.通过Cursor获得数据
        Record record = null;
        while (cursor.moveToNext()) {
            record = new Record();
            record.setName(cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)));
            record.setDate(cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE)));
            record.setNumber(cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)));
            record.setDuration(cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION)));
            switch (cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE))) {
                case CallLog.Calls.OUTGOING_TYPE:
                    record.setType("打出");
                    return record;
            }
        }

        throw new Exception("没有获取到通话记录");
    }


    public static class Record {

        private long   date     = 0;
        private String name     = "";
        private String type     = "";
        private long   duration = 0;
        private String number   = "";


        public long getDate() {
            return date;
        }


        public void setDate(long date) {
            this.date = date;
        }


        public String getName() {
            return name;
        }


        public void setName(String name) {
            this.name = name;
        }


        public String getType() {
            return type;
        }


        public void setType(String type) {
            this.type = type;
        }


        public double getDuration() {
            return duration;
        }


        public void setDuration(long duration) {
            this.duration = duration;
        }


        public String getNumber() {
            return number;
        }


        public void setNumber(String number) {
            this.number = number;
        }


        @Override
        public String toString() {
            @SuppressLint("SimpleDateFormat")
            String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(this.date));

            return "tel: " + number + ", time: " + date + ", date: " + dateStr + ", duration: " + duration + ", type: " + type;
        }
    }
}
