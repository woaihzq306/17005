package cn.yunhu.task;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


import cn.yunhu.api.AppInfoApi;
import cn.yunhu.service.CallService;

/**
 * 呼叫基本类
 */
public class TakeTask {

    protected AppInfoApi.TakeConfig takeConfig;
    protected TakeTaskReceiver      taskReceiver;
    protected Context               context;
    protected boolean isRun = false;


    protected TakeTask(Context context) {
        this.context = context;
        takeConfig = AppInfoApi.getTakeConfig();
        CallService.setQueueSurplus(takeConfig.getQueueSurplus());
    }


    /**
     * 停止任务
     */
    public void stop() {
        // 取消注册广播
        if (taskReceiver != null) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(taskReceiver);
            Log.d("stop", "取消注册广播");
        }

        // 停止服务
        CallService.stopService(context);

        isRun = false;
    }


    /**
     * 启动任务
     */
    public void start() {
        // 注册广播接收器
        taskReceiver = new TakeTaskReceiver();
        IntentFilter filter = new IntentFilter(CallService.class.getName());
        LocalBroadcastManager.getInstance(context).registerReceiver(taskReceiver, filter);

        // 启动服务
        CallService.startService(context);

        isRun = true;
    }


    /**
     * 需要列队
     */
    protected void onNeedQueue() {

    }


    /**
     * 没有列队了
     */
    protected void onEmptyQueue() {

    }


    /**
     * 开始呼叫
     */
    protected void onSingleCallStart(String phone) {

    }


    /**
     * 挂断电话
     */
    protected void onSingleCallEnd(String phone) {

    }


    /**
     * 错误，需要权限
     */
    protected void onError(String message) {

    }


    /**
     * 是否运行中
     */
    public boolean isRun() {
        return isRun;
    }


    /**
     * 任务广播接收器
     */
    class TakeTaskReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int    type  = intent.getIntExtra("call_server_type", 0);
            String value = intent.getStringExtra("call_server_value");
            switch (type) {
                case CallService.TYPE_NEED_QUEUE:
                    Log.e("tag","收到需要空广播.....................");
                    onNeedQueue();
                    break;
                case CallService.TYPE_EMPTY_QUEUE:
                    Log.e("tag","收到为空广播.....................");
                    onEmptyQueue();
                    break;
                case CallService.TYPE_SINGLE_START_CALL:
                    onSingleCallStart(value);
                    break;
                case CallService.TYPE_SINGLE_END_CALL:
                    onSingleCallEnd(value);
                    break;
                case CallService.TYPE_NO_CALL_PERMISSION:
                    onError(value);
                    break;
            }
        }
    }
}
