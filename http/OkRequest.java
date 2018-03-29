package cn.yunhu.http;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.builder.OkHttpRequestBuilder;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.builder.PostStringBuilder;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import cn.yunhu.utils.Common;
import cn.yunhu.utils.EncodeUtil;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkRequest {

    public static final String TAG = "OkRequest";
    private Context mContext;

    private        HashMap<String, String> params      = new HashMap<>();
    private        HashMap<String, File>   files       = new HashMap<>();
    private        HashMap<String, String> headers     = new HashMap<>();
    private        String                  contentType = null;
    private        long                    timeout     = 30 * 1000; // 默认超时时间30秒
    private        boolean                 isUpload    = false;
    private        Activity                activity    = null;
    private        boolean                 isDes       = true;
    private static String                  currentUrl  = "";


    public OkRequest(Context context) {
        this.mContext = context;
    }


    /**
     * 设置超时
     * @param timeout 秒
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout * 1000;
    }


    /**
     * 设置字符串Cookie
     */
    public void setCookie(String cookie) {
        addHeader("Cookie", cookie);
    }


    /**
     * 添加请求参数
     */
    public void addParam(String key, Object value) {
        params.put(key, Common.string(String.valueOf(value)));
    }


    public void isDes(boolean isDes) {
        this.isDes = isDes;
    }


    /**
     * 添加上传附件
     */
    public void addFile(String key, File file) {
        files.put(key, file);
    }


    /**
     * 添加请求头
     */
    public void addHeader(String key, String value) {
        headers.put(key, value);
    }


    /**
     * 设置请求头
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }


    public void setActivity(Activity activity) {
        this.activity = activity;
    }


    /**
     * 执行请求
     */
    public RequestCall send(RequestCall requestCall, final OkRequestCallback callback) {
//        LoadPD.show(context,"");
        // 读取超时
        requestCall.readTimeOut(timeout);

        // 链接超时
        requestCall.connTimeOut(timeout);

        // 写超时
        requestCall.writeTimeOut(timeout);

        // 绑定回调事件
        requestCall.execute(new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {
                if (e.getMessage() != null) {
                    Log.e(TAG, e.getMessage());
                }
                if (call.isCanceled()) {
                    callback.onCancelled(id);
                } else {
                    if (!callback.onError(e.getMessage(), id)) {
                        Toast.makeText(mContext, "网络异常，请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                }
            }


            @Override
            public void onResponse(String response, int id) {
                Log.e("返回数据", "[" + currentUrl + "] = " + response);
                try {
                    if (isDes) {
                        callback.onSuccess(EncodeUtil.decode3DES(response), id);
                    } else {
                        callback.onSuccess(response, id);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onError("请求数据异常", id);
                }
            }


            public void onBefore(Request request, int id) {
                callback.onStart(request, id);
            }


            public void onAfter(int id) {
                callback.onComplete(id);
            }


            public void inProgress(float progress, long total, int id) {
                callback.onLoading(progress, total, isUpload, id);
            }
        });

        return requestCall;
    }


    /**
     * 执行POST请求
     * @param url      请求的链接
     * @param callback 回调方法
     */
    public RequestCall post(String url, final OkRequestCallback callback) {

        currentUrl = url;
        PostFormBuilder http = OkHttpUtils.post().url(url);

        // 字段
        if (params != null && !params.isEmpty()) {
            Log.e(TAG, "post: " + params);
            http.params(params);
        }

        commonSetting(http);
        // 遍历file
        if (files != null && !files.isEmpty()) {

            for (String key : files.keySet()) {
                File file = files.get(key);
                http.addFile(key, file.getName(), file);
            }
            isUpload = true;
        }

        return send(http.build(), callback);
    }


    /**
     * 执行GET请求
     * @param url
     * @param callback
     * @return
     */
    public RequestCall get(String url, OkRequestCallback callback) {
        currentUrl = url;
        GetBuilder http = OkHttpUtils.get().url(url);

        // 字段
        if (params != null && !params.isEmpty()) {
            Log.e(TAG, "get: " + params);
            http.params(params);
        }

        commonSetting(http);

        return send(http.build(), callback);
    }


    /**
     * 执行POST字符请求
     * @param url      请求的链接
     * @param data     发送的字符串
     * @param callback 回调方法
     */
    public RequestCall postString(String url, String data, final OkRequestCallback callback) {
        currentUrl = url;
        PostStringBuilder http = OkHttpUtils.postString().url(url);

        commonSetting(http);

        // 提交类型
        if (contentType != null) {
            // todo MediaType
            //http.mediaType(MediaType.parse(contentType));
        }

        // 提交内容
        http.content(data);

        return send(http.build(), callback);
    }


    private void commonSetting(OkHttpRequestBuilder builder) {
        // viewHeader
        if (headers != null && !headers.isEmpty()) {
            builder.headers(headers);
        }

        // tag
        if (activity != null) {
            builder.tag(activity);
        }
    }


    /**
     * 下载
     */
    public void download(String url, final OkRequestCallback callback) {
        final Request request = new Request.Builder().url(url).build();
        Call          call    = new OkHttpClient().newCall(request);
        call.enqueue(new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                if (call.isCanceled()) {
                    callback.onCancelled(0);
                } else {
                    callback.onError(e.getMessage(), 0);
                }
            }


            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onSuccess(response.body().byteStream(), 0);
            }
        });
    }
}
