package cn.yunhu.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.allenliu.versionchecklib.core.AllenChecker;
import com.allenliu.versionchecklib.core.VersionDialogActivity;
import com.allenliu.versionchecklib.core.VersionParams;

import org.json.JSONObject;


import java.util.Timer;
import java.util.TimerTask;

import cn.yunhu.R;
import cn.yunhu.BaseApplication;
import cn.yunhu.api.AppInfoApi;
import cn.yunhu.api.LoginApi;
import cn.yunhu.dialog.ModelDialog;
import cn.yunhu.http.Rest;
import cn.yunhu.service.DemoService;
import cn.yunhu.utils.Common;
import cn.yunhu.utils.ToastUitil;

/**
 * 登录
 */
public class LoginActivity extends BaseActivity {

    private AppInfoApi.LoginConfig   loginConfig;
    private ImageView                verifyImage;
    private LinearLayout             verifyBox;
    private TextView                 feedbackBtn;
    private TextView                 findPwdBtn;
    private TextView                 registerBtn;
    private Button                   loginBtn;
    private EditText                 account;
    private EditText                 password;
    private EditText                 verify;
    private Button                   buyOnLock;
    private AppInfoApi.UserConfig    userConfig;
    private AppInfoApi.VersionConfig versionConfig;
    private Button                   buyBtn;
    private LinearLayout             passwordLL;
    private View                     passwordview;
    private LinearLayout             camiLL;
    private View                     camiview;
    private TextView                 passwordtv;


    private static boolean isPassword = true;
    private static boolean isCami     = false;


    @Override
    protected int getLayoutContent() {
        return R.layout.activity_login;
    }


    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        setBackToApp();
        setDisabledHeaderLeft(true);
        setHeaderTitle("登录");

        userConfig = AppInfoApi.getUserConfig();

        versionConfig = AppInfoApi.getVersionConfig();

        // 反馈
        feedbackBtn = (TextView) findViewById(R.id.feedbackBtn);
        feedbackBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(FeedbackActivity.class);
            }
        });

        // 找回密码
        findPwdBtn = (TextView) findViewById(R.id.findPwdBtn);
        findPwdBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(FindPwdActivity.class);
            }
        });

        // 注册
        registerBtn = (TextView) findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(RegisterActivity.class);
            }
        });


        // 设置验证码
        loginConfig = AppInfoApi.getLoginConfig();
        verifyImage = (ImageView) findViewById(R.id.verifyImage);
        verifyBox = (LinearLayout) findViewById(R.id.verifyBox);
        Common.setVerifyImageUrl(verifyImage, Common.getRandUrl(loginConfig.getVerifyUrl()));
        verifyImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Common.setVerifyImageUrl(verifyImage, Common.getRandUrl(loginConfig.getVerifyUrl()));
            }
        });

        camiview = findViewById(R.id.camiview);
        passwordview = findViewById(R.id.passwordview);
        passwordtv = (TextView) findViewById(R.id.passwordtv);

        passwordLL = (LinearLayout) findViewById(R.id.passwordLL);
        passwordLL.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isPassword = true;
                isCami = false;
                // 是否显示验证码
                if (loginConfig.isVerifyStatus()) {
                    verifyBox.setVisibility(View.VISIBLE);
                } else {
                    verifyBox.setVisibility(View.GONE);
                }
                passwordview.setBackgroundColor(Color.parseColor("#08BC05"));
                camiview.setBackgroundColor(Color.parseColor("#00FFFFFF"));
                passwordtv.setText("密    码");
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                password.setHint("请输入密码");

                String oldPassword = LoginApi.getPassword();
                if (!oldPassword.equals("")) {
                    password.setText(oldPassword);
                }
            }
        });

        camiLL = (LinearLayout) findViewById(R.id.camiLL);
        camiLL.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isCami = true;
                isPassword = false;

                camiview.setBackgroundColor(Color.parseColor("#08BC05"));
                passwordview.setBackgroundColor(Color.parseColor("#00FFFFFF"));
                passwordtv.setText("卡    密");
                verifyBox.setVisibility(View.GONE);
                password.setInputType(InputType.TYPE_CLASS_TEXT);
                password.setHint("请输入充值卡号");
                password.setText("");

            }
        });

        // 登录
        loginBtn = (Button) findViewById(R.id.loginBtn);
        account = (EditText) findViewById(R.id.account);
        password = (EditText) findViewById(R.id.password);
        verify = (EditText) findViewById(R.id.verify);
        buyOnLock = (Button) findViewById(R.id.buyOnLock);
        buyBtn = (Button) findViewById(R.id.buyBtn);

        // 购买选项显示切换
        if (userConfig.isLock()) {
            buyOnLock.setVisibility(View.VISIBLE);
            buyBtn.setVisibility(View.GONE);
        } else {
            buyOnLock.setVisibility(View.GONE);
            buyBtn.setVisibility(View.VISIBLE);
        }

        buyBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(BuyRechargeActivity.class);
            }
        });


        buyOnLock.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(BuyUnlockActivity.class);
            }
        });

        account.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Common.copy(nowContext, account.getText().toString().trim(), "账号复制成功");
            }
        });

        // 保存了账户密码
        String oldAccount = LoginApi.getUsername();
        String oldPassword = LoginApi.getPassword();
        if (!oldAccount.equals("")) {
            account.setText(oldAccount);
        } else {
            account.setText(Common.getIMEI(nowContext));
        }


        if (!oldPassword.equals("")) {
            password.setText(oldPassword);
        }

        // 是否显示验证码
        if (loginConfig.isVerifyStatus()) {
            verifyBox.setVisibility(View.VISIBLE);
        } else {
            verifyBox.setVisibility(View.GONE);
        }


        loginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                LoginApi loginApi = new LoginApi(nowContext);
                loginApi.setUsername(account.getText().toString().trim());
                loginApi.setPassword(password.getText().toString().trim());
                loginApi.setVerify(verify.getText().toString().trim());
                loginApi.request(new Rest.RestCallback() {

                    @Override
                    public void onSuccess(String message, JSONObject data) {
                        popup();
                    }


                    @Override
                    public void onError(String message, String field, int code) {
                        ToastUitil.showShort(message);

                        // 切换验证码
                        if (field.equals("verify")) {
                            Common.setVerifyImageUrl(verifyImage, Common.getRandUrl(loginConfig.getVerifyUrl()));
                        }
                    }
                });
            }
        });

    }


    private void popup() {
        LoginApi.AgreementConfig agreementConfig = LoginApi.getAgreementConfig();
        if (agreementConfig.isStatus()) {
            ModelDialog.Builder builder = new ModelDialog.Builder(this);
            builder.setMessage(Html.fromHtml(agreementConfig.getContent()));
            builder.setTitle("协议");
            builder.setConfirmBtn("同意", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    startActivity(HomeActivity.class);

                }
            });

            builder.setCancelBtn("不同意", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    ToastUitil.showLong(nowContext, "您拒绝了该协议，程序即将退出.");

                    // 退出程序
                    final Timer time = new Timer();
                    time.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Looper.prepare();//初始化Looper对象
                            time.cancel();
                            BaseApplication.getApplication().exit();
                            Looper.loop();//启动Looper
                        }
                    }, 2000);
                }
            });

            builder.create().show();
        } else {
            startActivity(HomeActivity.class);
        }
    }


    public static boolean isPassword() {
        return isPassword;
    }


    public static boolean isCami() {
        return isCami;
    }
}


