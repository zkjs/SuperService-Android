package com.zkjinshi.superservice.adapter;

import android.app.Activity;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 开发者：WinkyQin
 * 日期：2015/12/21
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public abstract class ServiceBaseAdapter <T> extends BaseAdapter {

    public List<T> mDatas;
    protected Activity mActivity;

    public ServiceBaseAdapter(Activity activity, List<T> datas) {
        this.mActivity = activity;
        this.mDatas = datas;
    }

    @Override
    public int getCount() {
        if (mDatas != null) {
            return mDatas.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mDatas != null) {
            return mDatas.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setData(List<T> datas) {
        if (null == datas) {
            this.mDatas = new ArrayList<T>();
        } else {
            this.mDatas = datas;
        }
        notifyDataSetChanged();
    }

    /**
     * 清空数据
     */
    public void clear(){
        if(null != mDatas && !mDatas.isEmpty()){
            mDatas.clear();
            this.notifyDataSetChanged();
        }
    }
}

