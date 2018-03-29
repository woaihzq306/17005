package cn.yunhu.api;

import android.content.Context;


import java.util.List;

import cn.yunhu.http.Rest.RestCallback;
import cn.yunhu.utils.Constants;

/**
 * 提交呼叫接口
 */
public class CallSubmitApi extends BaseApi {

    public final static String TAG = "CallSubmitApi";


    /**
     * 构造
     */
    public CallSubmitApi(Context context) {
        super(context);
        setApiPath(Constants.API_CALL_SUBMIT);
    }


    public void setPhoneList(List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            stringParams.put("phone[" + i + "]", list.get(i));
        }
    }


    //设置显示号码模式
    public void setShowType(String showType) {
        stringParams.put("show_type", showType);
    }


    //设置挂断模式
    public void setCallType(String callType) {
        stringParams.put("call_type", callType);
    }


    @Override
    public void request(final RestCallback callback) {
        super.request(callback);
    }
}
