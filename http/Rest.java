package cn.yunhu.http;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 接口请求数据解析器
 * @version $Id: 2017/7/15 上午9:54 Rest.java $
 */
public class Rest {

    public static void parse(String jsonString, RestCallback callback) {
        Log.e("Rest Result", jsonString);
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            String     message    = jsonObject.getString("message");
            if (jsonObject.getBoolean("status")) {
                JSONObject data = jsonObject.getJSONObject("result");
                callback.onSuccess(message, data);
            } else {
                String field = "";
                int    code  = jsonObject.getInt("code");
                if (code == 4010000) {
                    field = jsonObject.getString("name");
                }
                callback.onError(message, field, code);
            }
        } catch (JSONException e) {
            callback.onError("数据解析错误", "", 0);
        }
    }


    public abstract static class RestCallback {

        /**
         * 接口返回成功
         * @param message 成功的消息
         * @param data    成功的数据
         */
        public abstract void onSuccess(String message, JSONObject data);


        /**
         * 接口返回失败
         * @param message 失败的消息
         * @param field   错误的字段
         * @param code    错误代码
         */
        public abstract void onError(String message, String field, int code);
    }
}
