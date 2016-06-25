package com.zkjinshi.superservice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.zkjinshi.superservice.vo.ServiceHistoryVo;

import java.util.ArrayList;

/**
 * 开发者：jimmyzhang
 * 日期：16/6/25
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class TaskHistoryAdapter extends BaseAdapter {

    private ArrayList<ServiceHistoryVo> historyList;
    private Context context;
    private LayoutInflater inflater;

    public void setHistoryList(ArrayList<ServiceHistoryVo> historyList) {
        if(null == historyList){
            this.historyList = new ArrayList<ServiceHistoryVo>();
        }else {
            this.historyList = historyList;
        }
    }

    public TaskHistoryAdapter(Context context, ArrayList<ServiceHistoryVo> historyList){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.setHistoryList(historyList);
    }

    @Override
    public int getCount() {
        return historyList.size();
    }

    @Override
    public Object getItem(int i) {
        return historyList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }

    class ViewHolder{

    }
}
