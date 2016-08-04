package com.zkjinshi.superservice.pad.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zkjinshi.superservice.pad.R;
import com.zkjinshi.superservice.pad.activity.set.EmployeeInfoActivity;
import com.zkjinshi.superservice.pad.utils.ProtocolUtil;
import com.zkjinshi.superservice.pad.utils.RandomDrawbleUtil;
import com.zkjinshi.superservice.pad.view.slidelistview.SlideBaseAdapter;
import com.zkjinshi.superservice.pad.vo.EmployeeVo;

import java.util.ArrayList;
import java.util.List;

/**
 * 联系人适配器
 * 开发者：杜健德
 * 日期：2016/6/28
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class TeamContactsSlideAdapter extends SlideBaseAdapter implements SectionIndexer {

    private ArrayList<EmployeeVo> mDatas;
    private Activity mActivity;
    private CallBack callBack = null;

    public  interface CallBack{
        public void delete(EmployeeVo employeeVo);
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public void refresh(ArrayList<EmployeeVo> datas){
        this.mDatas = datas;
        notifyDataSetChanged();
    }

    public TeamContactsSlideAdapter(Activity activity, ArrayList<EmployeeVo> datas) {
        super(activity);

        this.mActivity = activity;
        this.mDatas = datas;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int i) {
        return mDatas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(null == convertView){
            holder      = new ViewHolder();
            convertView = createConvertView(position);
            holder.tvLetter          = (TextView) convertView.findViewById(R.id.catalog);
            holder.flContactAvatar   = (FrameLayout) convertView.findViewById(R.id.fl_contact_avatar);
            holder.civContactAvatar  = (SimpleDraweeView) convertView.findViewById(R.id.civ_contact_avatar);
            holder.tvContactAvatar   = (TextView) convertView.findViewById(R.id.tv_contact_avatar);
            holder.tvContactName     = (TextView) convertView.findViewById(R.id.tv_contact_name);
            holder.deleteBtn = (Button)convertView.findViewById(R.id.delete_btn);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //根据position获取分类的首字母的Char ascii值
        final EmployeeVo employeeVo = mDatas.get(position);
        int   section = getSectionForPosition(position);

        /**  显示普通商家成员信息  */
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

        final String empName = employeeVo.getUsername();
        if(!TextUtils.isEmpty(empName)){
            final String firstName = empName.substring(0, 1);
            holder.tvContactName.setText(empName);
            holder.tvContactAvatar.setText(firstName);
        }
        //设置团队成员头像单击事件, 进入员工详情
        holder.flContactAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent employeeInfo = new Intent(mActivity, EmployeeInfoActivity.class);
                employeeInfo.putExtra("shop_employee", employeeVo);
                mActivity.startActivity(employeeInfo);
            }
        });

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(callBack != null){
                    callBack.delete(employeeVo);
                }
            }
        });

        String empAvatarUrl = ProtocolUtil.getAvatarUrl(mActivity,employeeVo.getUserimage());
        holder.civContactAvatar.setImageURI( Uri.parse(empAvatarUrl));

        if(!TextUtils.isEmpty(empName)){
            if("?".equals(empName.trim().substring(0, 1))) {
                holder.tvContactName.setText(empName.substring(1));
            }else {
                holder.tvContactName.setText(empName);
            }
        }
        //获得默认背景颜色值
        int bgColorRes = RandomDrawbleUtil.getDrawableByIndex(position);
        holder.tvContactAvatar.setBackgroundResource(bgColorRes);
        holder.tvContactAvatar.setVisibility(View.VISIBLE);
        return convertView;
    }

    @Override
    public int getFrontViewId(int position) {
        return R.layout.item_team_contact;
    }

    @Override
    public int getLeftBackViewId(int position) {
        return 0;
    }

    @Override
    public int getRightBackViewId(int position) {
        return R.layout.row_right_view;
    }

    static class ViewHolder{

        public TextView         tvLetter;
        public FrameLayout      flContactAvatar;
        public SimpleDraweeView  civContactAvatar;
        public TextView         tvContactAvatar;
        public TextView         tvContactName;
        public Button deleteBtn;
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
