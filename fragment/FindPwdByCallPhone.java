package cn.yunhu.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import cn.yunhu.activity.FindPwdActivity;
import cn.yunhu.activity.LoginActivity;
import cn.yunhu.api.AppInfoApi;
import cn.yunhu.api.LoginApi;
import cn.yunhu.api.FindPwdApi;
import cn.yunhu.http.Rest;
import cn.yunhu.R;
import cn.yunhu.utils.Common;
import cn.yunhu.utils.ToastUitil;

/**
 * 用呼叫过的手机号找回密码
 */
public class FindPwdByCallPhone extends BaseFragment {

    private Button                   findPwdBtn;
    private TextView                 loginBtn;
    private EditText                 newPassword;
    private EditText                 phone;
    private LinearLayout             verifyBox;
    private ImageView                verifyImage;
    private EditText                 verify;
    private FindPwdActivity          activity;
    private AppInfoApi.FindPwdConfig findPwdConfig;


    @Override
    protected int getLayoutContent() {
        return R.layout.fragment_find_pwd_by_call_phone;
    }


    @Override
    protected void initFragment(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        findPwdBtn = (Button) findId(R.id.findPwdBtn);
        loginBtn = (TextView) findId(R.id.loginBtn);
        newPassword = (EditText) findId(R.id.password);
        phone = (EditText) findId(R.id.phone);
        verifyBox = (LinearLayout) findId(R.id.verifyBox);
        verifyImage = (ImageView) findId(R.id.verifyImage);
        verify = (EditText) findId(R.id.verify);
        activity = (FindPwdActivity) getActivity();
        findPwdConfig = AppInfoApi.getFindPwdConfig();


        // 验证码设置
        Common.setVerifyImageUrl(verifyImage, Common.getRandUrl(findPwdConfig.getVerifyUrl()));

        // 切换验证码
        verifyImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Common.setVerifyImageUrl(verifyImage, Common.getRandUrl(findPwdConfig.getVerifyUrl()));
            }
        });

        if (findPwdConfig.isVerifyStatus()) {
            verifyBox.setVisibility(View.VISIBLE);
        } else {
            verifyBox.setVisibility(View.GONE);
        }


        // 返回登录
        loginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                activity.goTopActivity(LoginActivity.class);
            }
        });


        // 找回密码
        findPwdBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FindPwdApi retrievePasswordApi = new FindPwdApi(getActivity());
                retrievePasswordApi.setUsername(LoginApi.getUsername(nowContext));
                retrievePasswordApi.setPassword(newPassword.getText().toString().trim());
                retrievePasswordApi.setVerify(verify.getText().toString().trim());
                retrievePasswordApi.setTypeValue(phone.getText().toString().trim());
                retrievePasswordApi.setType(1);
                retrievePasswordApi.request(new Rest.RestCallback() {

                    @Override
                    public void onSuccess(String message, JSONObject data) {
                        activity.goTopActivity(LoginActivity.class);

                        ToastUitil.showShort(message);
                    }


                    @Override
                    public void onError(String message, String field, int code) {
                        if (field.equals("verify")) {
                            Common.setVerifyImageUrl(verifyImage, Common.getRandUrl(findPwdConfig.getVerifyUrl()));
                        }
                        ToastUitil.showShort(message);
                    }
                });

            }
        });
    }


}
