package cn.yunhu.api;

import android.content.Context;

import org.json.JSONObject;

import cn.yunhu.http.Rest.RestCallback;
import cn.yunhu.utils.Constants;

/**
 * 注册接口
 */
public class RegisterApi extends BaseApi {

    public final static String TAG = "RegisterApi";

    /**
     * 构造
     */
    public RegisterApi(Context context) {
        super(context);
        setApiPath(Constants.API_PATH_REGISTER);
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
        stringParams.put("password", password);
    }


    /**
     * 设置验证码
     */
    public void setRegisterCode(String register_code) {
        stringParams.put("register_code", register_code);
    }

    /**
     * 设置QQ号
     * @param qq
     */
    public void setQQ(String qq){
        stringParams.put("qq",qq);
    }

    /**
     * 设置机器码
     * @param
     */
    public void setMachineCode(String machine_code){
        stringParams.put("machine_code",machine_code);
    }

    @Override
    public void request(final RestCallback callback) {
        super.request(new RestCallback() {

            @Override
            public void onSuccess(String message, JSONObject data) {
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
    public static String getUsername() {
        return getData(TAG, "username");
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
}
