package cn.yunhu.api;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.yunhu.BaseApplication;
import cn.yunhu.utils.Common;
import cn.yunhu.utils.Constants;
import cn.yunhu.utils.DeviceUuidFactory;

import static cn.yunhu.http.Rest.*;

/**
 * 获取系统基本信息接口
 */
public class AppInfoApi extends BaseApi {

    public static final String TAG = "AppInfoApi";


    /**
     * 构造
     */
    public AppInfoApi(Context context) {
        super(context);
        setApiPath(Constants.API_PATH_GET_APP_INFO);

    }


    @Override
    public void request(final RestCallback callback) {

        // 设置账号
        stringParams.put("username", LoginApi.getUsername(context));

        // 设置机器码
        DeviceUuidFactory uuidFactory = new DeviceUuidFactory(context);
        stringParams.put("machine_code", uuidFactory.getDeviceUuid().toString().trim());

        // 执行请求
        super.request(new RestCallback() {

            @Override
            public void onSuccess(String message, JSONObject data) {
                // 保存代理ID
                setData(TAG, "proxy_id", data.optString("proxy_id"));

                // 保存客户端
                setData(TAG, "client_title", data.optString("client_title"));

                // UserAgent
                setData(TAG, "user_agent", data.optString("user_agent"));

                // 保存服务端时间
                setData(TAG, "current_time", data.optString("current_time"));
                //    BaseApplication.setStartTime(Long.parseLong(data.optString("current_time")));
                BaseApplication.setStartTime(Long.parseLong(data.optString("current_time")));

                // 版本
                setData(TAG, "version", data.optJSONObject("version").toString());

                // 客服
                setData(TAG, "customer", data.optJSONObject("customer").toString());

                // 反馈
                setData(TAG, "feedback", data.optJSONObject("feedback").toString());

                // 购买
                setData(TAG, "buy", data.optJSONObject("buy").toString());

                // 登录
                setData(TAG, "login", data.optJSONObject("login").toString());

                // 找回密码
                setData(TAG, "find_pwd", data.optJSONObject("find_pwd").toString());

                // 注册
                setData(TAG, "register", data.optJSONObject("register").toString());

                //接任务配置
                setData(TAG, "take", data.optJSONObject("take").toString());

                //呼叫中心配置合集
                setData(TAG, "call", data.optJSONObject("call").toString());

                //呼叫中心配置合集
                setData(TAG, "user", data.optJSONObject("user").toString());

                callback.onSuccess(message, data);
            }


            @Override
            public void onError(String message, String field, int code) {
                callback.onError(message, field, code);
            }
        });
    }


    /**
     * 获取代理ID
     */
    public static String getProxyId() {
        return Common.string(getData(TAG, "proxy_id"));
    }


    /**
     * 获取客户端名
     */
    public static String getClientTitle() {
        String string = Common.string(getData(TAG, "client_title")).trim();
        return !string.equals("") ? string : "云呼";
    }


    /**
     * UserAgent
     */
    public static String getUserAgent() {
        String string = Common.string(getData(TAG, "user_agent")).trim();
        return !string.equals("") ? string : "Android App YunHu2.0";
    }


    /**
     * 获取版本配置
     */
    public static VersionConfig getVersionConfig() {
        return new VersionConfig();
    }


    /**
     * 获取客服配置
     */
    public static CustomerConfig getCustomerConfig() {
        return new CustomerConfig();
    }


    /**
     * 获取注册配置
     */
    public static RegisterConfig getRegisterConfig() {
        return new RegisterConfig();
    }


    /**
     * 获取找回密码配置
     */
    public static FindPwdConfig getFindPwdConfig() {
        return new FindPwdConfig();
    }


    /**
     * 获取登录配置
     */
    public static LoginConfig getLoginConfig() {
        return new LoginConfig();
    }


    /**
     * 反馈配置
     */
    public static FeedbackConfig getFeedbackConfig() {
        return new FeedbackConfig();
    }


    /**
     * 任务配置
     */
    public static TakeConfig getTakeConfig() {
        return new TakeConfig();
    }


    public static CallConfig getCallConfig() {
        return new CallConfig();
    }


    public static BuyConfig getBuyConfig() {
        return new BuyConfig();
    }


    public static UserConfig getUserConfig() {
        return new UserConfig();
    }


    public static class UserConfig {

        private String noticeContent = "";
        private String noticeTitle = "";
        private boolean isLock = false;
        private boolean noticeIsShow = false;


        UserConfig() {
            try {
                JSONObject json = new JSONObject(getData(TAG, "user"));
                isLock = json.optBoolean("is_lock");
                JSONObject notice = json.optJSONObject("notice");
                noticeIsShow = notice.optBoolean("status");
                noticeTitle = notice.optString("title");
                noticeContent = notice.optString("content");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        public boolean isLock() {
            return isLock;
        }


        public String getNoticeContent() {
            return noticeContent;
        }


        public String getNoticeTitle() {
            return noticeTitle;
        }


        public boolean getNoticeIsShow() {
            return noticeIsShow;
        }
    }


    public static class VersionConfig {

        private String name = "";
        private int code = 0;
        private String url = "";
        private String remark = "";
        private boolean is_must = true;


        VersionConfig() {
            try {
                JSONObject json = new JSONObject(getData(TAG, "version"));
                name = json.optString("name");
                code = json.optInt("code");
                url = json.optString("url");
                remark = json.optString("remark");
                is_must = json.optBoolean("is_must");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        /**
         * 获取版本名
         */
        public String getName() {
            return name;
        }


        /**
         * 版本号
         */
        public int getCode() {
            return code;
        }


        /**
         * 下载地址
         */
        public String getUrl() {
            return url;
        }


        /**
         * 升级说明
         */
        public String getRemark() {
            return remark;
        }


        /**
         * 是否强制升级
         */
        public boolean isMust() {
            return is_must;
        }
    }

    public static class CustomerConfig {

        private String qq = "";
        private String wechat = "";
        private String alipay = "";
        private static CustomerConfig init = null;


        public CustomerConfig() {
            try {
                JSONObject json = new JSONObject(getData(TAG, "customer"));
                qq = json.optString("qq");
                wechat = json.optString("wechat");
                alipay = json.optString("alipay");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        public static CustomerConfig init() {
            if (init == null) {
                init = new CustomerConfig();
            }
            return init;
        }


        /**
         * 获取QQ
         */
        public String getQq() {
            return qq;
        }


        /**
         * 获取微信
         */
        public String getWechat() {
            return wechat;
        }


        /**
         * 获取支付宝
         */
        public String getAlipay() {
            return alipay;
        }
    }

    public static class FeedbackConfig {

        private boolean verify_status = true;
        private String verify_url = "";
        private String content_placeholder = "";
        private String tel_placeholder = "";


        FeedbackConfig() {
            try {
                JSONObject json = new JSONObject(getData(TAG, "feedback"));
                verify_status = json.optBoolean("verify_status");
                verify_url = json.optString("verify_url");
                content_placeholder = json.optString("content_placeholder");
                tel_placeholder = json.optString("tel_placeholder");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        //是否启用图像验证码
        public boolean getVerifyStatus() {
            return verify_status;
        }


        //图形验证码网址
        public String getVerifyUrl() {
            return verify_url;
        }


        //反馈内容输入框提示内容
        public String getContentPlaceholder() {
            return content_placeholder;
        }


        //联系方式输入框提示内容
        public String getTelPlaceholder() {
            return tel_placeholder;
        }
    }

    public static class BuyConfig {

        private String card_group_message = "";
        private String card_group_url = "";
        private String card_unlock_message = "";
        private String card_unlock_url = "";


        public BuyConfig() {
            try {
                JSONObject json = new JSONObject(getData(TAG, "buy"));
                card_group_message = json.getString("card_group_message");
                card_group_url = json.optString("card_group_url");
                card_unlock_message = json.optString("card_unlock_message");
                card_unlock_url = json.optString("card_unlock_url");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        //获取购买充值卡步骤提示文案
        public String getCardGroupMessage() {
            return card_group_message;
        }


        //获取购买充值卡链接
        public String getCardGroupUrl() {
            return card_group_url;
        }


        //获取购买解锁码步骤提示文案
        public String getCardUnlockMessage() {
            return card_unlock_message;
        }


        //获取购买解锁码链接
        public String getCardUnlockUrl() {
            return card_unlock_url;
        }
    }

    public static class LoginConfig {

        private boolean verify_status;
        private String verify_url;


        LoginConfig() {
            try {
                JSONObject json = new JSONObject(getData(TAG, "login"));
                verify_status = json.optBoolean("verify_status");
                verify_url = json.optString("verify_url");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        //是否启用图形验证码
        public boolean isVerifyStatus() {
            return verify_status;
        }


        //图形验证码网址
        public String getVerifyUrl() {
            return verify_url;
        }
    }

    public static class FindPwdConfig {


        private boolean verify_status;
        private String verify_url;


        FindPwdConfig() {
            try {
                JSONObject json = new JSONObject(getData(TAG, "find_pwd"));
                verify_status = json.getBoolean("verify_status");
                verify_url = json.optString("verify_url");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        //是否需要启用图形验证码
        public boolean isVerifyStatus() {
            return verify_status;
        }


        //图形验证码网址
        public String getVerifyUrl() {
            return verify_url;
        }
    }

    public static class RegisterConfig {

        private String qqTip = "";
        private boolean qqStatus = true;
        private String qqName = "";
        private String qqToken = "";
        private List<RegisterConfigQQList> qqList = new ArrayList<>();

        private String emailName = "";
        private boolean emailStatus = true;
        private String emailTip = "";
        private boolean emailVerifyStatus = true;
        private String emailVerifyUrl = "";


        RegisterConfig() {
            try {
                JSONObject json = new JSONObject(getData(TAG, "register"));
                JSONObject email = json.optJSONObject("email");
                emailName = email.optString("name");
                emailStatus = email.optBoolean("status");
                emailTip = email.optString("message");
                emailVerifyStatus = email.optBoolean("verify_status");
                emailVerifyUrl = email.optString("verify_url");


                JSONObject qqRobot = json.optJSONObject("qq_robot");
                qqName = qqRobot.optString("name");
                qqStatus = qqRobot.optBoolean("status");
                qqTip = qqRobot.optString("message");
                qqToken = qqRobot.optString("token");


                JSONArray list = qqRobot.optJSONArray("list");
                int len = list.length();
                RegisterConfigQQList qq = new RegisterConfigQQList();
                for (int i = 0; i < len; i++) {
                    JSONObject job = list.optJSONObject(i);
                    qq.setName(job.optString("name"));
                    qq.setQq(job.optString("qq"));
                    qqList.add(qq);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        public String getEmailName() {
            return emailName;
        }


        public boolean getEmailStatus() {
            return emailStatus;
        }


        public String getEmailTip() {
            return emailTip;
        }


        public boolean getEmailVerifyStatus() {
            return emailVerifyStatus;
        }


        public String getEmailVerifyUrl() {
            return emailVerifyUrl;
        }


        public String getQQTip() {
            return qqTip;
        }


        public boolean getQQStatus() {
            return qqStatus;
        }


        public String getQQName() {
            return qqName;
        }


        public String getQQToken() {
            return qqToken;
        }


        public List<RegisterConfigQQList> getQQList() {
            return qqList;
        }
    }

    public static class RegisterConfigQQList {

        private String name;
        private String qq;


        public String getName() {
            return name;
        }


        public void setName(String name) {
            this.name = name;
        }


        public String getQq() {
            return qq;
        }


        public void setQq(String qq) {
            this.qq = qq;
        }
    }

    public static class TakeConfig {

        private String testLeadTip = "";
        private String testEmptyAuthTip = "";
        private String testTel = "4008006666";
        private int testMaxSecond = 40;
        private int testMinSecond = 6;
        private String test_fail_tip = "";
        private String flow_fail_tip = "";
        private JSONObject test = new JSONObject();


        TakeConfig() {
            try {
                JSONObject json = new JSONObject(getData(TAG, "take"));
                test = json.optJSONObject("test");
                testTel = test.optString("tel");
                testMaxSecond = test.optInt("max_second");
                testMinSecond = test.optInt("min_second");
                test_fail_tip = test.optString("fail_tip");
                flow_fail_tip = test.optString("flow_fail_tip");
                testLeadTip = test.optString("lead_tip");
                testEmptyAuthTip = test.optString("empty_auth_tip");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        /**
         * 获取当无任务列队的时候间隔访问接任务接口的秒数
         */
        public int getGetQueueInterval() {
        //    return getNewTakeConfig("get_queue_interval", 15) * 10000;
            return getNewTakeConfig("get_queue_interval", 15) * 1000;
        }


        /**
         * 获取从挂断电话后到下次开始拨打电话的间隔秒数
         */
        public int getHangUpWaitInterval() {
            return getNewTakeConfig("hang_up_wait_interval", 2);
        }

        /**
         * 获取开始呼叫到挂断电话的持续时间秒数
         */
        public int getCallContinueInterval() {
            return getNewTakeConfig("call_continue_interval", 15);
        }

        /**
         * 获取联系N个列队被非主动挂断就算失败
         */
        public int getCallContinuityQueueError() {
            return getNewTakeConfig("call_continuity_queue_error", 6);
        }

        /**
         * 获取呼叫剩余N秒的时候进行检测是否被挂断
         */
        public int getCallContinueLastSecond() {
            return getNewTakeConfig("call_continue_last_second", 2);
        }

        /**
         * 获取呼叫列队中剩余多少条未呼叫的记录则开始重新请求新的列队进来
         */
        public int getQueueSurplus() {
            return getNewTakeConfig("queue_surplus", 0);
        }

        /**
         * 获取新的配置
         */
        private int getNewTakeConfig(String name, int defVal) {
            try {
                JSONObject json = new JSONObject(getData(TAG, "take"));
                return json.optInt(name);
            } catch (JSONException e) {
                e.printStackTrace();
                return defVal;
            }
        }

        /**
         * 获取测试欠费提示
         */
        public String getTestFailTip() {
            return test_fail_tip;
        }
        /**
         * GPRS 测试
         */
        public String getGPRSFailTip() {
            return flow_fail_tip;
        }


        public String getTestTel() {
            try {
                JSONObject json = new JSONObject(getData(TAG, "take"));
                JSONObject test = json.optJSONObject("test");
                return test.optString("tel");
            } catch (JSONException e) {
                return testTel;
            }
        }


        public int getTestMaxSecond() {
            try {
                JSONObject json = new JSONObject(getData(TAG, "take"));
                JSONObject test = json.optJSONObject("test");
                return test.optInt("max_second");
            } catch (JSONException e) {
                return testMaxSecond;
            }
        }


        public int getTestMinSecond() {
            try {
                JSONObject json = new JSONObject(getData(TAG, "take"));
                JSONObject test = json.optJSONObject("test");
                return test.optInt("min_second");
            } catch (JSONException e) {
                return testMinSecond;
            }
        }


        public String getTestEmptyAuthTip() {
            return testEmptyAuthTip;
        }


        public String getTestLeadTip() {
            return testLeadTip;
        }
    }

    public static class CallConfig {

        private List<CallConfigList> showTypeList = new ArrayList<CallConfigList>();
        private List<CallConfigList> callTypeList = new ArrayList<CallConfigList>();


        CallConfig() {
            JSONObject json = null;
            try {
                json = new JSONObject(getData(TAG, "call"));
                JSONArray showType = json.optJSONArray("show_type");
                JSONArray callType = json.optJSONArray("call_type");
                for (int i = 0; i < showType.length(); i++) {
                    JSONObject data = showType.optJSONObject(i);
                    CallConfigList obj = new CallConfigList();
                    obj.setName(data.optString("name"));
                    obj.setId(data.optString("id"));
                    showTypeList.add(obj);
                }

                for (int i = 0; i < callType.length(); i++) {
                    JSONObject data = callType.optJSONObject(i);
                    CallConfigList obj = new CallConfigList();
                    obj.setName(data.optString("name"));
                    obj.setId(data.optString("id"));
                    callTypeList.add(obj);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        public List<CallConfigList> getShowTypeList() {
            return showTypeList;
        }


        public List<CallConfigList> getCallTypeList() {
            return callTypeList;
        }
    }

    public static class CallConfigList {

        private String name;
        private String id;


        public String getName() {
            return name;
        }


        public void setName(String name) {
            this.name = name;
        }


        public String getId() {
            return id;
        }


        public void setId(String id) {
            this.id = id;
        }
    }
}
