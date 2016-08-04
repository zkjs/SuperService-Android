package com.zkjinshi.superservice.pad.adapter;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.superservice.pad.R;
import com.zkjinshi.superservice.pad.bean.InviteCode;
import com.zkjinshi.superservice.pad.bean.InviteCodeUser;
import com.zkjinshi.superservice.pad.listener.RecyclerItemClickListener;
import com.zkjinshi.superservice.pad.utils.ProtocolUtil;
import com.zkjinshi.superservice.pad.view.CircleImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * 开发者：WinkyQin
 * 日期：2015/12/10
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class InviteCodeUserAdapter extends RecyclerView.Adapter<InviteCodeUserAdapter.ViewHolder> {

    private Activity             mActivity;
    private List<InviteCode> mDatas;

    private RecyclerItemClickListener itemClickListener;

    public InviteCodeUserAdapter(Activity activity, List<InviteCode> inviteCodes){
        super();
        this.mActivity = activity;
        this.setData(inviteCodes);
    }

    public void setData(List<InviteCode> data){
        if(null == data){
            this.mDatas = new ArrayList<>();
        }else {
            this.mDatas = data;
        }
        notifyDataSetChanged();
    }

    @Override
    public InviteCodeUserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_invite_code_used, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(InviteCodeUserAdapter.ViewHolder holder, int position) {
        InviteCode inviteCodeUser = mDatas.get(position);
        String inviteCode = inviteCodeUser.getSaleCode();
        String imageUrl     = inviteCodeUser.getUserimage();
        String userName   = inviteCodeUser.getUsername();
        String userPhone  = inviteCodeUser.getPhone();

        String userPhotoUrl = ProtocolUtil.getAvatarUrl(mActivity,imageUrl);
        holder.civContactAvatar.setImageURI(Uri.parse(userPhotoUrl));

        if (!TextUtils.isEmpty(inviteCode)) {
            holder.tvInviteCode.setText(inviteCode);
        }

        if (!TextUtils.isEmpty(userName)) {
            holder.tvContactName.setText(userName);
        }

        if (!TextUtils.isEmpty(userPhone)) {
            holder.tvContactPhone.setText(userPhone);
        }
    }

    @Override
    public int getItemCount() {
        if(null != mDatas){
            return mDatas.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
                                     implements View.OnClickListener {
        TextView tvInviteCode;
        TextView tvContactName;
        TextView tvContactPhone;
        SimpleDraweeView civContactAvatar;

        public ViewHolder(View itemView) {
            super(itemView);
            tvInviteCode     = (TextView) itemView.findViewById(R.id.tv_invite_code);
            tvContactName    = (TextView) itemView.findViewById(R.id.tv_contact_name);
            tvContactPhone   = (TextView) itemView.findViewById(R.id.tv_contact_phone);
            civContactAvatar = (SimpleDraweeView) itemView.findViewById(R.id.civ_contact_avatar);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(null != itemClickListener){
                itemClickListener.onItemClick(v,getAdapterPosition());
            }
        }
    }

    public void setOnItemClickListener(RecyclerItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void clear() {
        if(null != mDatas){
            mDatas.clear();
            notifyDataSetChanged();
        }
    }

    public void addAll(List<InviteCode> list) {
        mDatas.addAll(list);
        notifyDataSetChanged();
    }

}
