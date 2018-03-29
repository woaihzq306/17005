package cn.yunhu.task;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.util.Log;

import com.yanzhenjie.permission.Rationale;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.yunhu.api.AppInfoApi;
import cn.yunhu.dialog.ModelDialog;
import cn.yunhu.receiver.PhoneStateReceiver;
import cn.yunhu.utils.BPermission;
import cn.yunhu.utils.LogToFile;
import cn.yunhu.utils.PhoneUtil;

/**
 * 呼叫测试任务
 */
public class TakeTaskTest implements BPermission.BPermissionInterface {

    final public static int ERROR_TYPE_NONE    = -1;
    final public static int ERROR_TYPE_TOAST   = 0;
    final public static int ERROR_TYPE_ALERT   = 1;
    final public static int ERROR_TYPE_CONFIRM = 2;


    private Context               context;
    /** 回调 */
    private TakeTaskTestCallback  callback;
    /** 任务配置 */
    private AppInfoApi.TakeConfig takeConfig;
    /** 测试定时器 */
    private        Timer       testTimer     = null;
    /** 当前测试的号码 */
    private        String      testTel       = null;
    /** 当前是否测试状态 */
    private        boolean     testStatus    = false;
    /** 当前累计拨打的秒数 */
    private        int         testNowSecond = 0;
    /** 验证的最大秒数 */
    private        int         testMaxSecond = 0;
    /** 验证的最小秒数 */
    private        int         testMinSecond = 0;
    /** dialog */
    private        ModelDialog dialog        = null;
    /** 开始调试模式 */
    private static boolean     DEBUG         = false;
    private        long        startTime     = 0;
   // private        Timer       timer3        = null;


    public TakeTaskTest(Context con, TakeTaskTestCallback takeTaskTestCallback) {
        this.context = con;
        this.takeConfig = AppInfoApi.getTakeConfig();
        this.callback = takeTaskTestCallback;
        this.testTel = takeConfig.getTestTel();
        this.testMaxSecond = takeConfig.getTestMaxSecond();
        this.testMinSecond = takeConfig.getTestMinSecond();


        // 定时测试状态
        testTimer = new Timer();
        testTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                if (!testStatus) {
                    return;
                }


                testNowSecond++;
                callback.onPending(testNowSecond);
                log((PhoneStateReceiver.isCallPending() ? "拨打电话中: " + PhoneStateReceiver.getCallPhone() : "未拨打电话!!!!!!") + ", " + testNowSecond + "s");

                // 开始拨打电话后如果一定时间还没有挂断则认为有效
                // 拨打电话中
                if (PhoneStateReceiver.isCallPending()) {
                    // 当前已拨打的秒大于等于测试秒则认为正常
                    if (testNowSecond >= testMaxSecond) {
                        testStatus = false;
                        testNowSecond = 0;

                        // 测试结束，挂断电话
                        PhoneUtil.endCall(context);

                        // 测试通话记录
                        checkRecord();
                    }
                } else {
                    // 大于7秒才判断，避免手机延迟
                    if (testNowSecond >= testMinSecond) {
                        String message = takeConfig.getTestFailTip();

                        // 小余7秒被挂断的可能认为是双卡机
                        if (testNowSecond <= testMinSecond + 1) {
                            log("可能是双卡机弹出了选择拨号界面!!!");
                            message = takeConfig.getTestEmptyAuthTip().replace("{@number}", testTel);
                        } else {
                            log("不到一定的时间被挂断!!!");
                        }

                        // 测试结束，挂断电话
                        PhoneUtil.endCall(context);

                        stop();
                        testStatus = false;
                        testNowSecond = 0;
                        callback.onError(message, ERROR_TYPE_ALERT);
                    }
                }
            }
        }, 0, 1000);
    }


    /**
     * 开始测试
     */
    public void test() {
        if (DEBUG) {
            log("测试状态, 测试成功，主动挂断电话");
            callback.onSuccess();
        } else {
            // 弹出一个拨打引导说明
            log("引导说明");
            dialog = ModelDialog.confirm(context, takeConfig.getTestLeadTip().replace("{@number}", testTel), new ModelDialog.ModelDialogCallBack() {

                @Override
                protected boolean onCallback(DialogInterface dialog, int which) {

                    // 检测自己是否有监听通话状态的权限
                    log("同意引导说明, 开始权限校验");
                    new BPermission(context).setCallback(TakeTaskTest.this).apply(1, Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE, Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.READ_CALL_LOG);

                    // 获取一次通讯记录为了提权
//                    try {
//                        PhoneUtil.getFirstRecord(context);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }

                    return true;
                }
            }, new ModelDialog.ModelDialogCallBack() {

                @Override
                protected boolean onCallback(DialogInterface dialog, int which) {
                    log("主动取消了呼叫");
                    callback.onError("您取消了呼叫", ERROR_TYPE_TOAST);
                    return true;
                }
            });
        }
    }


    /**
     * 停止测试
     */
    private void stop() {
        if (testTimer != null) {
            testTimer.cancel();
            testTimer = null;
        }
//        if (timer3 != null) {
//            timer3.cancel();
//            timer3 = null;
//        }

        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }


    private void checkRecord() {

        log("测试成功，主动挂断电话");
        callback.onSuccess();

//        // 延迟1秒后判断，否则可能获取不到通讯录的内容
//        timer3 = new Timer();
//        timer3.schedule(new TimerTask() {
//
//            @Override
//            public void run() {
//                if (timer3 != null) {
//                    timer3.cancel();
//                    timer3 = null;
//                }
//
//                log("检测到已挂断电话，准备开始测试通话记录");
//
//                // 获取最新的通话记录并对比是否本次呼叫的记录
//                try {
//                    // 通话记录的号码和本次呼叫的号码不一致
//                    PhoneUtil.Record record = PhoneUtil.getFirstRecord(context);
//                    log(record.toString());
//                    if (!record.getNumber().equals(testTel)) {
//                        throw new Exception("获取到的通话记录号码不是本次的呼叫号码");
//                    }
//
//                    // 接通分钟数为0
//                    if (record.getDuration() == 0) {
//                        throw new Exception("接通的时长为0，不算");
//                    }
//
//                    // 开始时间小余本次稽查开始时间
//                    if (record.getDate() < startTime) {
//                        throw new Exception("开始呼叫的时间小余本次呼叫的时间");
//                    }
//
//                    log("测试成功，主动挂断电话");
//                    callback.onSuccess();
//                    startTime = 0;
//                } catch (Exception e) {
//                    log("测试失败, " + e.getMessage());
//
//                    testStatus = false;
//                    testNowSecond = 0;
//                    startTime = 0;
//                    callback.onError(takeConfig.getTestEmptyAuthTip().replace("{@number}", testTel), ERROR_TYPE_ALERT);
//                }
//
//                // 停止测试
//                stop();
    //}
//        }, 1000);
    }


    /**
     * 权限申请成功回调开始正式测试流程
     */
    @Override
    public void onBPermissionSuccess(int requestCode, @NonNull List<String> list) {
        log("拥有权限: " + requestCode + "; " + list.toString());
        log("电话: " + testTel + ", 识别秒: " + testMaxSecond);

        try {
            // 开始拨打电话
            PhoneUtil.call(context, testTel);

            // 设为开始测试
            testStatus = true;

            startTime = System.currentTimeMillis();
        } catch (Exception e) {

            log("无法拨打电话");

            // 错误提示
            callback.onError(takeConfig.getTestEmptyAuthTip().replace("{@number}", testTel), ERROR_TYPE_ALERT);
        }
    }


    @Override
    public void onBPermissionError(int requestCode, @NonNull List<String> list) {
        log("无权限: " + requestCode + "; " + list.toString());

        callback.onError(takeConfig.getTestEmptyAuthTip().replace("{@number}", testTel), ERROR_TYPE_ALERT);
    }


    @Override
    public void onBPermissionItemSuccess(int requestCode, @NonNull String permission) {
        log("权限成功: " + requestCode + "; " + permission);
    }


    @Override
    public void onBPermissionItemError(int requestCode, @NonNull String permission) {
        log("权限失败: " + requestCode + "; " + permission);
    }


    @Override
    public void onBPermissionRationale(int requestCode, final Rationale rationale) {
        ModelDialog.confirm(context, "权限不足提示", "本软件目前没有 <b>监听通话状态</b> 和 <b>拨打电话</b> 的权限，点击本界面上的确认按钮后，软件会请求获取权限，请允许本软件获取权限，或者在本机的安全软件中将本软件加为信任！", new ModelDialog.ModelDialogCallBack() {

            @Override
            protected boolean onCallback(DialogInterface dialog, int which) {
                rationale.resume();

                return true;
            }
        }, new ModelDialog.ModelDialogCallBack() {

            @Override
            protected boolean onCallback(DialogInterface dialog, int which) {
                callback.onError("您取消了呼叫", ERROR_TYPE_TOAST);

                return true;
            }
        }, "确认", "取消");
    }


    private void log(String log) {
        Log.e("测试状态", log);
        LogToFile.e("测试状态", log);
    }


    public abstract static class TakeTaskTestCallback {

        protected abstract void onError(String message, int type);


        protected abstract void onSuccess();


        protected abstract void onPending(int s);
    }
}
