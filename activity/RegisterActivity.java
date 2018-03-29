package cn.yunhu.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import cn.yunhu.R;
import cn.yunhu.api.RegisterApi;
import cn.yunhu.http.Rest;
import cn.yunhu.utils.Common;
import cn.yunhu.utils.DeviceUuidFactory;
import cn.yunhu.utils.ToastUitil;
import cn.yunhu.window.SendRegisterCodeWindow;


/**
 * 注册
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private TextView getRegisterCodeBtn;
    private TextView loginBtn;
    private EditText account;
    private EditText password;
    private EditText registerCode;
    private EditText qq;
    private Button   registerBtn;


    @Override
    protected int getLayoutContent() {
        return R.layout.activity_register;
    }


    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        setHeaderTitle("用户注册");

        getRegisterCodeBtn = (TextView) findViewById(R.id.getRegisterCodeBtn);
        getRegisterCodeBtn.setOnClickListener(this);


        loginBtn = (TextView) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);

        account = (EditText) findViewById(R.id.account);
        account.setOnClickListener(this);
        password = (EditText) findViewById(R.id.password);
        qq = (EditText) findViewById(R.id.qq);
        registerCode = (EditText) findViewById(R.id.registerCode);


        registerBtn = (Button) findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(this);
        account.setText(Common.getIMEI(nowContext));
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 获取注册码
            case R.id.getRegisterCodeBtn:
                SendRegisterCodeWindow sendRegisterCodeWindow = new SendRegisterCodeWindow();
                sendRegisterCodeWindow.setContext(nowContext);
                sendRegisterCodeWindow.show(getSupportFragmentManager(), "SendRegisterCodeWindow");
                break;

            case R.id.account:
                Common.copy(nowContext, account.getText().toString().trim(), "账号复制成功");
                break;


            // 回登录
            case R.id.loginBtn:
                finish();
                break;

            // 注册
            case R.id.registerBtn:
                RegisterApi registerApi = new RegisterApi(nowContext);
                registerApi.setUsername(account.getText().toString().trim());
                registerApi.setPassword(password.getText().toString().trim());
                registerApi.setRegisterCode(registerCode.getText().toString().trim());
                registerApi.setQQ(qq.getText().toString().trim());
                registerApi.setMachineCode(new DeviceUuidFactory(nowContext).getDeviceUuid().toString().trim());
                registerApi.request(new Rest.RestCallback() {

                    @Override
                    public void onSuccess(String message, JSONObject data) {
                        ToastUitil.showShort(message);
                        finish();
                    }


                    @Override
                    public void onError(String message, String field, int code) {
                        ToastUitil.showShort(message);
                    }
                });
                break;

        }
    }
}
