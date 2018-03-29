package cn.yunhu.api;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import cn.yunhu.http.Rest.RestCallback;
import cn.yunhu.utils.Constants;
import cn.yunhu.utils.ToastUitil;

/**
 * 获取会员信息
 */
public class GetUserInfoApi extends BaseApi {

    public final static String TAG = "GetUserInfoApi";


    /**
     * 构造
     */
    public GetUserInfoApi(Context context) {
        super(context);
        setApiPath(Constants.API_PATH_ACCOUNT_GET_INFO);
    }


    public void request(final GetUserInfoCallBack callback) {
        super.request(new RestCallback() {

            @Override
            public void onSuccess(String message, JSONObject data) {

                callback.callback(new UserInfo(data));
            }


            @Override
            public void onError(String message, String field, int code) {
                ToastUitil.showShort(message);
            }
        });
    }


    abstract public static class GetUserInfoCallBack {

        protected abstract void callback(UserInfo info);
    }


    public static class UserInfo {

        private  String expireName;
        private JSONObject user;
        private String     id;
        private boolean    isExpire;
        private boolean    isLong;
        private String     groupName;
        private boolean    isTrial;
        private String     username;
        private int max_call_total;

        UserInfo(JSONObject data) {
            Log.e("tag","会员信息："+data);
            user = data.optJSONObject("user");
            id = user.optString("id");
            username = user.optString("username");
            isExpire = user.optBoolean("is_expire");
            isLong = user.optBoolean("is_long");
            groupName = user.optString("group_name");
            expireName = user.optString("expire_name");
            isTrial = user.optBoolean("is_trial");
            max_call_total = user.optInt("max_call_total");
        }


        public JSONObject getUser() {
            return user;
        }


        public String getId() {
            return id;
        }


        public boolean isExpire() {
            return isExpire;
        }


        public boolean isLong() {
            return isLong;
        }


        public String getGroupName() {
            return groupName;
        }


        public boolean isTrial() {
            return isTrial;
        }


        public String getUsername() {
            return username;
        }


        public String getExpireName() {
            return expireName;
        }

        public int getMaxCallTotal(){
            return max_call_total;
        }
    }
}
