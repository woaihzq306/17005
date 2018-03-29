package cn.yunhu.window;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import cn.yunhu.R;
import cn.yunhu.api.AppInfoApi;
import cn.yunhu.utils.Common;

/**
 * 客服弹窗
 */
public class CustomerServiceWindow extends BaseWindow {

    private LinearLayout              wechat;
    private LinearLayout              qq;
    private TextView                  qqText;
    private TextView                  wechatText;
    private TextView                  alipayText;
    private LinearLayout              alipay;
    private AppInfoApi.CustomerConfig customerConfig;


    @Override
    protected int getContentLayout() {
        return R.layout.pupup_window_customer_service;
    }


    @SuppressLint("SetTextI18n")
    @Override
    protected void initDialog() {
        customerConfig = AppInfoApi.getCustomerConfig();

        // QQ
        qq = (LinearLayout) findId(R.id.qq);
        qqText = (TextView) findId(R.id.qqText);
        qqText.setText(customerConfig.getQq());
        qq.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Common.copy(context, customerConfig.getQq(), "复制QQ号成功");
                dismiss();
            }
        });


        // 微信
        wechat = (LinearLayout) findId(R.id.wechat);
        wechatText = (TextView) findId(R.id.wechatText);
        wechatText.setText(customerConfig.getWechat());
        wechat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Common.copy(context, customerConfig.getWechat(), "复制微信成功");
                dismiss();
            }
        });

        // 支付宝
        alipay = (LinearLayout) findId(R.id.alipay);
        alipayText = (TextView) findId(R.id.alipayText);
        alipayText.setText(customerConfig.getAlipay());
        alipay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Common.copy(context, customerConfig.getAlipay(), "复制支付宝成功");
                dismiss();
            }
        });
    }
}
