package com.zkjinshi.superservice.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.set.EmployeeInfoActivity;
import com.zkjinshi.superservice.listener.RecyclerItemClickListener;
import com.zkjinshi.superservice.sqlite.ShopEmployeeDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.utils.RandomDrawbleUtil;
import com.zkjinshi.superservice.view.CircleImageView;
import com.zkjinshi.superservice.vo.OnlineStatus;
import com.zkjinshi.superservice.vo.ShopEmployeeVo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 联系人适配器
 * 开发者：vincent
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class TeamContactsAdapter extends ServiceBaseAdapter<ShopEmployeeVo>
                                  implements SectionIndexer {

    private Activity             mActivity;
    private List<ShopEmployeeVo> mList;
    private DisplayImageOptions  options;

    public TeamContactsAdapter(Activity activity, List<ShopEmployeeVo> datas) {
        super(activity, datas);

        this.mActivity = activity;
        this.mList     = datas;
        this.options   = new DisplayImageOptions.Builder()
                .showImageOnLoading(null)
                .showImageForEmptyUri(null)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(null)// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(null == convertView){
            holder      = new ViewHolder();
            convertView = View.inflate(mActivity, R.layout.item_team_contact, null);
            holder.tvLetter          = (TextView) convertView.findViewById(R.id.catalog);
            holder.flContactAvatar   = (FrameLayout) convertView.findViewById(R.id.fl_contact_avatar);
            holder.civContactAvatar  = (CircleImageView) convertView.findViewById(R.id.civ_contact_avatar);
            holder.tvContactAvatar   = (TextView) convertView.findViewById(R.id.tv_contact_avatar);
            holder.tvContactName     = (TextView) convertView.findViewById(R.id.tv_contact_name);
            holder.rlContactOnStatus = (RelativeLayout) convertView.findViewById(R.id.rl_contact_on_status);
            holder.tvContactOnLine   = (TextView) convertView.findViewById(R.id.tv_contact_on_line);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //根据position获取分类的首字母的Char ascii值
        final ShopEmployeeVo shopEmployeeVo = mList.get(position);
        int   section = getSectionForPosition(position);

        final TextView tvContactAvatar = holder.tvContactAvatar;
        final CircleImageView civContactAvatar = holder.civContactAvatar;

        if(position == 0){
            /** 1:显示商家信息 */
            holder.tvLetter.setVisibility(View.GONE);
            String shopLogoUrl = ProtocolUtil.getShopLogoUrl(CacheUtil.getInstance().getShopID());
            final String shopName = CacheUtil.getInstance().getShopFullName();

            if(!TextUtils.isEmpty(shopName)){
                holder.tvContactName.setText(shopName);
                holder.tvContactAvatar.setText(shopName.substring(0, 1));
            }

            //设置团队成员头像单击事件, 进入员工详情
            holder.flContactAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogUtil.getInstance().showCustomToast(mActivity, "TODO:", Gravity.CENTER);
                }
            });

            ImageLoader.getInstance().displayImage(shopLogoUrl, holder.civContactAvatar, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    civContactAvatar.setBackgroundColor(Color.TRANSPARENT);
                    tvContactAvatar.setBackgroundResource(RandomDrawbleUtil.getRandomDrawable());
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    civContactAvatar.setBackgroundColor(Color.TRANSPARENT);
                    tvContactAvatar.setBackgroundResource(RandomDrawbleUtil.getRandomDrawable());
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    civContactAvatar.setBackgroundColor(Color.TRANSPARENT);
                    tvContactAvatar.setBackgroundResource(RandomDrawbleUtil.getRandomDrawable());
                }
            });
            holder.tvContactOnLine.setText("点击消息群发");

        } else {
            /** 2 显示普通商家成员信息  */
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


            ImageLoader.getInstance().displayImage(empAvatarUrl, holder.civContactAvatar, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    tvContactAvatar.setBackgroundResource(shopEmployeeVo.getBg_color_res());
                    civContactAvatar.setBackgroundColor(Color.TRANSPARENT);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    civContactAvatar.setBackgroundColor(Color.TRANSPARENT);
                    tvContactAvatar.setBackgroundResource(shopEmployeeVo.getBg_color_res());
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    civContactAvatar.setBackgroundColor(Color.TRANSPARENT);
                    tvContactAvatar.setBackgroundResource(shopEmployeeVo.getBg_color_res());
                }
            });

            if(!TextUtils.isEmpty(empName)){
                if("?".equals(empName.trim().substring(0, 1))) {
                    holder.tvContactName.setText(empName.substring(1));
                }else {
                    holder.tvContactName.setText(empName);
                }
            }
            holder.tvContactOnLine.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    public static class ViewHolder{

        public TextView         tvLetter;
        public FrameLayout      flContactAvatar;
        public CircleImageView  civContactAvatar;
        public TextView         tvContactAvatar;
        public TextView         tvContactName;
        public RelativeLayout   rlContactOnStatus;
        public TextView         tvContactOnLine;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        if(!TextUtils.isEmpty(mList.get(position).getDept_name())){
            return mList.get(position).getDept_name().charAt(0);
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
            String deptName = mList.get(i).getDept_name();
            if(!TextUtils.isEmpty(deptName)){
                if (deptName.charAt(0) == section) {
                    return i;
                }
            }
        }
        return -1;
    }

}
