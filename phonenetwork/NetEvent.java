package cn.yunhu.phonenetwork;

/**
 * Created by Administrator on 2017\12\28 0028.
 */

public interface NetEvent {
    void onNetChange(int netMobile);

    void onWifiChange(int wifistate);
}
