package cn.yunhu.api;

import android.content.Context;

import org.json.JSONObject;

import cn.yunhu.http.Rest.RestCallback;
import cn.yunhu.utils.Constants;

/**
 * 找回密码接口
 */
public class FindPwdApi extends BaseApi {

    public final static String TAG = "FindPwdApi";


    /**
     * 构造
     */
    public FindPwdApi(Context context) {
        super(context);
        setApiPath(Constants.API_PATH_FIND_PWD);
    }


    /**
     * 设置登录账号
     */
    public void setUsername(String username) {
        stringParams.put("username", username);
    }


    /**
     * 设置新密码
     */
    public void setPassword(String password) {
        stringParams.put("password", password);
    }


    /**
     * 设置验证码
     */
    public void setVerify(String verify) {
        stringParams.put("verify", verify);
    }


    /**
     * 设置找回方式
     */
    public void setType(int type) {
        stringParams.put("type", type + "");
    }


    /**
     * 设置类型值
     */
    public void setTypeValue(String type_value) {
        stringParams.put("type_value", type_value);
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

}
