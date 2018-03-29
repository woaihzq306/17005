package cn.yunhu.api;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.yunhu.http.Rest.RestCallback;
import cn.yunhu.utils.Constants;

/**
 * 上传反馈接口
 */
public class FeedBackApi extends BaseApi {

    public final static String TAG = "FeedBackApi";

    private List<String> sayImages = new ArrayList<>();


    /**
     * 构造
     */
    public FeedBackApi(Context context) {
        super(context);
        setApiPath(Constants.API_UPLOAD_FEED_BACK);
    }


    //设置反馈内容
    public void setSay(String say) {
        stringParams.put("say", say);
    }


    //设置联系方式
    public void setContact(String contact) {
        stringParams.put("contact", contact);
    }


    //设置图片ID
    public void addSayImage(String string) {
        sayImages.add(string);
    }


    //删除图片
    public void removeSayImage(String string) {
        for (int i =  0;i<sayImages.size();i++) {
            if (sayImages.get(i).equals(string)) {
                sayImages.remove(i);
            }
        }
    }


    //设置验证码
    public void setVerify(String verify) {
        stringParams.put("verify", verify);
    }


    @Override
    public void request(final RestCallback callback) {
        for (int i = 0; i < sayImages.size(); i++) {
            stringParams.put("say_images[" + i + "]", sayImages.get(i));
        }


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
