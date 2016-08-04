package com.zkjinshi.superservice.pad.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zkjinshi.superservice.pad.R;
import com.zkjinshi.superservice.pad.bean.ZoneBean;
import com.zkjinshi.superservice.pad.vo.AgencyVo;

import java.util.ArrayList;
import java.util.Map;

/**
 * 选择服务区域适配器
 * 开发者：jimmyzhang
 * 日期：16/6/24
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class AgencyAdapter extends BaseAdapter {

    private ArrayList<AgencyVo> agencyList;
    private Context context;
    private LayoutInflater inflater;
    private Map<String, Boolean> selectMap;

    public void setSelectMap(Map<String, Boolean> selectMap) {
        this.selectMap = selectMap;
        notifyDataSetChanged();
    }

    public void setAgencyList(ArrayList<AgencyVo> agencyList) {
        if(null == agencyList){
            this.agencyList = new ArrayList<AgencyVo>();
        }else {
            this.agencyList = agencyList;
        }
        notifyDataSetChanged();
    }

    public AgencyAdapter(Context context, ArrayList<AgencyVo> agencyList){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.setAgencyList(agencyList);
    }

    @Override
    public int getCount() {
        return agencyList.size();
    }

    @Override
    public Object getItem(int i) {
        return agencyList.get(i);
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
        AgencyVo agencyVo = agencyList.get(position);
        String roleId = agencyVo.getRoleid();
        if (selectMap != null && selectMap.containsKey(roleId) && selectMap.get(roleId)) {
            viewHolder.chooseIv.setImageResource(R.mipmap.ic_jia_pre);
        } else {
            viewHolder.chooseIv.setImageResource(R.mipmap.ic_jia_nor);
        }
        String roleNameStr = agencyVo.getRolename();
        if(!TextUtils.isEmpty(roleNameStr)){
            viewHolder.coverageTv.setText(roleNameStr);
        }
        return convertView;
    }

    static class ViewHolder{
        TextView coverageTv;
        ImageView chooseIv;
    }
}
