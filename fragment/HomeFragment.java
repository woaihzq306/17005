package cn.yunhu.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.yunhu.BaseApplication;
import cn.yunhu.R;
import cn.yunhu.activity.FeedbackActivity;
import cn.yunhu.activity.HomeActivity;
import cn.yunhu.activity.LoginActivity;
import cn.yunhu.adapter.CommonAdapter;
import cn.yunhu.adapter.ViewHolder;
import cn.yunhu.api.AppInfoApi;
import cn.yunhu.api.CallStopApi;
import cn.yunhu.api.CallSubmitApi;
import cn.yunhu.api.CallTakeApi;
import cn.yunhu.api.GetUserInfoApi;
import cn.yunhu.api.LoginApi;
import cn.yunhu.dialog.ModelDialog;
import cn.yunhu.dialog.Pending;
import cn.yunhu.http.Rest;
import cn.yunhu.task.CloudTakeTask;
import cn.yunhu.task.GPRSTaskTest;
import cn.yunhu.task.LocalTakeTask;
import cn.yunhu.task.TakeTaskTest;
import cn.yunhu.utils.Common;
import cn.yunhu.utils.EncodeUtil;
import cn.yunhu.window.CallPendingWindow;
import cn.yunhu.window.CallTypeWindow;
import cn.yunhu.window.ShowTypeWindow;
import cn.yunhu.utils.ToastUitil;
import cn.yunhu.window.TestPopWindow;

/**
 * 呼叫中心
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener {

    private ShowTypeWindow showTypeWindow;
    private CallTypeWindow callTypeWindow;
    private TextView showType;
    private TextView callType;
    private LinearLayout cloudCallBtn;
    private LinearLayout localCallBtn;
    private TextView accountUser;
    private TextView accountLevel;
    private TextView accountExpire;
    private TextView buyBtn;
    private TextView addPhoneBtn;
    private EditText phone;
    private ListView phoneListView;
    private CommonAdapter<String> phoneAdapder;
    private TextView phoneListViewTip;
    private TextView outLoginBtn;
    private HomeActivity parentActivity;
    private LinearLayout qqBtn;
    private LinearLayout wechatBtn;
    private TextView qqText;
    private TextView wechatText;
    private TextView feedbackBtn;
    private AppInfoApi.CustomerConfig customerConfig;
    private CallStopApi callStopApi;


    private static String encryption;
    private static final String target = "PULL_OUT";

    private boolean islocalCall = false;

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;

    private Timer requestTiming = null;

    private boolean isFirstLoadUserInfo = true;
    private List<String> phoneList = new ArrayList<>();
    private String callTypeId = "0";
    private String showTypeId = "0";
    private GetUserInfoApi.UserInfo userInfo = null;
    private CloudTakeTask cloudTakeTask = null;
    private LocalTakeTask localTakeTask = null;
    private AppInfoApi.TakeConfig takeConfig = null;
    private CallPendingWindow callPendingWindow = null;

    String[] items = new String[]{"打电话测试", "数据流量测试"};
    /**
     * 加载等待提示框
     */
    private Pending pending = null;
    /**
     * 是否显示加载提示框
     */
    private boolean pendingStatus = true;
    /**
     * 加载等待提示文字
     */
    private String pendingMessage = "正在检测中...";

    @SuppressLint("HandlerLeak")
    private Handler callHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 开始云呼
                case 1:
                    if (cloudTakeTask != null && cloudTakeTask.isRun()) {
                        ToastUitil.showShort("云呼正在进行中...");
                        return;
                    }

                    if (phoneList.size() == 0) {
                        ToastUitil.showShort("请添加手机号后开始呼叫");
                        return;
                    }

                    callSubmit(true);

                    break;

                // 云呼失败
                case 2:
                    // 隐藏呼叫框
                    hideStopWindow();

                    Bundle bundle = msg.getData();
                    int type = bundle.getInt("type");
                    String message = bundle.getString("message");
                    switch (type) {
                        case TakeTaskTest.ERROR_TYPE_ALERT:
                        case TakeTaskTest.ERROR_TYPE_CONFIRM:
                            ModelDialog.alert(nowContext, message);
                            break;

                        case TakeTaskTest.ERROR_TYPE_NONE:
                            break;
                        default:
                            ToastUitil.showShort(message);
                    }
                    break;

                case 3:
                    Log.e("tag", "断开网络*********************************");
                    if (userInfo.isTrial()) {
                        callSubmit(false);
                    } else {
                        callSubmit(true);
                    }
                    break;

                case 4:
                    if (isNetworkAvailable(nowContext)){
                        callHandler.sendEmptyMessage(1);
                        Log.e("tag","============++++++++++++++++++++++++++++");
                    }else {
                        callHandler.sendEmptyMessageAtTime(4,1000);
                    }
                    break;
            }

            super.handleMessage(msg);
        }
    };


    @Override
    protected int getLayoutContent() {
        return R.layout.fragment_home;
    }


    @Override
    protected void initFragment(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        parentActivity = (HomeActivity) nowActivity;
        takeConfig = AppInfoApi.getTakeConfig();
        addPhoneBtn = (TextView) findId(R.id.addPhoneBtn);
        addPhoneBtn.setOnClickListener(this);
        phone = (EditText) findId(R.id.phone);
        buyBtn = (TextView) findId(R.id.buyBtn);
        outLoginBtn = (TextView) findId(R.id.outLoginBtn);
        outLoginBtn.setOnClickListener(this);
        buyBtn.setOnClickListener(this);
        feedbackBtn = (TextView) findId(R.id.feedbackBtn);
        feedbackBtn.setOnClickListener(this);


        // 客服
        customerConfig = AppInfoApi.getCustomerConfig();
        qqBtn = (LinearLayout) findId(R.id.qq);
        qqText = (TextView) findId(R.id.qqText);
        wechatBtn = (LinearLayout) findId(R.id.wechat);
        wechatText = (TextView) findId(R.id.wechatText);
        wechatText.setText(customerConfig.getWechat());
        qqText.setText(customerConfig.getQq());
        qqBtn.setOnClickListener(this);
        wechatBtn.setOnClickListener(this);


        // 账号信息
        accountUser = (TextView) findId(R.id.accountUser);
        accountUser.setOnClickListener(this);
        accountLevel = (TextView) findId(R.id.accountLevel);
        accountExpire = (TextView) findId(R.id.accountExpire);


        // 云呼叫
        cloudCallBtn = (LinearLayout) findId(R.id.cloudCallBtn);
        cloudCallBtn.setOnClickListener(this);

        // 本地呼
        localCallBtn = (LinearLayout) findId(R.id.localCallBtn);
        localCallBtn.setOnClickListener(this);

        // 初始化选项
        initSelect();

        // 初始化手机列表
        initPhoneList();

        Log.e("fragment", "fragment************创建布局");

//        // 公告
//        AppInfoApi.UserConfig userConfig = AppInfoApi.getUserConfig();
//        if (userConfig.getNoticeIsShow()) {
//            ModelDialog.alert(nowContext, userConfig.getNoticeTitle(), userConfig.getNoticeContent(), new ModelDialog.ModelDialogCallBack() {
//
//                @Override
//                protected boolean onCallback(DialogInterface dialog, int which) {
//                    return true;
//                }
//            });
//        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("tag", "--------------------------------------------: " + savedInstanceState);
        if (savedInstanceState != null) {
            return;
        }
        // 公告
        AppInfoApi.UserConfig userConfig = AppInfoApi.getUserConfig();
        if (userConfig.getNoticeIsShow()) {
            ModelDialog.alert(nowContext, userConfig.getNoticeTitle(), userConfig.getNoticeContent(), new ModelDialog.ModelDialogCallBack() {

                @Override
                protected boolean onCallback(DialogInterface dialog, int which) {
                    return true;
                }
            });
        }
    }

    /**
     * 初始化手机列表
     */
    private void initPhoneList() {
        // 手机号列表
        phoneListView = (ListView) findId(R.id.phoneListView);
        phoneListViewTip = (TextView) findId(R.id.phoneListViewTip);
        phoneAdapder = new CommonAdapter<String>(nowContext, phoneList, R.layout.fragment_home_phone_list_item) {

            @Override
            public void convert(ViewHolder holder, String s) {
                holder.setText(R.id.phone, s);
            }
        };
        phoneListView.setAdapter(phoneAdapder);
        phoneListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("remove", phoneList.get(position));
                removePhone(phoneList.get(position));
            }
        });
        listViewShowHide();
        setListViewHeight();
    }


    /**
     * 添加手机号
     */
    private void addPhone(String phone) {
        phone = Common.string(phone.trim());
        if (phone.equals("")) {
            throw new InputMismatchException("请输入手机号");
        }

        // 格式
        if (!Common.isMobileNO(phone)) {
            throw new InputMismatchException("手机号填写有误");
        }

        // 去重
        for (int i = 0; i < phoneList.size(); i++) {
            if (phoneList.get(i).equals(phone)) {
                throw new InputMismatchException("请勿填写重复手机号");
            }
        }

        if (userInfo != null) {
            if (userInfo.getMaxCallTotal() > 0 && phoneList.size() + 1 > userInfo.getMaxCallTotal()) {
                throw new InputMismatchException("您的账号最多允许提交" + userInfo.getMaxCallTotal() + "个手机号，请升级您的账号或删除一定的号码后再进行呼叫.");
            }
        }

        phoneList.add(phone);
        phoneAdapder.notifyDataSetChanged();
        listViewShowHide();
        setListViewHeight();
    }


    /**
     * 删除手机号
     */
    private void removePhone(String phone) {
        for (int i = 0; i < phoneList.size(); i++) {
            if (phoneList.get(i).equals(phone)) {
                phoneList.remove(i);
            }
        }

        phoneAdapder.notifyDataSetChanged();
        listViewShowHide();
        setListViewHeight();
    }


    /**
     * 重新计算ListView的高度，解决ScrollView和ListView两个View都有滚动的效果，在嵌套使用时起冲突的问题
     */
    public void setListViewHeight() {
        if (phoneAdapder == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = phoneAdapder.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = phoneAdapder.getView(i, null, phoneListView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }
        ViewGroup.LayoutParams params = phoneListView.getLayoutParams();
        params.height = totalHeight + (phoneListView.getDividerHeight() * (phoneListView.getCount() - 1));
        phoneListView.setLayoutParams(params);
    }


    /**
     * ListView提示显示于隐藏
     */
    private void listViewShowHide() {
        if (phoneList.size() == 0) {
            phoneListViewTip.setVisibility(View.VISIBLE);
            phoneListView.setVisibility(View.GONE);
        } else {
            phoneListViewTip.setVisibility(View.GONE);
            phoneListView.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 初始化选项
     */
    private void initSelect() {
        // 选项
        showType = (TextView) findId(R.id.showType);
        showType.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showTypeWindow.setWidth(v.getWidth());
                showTypeWindow.showAsDropDown(v);
            }
        });

        callType = (TextView) findId(R.id.callType);
        callType.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                callTypeWindow.setWidth(v.getWidth());
                callTypeWindow.showAsDropDown(v);
            }
        });
        showTypeWindow = new ShowTypeWindow(nowContext, new CallTypeWindow.SelectCallback() {

            @Override
            public void onSelected(String name, String id) {
                showType.setText(name);
                showTypeId = id;
            }
        });
        callTypeWindow = new CallTypeWindow(nowContext, new CallTypeWindow.SelectCallback() {

            @Override
            public void onSelected(String name, String id) {
                callType.setText(name);
                callTypeId = id;
            }
        });
        callTypeId = callTypeWindow.getFirstId();
        showTypeId = showTypeWindow.getFirstId();
    }


    /**
     * 初始化会员信息
     */
    public void initUserInfo(boolean isPending) {
        isFirstLoadUserInfo = false;
        if (nowContext == null) {
            return;
        }
        GetUserInfoApi userInfoApi = new GetUserInfoApi(nowContext);
        userInfoApi.setPendingStatus(isPending);
        userInfoApi.request(new GetUserInfoApi.GetUserInfoCallBack() {

            @Override
            protected void callback(GetUserInfoApi.UserInfo info) {
                userInfo = info;
                accountUser.setText(userInfo.getUsername());
                accountExpire.setText(Html.fromHtml(userInfo.getExpireName()));
                accountLevel.setText(userInfo.getGroupName());
                // BaseApplication.setIsTrial(userInfo.isTrial());
                BaseApplication.setIsTrial(userInfo.isTrial());
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 云端呼叫
            case R.id.cloudCallBtn:
                if (cloudTakeTask != null && cloudTakeTask.isRun()) {
                    return;
                }

                islocalCall = false;
                //获取通讯的加密字符串
                getKey(target);

                String phoneNumber = Common.string(phone.getText().toString().trim());
                if (!phoneNumber.equals("")) {
                    phone.setText("");
                    try {
                        addPhone(phoneNumber);
                    } catch (InputMismatchException e) {
                        ToastUitil.showShort(e.getMessage());
                        return;
                    }
                }

                if (phoneList.size() == 0) {
                    ToastUitil.showShort("请添加手机号后开始呼叫");
                    return;
                }

                if (userInfo != null) {
                    // 试用用户不接任务
                    if (userInfo.isTrial()) {
                        callSubmit(false);
                    } else {
                        parentActivity.loadAppInfo(false);
                        // 进入测试流程
                        //takeTaskTest();
                        TestPopWindow testPopWindow = new TestPopWindow();
                        testPopWindow.setOnPicture(new TestPopWindow.onPictureCallBack() {
                            @Override
                            public void onClickPhone() {
                                takeTaskTest();
                            }

                            @Override
                            public void onClickRGPS() {
                                GPRStaskTest();
                            }
                        });
                        testPopWindow.show(parentActivity.getSupportFragmentManager(), "TestPopWindow");
                    }
                }

                break;

            // 本地呼叫
            case R.id.localCallBtn:
                if (localTakeTask != null && localTakeTask.isRun()) {
                    return;
                }

                islocalCall = true;
                //获取通讯的加密字符串
                getKey(target);
                String phoneNumber1 = Common.string(phone.getText().toString().trim());
                if (!phoneNumber1.equals("")) {
                    phone.setText("");
                    try {
                        addPhone(phoneNumber1);
                    } catch (InputMismatchException e) {
                        ToastUitil.showShort(e.getMessage());
                        return;
                    }
                }
                if (phoneList.size() == 0) {
                    ToastUitil.showShort("请添加手机号后开始呼叫");
                    return;
                }

                showStopWindow();

                localTakeTask = new LocalTakeTask(nowContext, phoneList, new LocalTakeTask.LocalTakeTaskCallback() {

                    @Override
                    protected void onCallError(String message) {
                        hideStopWindow();

                        ToastUitil.showLong(nowContext, message);
                    }
                });

                localTakeTask.start();
                break;

            // 购买
            case R.id.buyBtn:
                parentActivity.showBuyRecharge();
                break;

            case R.id.accountUser:
                Common.copy(nowContext, accountUser.getText().toString().trim(), "账号复制成功");
                break;

            // 添加手机号
            case R.id.addPhoneBtn:
                try {
                    addPhone(Common.string(phone.getText().toString().trim()));

                    phone.setText("");
                } catch (InputMismatchException e) {
                    ToastUitil.showShort(e.getMessage());
                }

                break;

            // 退出登录
            case R.id.outLoginBtn:
                LoginApi.outLogin();
                nowActivity.goTopActivity(LoginActivity.class);
                break;

            // 复制QQ
            case R.id.qq:
                Common.copy(nowContext, customerConfig.getQq(), "复制QQ成功");
                break;

            // 复制微信
            case R.id.wechat:
                Common.copy(nowContext, customerConfig.getQq(), "复制微信成功");
                break;

            // 投诉反馈
            case R.id.feedbackBtn:
                parentActivity.startActivity(FeedbackActivity.class);
                break;
        }
    }


    private void showStopWindow() {
        callPendingWindow = new CallPendingWindow(getActivity());
        callPendingWindow.setContext(nowContext);
        callPendingWindow.setCallback(new CallPendingWindow.CallPendingWindowCallback() {

            @Override
            public void onDismiss() {
                getKey("");
                stopCall();
            }
        });
        callPendingWindow.showStateLoss();
    }

    private void hideStopWindow() {
        if (callPendingWindow != null) {
            callPendingWindow.dismissStateLoss();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                // Permission Denied
                //Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    /**
     * 停止呼叫
     */
    private void stopCall() {
        if (islocalCall) {
            if (localTakeTask != null) {
                localTakeTask.stop();
            }
        } else {
            if (cloudTakeTask != null) {
                Log.e("tag","=====================================================");
                cloudTakeTask.stop();
                // 请求接口停止
                callStopApi = new CallStopApi(nowContext);
                callStopApi.request(new Rest.RestCallback() {

                    @Override
                    public void onSuccess(String message, JSONObject data) {
                    }

                    @Override
                    public void onError(String message, String field, int code) {
                        ToastUitil.showShort(message);
                    }
                });
            } else {

                CloudTakeTask.stopTime();

                // 请求接口停止
                callStopApi = new CallStopApi(nowContext);
                callStopApi.request(new Rest.RestCallback() {

                    @Override
                    public void onSuccess(String message, JSONObject data) {
                    }

                    @Override
                    public void onError(String message, String field, int code) {
                        ToastUitil.showShort(message);
                    }
                });
            }
        }


    }

    public void callSubmit(final boolean isTake) {
        // 提交手机号
        CallSubmitApi callSubmitApi = new CallSubmitApi(nowContext);
        callSubmitApi.setCallType(callTypeId);
        callSubmitApi.setShowType(showTypeId);
        callSubmitApi.setPhoneList(phoneList);
        callSubmitApi.request(new Rest.RestCallback() {

            @Override
            public void onSuccess(String message, JSONObject data) {
                // 显示窗口
                showStopWindow();

                if (isTake) {
                    //接受任务
                    // 接任务
                    cloudTakeTask = new CloudTakeTask(nowContext, new CloudTakeTask.CloudTakeTaskCallback() {

                        @Override
                        protected void onRequestError(CallTakeApi api, String message, String field, int code) {

                            // 停止呼叫
                            hideStopWindow();
                            // 执行错误处理
                            api.executeError(message, field, code);
                        }

                        @Override
                        protected void onCallError(String message) {
                            // 停止呼叫
                            hideStopWindow();

                            ToastUitil.showLong(nowContext, message);
                        }

                        @Override
                        protected void onNeedReSubmit() {
                            if (phoneList.size() > 0) {
                                Log.e("tag", "**********************");
                                callSubmit(false);
                            }
                        }
                    });
                    cloudTakeTask.start();
                }
            }

            @Override
            public void onError(String message, String field, int code) {
                ToastUitil.showLong(nowContext, message);
                Log.e("tag", "测试时的任务请求............：" + message);
                requestTiming = new Timer();
                requestTiming.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        callHandler.sendEmptyMessage(3);
                    }
                }, 15000);

            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e("fragment", "fragment*****************onHiddenChanged");
        // 显示的时候重载会员数据
        if (!hidden) {
            Log.e("onHiddenChanged", "onHiddenChanged");
            initUserInfo(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("fragment", "fragment**************可见");
        initUserInfo(false);
    }

    @Override
    public void onDestroy() {
        if (callStopApi != null) {
            callStopApi.destroy();
        }
        Log.e("fragment", "fragment**************销毁");

        GPRSTaskTest.unRegisterReceiver();

        super.onDestroy();
    }

    /**
     * 设置传输密码
     */
    private void getKey(String string) {
        //获取当前时间
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss ");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        try {
            //加密
            encryption = EncodeUtil.get3DES(string + "," + str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取加密字符串
     *
     * @return
     */
    public static String getEncryption() {
        return encryption;
    }

    /**
     * 打电话测试
     */
    private void takeTaskTest() {
        new TakeTaskTest(nowContext, new TakeTaskTest.TakeTaskTestCallback() {

            // 测试失败
            @Override
            protected void onError(String message, int type) {
                Log.e("tag", "测试失败---------------------");
                getKey("");
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("message", message);
                bundle.putInt("type", type);
                msg.what = 2;
                msg.setData(bundle);
                callHandler.sendMessage(msg);
            }


            // 测试成功
            @Override
            protected void onSuccess() {
                callHandler.sendEmptyMessage(1);
            }


            // 测试中
            @Override
            protected void onPending(int s) {

            }
        }).test();
    }

    /**
     * 数据流量测试
     */
    private void GPRStaskTest() {
        new GPRSTaskTest(nowContext, new GPRSTaskTest.GPRSTaskTestCallback() {
            @Override
            protected void onError(String message) {
                pending.dismiss();
                Log.e("tag", "测试失败---------------------");
                getKey("");
                ModelDialog.alert(nowContext, message);
            }

            @Override
            protected void onSuccess() {
                pending.dismiss();
                callHandler.sendEmptyMessage(1);
            }

            @Override
            protected void onLoad() {
                pending = new Pending(nowContext);
                pending.setCanceledOnTouchOutside(false);
                if (nowActivity != null && !nowActivity.isFinishing()) {
                    pending.show();
                    pending.setMessage(pendingMessage);
                }
            }
            @Override
            protected void onCancel() {
                pending.dismiss();
            //    ModelDialog.alert(nowContext, "您已取消检测!");
                ToastUitil.showLong(nowContext,"您已取消检测!");
            }
        }).test();
    }

    /**
     * 检测当的网络（WLAN、3G/2G）状态
     * @param context Context
     * @return true 表示网络可用
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected())
            {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED)
                {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }
}
