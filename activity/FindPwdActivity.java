package cn.yunhu.activity;


import android.os.Bundle;
import android.support.annotation.Nullable;
//import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.yunhu.R;
import cn.yunhu.adapter.TitleFragmentPagerAdapter;
import cn.yunhu.fragment.FindPwdByPasswordFragment;
import cn.yunhu.fragment.FindPwdByCallPhone;

/*
 * 找回密码界面
 */

public class FindPwdActivity extends BaseActivity implements View.OnClickListener {

//    private TabLayout tabLayout;
//    private ViewPager viewpager;

    private LinearLayout phonell;
    private TextView phoneTV;
    private View phoneview;

    private LinearLayout passwordLL;
    private TextView passwordtv;
    private View passwordview;

    private FrameLayout fragment;


    @Override
    protected int getLayoutContent() {
        return R.layout.activity_find_pwd;
    }


    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        setHeaderTitle("找回密码");

//        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
//        viewpager = (ViewPager) findViewById(R.id.viewPager);

//        List<Fragment> fragments = new ArrayList<>();
//
//        fragments.add(new FindPwdByCallPhone());
//        fragments.add(new FindPwdByPasswordFragment());

//        TitleFragmentPagerAdapter adapter = new TitleFragmentPagerAdapter(getSupportFragmentManager(), fragments, new String[]{"呼过的手机号", "用过的密码"});
//        viewpager.setAdapter(adapter);
//        tabLayout.setupWithViewPager(viewpager);
        phonell = (LinearLayout) findViewById(R.id.phonell);
        phonell.setOnClickListener(this);
        phoneTV = (TextView) findViewById(R.id.phoneTV);
        phoneview = findViewById(R.id.phoneview);

        passwordLL = (LinearLayout) findViewById(R.id.passwordLL);
        passwordLL.setOnClickListener(this);
        passwordtv = (TextView) findViewById(R.id.passwordtv);
        passwordview = findViewById(R.id.passwordview);

        fragment = (FrameLayout) findViewById(R.id.fragment);

        //获取到FragmentManager，在V4包中通过getSupportFragmentManager，
        //在系统中原生的Fragment是通过getFragmentManager获得的。
        FragmentManager FM = getSupportFragmentManager();
        //2.开启一个事务，通过调用beginTransaction方法开启。
        FragmentTransaction MfragmentTransaction =FM.beginTransaction();
        //把自己创建好的fragment创建一个对象
        FindPwdByCallPhone  f1 = new FindPwdByCallPhone();
        //向容器内加入Fragment，一般使用add或者replace方法实现，需要传入容器的id和Fragment的实例。
        MfragmentTransaction.add(R.id.fragment,f1);
        //提交事务，调用commit方法提交。
        MfragmentTransaction.commit();
        phoneTV.setTextColor(getResources().getColor(R.color.title_color));
        phoneview.setBackgroundResource(R.color.title_color);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.phonell:
//获取到FragmentManager，在V4包中通过getSupportFragmentManager，
                //在系统中原生的Fragment是通过getFragmentManager获得的。
                FragmentManager FM = getSupportFragmentManager();
                //2.开启一个事务，通过调用beginTransaction方法开启。
                FragmentTransaction MfragmentTransaction =FM.beginTransaction();
                //把自己创建好的fragment创建一个对象
                FindPwdByCallPhone  f1 = new FindPwdByCallPhone();
                //向容器内加入Fragment，一般使用add或者replace方法实现，需要传入容器的id和Fragment的实例。
                MfragmentTransaction.add(R.id.fragment,f1);
                //提交事务，调用commit方法提交。
                MfragmentTransaction.commit();
                phoneTV.setTextColor(getResources().getColor(R.color.title_color));
                phoneview.setBackgroundResource(R.color.title_color);
                passwordtv.setTextColor(getResources().getColor(R.color.text));
                passwordview.setBackgroundResource(R.color.transparent);
                break;
            case R.id.passwordLL:
//获取到FragmentManager，在V4包中通过getSupportFragmentManager，
                //在系统中原生的Fragment是通过getFragmentManager获得的。
                FragmentManager FM1 = getSupportFragmentManager();
                //2.开启一个事务，通过调用beginTransaction方法开启。
                FragmentTransaction MfragmentTransaction1 =FM1.beginTransaction();
                //把自己创建好的fragment创建一个对象
                FindPwdByPasswordFragment  f2 = new FindPwdByPasswordFragment();
                //向容器内加入Fragment，一般使用add或者replace方法实现，需要传入容器的id和Fragment的实例。
                MfragmentTransaction1.add(R.id.fragment,f2);
                //提交事务，调用commit方法提交。
                MfragmentTransaction1.commit();
                passwordtv.setTextColor(getResources().getColor(R.color.title_color));
                passwordview.setBackgroundResource(R.color.title_color);
                phoneTV.setTextColor(getResources().getColor(R.color.text));
                phoneview.setBackgroundResource(R.color.transparent);
                break;
        }
    }
}
