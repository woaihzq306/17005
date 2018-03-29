package cn.yunhu.api;

import android.content.Context;

import org.json.JSONObject;

import cn.yunhu.http.Rest.RestCallback;
import cn.yunhu.utils.Constants;

/**
 * 停止呼叫接口
 */
public class CallStopApi extends BaseApi {

    public final static String TAG = "CallStopApi";


    /**
     * 构造
     */
    public CallStopApi(Context context) {
        super(context);
        setApiPath(Constants.API_CALL_STOP);
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
