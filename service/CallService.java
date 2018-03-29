package cn.yunhu.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.yunhu.api.AppInfoApi;
import cn.yunhu.receiver.PhoneStateReceiver;
import cn.yunhu.utils.BPermission;
import cn.yunhu.utils.Common;
import cn.yunhu.utils.LogToFile;
import cn.yunhu.utils.PhoneUtil;

/**
 * 呼叫服务
 */
public class CallService extends Service {

    /**
     * 呼叫列队
     */
    private static List<String> callQueueList = new ArrayList<>();
    /**
     * 当列队剩余多少数量的时候请求拉取新数据
     */
    private static int queueSurplus = 0;
    /**
     * 当前呼叫的手机号
     */
    private static String currentPhone = "";
    private static String currentPrefix = "";
    /**
     * 任务配置
     */
    private AppInfoApi.TakeConfig takeConfig = null;
    /**
     * 首次延时器
     */
    private Timer firstTimer = null;
    /**
     * 守护定时器
     */
    private Timer guardTimer = null;
    /**
     * 开始呼叫的时长
     */
    private int callDuration = 0;
    /**
     * 开始挂断的时长
     */
    private int stopDuration = 0;
    /**
     * 是否处于挂断等待中
     */
    private boolean isStopWait = false;
    /**
     * 是否等待新列队
     */
    private boolean isWaitNewQueue = false;
    /**
     * 是否启动呼叫
     */
    private boolean isStart = false;
    /**
     * 是否开始了呼叫
     */
    private boolean isCallPending = false;
    /**
     * 是否进入正常不拨号等待
     */
    private boolean isNoCallPending = false;
    /**
     * 连续N个列队呼叫错误
     */
    private int callContinuityError = 0;
    /**
     * 本次是否检测到了错误
     */
    private boolean isContinuityError = false;
    /**
     * 连续N次检测通讯记录失败
     */
    private int checkRecordError = 0;
    /**
     * 开始呼叫的时间
     */
    private long callStartTime = 0;


    //+--------------------------------------
    //| 呼叫状态
    //+--------------------------------------
    /**
     * 呼叫下一个列队
     */
    final private static int CALL_STATUS_STOP = 2;
    /**
     * 开始呼叫
     */
    final private static int CALL_STATUS_START = 1;


    //+--------------------------------------
    //| 通知状态类型
    //+--------------------------------------
    /**
     * 没有呼叫权限
     */
    final public static int TYPE_NO_CALL_PERMISSION = 1;
    /**
     * 需要新的手机号列队
     */
    final public static int TYPE_NEED_QUEUE = 2;
    /**
     * 开始呼叫
     */
    final public static int TYPE_SINGLE_START_CALL = 3;
    /**
     * 单个列队呼叫结束
     */
    final public static int TYPE_SINGLE_END_CALL = 4;
    /**
     * 无列队
     */
    final public static int TYPE_EMPTY_QUEUE = 5;


    /**
     * 呼叫Handler
     */
    @SuppressLint("HandlerLeak")
    private Handler callHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 开始呼叫
                case CALL_STATUS_START:
                    startCall();
                    break;


                // 挂断电话
                case CALL_STATUS_STOP:
                    stopCall();
                    break;
            }
            super.handleMessage(msg);
        }
    };


    @SuppressWarnings("deprecation")
    @Override
    public void onCreate() {
        super.onCreate();

        // 获取任务配置
        takeConfig = AppInfoApi.getTakeConfig();

        // LOG
        log("服务被创建，剩余数量为" + queueSurplus + "条才触发拉取政策");


        // 设置保持唤醒
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        if (pm != null) {
            WakeLock wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "CallService");
            wakeLock.acquire();
        }


        // 首次启动延迟一秒
        firstTimer = new Timer();
        firstTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                firstTimer.cancel();

                isStart = true;

                callHandler.sendEmptyMessage(CALL_STATUS_START);
            }
        }, 1000);


        // 定时守护
        guardTimer = new Timer();
        guardTimer.schedule(new TimerTask() {

            @Override
            public void run() {

                // 无号码等待中
                if (isNoCallPending) {
                    callDuration++;
                    log("无号码, 等待中, " + currentPrefix + ", " + callDuration + "s");
                }


                // 有号码呼叫中  isCallPending
                if (isCallPending) {

                    // 连续呼叫N个号码都错误就结束流程
                    int maxError = takeConfig.getCallContinuityQueueError();
                    if (maxError > 0 && callContinuityError >= maxError) {
                        sendNotice(TYPE_NO_CALL_PERMISSION, "呼叫被中断[L]");
                        guardTimer.cancel();
                        log("检测错误, 连续 " + callContinuityError + " 个呼叫错误，呼叫退出");
                        return;
                    }


                    // 当呼叫时间剩余一定值的时候进行检测是否在呼叫中
                    // 且本次没有检测到错误 isContinuityError
                    if (callDuration == takeConfig.getCallContinueInterval() - takeConfig.getCallContinueLastSecond() && !isContinuityError) {
                        if (!PhoneStateReceiver.isCallPending()) {
                            callContinuityError++;
                            isContinuityError = true;
                            log("检测错误, " + callContinuityError + " 次 @@@@@@@@@@@@@@@@@@@@@");
                        } else {
                            callContinuityError = 0;
                        }
                    }

                    callDuration++;
                    log("呼叫中, " + currentPhone + ", " + callDuration + "s");
                }


                // 一定秒数后挂断
                // 清理呼叫时长
                // 设为等待挂断中为true
                if (callDuration >= takeConfig.getCallContinueInterval()) {
                    log("呼叫到时，开始挂断");
                    callHandler.sendEmptyMessage(CALL_STATUS_STOP);
                    callDuration = 0;
                    isCallPending = false;
                    isNoCallPending = false;
                    isContinuityError = false;
                    isStopWait = true;
                }


                // 如果是挂断等待中则计时挂断时长
                if (isStopWait) {
                    stopDuration++;
                    log("挂断中, " + stopDuration + "s");
                }


                // 一定秒数后开始下一次呼叫
                // 清理挂断时长
                if (stopDuration >= takeConfig.getHangUpWaitInterval() + 1) {


                    // 检测通话记录
//                    try {
//                        PhoneUtil.Record record = PhoneUtil.getFirstRecord(getApplicationContext());
//
//                        // 最后的记录号码不是本次的呼叫号码
//                        if (!record.getNumber().equals(currentPhone)) {
//                            throw new Exception("通话记录的号码和本次呼叫的号码不同");
//                        }
//
//                        // 最后的开始呼叫时间不是本次开始的时间
//                        if (record.getDate() < callStartTime) {
//                            throw new Exception("记录的时间小余本次开始呼叫的时间");
//                        }
//                        checkRecordError = 0;
//                    } catch (Exception e) {
//                        log(e.getMessage());
//                        checkRecordError++;
//                    }
//
//
//                    // 连续呼叫N个号码都检测失败就结束
//                    int maxError = takeConfig.getCallContinuityQueueError();
//                    if (maxError > 0 && checkRecordError >= maxError) {
//                        sendNotice(TYPE_NO_CALL_PERMISSION, "呼叫被中断[CM]");
//                        log("检测错误, 连续 " + checkRecordError + " 个检测错误，呼叫退出");
//                        return;
//                    }

                    isStopWait = false;
                    stopDuration = 0;
                    callHandler.sendEmptyMessage(CALL_STATUS_START);
                    log("本次呼叫结束");
                    log("");
                }


                // 任务启动才走这里
                if (isStart) {

                    // 列队为空则清理列队并发送列队为空消息，并开始等待新的列队进来
                    if (callQueueList.size() == 0 && !isWaitNewQueue) {
                        Log.e("tag", "@@@@@@@@@@@@@@@@@@@@@@@@@@**********************");
                        isWaitNewQueue = true;
                        callQueueList.clear();
                        log("列队数量剩余0条，触发请求列队");
                        sendNotice(TYPE_EMPTY_QUEUE);
                    }

                    // 当列队剩余N条的时候请求添加新的列队
                    if (queueSurplus > 0 && callQueueList.size() <= queueSurplus) {
                        log("列队数量剩余" + callQueueList.size() + "条，触发请求列队政策");
                        sendNotice(TYPE_NEED_QUEUE);
                    }


                    // 列队不为空的前提下，
                    // 也没有在呼叫中的状态，
                    // 且正在等待新的列队进来，
                    // 则重新开始呼叫
                    if (callQueueList.size() > 0 && isWaitNewQueue && !isCallPending) {
                        isWaitNewQueue = false;
                        callHandler.sendEmptyMessage(CALL_STATUS_START);
                    }
                }
            }
        }, 0, 1000);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        // 清理列队
        callQueueList.clear();

        if (firstTimer != null) {
            firstTimer.cancel();
        }

        if (guardTimer != null) {
            guardTimer.cancel();
        }

        // 挂断电话
        PhoneUtil.endCall(this);

        log("服务被销毁");
    }


    /**
     * 开始拨打电话
     */
    private void startCall() {
        isNoCallPending = false;
        isCallPending = false;


        // 获取一个号码
        currentPhone = getCallQueue();
        if (currentPhone == null) {
            log("当前无列队");
            return;
        }


        // 校验权限
        if (!BPermission.checkHasPermission(this, Manifest.permission.CALL_PHONE)) {
            // 发送无权限通知
            log("无呼叫权限");
            sendNotice(TYPE_NO_CALL_PERMISSION, "没有呼叫权限[P]");
            return;
        }


        // 号码解析
        currentPrefix = "";
        if (currentPhone.contains(":")) {
            String[] phoneArr = currentPhone.split(":");
            currentPrefix = parseId(phoneArr[0].replace("#", ""));
            currentPhone = "";

            if (phoneArr.length >= 2) {
                currentPhone = Common.string(phoneArr[1]).trim();
            }

            // 没有手机号就等待不呼叫
            if (currentPhone.equals("")) {
                log("当前列队项无号码, " + currentPrefix + ", 进入等待中...");
                isNoCallPending = true;
                return;
            }
        }


        // 发送单个电话呼叫开始通知
        log("剩余 " + callQueueList.size() + " 个列队, 本次呼叫: " + currentPhone);
        sendNotice(TYPE_SINGLE_START_CALL, (System.currentTimeMillis() / 1000) + currentPrefix + currentPhone);


        try {
            isCallPending = true;
            callStartTime = System.currentTimeMillis();
            log("呼叫服务, 跳转打电话界面");
            PhoneUtil.call(this, currentPhone);
        } catch (Exception e) {
            log("呼叫服务" + e.getMessage() + "[E]");
            sendNotice(TYPE_NO_CALL_PERMISSION, e.getMessage());
        }
    }


    /**
     * 挂断现有的拨打
     */
    public void stopCall() {
        PhoneUtil.endCall(this);

        // 移除列队
        removeCallQueue(currentPrefix + currentPhone);

        // 发送单个结束消息
        sendNotice(TYPE_SINGLE_END_CALL, (System.currentTimeMillis() / 1000) + currentPrefix + currentPhone);

        log("本次呼叫结束: " + currentPrefix + currentPhone);
        log(".");
        log(".");
        log("-----------------------------------------");
        log(".");
        log(".");
    }


    /**
     * 发送通知给主进程
     */
    public void sendNotice(int type, String value) {
        Intent intent = new Intent(CallService.class.getName());
        intent.putExtra("call_server_type", type);
        intent.putExtra("call_server_value", Common.string(value));
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }


    /**
     * 发送通知给主进程
     */
    public void sendNotice(int type) {
        sendNotice(type, null);
    }


    /**
     * 将手机号添加到列队
     */
    public static void addCallQueue(String phone) {
        phone = Common.string(phone);
        if (phone.equals("")) {
            return;
        }

        log("添加: " + phone);
        callQueueList.add(phone);
    }


    /**
     * 添加手机号
     *
     * @param id    ID
     * @param phone 手机号
     */
    public static void addCallQueue(String id, String phone) {
        id = Common.string(id);
        phone = Common.string(phone);
        if (id.equals("") || phone.equals("")) {
            return;
        }

        addCallQueue(parseId(id) + phone);
    }


    /**
     * 将手机号从列队中删除
     */
    public static void removeCallQueue(String phone) {
        phone = Common.string(phone);
        if (phone.equals("")) {
            return;
        }

        for (int i = 0; i < callQueueList.size(); i++) {
            if (callQueueList.get(i).equals(phone)) {
                callQueueList.remove(i);
                log("删除: " + phone);
            }
        }
    }


    /**
     * 将手机号从列队中删除，并替换
     *
     * @param id           ID
     * @param replacePhone 要替换的手机号
     */
    public static void removeCallQueue(String id, String replacePhone) {
        replacePhone = Common.string(replacePhone);
        id = Common.string(id);
        if (id.equals("")) {
            return;
        }

        id = parseId(id);
        for (int i = 0; i < callQueueList.size(); i++) {
            String item = callQueueList.get(i);
            if (item.contains(id)) {
                callQueueList.set(i, id + replacePhone);

                log("替换: " + item + " 为: " + callQueueList.get(i));
            }
        }
    }


    /**
     * 获取一个手机号
     */
    public static String getCallQueue() {
        if (callQueueList.size() > 0) {
            return callQueueList.get(0);
        }

        return null;
    }


    /**
     * 设置剩余多少触发拉取新数据
     */
    public static void setQueueSurplus(int queueSurplus) {
        CallService.queueSurplus = queueSurplus;
    }


    /**
     * 启动服务
     */
    public static Intent startService(Context context) {
        Intent intent = new Intent(context, CallService.class);
        context.startService(intent);

        return intent;
    }


    /**
     * 停止服务
     */
    public static void stopService(Context context) {
        final Intent intent = new Intent(context, CallService.class);
        context.stopService(intent);
    }


    /**
     * 解析ID
     */
    private static String parseId(String id) {
        return "#" + id + ":";
    }


    private static void log(String string) {
        Log.e("呼叫服务", string);
        LogToFile.e("呼叫服务", string);
    }

    /**
     * 获取呼叫队列的长度
     */

    public static int getCallNumberLen(){
        return callQueueList.size();
    }
}
