package cn.yunhu.api;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;

import cn.yunhu.http.Rest.RestCallback;
import cn.yunhu.utils.Constants;

/**
 * 上传图片接口
 */
public class UploadPictureApi extends BaseApi {

    public final static String TAG = "UploadPictureApi";


    /**
     * 构造
     */
    public UploadPictureApi(Context context) {
        super(context);
        setApiPath(Constants.API_UPLOAD_PICTURE);
    }


    //设置上传图片
    public void setImage(File file) {
        Log.e("tag","file..............: "+file.getAbsolutePath());
        fileParams.put("upload", file);
    }


    @Override
    public void request(final RestCallback callback) {
        super.request(new RestCallback() {

            @Override
            public void onSuccess(String message, JSONObject data) {
                Log.e("tag", "data.................: " + data);
                callback.onSuccess(message, data);
            }


            @Override
            public void onError(String message, String field, int code) {
                callback.onError(message, field, code);
            }
        });

    }
}
