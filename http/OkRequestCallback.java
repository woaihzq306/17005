package cn.yunhu.http;

import java.io.InputStream;

import okhttp3.Request;

public abstract class OkRequestCallback {

    /**
     * 开始请求
     */
    public void onStart(Request request, int id) {
    }


    /**
     * 请求被取消
     */
    public void onCancelled(int id) {
    }


    /**
     * 请求中
     */
    public void onLoading(float progress, long total, boolean isUploading, int id) {
    }


    /**
     * 请求成功
     */
    public void onSuccess(String result, int id) {

    }


    /**
     * 请求成功
     */
    public void onSuccess(InputStream result, int id) {

    }


    /**
     * 请求完成
     */
    public void onComplete(int id) {

    }


    /**
     * 请求失败
     * @return true 接管执行，false回弹出错误提示
     */
    public boolean onError(String message, int id) {
        return false;
    }
}
