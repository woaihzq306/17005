package cn.yunhu.fragment;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.yunhu.activity.BaseActivity;

/**
 * Fragment基本类
 */
abstract class BaseFragment extends Fragment {

    protected View         view;
    protected Activity     nowContext;
    protected BaseActivity nowActivity;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(getLayoutContent(), container, false);
//        nowContext = (Activity) getContext();
//        nowActivity = (BaseActivity) getActivity();

        // 初始化
        initFragment(inflater, container, savedInstanceState);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nowContext = (Activity) getContext();
        nowActivity = (BaseActivity) getActivity();
    }

    protected View findId(@IdRes int id) {
        return view.findViewById(id);
    }


    /**
     * 设置布局文件
     */
    abstract protected int getLayoutContent();


    /**
     * 初始化
     */
    abstract protected void initFragment(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);
}
