package cn.yunhu.window;

import android.app.Activity;
import android.app.Dialog;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import cn.yunhu.R;

/**
 * PopupWindow基本类
 */
abstract public class BaseWindow extends DialogFragment {


    protected Context  context;
    protected Dialog   dialog;
    private   TextView close;

    public void setContext(Context context) {
        this.context = context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        dialog = getDialog();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(getContentLayout(), container);
    }

    @Override
    public void onStart() {
        super.onStart();

        DisplayMetrics dm            = new DisplayMetrics();
        Window         window        = dialog.getWindow();
        WindowManager  windowManager = getActivity().getWindowManager();
        windowManager.getDefaultDisplay().getMetrics(dm);
        window.setLayout((int) (dm.widthPixels * 0.85), ViewGroup.LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0.5f;
        window.setAttributes(windowParams);


        // 关闭
        close = (TextView) findId(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!getActivity().isFinishing()){
                    dismiss();
                }
            }
        });

        // 初始化
        initDialog();
    }


//    public void showStateLoss() {
//        showStateLoss(null);
//    }
//
//    public void showStateLoss(Activity activity) {
//        if (activity == null) {
//            activity = (Activity) context;
//        }
//        Log.e("tag","getActivity()+++++++++++++++++++++++: "+ getActivity());
//        Log.e("tag","+++++++++++++++++++++++: "+ getActivity().getSupportFragmentManager().beginTransaction());
//        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//        ft.add(this,activity.getClass().getSimpleName());
//        ft.commitAllowingStateLoss();
//    }


    public void dismissStateLoss() {
        this.dismissAllowingStateLoss();
    }


    /**
     * 获取ID
     */
    protected View findId(@IdRes int id) {
        return dialog.findViewById(id);
    }


    /**
     * 返回布局文件
     */
    abstract protected int getContentLayout();


    /**
     * 初始化
     */
    abstract protected void initDialog();
}
