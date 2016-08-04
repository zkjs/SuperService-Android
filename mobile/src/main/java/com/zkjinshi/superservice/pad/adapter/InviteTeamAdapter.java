package com.zkjinshi.superservice.pad.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.zkjinshi.superservice.pad.R;
import com.zkjinshi.superservice.pad.listener.RecyclerItemClickListener;
import com.zkjinshi.superservice.pad.utils.ProtocolUtil;
import com.zkjinshi.superservice.pad.utils.RandomDrawbleUtil;
import com.zkjinshi.superservice.pad.view.CircleImageView;
import com.zkjinshi.superservice.pad.vo.EmployeeVo;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 开发者：JimmyZhang
 * 日期：2015/11/26
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class InviteTeamAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder> implements SectionIndexer {

    private List<EmployeeVo> mList;
    private Context mContext;
    private DisplayImageOptions options;
    private Map<Integer, Boolean> selectMap;
    private Map<Integer, Boolean> enabledMap;

    public void setSelectMap(Map<Integer, Boolean> selectMap) {
        this.selectMap = selectMap;
        notifyDataSetChanged();
    }

    public void setEnabledMap(Map<Integer, Boolean> enabledMap) {
        this.enabledMap = enabledMap;
        notifyDataSetChanged();
    }
    private RecyclerItemClickListener mRecyclerItemClickListener;

    public InviteTeamAdapter(Context mContext, List<EmployeeVo> list) {
        this.mContext = mContext;
        this.mList    = list;
        this.options  = new DisplayImageOptions.Builder()
                .showImageOnLoading(null)
                .showImageForEmptyUri(null)
                .showImageOnFail(null)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_invite_team_list, null);
        view.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        ContactViewHolder holder = new ContactViewHolder(view, mRecyclerItemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        EmployeeVo employeeVo = mList.get(position);
        int section = getSectionForPosition(position);

        if (position == getPositionForSection(section)) {
            ((ContactViewHolder)holder).tvLetter.setVisibility(View.VISIBLE);
            String deptName = employeeVo.getRolename();
            if(!TextUtils.isEmpty(deptName)){
                ((ContactViewHolder)holder).tvLetter.setText(deptName);
            }
        } else {
            ((ContactViewHolder)holder).tvLetter.setVisibility(View.GONE);
        }

        //显示客户名称
        final String employeeName = employeeVo.getUsername();
        if(!TextUtils.isEmpty(employeeName)) {
            final String firstName = employeeName.substring(0, 1);
            ((ContactViewHolder) holder).tvContactAvatar.setText(firstName);
            ((ContactViewHolder) holder).tvContactName.setText(employeeName);
        }

        //根据url显示图片
        String avatarUrl = ProtocolUtil.getAvatarUrl(employeeVo.getUserid());
        ImageLoader.getInstance().displayImage(avatarUrl, ((ContactViewHolder) holder).civContactAvatar, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                ((ContactViewHolder) holder).civContactAvatar.setBackgroundColor(Color.TRANSPARENT);
                ((ContactViewHolder) holder).tvContactAvatar.setBackgroundResource(RandomDrawbleUtil.getRandomDrawable());
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                ((ContactViewHolder) holder).civContactAvatar.setBackgroundColor(Color.TRANSPARENT);
                ((ContactViewHolder) holder).tvContactAvatar.setBackgroundResource(RandomDrawbleUtil.getRandomDrawable());
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                ((ContactViewHolder) holder).civContactAvatar.setBackgroundColor(Color.TRANSPARENT);
                ((ContactViewHolder) holder).tvContactAvatar.setBackgroundResource(RandomDrawbleUtil.getRandomDrawable());
            }
        });

        ((ContactViewHolder) holder).cbCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selectMap.put(position, isChecked);
            }
        });

        if(null != selectMap && selectMap.containsKey(position) && selectMap.get(position)){
            ((ContactViewHolder) holder).cbCheck.setChecked(true);
        } else {
            ((ContactViewHolder) holder).cbCheck.setChecked(false);
        }

        if (enabledMap != null && enabledMap.containsKey(position)
                && enabledMap.get(position)) {
            ((ContactViewHolder) holder).cbCheck.setEnabled(false);
        } else {
            ((ContactViewHolder) holder).cbCheck.setEnabled(true);
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setOnItemClickListener(RecyclerItemClickListener listener) {
        this.mRecyclerItemClickListener = listener;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return mList.get(position).getRolename().charAt(0);
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
            String sortStr = mList.get(i).getRolename();
            char firstChar = sortStr.toUpperCase(Locale.CHINESE).charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder{

        public TextView tvLetter;
        public CircleImageView civContactAvatar;
        public TextView         tvContactAvatar;
        public TextView         tvContactName;
        //        public RelativeLayout   rlContactStatus;
//        public TextView         tvContactStatus;
//        public RelativeLayout   rlContactOnStatus;
//        public TextView         tvContactOnLine;
        public CheckBox cbCheck;

        private RecyclerItemClickListener mItemClickListener;

        public ContactViewHolder(View view, RecyclerItemClickListener itemClickListener) {
            super(view);
            tvLetter         = (TextView) view.findViewById(R.id.catalog);
            civContactAvatar = (CircleImageView) view.findViewById(R.id.civ_contact_avatar);
            tvContactAvatar  = (TextView) view.findViewById(R.id.tv_contact_avatar);
            tvContactName    = (TextView) view.findViewById(R.id.tv_contact_name);
//            rlContactStatus  = (RelativeLayout) view.findViewById(R.id.rl_contact_status);
//            tvContactStatus    = (TextView) view.findViewById(R.id.tv_contact_status);
//            rlContactOnStatus  = (RelativeLayout) view.findViewById(R.id.rl_contact_on_status);
//            tvContactOnLine    = (TextView) view.findViewById(R.id.tv_contact_on_line);
            cbCheck            = (CheckBox) view.findViewById(R.id.cb_check);

            this.mItemClickListener = itemClickListener;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mItemClickListener != null){
                        //设置复选框选中
                        cbCheck.setChecked(!cbCheck.isChecked());
                        mItemClickListener.onItemClick(v, getPosition());
                    }
                }
            });
        }
    }
}
