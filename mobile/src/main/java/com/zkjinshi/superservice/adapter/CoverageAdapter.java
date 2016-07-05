package com.zkjinshi.superservice.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.bean.ZoneBean;

import java.util.ArrayList;
import java.util.Map;

/**
 * 选择服务区域适配器
 * 开发者：jimmyzhang
 * 日期：16/6/24
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class CoverageAdapter extends BaseAdapter {

    private ArrayList<ZoneBean> zoneList;
    private Context context;
    private LayoutInflater inflater;
    private Map<String, Boolean> selectMap;

    public void setSelectMap(Map<String, Boolean> selectMap) {
        this.selectMap = selectMap;
        notifyDataSetChanged();
    }

    public void setZoneList(ArrayList<ZoneBean> zoneList) {
        if(null == zoneList){
            this.zoneList = new ArrayList<ZoneBean>();
        }else {
            this.zoneList = zoneList;
        }
        notifyDataSetChanged();
    }

    public CoverageAdapter(Context context, ArrayList<ZoneBean> zoneList){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.setZoneList(zoneList);
    }

    @Override
    public int getCount() {
        return zoneList.size();
    }

    @Override
    public Object getItem(int i) {
        return zoneList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(null == convertView){
            convertView = inflater.inflate(R.layout.item_choose_coverage,null);
            viewHolder = new ViewHolder();
            viewHolder.coverageTv = (TextView)convertView.findViewById(R.id.tv_coverage_name);
            viewHolder.chooseIv = (ImageView)convertView.findViewById(R.id.iv_select_coverage);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ZoneBean zoneBean = zoneList.get(position);
        String locId = zoneBean.getLocid();
        if (selectMap != null && selectMap.containsKey(locId) && selectMap.get(locId)) {
            viewHolder.chooseIv.setImageResource(R.mipmap.ic_jia_pre);
        } else {
            viewHolder.chooseIv.setImageResource(R.mipmap.ic_jia_nor);
        }
        String coverageNameStr = zoneBean.getArea();
        if(!TextUtils.isEmpty(coverageNameStr)){
            viewHolder.coverageTv.setText(coverageNameStr);
        }
        return convertView;
    }

    static class ViewHolder{
        TextView coverageTv;
        ImageView chooseIv;
    }
}
