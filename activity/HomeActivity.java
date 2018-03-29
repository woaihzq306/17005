package cn.yunhu.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.tencent.bugly.crashreport.CrashReport;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.yunhu.R;
import cn.yunhu.api.AppInfoApi;
import cn.yunhu.fragment.BuyRechargeFragment;
import cn.yunhu.fragment.HomeFragment;
import cn.yunhu.service.LogService;
import cn.yunhu.utils.Common;
import cn.yunhu.utils.EncodeUtil;
import cn.yunhu.utils.PhoneUtil;

/*
 * 软件的主界面
 */
public class HomeActivity extends BaseActivity implements View.OnClickListener {

    private FragmentManager fragmentManager;
    private Fragment        currentFragment;
    private LinearLayout    fragmentContainer;
    private LinearLayout    callCenterFooter;
    private LinearLayout    buyRechargeFooter;




    @Override
    protected int getLayoutContent() {
        // 据说能禁止休眠
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        return R.layout.activity_home;
    }


    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        nowActivity.setDisabledHeaderLeft(true);
        nowActivity.setBackToApp();

        fragmentContainer = (LinearLayout) findViewById(R.id.fragmentContainer);
        callCenterFooter = (LinearLayout) findViewById(R.id.callCenterFooter);
        callCenterFooter.setOnClickListener(this);


        buyRechargeFooter = (LinearLayout) findViewById(R.id.buyRechargeFooter);
        buyRechargeFooter.setOnClickListener(this);

        fragmentManager = getSupportFragmentManager();

        /**
         * 收集Logcat日志
         */
        startService(new Intent(this, LogService.class));


        Intent intent   = getIntent();
        String gotoName = Common.string(intent.getStringExtra("goto"));
        if (gotoName.equals("BuyRecharge")) {
            showBuyRecharge();
        } else {
            showHome();
        }

    }


    /**
     * 切换显示
     */
    private void change(Class<? extends Fragment> fragmentClass) {
        String fragmentTag = fragmentClass.getSimpleName();

        // 已经显示的不在继续操作
        if (currentFragment != null && currentFragment.getClass().getSimpleName().equals(fragmentTag)) {
            return;
        }


        FragmentTransaction transaction = fragmentManager.beginTransaction();

        try {
            // 隐藏当前的Fragment
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }

            // 已加载
            Fragment fragmentByTag = fragmentManager.findFragmentByTag(fragmentTag);
            if (fragmentByTag != null) {
                transaction.show(fragmentByTag);
                currentFragment = fragmentByTag;
            } else {
                Fragment fragment = fragmentClass.newInstance();
                transaction.add(R.id.fragmentContainer, fragment, fragmentTag);
                currentFragment = fragment;
            }

            transaction.commit();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onClick(View view) {
        ImageView imageView;
        TextView  textView;
        switch (view.getId()) {
            // 呼叫中心
            case R.id.callCenterFooter:
                change(HomeFragment.class);
                imageView = (ImageView) callCenterFooter.getChildAt(0);
                imageView.setImageResource(R.mipmap.icon_call_white);
                textView = (TextView) callCenterFooter.getChildAt(1);
                textView.setTextColor(Color.parseColor("#FFFFFF"));
                callCenterFooter.setBackgroundColor(Color.parseColor("#08BC05"));


                imageView = (ImageView) buyRechargeFooter.getChildAt(0);
                imageView.setImageResource(R.mipmap.icon_cami_black);
                textView = (TextView) buyRechargeFooter.getChildAt(1);
                textView.setTextColor(Color.parseColor("#666666"));
                buyRechargeFooter.setBackgroundColor(Color.parseColor("#FFFFFF"));

                nowActivity.setHeaderTitle(AppInfoApi.getClientTitle());
                onResume();
                break;

            // 购买软件
            case R.id.buyRechargeFooter:
                change(BuyRechargeFragment.class);


                imageView = (ImageView) callCenterFooter.getChildAt(0);
                imageView.setImageResource(R.mipmap.icon_call_black);
                textView = (TextView) callCenterFooter.getChildAt(1);
                textView.setTextColor(Color.parseColor("#666666"));
                callCenterFooter.setBackgroundColor(Color.parseColor("#FFFFFF"));


                imageView = (ImageView) buyRechargeFooter.getChildAt(0);
                imageView.setImageResource(R.mipmap.icon_cami_white);
                textView = (TextView) buyRechargeFooter.getChildAt(1);
                textView.setTextColor(Color.parseColor("#FFFFFF"));
                buyRechargeFooter.setBackgroundColor(Color.parseColor("#08BC05"));
                onResume();
                nowActivity.setHeaderTitle("购买卡密");
                break;
        }
    }


    public void showHome() {
        callCenterFooter.performClick();
    }


    public void showBuyRecharge() {
        buyRechargeFooter.performClick();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }
}
