package cn.yunhu.api;

import android.content.Context;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.yunhu.http.Rest;
import cn.yunhu.utils.Common;
import cn.yunhu.utils.Constants;
import cn.yunhu.utils.SIMUtil;
import cn.yunhu.utils.ToastUitil;


/**
 * 接呼叫任务接口
 */
public class CallTakeApi extends BaseApi {

    public final static String TAG = "CallTakeApi";


    /**
     * 构造
     */
    public CallTakeApi(Context context) {
        super(context);
        setApiPath(Constants.API_CALL_TAKE);
        setParseError(false);
    }


    /**
     * 设置呼叫完毕的手机号列队
     */
    public CallTakeApi setEndCallList(List<String> list) {
        int    i        = 0;
        String callList = "";
        String line     = "";
        for (; i < list.size(); i++) {
            callList += line + list.get(i);
            line = ",";
        }
        stringParams.put("end_call_list", callList);

        return this;
    }


    /**
     * 设置请求类型
     */
    public CallTakeApi setType(int type) {
        stringParams.put("type", type + "");

        return this;
    }


    /**
     * 请求列队
     */
    public void request(Rest.RestCallback callback) {
        try {
            stringParams.put("sim", SIMUtil.getImsi(context));
        } catch (Exception e) {
            callback.onError("抱歉，无法获取您的SIM卡信息", "", 0);
            return;
        }

        // 执行请求
        super.request(callback);
    }
}
