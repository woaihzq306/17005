package cn.yunhu.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import cn.yunhu.R;
import cn.yunhu.activity.FeedbackActivity;
import cn.yunhu.activity.HomeActivity;
import cn.yunhu.api.AppInfoApi;
import cn.yunhu.api.BuyRechargeApi;
import cn.yunhu.api.LoginApi;
import cn.yunhu.http.Rest;
import cn.yunhu.utils.Common;
import cn.yunhu.utils.ToastUitil;

/**
 * 购买充值卡
 */
public class BuyRechargeFragment extends BaseFragment implements View.OnClickListener {


    private TextView             account;
    private TextView             gotoBuy;
    private EditText             cami;
    private TextView             message;
    private Button               buyBtn;
    private AppInfoApi.BuyConfig buyConfig;
    private TextView feedbackBtn;
    private HomeActivity parentActivity;


    @Override
    protected int getLayoutContent() {
        return R.layout.fragment_buy_recharge;
    }


    @Override
    protected void initFragment(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        parentActivity = (HomeActivity)nowActivity;
        buyConfig = AppInfoApi.getBuyConfig();
        account = (TextView) findId(R.id.account);
        account.setText(LoginApi.getUsername(nowContext));
        account.setOnClickListener(this);
        gotoBuy = (TextView) findId(R.id.goToBuy);
        gotoBuy.setOnClickListener(this);
        cami = (EditText) findId(R.id.cami);
        buyBtn = (Button) findId(R.id.buyBtn);
        buyBtn.setOnClickListener(this);
        message = (TextView) findId(R.id.message);
        feedbackBtn = (TextView) findId(R.id.feedbackBtn);
        feedbackBtn.setOnClickListener(this);
        message.setText(Html.fromHtml(buyConfig.getCardGroupMessage()));
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 获取卡密
            case R.id.goToBuy:
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                String url = buyConfig.getCardGroupUrl();
                Uri content_url = Uri.parse(url);
                intent.setData(content_url);
                startActivity(intent);
                break;

            case R.id.account:
                Common.copy(nowContext, account.getText().toString().trim(), "账号复制成功");
                break;

            // 提交卡密
            case R.id.buyBtn:
                BuyRechargeApi buyAccountApi = new BuyRechargeApi(getActivity());
                buyAccountApi.setCami(cami.getText().toString().trim());
                buyAccountApi.request(new Rest.RestCallback() {

                    @Override
                    public void onSuccess(String message, JSONObject data) {
                        cami.setText("");
                        ToastUitil.showShort(message);
                    }


                    @Override
                    public void onError(String message, String field, int code) {
                        ToastUitil.showShort(message);
                    }
                });
                break;

            // 投诉
            case R.id.feedbackBtn:
                parentActivity.startActivity(FeedbackActivity.class);
                break;
        }
    }
}
