package cn.yunhu.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allenliu.versionchecklib.core.AllenChecker;
import com.allenliu.versionchecklib.core.VersionDialogActivity;
import com.allenliu.versionchecklib.core.VersionParams;

import org.json.JSONObject;


import cn.yunhu.BaseApplication;
import cn.yunhu.R;
import cn.yunhu.api.AppInfoApi;
import cn.yunhu.http.Rest;
import cn.yunhu.service.DemoService;
import cn.yunhu.utils.SPUtil;
import cn.yunhu.window.CustomerServiceWindow;

/**
 * Activity基本类
 */
abstract public class BaseActivity extends AppCompatActivity {
    //+--------------------------------------
    //| 访问相关
    //+--------------------------------------
    /** 当前Context */
    public Activity     nowContext  = null;
    /** 当前Activity */
    public BaseActivity nowActivity = null;


    //+--------------------------------------
    //| APP信息相关
    //+--------------------------------------
    /** 是否加载APP基本信息 */
    public  boolean isLoadAppInfo = true;
    /** 是否检测升级 */
    //public  boolean checkUpdate   = true;
    /** 升级服务 */
    //private Intent  updateIntent  = null;


    //+--------------------------------------
    //| 退出相关
    //+--------------------------------------
    /** 退出类型 */
    private int  exitType = 0;
    /** 当前时间 */
    private long exitTime = 0;


    //+--------------------------------------
    //| 页头相关
    //+--------------------------------------
    /** viewHeader */
    public RelativeLayout viewHeader            = null;
    /** 左侧功能区 */
    public LinearLayout   viewHeaderLeft        = null;
    /** 左侧文本 */
    public TextView       viewHeaderLeftText    = null;
    /** 左侧图标 */
    public ImageView      viewHeaderLeftImage   = null;
    /** 标题 */
    public TextView       viewHeaderTitle       = null;
    /** 右侧水平功能区 */
    public LinearLayout   viewHeaderRight       = null;
    /** 右侧水平图标 */
    public ImageView      viewHeaderRightImage  = null;
    /** 右侧水平文本 */
    public TextView       viewHeaderRightText   = null;
    /** 右侧垂直功能区 */
    public LinearLayout   viewHeaderVRight      = null;
    /** 右侧垂直图标 */
    public ImageView      viewHeaderVRightImage = null;
    /** 右侧垂直文本 */
    public TextView       viewHeaderVRightText  = null;


    public AppInfoApi appInfoApi;

    AppInfoApi.VersionConfig version;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 添加Activity到合集中
        //BaseApplication.addActivity(this);
        BaseApplication.addActivity(this);

        // 设置布局
        setContentView(getLayoutContent());


        // 变量初始化
        nowContext = this;
        nowActivity = this;
        viewHeader = (RelativeLayout) findViewById(R.id.header);
        viewHeaderLeft = (LinearLayout) findViewById(R.id.headerLeft);
        viewHeaderLeftImage = (ImageView) findViewById(R.id.headerLeftImage);
        viewHeaderLeftText = (TextView) findViewById(R.id.headerLeftText);
        viewHeaderTitle = (TextView) findViewById(R.id.headerTitle);
        viewHeaderRight = (LinearLayout) findViewById(R.id.headerRight);
        viewHeaderRightImage = (ImageView) findViewById(R.id.headerRightImage);
        viewHeaderRightText = (TextView) findViewById(R.id.headRightText);
        viewHeaderVRight = (LinearLayout) findViewById(R.id.headerRightVertical);
        viewHeaderVRightImage = (ImageView) findViewById(R.id.headerRightVerticalImage);
        viewHeaderVRightText = (TextView) findViewById(R.id.headerRightVerticalText);
        viewHeaderLeft.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onClickByLeft();
            }
        });
        viewHeaderRight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onClickByRight();
            }
        });
        viewHeaderVRight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onClickByVRight();
            }
        });
        viewHeaderLeftText.setText("");
        viewHeaderRight.setVisibility(View.GONE);

        // 初始化
        initView(savedInstanceState);


    }


    /**
     * 通过类名启动Activity
     * @param classes Activity类
     */
    public void startActivity(Class<?> classes) {
        startActivity(classes, null);
    }


    /**
     * 通过类名启动Activity，含有Bundle数据
     * @param classes Activity类
     * @param bundle  Bundle数据
     */
    public void startActivity(Class<?> classes, Bundle bundle) {
        Intent intent = new Intent(this, classes);
        if (bundle != null) {
            intent.putExtras(bundle);
        }

        startActivity(intent);
    }


    public void goTopActivity(Class classes) {
        Intent intent = new Intent(this, classes);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }


    public void goSingleTopActivity(Class cls) {
        Intent intent = new Intent(this, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }


    public void goNoHistoryActivity(Class classes) {
        Intent intent = new Intent(this, classes);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        startActivity(intent);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (exitType) {
                // 回到首页
                case 1:
                    goTopActivity(HomeActivity.class);
                    return true;

                // 退出程序
                case 2:
                    if ((System.currentTimeMillis() - exitTime) > 2000) {
                        Toast.makeText(getApplicationContext(), "再按一次退出", Toast.LENGTH_SHORT).show();
                        exitTime = System.currentTimeMillis();
                    }

                    // 完全退出
                    else {
                        BaseApplication.getApplication().exit();
                    }

                    return true;

                // 返回上一页
                default:
                //    BaseApplication.removeActivity(nowContext);
                    BaseApplication.removeActivity(nowContext);
                    finish();
                    return true;
            }
        }

        return false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 删除所有的Activity
        //BaseApplication.removeActivity(this);
        BaseApplication.removeActivity(this);
    }


    @Override
    protected void onResume() {
        super.onResume();

        // 定时重新请求APP基本信息
        if (isLoadAppInfo) {
            long expireTime = SPUtil.getInt("loadAppInfoLock");
            long nowTime    = System.currentTimeMillis() / 1000;
            Log.d("当前系统时间", nowTime + "");
            Log.d("重新请求时间", expireTime + "");
            if (expireTime <= 0 || expireTime < nowTime) {
                loadAppInfo(false);
                long nextTime = nowTime + 10 * 60;
                SPUtil.putInt("loadAppInfoLock", (int) nextTime);
                Log.d("下次请求时间", nextTime + "");
            }
        }
    }


    /**
     * 请求系统基本信息
     * @param isPending 是否显示等待提示
     */
    public void loadAppInfo(boolean isPending) {
        appInfoApi = new AppInfoApi(nowContext);
        appInfoApi.setPendingStatus(isPending);

        appInfoApi.request(new Rest.RestCallback() {

            @Override
            public void onSuccess(String message, JSONObject data) {
                onLoadAppInfoSuccess(message, data);
            }


            @Override
            public void onError(String message, String field, int code) {
                onLoadAppInfoError(message, field, code);
            }
        });
    }


    /**
     * 请求系统基本信息成功回调
     * @param message 成功的消息
     * @param data    成功的数据
     */
    public void onLoadAppInfoSuccess(String message, JSONObject data) {
    }


    /**
     * 请求系统基本信息失败回调
     * @param message 成功的消息
     * @param field   出错的字段
     * @param code    错误代码
     */
    public void onLoadAppInfoError(String message, String field, int code) {
    }


    /**
     * 设置按返回键退出程序
     */
    public void setBackToApp() {
        exitType = 2;
    }


    /**
     * 设置按返回键回到首页
     */
    public void setBackToHome() {
        exitType = 1;
    }


    /**
     * 设置按返回键回到上一页
     */
    public void setBackToPrev() {
        exitType = 0;
    }


    /**
     * 获取布局
     */
    protected abstract int getLayoutContent();


    /**
     * 初始化
     */
    protected abstract void initView(@Nullable Bundle savedInstanceState);


    /**
     * 设置是否禁用页头
     */
    public void setDisabledHeader(boolean status) {
        if (status) {
            viewHeader.setVisibility(View.GONE);
        } else {
            viewHeader.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 设置禁用左侧功能区
     */
    public void setDisabledHeaderLeft(boolean status) {
        if (status) {
            viewHeaderLeft.setVisibility(View.GONE);
        } else {
            viewHeaderLeft.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 设置禁用右侧功能区
     */
    public void setDisabledHeaderRight(boolean stauts) {
        if (stauts) {
            viewHeaderRight.setVisibility(View.GONE);
        } else {
            viewHeaderRight.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 设置禁用右侧垂直功能区
     */
    public void setDisabledHeaderVRight(boolean stauts) {
        if (stauts) {
            viewHeaderVRight.setVisibility(View.GONE);
        } else {
            viewHeaderVRight.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 设置页面标题
     */
    public void setHeaderTitle(String headerTitle) {
        viewHeaderTitle.setText(headerTitle);
    }


    /**
     * 左侧按钮单击事件
     */
    public void onClickByLeft() {
        finish();
    }


    /**
     * 左侧按钮单击事件
     */
    public void onClickByRight() {
    }


    /**
     * 左侧垂直按钮单击事件
     */
    public void onClickByVRight() {
        CustomerServiceWindow customerServiceWindow = new CustomerServiceWindow();
        customerServiceWindow.setContext(nowContext);
        customerServiceWindow.show(getSupportFragmentManager(), "CustomerServiceWindow");
    }
}
