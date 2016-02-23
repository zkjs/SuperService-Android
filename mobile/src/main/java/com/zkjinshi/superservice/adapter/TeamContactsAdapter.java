package com.zkjinshi.superservice.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.set.EmployeeInfoActivity;
import com.zkjinshi.superservice.sqlite.ShopEmployeeDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.utils.RandomDrawbleUtil;
import com.zkjinshi.superservice.view.CircleImageView;
import com.zkjinshi.superservice.vo.ShopEmployeeVo;

import java.util.List;

/**
 * 联系人适配器
 * 开发者：vincent
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class TeamContactsAdapter extends ServiceBaseAdapter<ShopEmployeeVo> implements SectionIndexer {



    public TeamContactsAdapter(Activity activity, List<ShopEmployeeVo> datas) {
        super(activity, datas);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(null == convertView){
            holder      = new ViewHolder();
            convertView = View.inflate(mActivity, R.layout.item_team_contact, null);
            holder.tvLetter          = (TextView) convertView.findViewById(R.id.catalog);
            holder.flContactAvatar   = (FrameLayout) convertView.findViewById(R.id.fl_contact_avatar);
            holder.civContactAvatar  = (SimpleDraweeView) convertView.findViewById(R.id.civ_contact_avatar);
            holder.tvContactAvatar   = (TextView) convertView.findViewById(R.id.tv_contact_avatar);
            holder.tvContactName     = (TextView) convertView.findViewById(R.id.tv_contact_name);
            holder.rlContactOnStatus = (RelativeLayout) convertView.findViewById(R.id.rl_contact_on_status);
            holder.tvContactOnLine   = (TextView) convertView.findViewById(R.id.tv_contact_on_line);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //根据position获取分类的首字母的Char ascii值
        final ShopEmployeeVo shopEmployeeVo = mDatas.get(position);
        int   section = getSectionForPosition(position);

        /**  显示普通商家成员信息  */
        String deptName = shopEmployeeVo.getDept_name();
        if(TextUtils.isEmpty(deptName)){
            deptName = shopEmployeeVo.getDept_id()+"";
        }
        if (position == getPositionForSection(section)) {
            holder.tvLetter.setVisibility(View.VISIBLE);
            if(!TextUtils.isEmpty(deptName)){
                if("?".equals(deptName.substring(0, 1))){
                    //最近联系人的处理
                    holder.tvLetter.setText(mActivity.getString(R.string.latest_contact));
                }else {
                    holder.tvLetter.setText(deptName);
                }
            } else {
                holder.tvLetter.setText("#");
            }
        } else {
            /** 不显示首字母 */
            holder.tvLetter.setVisibility(View.GONE);
        }

        final String empID   = shopEmployeeVo.getEmpid();
        final String empName = shopEmployeeVo.getName();
        if(!TextUtils.isEmpty(empName)){
            final String firstName = empName.substring(0, 1);
            holder.tvContactName.setText(empName);
            holder.tvContactAvatar.setText(firstName);
        }

        //获得默认背景颜色值
        int bgColorRes = ShopEmployeeDBUtil.getInstance().queryBgColorResByEmpID(empID);

        if(bgColorRes != 0){
            shopEmployeeVo.setBg_color_res(bgColorRes);
        }else {
            shopEmployeeVo.setBg_color_res(RandomDrawbleUtil.getRandomDrawable());
            if(ShopEmployeeDBUtil.getInstance().isEmployeeExistByEmpID(empID)){
                ShopEmployeeDBUtil.getInstance().updateShopEmployee(shopEmployeeVo);
            }
        }

        //设置团队成员头像单击事件, 进入员工详情
        holder.flContactAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent employeeInfo = new Intent(mActivity, EmployeeInfoActivity.class);
                employeeInfo.putExtra("shop_employee", shopEmployeeVo);
                mActivity.startActivity(employeeInfo);
            }
        });

        String empAvatarUrl = ProtocolUtil.getAvatarUrl(empID);
        holder.civContactAvatar.setImageURI( Uri.parse(empAvatarUrl));

        if(!TextUtils.isEmpty(empName)){
            if("?".equals(empName.trim().substring(0, 1))) {
                holder.tvContactName.setText(empName.substring(1));
            }else {
                holder.tvContactName.setText(empName);
            }
        }
        holder.tvContactAvatar.setBackgroundResource(shopEmployeeVo.getBg_color_res());
        holder.tvContactAvatar.setVisibility(View.VISIBLE);
        holder.tvContactOnLine.setVisibility(View.INVISIBLE);
        return convertView;
    }

    public static class ViewHolder{

        public TextView         tvLetter;
        public FrameLayout      flContactAvatar;
        public SimpleDraweeView  civContactAvatar;
        public TextView         tvContactAvatar;
        public TextView         tvContactName;
        public RelativeLayout   rlContactOnStatus;
        public TextView         tvContactOnLine;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        String deptName = mDatas.get(position).getDept_name();
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
            String deptName = mDatas.get(i).getDept_name();
            if(!TextUtils.isEmpty(deptName)){
                if (deptName.charAt(0) == section) {
                    return i;
                }
            }
        }
        return -1;
    }

}
