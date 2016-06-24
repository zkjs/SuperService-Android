package com.zkjinshi.superservice.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.vo.EmployeeVo;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 联系人适配器
 * 开发者：vincent
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class TeamMemberAdapter extends ServiceBaseAdapter<EmployeeVo> implements SectionIndexer {

    private Map<String, Boolean> selectMap;

    public void setSelectMap(Map<String, Boolean> selectMap) {
        this.selectMap = selectMap;
        notifyDataSetChanged();
    }

    public TeamMemberAdapter(Activity activity, List<EmployeeVo> datas) {
        super(activity, datas);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(null == convertView){
            holder      = new ViewHolder();
            convertView = View.inflate(mActivity, R.layout.item_team_member, null);
            holder.tvLetter          = (TextView) convertView.findViewById(R.id.catalog);
            holder.tvMemberName = (TextView) convertView.findViewById(R.id.tv_member_name);
            holder.tvPhoneNum = (TextView)convertView.findViewById(R.id.tv_phone_num);
            holder.ivSelect = (ImageView)convertView.findViewById(R.id.iv_select_member);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        EmployeeVo employeeVo = mDatas.get(position);
        int section = getSectionForPosition(position);
        String rolename = employeeVo.getRolename();
        if (position == getPositionForSection(section)) {
            holder.tvLetter.setVisibility(View.VISIBLE);
            if(!TextUtils.isEmpty(rolename)){
                if("?".equals(rolename.substring(0, 1))){
                    //最近联系人的处理
                    holder.tvLetter.setText(mActivity.getString(R.string.latest_contact));
                }else {
                    holder.tvLetter.setText(rolename);
                }
            } else {
                holder.tvLetter.setText("#");
            }
        } else {
            /** 不显示首字母 */
            holder.tvLetter.setVisibility(View.GONE);
        }

        String empName = employeeVo.getUsername();
        if(!TextUtils.isEmpty(empName)){
            holder.tvMemberName.setText(empName);
        }

        String phoneNumStr = employeeVo.getPhone();
        if(!TextUtils.isEmpty(phoneNumStr)){
            holder.tvPhoneNum.setText(phoneNumStr);
        }
        String userId = employeeVo.getUserid();
        if (selectMap != null && selectMap.containsKey(userId) && selectMap.get(userId)) {
            holder.ivSelect.setImageResource(R.mipmap.ic_jia_pre);
        } else {
            holder.ivSelect.setImageResource(R.mipmap.ic_jia_nor);
        }
        return convertView;
    }

    public static class ViewHolder{

        public TextView tvLetter;
        public TextView tvMemberName;
        public TextView tvPhoneNum;
        public ImageView ivSelect;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        if(position < 0){
            return -1;
        }
        String deptName = mDatas.get(position).getRolename();
        if(!TextUtils.isEmpty(deptName)){
            return deptName.charAt(0);
        }
        return -1;
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String deptName = mDatas.get(i).getRolename();
            if(!TextUtils.isEmpty(deptName)){
                if (deptName.charAt(0) == section) {
                    return i;
                }
            }
        }
        return -1;
    }

}
