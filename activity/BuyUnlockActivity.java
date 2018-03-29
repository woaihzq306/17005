package cn.yunhu.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;


import cn.yunhu.R;
import cn.yunhu.api.AppInfoApi;
import cn.yunhu.api.BuyUnLockApi;
import cn.yunhu.api.LoginApi;
import cn.yunhu.http.Rest;
import cn.yunhu.utils.Common;
import cn.yunhu.utils.ToastUitil;

/**
 * 购买解锁码
 */
public class BuyUnlockActivity extends BaseActivity implements View.OnClickListener {

    private TextView             account;
    private Button               buyBtn;
    private TextView             gotoBuy;
    private AppInfoApi.BuyConfig buyConfig;
    private EditText             cami;
    private TextView             message;
    private TextView             feedbackBtn;


    @Override
    protected int getLayoutContent() {
        return R.layout.activity_buy_unlock;
    }


    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        setHeaderTitle("购买解锁码");

        buyConfig = AppInfoApi.getBuyConfig();
        account = (TextView) findViewById(R.id.account);
        account.setText(LoginApi.getUsername(nowContext));
        account.setOnClickListener(this);
        gotoBuy = (TextView) findViewById(R.id.goToBuy);
        gotoBuy.setOnClickListener(this);
        cami = (EditText) findViewById(R.id.cami);
        buyBtn = (Button) findViewById(R.id.buyBtn);
        buyBtn.setOnClickListener(this);
        message = (TextView) findViewById(R.id.message);
        message.setText(Html.fromHtml(buyConfig.getCardUnlockMessage()));
        feedbackBtn = (TextView) findViewById(R.id.feedbackBtn);
        feedbackBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 获取卡密
            case R.id.goToBuy:
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                String url = buyConfig.getCardUnlockUrl();
                Uri content_url = Uri.parse(url);
                intent.setData(content_url);
            //    startActivity(intent);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
                break;

            case R.id.account:
                Common.copy(nowContext, account.getText().toString().trim(), "账号复制成功");
                break;


            // 提交卡密
            case R.id.buyBtn:
                BuyUnLockApi buyAccountApi = new BuyUnLockApi(nowContext);
                buyAccountApi.setCami(cami.getText().toString().trim());
                buyAccountApi.request(new Rest.RestCallback() {

                    @Override
                    public void onSuccess(String message, JSONObject data) {
                        finish();
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
                startActivity(FeedbackActivity.class);
                break;
        }
    }
}


