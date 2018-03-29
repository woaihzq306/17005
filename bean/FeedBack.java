package cn.yunhu.bean;


import java.io.Serializable;

/**
 * Created by Administrator on 2017\10\27 0027.
 */

public class FeedBack implements Serializable{

    private String id;
    private String say;
    private String say_time;
    private String say_images;
    private String answer;
    private String answer_images;
    private String answer_time;
    private boolean is_answer;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSay() {
        return say;
    }

    public void setSay(String say) {
        this.say = say;
    }

    public String getSay_time() {
        return say_time;
    }

    public void setSay_time(String say_time) {
        this.say_time = say_time;
    }

    public String getSay_images() {
        return say_images;
    }

    public void setSay_images(String say_images) {
        this.say_images = say_images;
    }

    public String getAnswer_images() {
        return answer_images;
    }

    public void setAnswer_images(String answer_images) {
        this.answer_images = answer_images;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswer_time() {
        return answer_time;
    }

    public void setAnswer_time(String answer_time) {
        this.answer_time = answer_time;
    }

    public boolean isIs_answer() {
        return is_answer;
    }

    public void setIs_answer(boolean is_answer) {
        this.is_answer = is_answer;
    }
}
