package cn.yunhu.window;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import cn.yunhu.R;

/**
 * 呼叫中弹窗
 */
public class CallPendingWindow extends BaseWindow {

    private CallPendingWindowCallback callback;

    private FragmentActivity activity;
    public CallPendingWindow(){}
    @SuppressLint("ValidFragment")
    public  CallPendingWindow(FragmentActivity activity){
        this.activity = activity;
    }

    @Override
    protected int getContentLayout() {
        return R.layout.pupup_window_call_pending;
    }

    @Override
    protected void initDialog() {
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if (callback != null){
            callback.onDismiss();
        }
    }

    public void showStateLoss() {
//        if (activity == null) {
//            activity = (Activity) context;
//        }
        Log.e("tag","getActivity()+++++++++++++++++++++++: "+ activity);
        Log.e("tag","+++++++++++++++++++++++: "+ activity.getSupportFragmentManager().beginTransaction());
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.add(this,activity.getClass().getSimpleName());
        ft.commitAllowingStateLoss();
    }

    public void setCallback(CallPendingWindowCallback callback) {
        this.callback = callback;
    }


    abstract public static class CallPendingWindowCallback {

        public abstract void onDismiss();
    }
}
