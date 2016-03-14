package com.zkjinshi.superservice.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.bean.InviteCode;
import com.zkjinshi.superservice.bean.InviteCodeUser;
import com.zkjinshi.superservice.listener.RecyclerItemClickListener;
import com.zkjinshi.superservice.vo.IsValidity;

import java.util.ArrayList;
import java.util.List;

/**
 * 开发者：WinkyQin
 * 日期：2015/12/10
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class InviteCodeAdapter extends RecyclerView.Adapter<InviteCodeAdapter.ViewHolder> {

    private Activity mActivity;
    private List<InviteCode> mDatas;
    private RecyclerItemClickListener itemClickListener;

    public InviteCodeAdapter(Activity activity, List<InviteCode> inviteCodes){
        super();
        this.mActivity = activity;
        if(null == inviteCodes){
            this.mDatas = new ArrayList<>();
        }else {
            this.mDatas = inviteCodes;
        }
    }

    @Override
    public InviteCodeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_invite_code, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(InviteCodeAdapter.ViewHolder holder, int position) {
        InviteCode inviteCode = mDatas.get(position);
        int isValid = inviteCode.getIs_validity();

        //邀请码有效
        if(isValid == IsValidity.ISVALID.getValue()){
            String salesCode = inviteCode.getSaleCode();
            if (!TextUtils.isEmpty(salesCode)) {
                holder.tvInviteCode.setText(salesCode);
            }
        }

        //生成短信链接并且发送
        holder.ibtnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
        TextView    tvInviteCode;
        ImageButton ibtnTransfer;

        public ViewHolder(View itemView) {
            super(itemView);
            tvInviteCode = (TextView)    itemView.findViewById(R.id.tv_invite_code);
            ibtnTransfer = (ImageButton) itemView.findViewById(R.id.ibtn_transfer);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(null != itemClickListener){
                itemClickListener.onItemClick(v, getAdapterPosition());
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
