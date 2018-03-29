package cn.yunhu.window;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.yunhu.R;
import cn.yunhu.adapter.CommonAdapter;
import cn.yunhu.adapter.ViewHolder;
import cn.yunhu.api.AppInfoApi;
import cn.yunhu.api.AppInfoApi.RegisterConfig;
import cn.yunhu.api.registerCodeByEmailApi;
import cn.yunhu.http.Rest;
import cn.yunhu.utils.Common;
import cn.yunhu.utils.PagerSlidingTabStrip;
import cn.yunhu.utils.ToastUitil;


/**
 * 获取注册码弹窗
 */
public class SendRegisterCodeWindow extends BaseWindow {

    private PagerSlidingTabStrip tab;
    private LinearLayout         tabQQ;
    private LinearLayout         tabEmail;
    private CommonAdapter        mAdapter;


    private RegisterConfig                        registerConfig;
    private ImageView                             verifyImage;
    private ListView                              qqList;
    private List<AppInfoApi.RegisterConfigQQList> list;
    private TextView                              qqTip;
    private TextView                              emailTip;
    private TextView                              copyToken;


    //+--------------------------------------
    //| 监控获取LineLayout的高度
    //+--------------------------------------
    private Timer   timer   = null;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (tabQQ.getHeight() != 0) {
                    int   tabQQHeight = tabQQ.getHeight();
                    int[] windows     = Common.getScreenSize(context);
                    int   maxHeight   = (int) (windows[1] / 1.5);
                    if (tabQQHeight > maxHeight) {
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) tabQQ.getLayoutParams();
                        layoutParams.height = maxHeight;
                        tabQQ.setLayoutParams(layoutParams);
                    }

                    timer.cancel();
                }
            }
        }
    };


    @Override
    protected int getContentLayout() {
        return R.layout.popup_window_send_register_code;
    }


    @Override
    protected void initDialog() {
        tab = (PagerSlidingTabStrip) findId(R.id.PagerSlidingTabStrip);
        tabEmail = (LinearLayout) findId(R.id.tabEmail);
        tabQQ = (LinearLayout) findId(R.id.tabQQ);
        qqTip = (TextView) findId(R.id.qqTip);
        emailTip = (TextView) findId(R.id.emailTip);
        qqList = (ListView) findId(R.id.qqList);
        copyToken = (TextView) findId(R.id.copyToken);
        registerConfig = AppInfoApi.getRegisterConfig();


        // 选项设置
        setTabsValue();
        if (registerConfig.getEmailStatus() && registerConfig.getQQStatus()) {
            tab.setViewPagerNo(new String[]{registerConfig.getQQName(), registerConfig.getEmailName()});
            tab.getSelectObj(new PagerSlidingTabStrip.SelectTab() {

                @Override
                public void select(int position) {
                    switch (position) {
                        case 0:
                            tabQQ.setVisibility(View.VISIBLE);
                            tabEmail.setVisibility(View.GONE);
                            break;
                        case 1:
                            tabQQ.setVisibility(View.GONE);
                            tabEmail.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            });
        } else if (registerConfig.getEmailStatus()) {
            tab.setViewPagerNo(new String[]{registerConfig.getEmailName()});
            tabEmail.setVisibility(View.VISIBLE);
            tabQQ.setVisibility(View.GONE);
            tab.getSelectObj(new PagerSlidingTabStrip.SelectTab() {

                @Override
                public void select(int position) {
                }
            });
        } else {
            tab.setViewPagerNo(new String[]{registerConfig.getQQName()});
            tabEmail.setVisibility(View.GONE);
            tabQQ.setVisibility(View.VISIBLE);
            tab.getSelectObj(new PagerSlidingTabStrip.SelectTab() {

                @Override
                public void select(int position) {
                }
            });
        }


        //+--------------------------------------
        //| QQ选项
        //+--------------------------------------
        String text = registerConfig.getQQTip();
        if (text.equals("")) {
            qqTip.setVisibility(View.GONE);
        } else {
            qqTip.setVisibility(View.VISIBLE);
            qqTip.setText(Html.fromHtml(text));
        }

        // 绘制QQ客服列表
        list = registerConfig.getQQList();
        if (list.size() > 0) {
            qqList.setAdapter(mAdapter = new CommonAdapter<AppInfoApi.RegisterConfigQQList>(dialog.getContext(), list, R.layout.pupup_window_send_qq_item) {

                @Override
                public void convert(ViewHolder holder, AppInfoApi.RegisterConfigQQList item) {
                    holder.setText(R.id.name, item.getName());
                    holder.setText(R.id.number, item.getQq());
                }
            });

            qqList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Common.copy(context, list.get(position).getQq(), "QQ号码复制成功");
                }
            });
        }

        // 复制口令
        copyToken.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Common.copy(context, registerConfig.getQQToken(), "口令复制成功");
                dismiss();
            }
        });


        // QQ高度
        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        }, 10);


        //+--------------------------------------
        //| Email选项
        //+--------------------------------------

        // 发送邮件注册码
        TextView       sendBtn = (TextView) findId(R.id.sendBtn);
        final EditText email   = (EditText) findId(R.id.email);
        final EditText verify  = (EditText) findId(R.id.verify);
        verifyImage = (ImageView) findId(R.id.verifyImage);
        final LinearLayout verifyBox = (LinearLayout) findId(R.id.verifyBox);
        text = registerConfig.getEmailTip();
        if (text.equals("")) {
            emailTip.setVisibility(View.GONE);
        } else {
            emailTip.setVisibility(View.VISIBLE);
            emailTip.setText(Html.fromHtml(text));
        }


        // 隐藏验证码
        if (registerConfig.getEmailVerifyStatus()) {
            verifyBox.setVisibility(View.VISIBLE);
            Common.setVerifyImageUrl(verifyImage, Common.getRandUrl(registerConfig.getEmailVerifyUrl()));
        } else {
            verifyBox.setVisibility(View.GONE);
        }

        // 验证码切换
        verifyImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Common.setVerifyImageUrl(verifyImage, Common.getRandUrl(registerConfig.getEmailVerifyUrl()));
            }
        });


        // 发送邮件
        sendBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String emailText = Common.string(email.getText().toString().trim());
                if (emailText.equals("")) {
                    ToastUitil.showShort("请输入邮箱地址");
                    return;
                }

                String verifyText = Common.string(verify.getText().toString().trim());
                if (registerConfig.getEmailVerifyStatus() && verifyText.equals("")) {
                    ToastUitil.showShort("请输入验证码");
                    return;
                }

                // 发送邮件
                sendEmail(emailText, verifyText);
            }
        });
    }


    /**
     * 发送邮件
     */
    private void sendEmail(String email, String verify) {
        registerCodeByEmailApi emailApi = new registerCodeByEmailApi(context);
        emailApi.setemail(email);
        emailApi.setverify(verify);
        emailApi.request(new Rest.RestCallback() {

            @Override
            public void onSuccess(String message, JSONObject data) {
                dismiss();
                ToastUitil.showShort(message);
            }


            @Override
            public void onError(String message, String field, int code) {
                if (field.equals("verify") && verifyImage != null && registerConfig != null) {
                    Common.setVerifyImageUrl(verifyImage, Common.getRandUrl(registerConfig.getEmailVerifyUrl()));
                }

                ToastUitil.showShort(message);
            }
        });
    }


    /*
     * 对PagerSlidingTabStrip的各项属性进行赋值。
     */
    private void setTabsValue() {

        // 设置Tab是自动填充满屏幕的
        tab.setShouldExpand(true);
        // 设置Tab的分割线是透明的
        tab.setDividerColor(Color.TRANSPARENT);
        // 设置Tab底部线的高度
        tab.setUnderlineHeight(1);
        // 设置Tab Indicator的高度
        tab.setIndicatorHeight(4);
        // 设置Tab标题文字的大小
        tab.setTextSize(14);
        // 设置Tab Indicator的颜色
        tab.setIndicatorColor(getResources().getColor(R.color.title_color));
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        tab.setSelectedTextColor(getResources().getColor(R.color.title_color));
        // 设置Tab标题文字颜色
        tab.setTextColorResource(R.color.title_background);
        // 取消点击Tab时的背景色
        tab.setTabBackground(0);
    }
}
