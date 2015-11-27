package com.zkjinshi.superservice.adapter;

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
public class TranspondAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
                                  implements SectionIndexer {

    private Context              mContext;
    private List<ShopEmployeeVo> mList;
    private DisplayImageOptions  options;

    private RecyclerItemClickListener mRecyclerItemClickListener;

    public TranspondAdapter(Context mContext, List<ShopEmployeeVo> list) {
        this.mContext = mContext;
        this.mList    = list;

        this.options  = new DisplayImageOptions.Builder()
                .showImageOnLoading(null)
                .showImageForEmptyUri(null)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(null)// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build();
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     * @param list
     */
    public void updateListView(List<ShopEmployeeVo> list) {
        if (list == null) {
            this.mList = new ArrayList<>();
        } else {
            this.mList = list;
        }
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setOnItemClickListener(RecyclerItemClickListener listener) {
        this.mRecyclerItemClickListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_transpond_list, null);
        view.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
        ContactViewHolder holder = new ContactViewHolder(view, mRecyclerItemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        //根据position获取分类的首字母的Char ascii值
        final ShopEmployeeVo shopEmployeeVo = mList.get(position);
        int section  = getSectionForPosition(position);

        /** 2 显示普通商家成员信息  */
        String deptName = shopEmployeeVo.getDept_name();
        if(TextUtils.isEmpty(deptName)){
            deptName = shopEmployeeVo.getDept_id()+"";
        }
        if (position == getPositionForSection(section)) {
            ((ContactViewHolder)holder).tvLetter.setVisibility(View.VISIBLE);
            if(!TextUtils.isEmpty(deptName)){
                if("?".equals(deptName.substring(0, 1))){
                    //最近联系人的处理
                    ((ContactViewHolder)holder).tvLetter.setText(mContext.getString(R.string.latest_contact));
                }else {
                    ((ContactViewHolder)holder).tvLetter.setText(deptName);
                }
            } else {
                ((ContactViewHolder)holder).tvLetter.setText("#");
            }
        } else {
            /** 不显示首字母 */
            ((ContactViewHolder)holder).tvLetter.setVisibility(View.GONE);
        }

        final String empID   = shopEmployeeVo.getEmpid();
        final String empName = shopEmployeeVo.getName();
        if(!TextUtils.isEmpty(empName)){
            final String firstName = empName.substring(0, 1);
            ((ContactViewHolder)holder).tvContactName.setText(empName);
            ((ContactViewHolder)holder).tvContactAvatar.setText(firstName);
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
        ((ContactViewHolder) holder).flContactAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent employeeInfo = new Intent(mContext, EmployeeInfoActivity.class);
                employeeInfo.putExtra("shop_employee", shopEmployeeVo);
                mContext.startActivity(employeeInfo);
            }
        });

        String empAvatarUrl = ProtocolUtil.getAvatarUrl(empID);
        ImageLoader.getInstance().displayImage(empAvatarUrl, ((ContactViewHolder) holder).civContactAvatar, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                ((ContactViewHolder) holder).tvContactAvatar.setBackgroundResource(shopEmployeeVo.getBg_color_res());
                ((ContactViewHolder) holder).civContactAvatar.setBackgroundColor(Color.TRANSPARENT);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                ((ContactViewHolder) holder).civContactAvatar.setBackgroundColor(Color.TRANSPARENT);
                ((ContactViewHolder) holder).tvContactAvatar.setBackgroundResource(shopEmployeeVo.getBg_color_res());
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                ((ContactViewHolder) holder).civContactAvatar.setBackgroundColor(Color.TRANSPARENT);
                ((ContactViewHolder) holder).tvContactAvatar.setBackgroundResource(shopEmployeeVo.getBg_color_res());
            }
        });

        if(!TextUtils.isEmpty(empName)){
            //去除wen
            if("?".equals(empName.trim().substring(0, 1))) {
                ((ContactViewHolder)holder).tvContactName.setText(empName.substring(1));
            }else {
                ((ContactViewHolder)holder).tvContactName.setText(empName);
            }
        }
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder{

        public TextView         tvLetter;
        public FrameLayout      flContactAvatar;
        public CircleImageView  civContactAvatar;
        public TextView         tvContactAvatar;
        public TextView         tvContactName;
        public RelativeLayout   rlContactOnStatus;

        private RecyclerItemClickListener mItemClickListener;

        public ContactViewHolder(View view, RecyclerItemClickListener itemClickListener) {
            super(view);
            tvLetter         = (TextView) view.findViewById(R.id.catalog);
            flContactAvatar  = (FrameLayout) view.findViewById(R.id.fl_contact_avatar);
            civContactAvatar = (CircleImageView) view.findViewById(R.id.civ_contact_avatar);
            tvContactAvatar = (TextView) view.findViewById(R.id.tv_contact_avatar);
            tvContactName    = (TextView) view.findViewById(R.id.tv_contact_name);
            rlContactOnStatus  = (RelativeLayout) view.findViewById(R.id.rl_contact_on_status);
            this.mItemClickListener = itemClickListener;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mItemClickListener != null){
                        mItemClickListener.onItemClick(v, getPosition());
                    }
                }
            });
        }
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        if(!TextUtils.isEmpty(mList.get(position).getDept_name())){
            return mList.get(position).getDept_name().charAt(0);
        }
        return ( mList.get(position).getDept_id()+"").charAt(0);
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getItemCount(); i++) {
            String sortStr = mList.get(i).getDept_name();
            char firstChar = sortStr.toUpperCase(Locale.CHINESE).charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

}