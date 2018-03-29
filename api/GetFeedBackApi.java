package cn.yunhu.api;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.yunhu.bean.FeedBack;
import cn.yunhu.http.Rest.RestCallback;
import cn.yunhu.utils.Constants;

/**
 * 获得反馈列表接口
 */
public class GetFeedBackApi extends BaseApi {

    public final static String TAG = "GetFeedBackApi";

    private static List<FeedBack> answerList = new ArrayList<>();

    /**
     * 构造
     */
    public GetFeedBackApi(Context context) {
        super(context);
        setApiPath(Constants.API_GET_FEED_BACK);
    }

    @Override
    public void request(final RestCallback callback) {
        super.request(new RestCallback() {
            @Override
            public void onSuccess(String message, JSONObject data) {
                Log.e("tag","data.................: "+data);
                JSONArray list = data.optJSONArray("list");

                for (int i=0; i<list.length(); i++){
                    JSONObject jsonObject = list.optJSONObject(i);

                    //保存反馈ID
                    setData(TAG,"id",jsonObject.optString("id"));
                    //保存投诉内容
                    setData(TAG,"say",jsonObject.optString("say"));
                    //保存提交投诉的时间
                    setData(TAG,"say_time",jsonObject.optString("say_time"));
                    //保存是否有图片
                    setData(TAG,"say_have_images",jsonObject.optBoolean("say_have_images"));
                    //保存图片列表
                    setData(TAG,"say_images",jsonObject.optJSONArray("say_images").toString());
                    //保存是否回复
                    setData(TAG,"is_answer",jsonObject.optBoolean("is_answer"));
                    //保存反馈内容
                    setData(TAG,"answer",jsonObject.optString("answer"));
                    //保存回复时间
                    setData(TAG,"answer_time",jsonObject.optString("answer_time"));
                    //保存回复是否有图片
                    setData(TAG,"answer_have_images",jsonObject.optBoolean("answer_have_images"));
                    //保存回复的图片
                    setData(TAG,"answer_images",jsonObject.optJSONArray("answer_images").toString());

                    FeedBack feedBack = new FeedBack();
                    feedBack.setAnswer(jsonObject.optString("answer"));
                    feedBack.setAnswer_images(jsonObject.optJSONArray("answer_images").toString());
                    feedBack.setAnswer_time(jsonObject.optString("answer_time"));
                    feedBack.setId(jsonObject.optString("id"));
                    feedBack.setIs_answer(jsonObject.optBoolean("is_answer"));
                    feedBack.setSay(jsonObject.optString("say"));
                    feedBack.setSay_images(jsonObject.optJSONArray("say_images").toString());
                    feedBack.setSay_time(jsonObject.optString("say_time"));
                    answerList.add(feedBack);
                }

                Gson gson = new Gson();
                //转换成json数据，再保存
                String strJson = gson.toJson(answerList);
                setData(TAG,"answerList",strJson);


                callback.onSuccess(message, data);
            }

            @Override
            public void onError(String message, String field, int code) {
                callback.onError(message, field, code);
            }
        });


    }

    //获取反馈记录ID
    public static String getID(){
        return getData(TAG,"id");
    }

    //获取反馈内容
    public static String getSay(){
        return getData(TAG,"say");
    }
    //获取反馈时间
    public static String getSayTime(){
        return getData(TAG,"say_time");
    }

    //获取反馈是否包含图片
    public static boolean isSayHaveImages(){
        return getBoolean(TAG, "say_have_images");
    }
    //获取反馈图片集合
    public static List<String> getSayImages(){
        List<String> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(getData(TAG,"say_images"));
            for (int i=0; i<jsonArray.length(); i++){
//                JSONObject jsonObject = jsonArray.optJSONObject(i);
//                list.add(jsonObject.toString().trim());
                list.add(String.valueOf(jsonArray.get(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    //获取是否回复
    public static boolean isAnswer(){
        return getBoolean(TAG, "is_answer");
    }
    //获取回复内容

    public static String getAnswer(){
        return getData(TAG,"answer");
    }
    //获取回复时间

    public static String getAnswerTime(){
        return getData(TAG,"answer_time");
    }

    //获取回复是否包含图片
    public static boolean isAnswerHaveImages(){
        return getBoolean(TAG, "answer_have_images");
    }

    //获取回复的图片
    public static List<String> getAnswerImages(){
        List<String> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(getData(TAG,"answer_images"));
            for (int i=0; i<jsonArray.length(); i++){
//                JSONObject jsonObject = jsonArray.optJSONObject(i);
//                list.add(jsonObject.toString().trim());
                list.add(String.valueOf(jsonArray.get(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    //获取回复列表
    public static String getAnswerList(){
        return getData(TAG,"answerList");
    }

}
