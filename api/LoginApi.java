package cn.yunhu.api;

import android.content.Context;


import org.json.JSONException;
import org.json.JSONObject;

import cn.yunhu.activity.LoginActivity;
import cn.yunhu.http.Rest.RestCallback;
import cn.yunhu.utils.Common;
import cn.yunhu.utils.Constants;

/**
 * 登录接口
 */
public class LoginApi extends BaseApi {

    public final static String TAG = "LoginApi";


    /**
     * 构造
     */
    public LoginApi(Context context) {
        super(context);
        setApiPath(Constants.API_PATH_LOGIN);
    }


    /**
     * 设置登录账号
     */
    public void setUsername(String username) {
        stringParams.put("username", username);
    }


    /**
     * 设置登录密码
     */
    public void setPassword(String password) {
        if (LoginActivity.isPassword()) {
            stringParams.put("password", password);
        }

        if (LoginActivity.isCami()) {
            stringParams.put("cami", password);
        }
    }


    /**
     * 设置验证码
     */
    public void setVerify(String verify) {
        stringParams.put("verify", verify);
    }


    @Override
    public void request(final RestCallback callback) {
        super.request(new RestCallback() {

            @Override
            public void onSuccess(String message, JSONObject data) {
                setData(TAG, "user_token", data.optString("user_token"));
                setData(TAG, "agreement", data.optJSONObject("agreement").toString());
                setData(TAG, "username", stringParams.get("username"));
                setData(TAG, "password", stringParams.get("password"));
                callback.onSuccess(message, data);
            }


            @Override
            public void onError(String message, String field, int code) {
                callback.onError(message, field, code);
            }
        });
    }


    /**
     * 获取登录密钥
     */
    public static String getUserToken() {
        return getData(TAG, "user_token");
    }


    /**
     * 获取登录用户名
     */
    public static String getUsername(Context context) {
        String username = Common.string(getData(TAG, "username"));
        if (username.equals("") && context != null) {
            return Common.getIMEI(context);
        }

        return username;
    }


    public static String getUsername() {
        return getUsername(null);
    }


    /**
     * 获取登录密码
     */
    public static String getPassword() {
        return getData(TAG, "password");
    }


    /**
     * 执行退出登录
     */
    public static void outLogin() {
        setData(TAG, "user_token", "");
    }


    public static AgreementConfig getAgreementConfig() {
        return new AgreementConfig();
    }


    /**
     * 获取协议信息
     */
    public static class AgreementConfig {

        private boolean status  = false;
        private String  content = "";


        public AgreementConfig() {
            try {
                JSONObject json = new JSONObject(getData(TAG, "agreement"));

                status = json.getBoolean("status");
                content = json.getString("content");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        //获取协议是否显示
        public boolean isStatus() {
            return status;
        }


        //获取协议内容
        public String getContent() {
            return content;
        }
    }
}
