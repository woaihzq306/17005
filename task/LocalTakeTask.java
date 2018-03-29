package cn.yunhu.task;


import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.yunhu.service.CallService;


/**
 * 本地呼叫
 */
public class LocalTakeTask extends TakeTask {


    private LocalTakeTaskCallback callback  = null;
    private List<String>          phoneList = new ArrayList<>();


    public LocalTakeTask(Context context, List<String> list, LocalTakeTaskCallback callback) {
        super(context);

        CallService.setQueueSurplus(0);

        phoneList = list;
        this.callback = callback;
    }


    @Override
    public void start() {
        super.start();

        load();
    }


    @Override
    public void stop() {
        super.stop();

        phoneList = new ArrayList<>();
    }


    @Override
    protected void onEmptyQueue() {
        super.onEmptyQueue();
        Log.e("本地呼叫", "请求添加新的列队");
        load();
    }


    /**
     * 加载数据
     */
    private void load() {
        for (int i = 0; i < phoneList.size(); i++) {
            CallService.addCallQueue(phoneList.get(i));
        }
    }


    @Override
    protected void onError(String message) {
        super.onError(message);
        callback.onCallError(message);
    }


    abstract public static class LocalTakeTaskCallback {

        abstract protected void onCallError(String message);
    }

}
