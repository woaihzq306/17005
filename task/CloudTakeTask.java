package cn.yunhu.task;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.yunhu.api.BaseApi;
import cn.yunhu.api.CallTakeApi;
import cn.yunhu.fragment.HomeFragment;
import cn.yunhu.http.Rest;
import cn.yunhu.service.CallService;
import cn.yunhu.utils.LogToFile;

/**
 * 云端呼叫
 */
public class CloudTakeTask extends TakeTask {

    /**
     * 直接请求
     */
    final private static int CODE_REQUEST = 1;
    /**
     * 定时请求
     */
    final private static int CODE_TIMING_REQUEST = 2;

    /**
     * 回调
     */
    private CloudTakeTaskCallback callback = null;
    /**
     * 请求定时器
     */
    private static Timer requestTiming = null;
    /**
     * 请求器
     */
    private static CallTakeApi callTakeApi = null;
    /**
     * 锁定定时器
     */
    private Timer lockTimer = null;
    /**
     * 是否锁定
     */
    private boolean isLock = false;
    /**
     * 呼叫完毕的列表
     */
    private List<String> endList = new ArrayList<>();
    /**
     * 提交类型
     * 1, 需求 剩余 n 个
     * 2, 为空
     * 0, 正常 列队不为空
     */
    private int type = 2; //


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 直接请求新数据
                case CODE_REQUEST:

                    // 直接请求取消当前正在执行的定时操作
                    if (requestTiming != null) {
                        requestTiming.cancel();
                        requestTiming = null;
                    }

                    load();
                    break;

                // 定时请求
                case CODE_TIMING_REQUEST:

                    if (requestTiming != null) {
                        requestTiming.cancel();
                        requestTiming = null;
                    }

                    requestTiming = new Timer();
                    requestTiming.schedule(new TimerTask() {

                        @Override
                        public void run() {
                            Log.e("tag", "定时请求************");
                            handler.sendEmptyMessage(CODE_REQUEST);
                        }
                    }, takeConfig.getGetQueueInterval());

                    Log.e("sssssssssss", takeConfig.getGetQueueInterval() + "");
                    break;
            }
            super.handleMessage(msg);
        }
    };


    /**
     * 实例化
     */
    public CloudTakeTask(Context context, CloudTakeTaskCallback callback) {
        super(context);

        this.callback = callback;
    }


    @Override
    public void start() {
        super.start();

        // 实例化请求
        callTakeApi = new CallTakeApi(context);

        // 直接请求新数据
        handler.sendEmptyMessage(CODE_REQUEST);
    }


    @Override
    public void stop() {
        super.stop();

        // 释放请求
        callTakeApi = null;

        // 清理请求定时器
        if (requestTiming != null) {
            requestTiming.cancel();
            requestTiming = null;
        }
    }



    /**
     * 当列队剩余N条的时候触发，如果N为0则不触发
     */
    @Override
    protected void onNeedQueue() {
        super.onNeedQueue();

        log("列队补充, 开始请求新的列队");
        if (isLock) {
            return;
        }
        lockTimer();
        type = 1;
        handler.sendEmptyMessage(CODE_REQUEST);

    }

    /**
     * 列队为空触发
     */
    @Override
    protected void onEmptyQueue() {
        super.onEmptyQueue();

        log("列队为空, 开始请求新的列队");

        if (isLock) {
            return;
        }
        lockTimer();

        type = 2;
        handler.sendEmptyMessage(CODE_REQUEST);

    }


    /**
     * 呼叫过程中出错触发
     */
    @Override
    protected void onError(String message) {
        super.onError(message);

        callback.onCallError(message);
    }


    /**
     * 单个列队呼叫结束
     */
    @Override
    protected void onSingleCallEnd(String phone) {
        super.onSingleCallEnd(phone);
        endList.add(phone);
        Log.e("云呼请求", "*******type********* " + type);
        Log.e("云呼请求", "单个列队呼叫结束, " + phone);
    }


    /**
     * 单个列队呼叫开始
     */
    @Override
    protected void onSingleCallStart(String phone) {
        super.onSingleCallEnd(phone);
        type = 0;
        Log.e("云呼请求", "单个列队开始呼叫, " + phone);

    }


    /**
     * 加载数据
     */
    private void load() {
        if (callTakeApi == null) {
            return;
        }
        // 如果在请求数据中，本次忽略
        if (callTakeApi.isPending()) {
            return;
        }

        log("开始请求列队");
        callTakeApi.setPendingStatus(false);
        callTakeApi.setType(type);
        if (type == 2) {
            callTakeApi.setEndCallList(endList);
            endList = new ArrayList<>();
        } else {
            callTakeApi.setEndCallList(new ArrayList<String>());
        }
        // 重置参数
        type = 0;
        callTakeApi.request(new Rest.RestCallback() {

            @Override
            public void onSuccess(String message, JSONObject data) {
                log("列队请求成功: " + data.toString());

                String type = data.optString("type");
                if (type.equals("NONE_SUBMIT")) {
                    callback.onNeedReSubmit();
                }

                // 添加到列队
                addCallQueue(data.optJSONArray("queue"));

                // 移除列队
                removeCallQueue(data.optJSONArray("remove_queue"));

                // 进入定时请求倒计时
                handler.sendEmptyMessage(CODE_TIMING_REQUEST);

            }

            @Override
            public void onError(String message, String field, int code) {
                if (code == BaseApi.CODE_LOGIN || code == BaseApi.CODE_NEED_BUY_GROUP || code == BaseApi.CODE_NEED_BUY_UNLOCK) {
                    callback.onRequestError(callTakeApi, message, field, code);
                }
                // 进入定时请求倒计时
                handler.sendEmptyMessage(CODE_TIMING_REQUEST);
                return;
            }
        });
    }

    /**
     * 锁定定时器
     */
    private void lockTimer() {
        if (isLock) {
            return;
        }

        isLock = true;
        lockTimer = new Timer();
        lockTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                if (lockTimer != null) {
                    lockTimer.cancel();
                    lockTimer = null;
                }
                isLock = false;
            }
        }, takeConfig.getGetQueueInterval());
    }


    /**
     * 添加到列队
     */
    private void addCallQueue(JSONArray array) {
        if (array.length() == 0) {
            type = 2;
        }
        if (CallService.getCallNumberLen() > 0) {
            type = 0;
        }
        for (int i = 0; i < array.length(); i++) {
            JSONObject json = array.optJSONObject(i);
            String id = json.optString("id");
            String phone = json.optString("phone");
            CallService.addCallQueue(id, phone);
        }
    }


    /**
     * 从列队移除
     */
    private void removeCallQueue(JSONArray array) {
        for (int i = 0; i < array.length(); i++) {
            JSONObject json = array.optJSONObject(i);
            String id = json.optString("id");
            String phone = json.optString("phone");
            String replacePhone = json.optString("replace_phone");
            CallService.removeCallQueue(id, replacePhone);
        }
    }


    private void log(String log) {
        Log.e("云呼请求", log);
        LogToFile.e("云呼请求", log);
    }


    abstract public static class CloudTakeTaskCallback {

        /**
         * 请求数据错误
         */
        abstract protected void onRequestError(CallTakeApi api, String message, String field, int code);


        /**
         * 呼叫错误
         */
        abstract protected void onCallError(String message);

        /**
         * 需要重新提交任务
         */
        abstract protected void onNeedReSubmit();
    }

    public static void stopTime(){
        // 释放请求
        if (callTakeApi!= null){
            callTakeApi = null;
        }
        // 清理请求定时器
        if (requestTiming != null) {
            requestTiming.cancel();
            requestTiming = null;
        }
    }
}
