package cn.yunhu.api;

import android.content.Context;

import org.json.JSONObject;

import cn.yunhu.http.Rest.RestCallback;
import cn.yunhu.utils.Constants;

/**
 * 用邮箱获取注册码接口
 */
public class registerCodeByEmailApi extends BaseApi {

    public final static String TAG = "registerCodeByEmailApi";

    /**
     * 构造
     */
    public registerCodeByEmailApi(Context context) {
        super(context);
        setApiPath(Constants.API_PATH_REGISTER_CODE_BY_EMAIL);
    }

    //设置邮箱地址
    public void setemail(String emal){
        stringParams.put("email",emal);
    }
    //设置验证码
    public void setverify(String verify){
        stringParams.put("verify",verify);
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
