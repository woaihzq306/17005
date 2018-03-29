package cn.yunhu.api;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import cn.yunhu.http.Rest;
import cn.yunhu.http.Rest.RestCallback;
import cn.yunhu.utils.Constants;

/**
 * 购买账号接口
 */
public class BuyRechargeApi extends BaseApi {

    public final static String TAG = "BuyRechargeApi";


    /**
     * 构造
     */
    public BuyRechargeApi(Context context) {
        super(context);
        setApiPath(Constants.API_BUY_ACCOUNT);
    }


    //设置卡密
    public void setCami(String number) {
        stringParams.put("cami", number);
    }


    @Override
    public void request(RestCallback callback) {
        stringParams.put("username", LoginApi.getUsername(context));
        super.request(callback);
    }
}
