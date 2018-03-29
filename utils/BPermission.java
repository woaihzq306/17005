package cn.yunhu.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.AppOpsManagerCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;
import com.yanzhenjie.permission.Request;

import java.util.Iterator;
import java.util.List;

/**
 * 权限申请
 */
public class BPermission {

    private Activity             activity  = null;
    private Fragment             fragment  = null;
    private android.app.Fragment aFragment = null;
    private BPermissionInterface callback  = null;
    private Context              context   = null;


    /** 申请成功或失败 */
    private PermissionListener permissionListener = new PermissionListener() {

        @Override
        public void onSucceed(int requestCode, @NonNull List<String> list) {
            if (callback != null) {
                for (int i = 0; i < list.size(); i++) {
                    String name = list.get(i);
                    if (checkHasPermission(context, name)) {
                        callback.onBPermissionItemSuccess(requestCode, name);
                    } else {
                        callback.onBPermissionItemError(requestCode, name);
                    }
                }

                if (AndPermission.hasPermission(context, list)) {
                    callback.onBPermissionSuccess(requestCode, list);
                } else {
                    callback.onBPermissionError(requestCode, list);
                }
            }
        }


        @Override
        public void onFailed(int requestCode, @NonNull List<String> list) {
            if (callback != null) {
                // 如果失败了则遍历检测权限是否拥有
                for (int i = 0; i < list.size(); i++) {
                    String name = list.get(i);
                    if (checkHasPermission(context, name)) {
                        callback.onBPermissionItemError(requestCode, name);
                    } else {
                        callback.onBPermissionItemSuccess(requestCode, name);
                    }
                }

                if (AndPermission.hasPermission(context, list)) {
                    callback.onBPermissionSuccess(requestCode, list);
                } else {
                    callback.onBPermissionError(requestCode, list);
                }
            }

        }
    };

    /** 需要解释 */
    private RationaleListener rationaleListener = new RationaleListener() {

        @Override
        public void showRequestPermissionRationale(int i, Rationale rationale) {
            if (callback != null) {
                callback.onBPermissionRationale(i, rationale);
            }
        }
    };


    public BPermission(Context context) {
        this.context = context;
    }


    public BPermission(Activity activity) {
        this.context = activity.getApplicationContext();
        this.activity = activity;
    }


    public static boolean checkHasPermission(Context context, String permission) {
        int result = ContextCompat.checkSelfPermission(context, permission);
        if (result == -1) {
            return false;
        }

        String op = AppOpsManagerCompat.permissionToOp(permission);
        if (!TextUtils.isEmpty(op)) {
            result = AppOpsManagerCompat.noteProxyOp(context, op, context.getPackageName());
            if (result != 0) {
                return false;
            }
        }

        return true;
    }


    public BPermission(Fragment fragment) {
        this.context = fragment.getActivity().getApplicationContext();
        this.fragment = fragment;
    }


    public BPermission(android.app.Fragment fragment) {
        this.context = fragment.getActivity().getApplicationContext();
        this.aFragment = fragment;
    }


    /**
     * 设置回调
     */
    public BPermission setCallback(BPermissionCallback callback) {
        this.callback = callback;

        return this;
    }


    /**
     * 设置回调
     */
    public BPermission setCallback(Object callback) {
        this.callback = (BPermissionInterface) callback;

        return this;
    }


    public void apply(int requestCode, String... strings) {
        parseRequest().permission(strings).requestCode(requestCode).callback(permissionListener).rationale(rationaleListener).start();
    }


    public void apply(int requestCode, String[]... strings) {
        parseRequest().permission(strings).requestCode(requestCode).callback(permissionListener).rationale(rationaleListener).start();
    }


    private Request parseRequest() {
        Request request;
        if (activity != null) {
            request = AndPermission.with(activity);
        } else if (fragment != null) {
            request = AndPermission.with(fragment);
        } else if (aFragment != null) {
            request = AndPermission.with(aFragment);
        } else {
            request = AndPermission.with(context);
        }
        return request;
    }


    public static class BPermissionCallback implements BPermissionInterface {

        @Override
        public void onBPermissionSuccess(int requestCode, @NonNull List<String> list) {

        }


        @Override
        public void onBPermissionError(int requestCode, @NonNull List<String> list) {

        }


        @Override
        public void onBPermissionItemSuccess(int requestCode, @NonNull String permission) {

        }


        @Override
        public void onBPermissionItemError(int requestCode, @NonNull String permission) {

        }


        @Override
        public void onBPermissionRationale(int requestCode, Rationale rationale) {

        }
    }

    public interface BPermissionInterface {

        /**
         * 一批权限申请成功
         * @param requestCode 申请代码
         * @param list        权限合集
         */
        void onBPermissionSuccess(int requestCode, @NonNull List<String> list);


        /**
         * 一批权限申请失败
         * @param requestCode 申请代码
         * @param list        权限合集
         */
        void onBPermissionError(int requestCode, @NonNull List<String> list);


        /**
         * 单个权限申请成功
         * @param requestCode 申请代码
         * @param permission  权限
         */
        void onBPermissionItemSuccess(int requestCode, @NonNull String permission);


        /**
         * 单个权限申请失败
         * @param requestCode 申请代码
         * @param permission  权限
         */
        void onBPermissionItemError(int requestCode, @NonNull String permission);


        /**
         * 某个权限请求重新赋予
         * @param requestCode 申请代码
         * @param rationale
         */
        void onBPermissionRationale(int requestCode, Rationale rationale);
    }
}
