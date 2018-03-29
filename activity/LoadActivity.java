package cn.yunhu.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.allenliu.versionchecklib.core.AllenChecker;
import com.allenliu.versionchecklib.core.VersionDialogActivity;
import com.allenliu.versionchecklib.core.VersionParams;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.yunhu.R;
import cn.yunhu.BaseApplication;
import cn.yunhu.api.AppInfoApi;
import cn.yunhu.api.LoginApi;
import cn.yunhu.dialog.ModelDialog;
import cn.yunhu.dialog.Pending;
import cn.yunhu.http.OkRequest;
import cn.yunhu.http.OkRequestCallback;
import cn.yunhu.service.DemoService;
import cn.yunhu.utils.BPermission;
import cn.yunhu.utils.Common;
import cn.yunhu.utils.Constants;
import cn.yunhu.utils.ToastUitil;

/**
 * APP加载页面
 */
public class LoadActivity extends BaseActivity implements BPermission.BPermissionInterface {


    private ModelDialog dialog;
    private ArrayList<String> rootList = new ArrayList<>();

    private int rootIndex = 0;
    private Pending pending;


    @Override
    protected int getLayoutContent() {
        return R.layout.activity_load;
    }


    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        setDisabledHeader(true);

        isLoadAppInfo = false;


        // 申请SD卡权限和手机权限
        new BPermission(nowActivity).setCallback(this).apply(0, new String[]{Manifest.permission.READ_PHONE_STATE}, Permission.STORAGE);


        // 初始化界面
        TextView    versionView = (TextView) findViewById(R.id.versionView);
        PackageInfo packageInfo = Common.getAppInfo(nowContext);
        String      versionName = "1.0.0";
        if (packageInfo != null) {
            versionName = packageInfo.versionName;
        }

        versionView.setText(Common.getAppName(nowContext) + " V" + versionName);
    }


    @Override
    public void onLoadAppInfoSuccess(String message, JSONObject data) {
        /**
         * 检测更新
         */
        version = AppInfoApi.getVersionConfig();
        int currentVersionCode = 0;
        PackageManager manager = getApplicationContext().getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(getApplicationContext().getPackageName(), 0);
            String appVersionName = info.versionName; // 版本名
            currentVersionCode = info.versionCode; // 版本号
            System.out.println(currentVersionCode + " " + appVersionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Log.e("tag","currentVersionCode****************: "+currentVersionCode);
        if ((version.getCode()>currentVersionCode)){
            VersionParams.Builder builder = new VersionParams.Builder()
//                .setHttpHeaders(headers)
//                .setRequestMethod(requestMethod)
//                .setRequestParams(httpParams)
                    .setRequestUrl("http://www.baidu.com")
                    .setDownloadAPKPath("/storage/emulated/0/YunhuUpdata/")
                    .setService(DemoService.class);
            Log.e("tag","currentVersionCode****************: "+version.getUrl());
            stopService(new Intent(this, DemoService.class));
            //更新的界面
            CustomVersionDialogActivity.customVersionDialogIndex = 2;
            builder.setCustomDownloadActivityClass(VersionDialogActivity.class);
            //下载进度界面
            CustomVersionDialogActivity.isCustomDownloading = false;
            builder.setCustomDownloadActivityClass(VersionDialogActivity.class);
            //强制更新
            if (version.isMust()) {
                CustomVersionDialogActivity.isForceUpdate = true;
                builder.setCustomDownloadActivityClass(CustomVersionDialogActivity.class);

            } else {
                //同理
                CustomVersionDialogActivity.isForceUpdate = false;
                builder.setCustomDownloadActivityClass(CustomVersionDialogActivity.class);
                CustomVersionDialogActivity.customVersionDialogIndex = 1;
                builder.setCustomDownloadActivityClass(VersionDialogActivity.class);
            }
            //是否强制重新下载
            builder.setForceRedownload(true);
            //是否仅使用下载功能
            builder.setOnlyDownload(false);
            //是否显示通知栏
            builder.setShowNotification(true);

            AllenChecker.startVersionCheck(this, builder.build());

        }else {
            // 进入登录页面
            if (LoginApi.getUserToken().equals("")) {
                goTopActivity(LoginActivity.class);
            }

            // 进入软件首页
            else {
                goTopActivity(HomeActivity.class);
            }
        }

    }


    @Override
    public void onLoadAppInfoError(String message, String field, int code) {
        exit(message);
    }


    private void exit(String message) {
        ToastUitil.showShort(message);

        // 退出程序
        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                    BaseApplication.getApplication().exit();
            }
        }, 2000);
    }


    @Override
    protected void onDestroy() {
        if (dialog != null) {
            dialog.dismiss();
        }

        /**
         * 取消对话框 不然会窗体泄漏
         */
        if (appInfoApi != null){
            appInfoApi.destroy();
        }

        super.onDestroy();
    }


    @Override
    public void onBPermissionSuccess(int requestCode, @NonNull List<String> list) {

        getAppRoot();
    }


    @Override
    public void onBPermissionError(int requestCode, @NonNull List<String> list) {

    }


    @Override
    public void onBPermissionItemSuccess(int requestCode, @NonNull String permission) {
    }


    @Override
    public void onBPermissionItemError(int requestCode, @NonNull String permission) {
        if (permission.equals(Manifest.permission.READ_PHONE_STATE)) {
            dialog = ModelDialog.alert(nowContext, "您拒绝了系统使用 <b>拨打电话</b> 和 <b>管理通话</b> 的权限，我们将无法继续为您提供服务。如需继续使用，请前往设置允许本软使用以上权限或，或者在安全软件中将本软件加为信任", new ModelDialog.ModelDialogCallBack() {

                @Override
                protected boolean onCallback(DialogInterface dialog, int which) {
                       BaseApplication.getApplication().exit();

                    return true;
                }
            });
        } else {
            dialog = ModelDialog.alert(nowContext, "您拒绝了系统访问您的 <b>存储</b> 权限，我们将无法继续为您提供服务。如需继续使用，请前往设置允许本软使用以上权限或，或者在安全软件中将本软件加为信任", new ModelDialog.ModelDialogCallBack() {

                @Override
                protected boolean onCallback(DialogInterface dialog, int which) {
                        BaseApplication.getApplication().exit();

                    return true;
                }
            });
        }
    }


    @Override
    public void onBPermissionRationale(int requestCode, final Rationale rationale) {
        rationale.resume();
    }


    /**
     * 遍历地址寻找可执行地址
     */
    private void getOkRoot() {
        if (rootIndex >= rootList.size()) {
            if (pending != null) {
                pending.dismiss();
            }
            exit("启动错误[server root]");
            return;
        }

        if (pending == null) {
            pending = new Pending(nowContext);
            pending.setCanceledOnTouchOutside(true);
            pending.setCancelable(false);
            pending.show();
            pending.setMessage("请稍后...");
        }

        Log.e("获取入口", "获取入口地址" + rootIndex + ", " + rootList.get(rootIndex));
        OkRequest okRequest = new OkRequest(nowContext);
        okRequest.isDes(false);
        okRequest.addHeader("User-Agent", AppInfoApi.getUserAgent());
        okRequest.get("http://" + rootList.get(rootIndex) + "/", new OkRequestCallback() {

            @Override
            public void onSuccess(String result, int id) {
                super.onSuccess(result, id);
                //    BaseApplication.setApiRoot("http://" + rootList.get(rootIndex) + "/");
                BaseApplication.setApiRoot("http://" + rootList.get(rootIndex) + "/");
                //    Log.e("获取入口地址", "成功, " + BaseApplication.getApiRoot());
                Log.e("获取入口地址", "成功, " + BaseApplication.getApiRoot());

                if (pending != null) {
                    pending.dismiss();
                    loadAppInfo(true);
                }
            }


            @Override
            public boolean onError(String message, int id) {
                Log.e("获取入口地址", "失败, " + message);

                rootIndex++;
                if (rootIndex < rootList.size()) {
                    getOkRoot();
                }
                return super.onError(message, id);
            }


            @Override
            public void onComplete(int id) {
                super.onComplete(id);
                if (LoadActivity.this != null && !LoadActivity.this.isFinishing() && pending != null){
                    if (pending != null && !LoadActivity.this.isFinishing()){
                        pending.dismiss();
                    }

                }
            }
        });
    }


    /**
     * 请求系统入口
     */
    private void getAppRoot() {
        pending = new Pending(nowContext);
        pending.setCanceledOnTouchOutside(true);
        pending.setCancelable(false);
        pending.show();
        pending.setMessage("请稍后...");

        OkRequest okRequest = new OkRequest(nowContext);
        okRequest.isDes(false);
        okRequest.get(Constants.GET_ADDRESS, new OkRequestCallback() {

            @Override
            public void onSuccess(String result, int id) {
                super.onSuccess(result, id);

                Pattern pattern = Pattern.compile("\\*\\*(.*)\\*\\*");
                Matcher matcher = pattern.matcher(result);
                if (matcher.find()) {
                    String   temp = matcher.group(1);
                    String[] arr  = temp.split("\\*\\*");
                    String   url  = "";
                    for (int i = 0; i < arr.length; i++) {
                        url = arr[i].trim();
                        if (url.equals("")) {
                            continue;
                        }

                        rootList.add(url);
                        Log.e("root-" + i, url);
                    }
                }

                if (rootList.size() == 0) {
                    onError("", 0);
                    return;
                }

                getOkRoot();
            }


            @Override
            public boolean onError(String message, int id) {
                if (pending != null && !LoadActivity.this.isFinishing()){
                    pending.dismiss();
                }
                Log.e("tag","message*****************: "+message);
                exit("系统异常，请稍后再试");

                return super.onError(message, id);
            }
        });
    }
}

