package com.zkjinshi.superservice.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.listener.RecyclerItemClickListener;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.view.CircleImageView;
import com.zkjinshi.superservice.vo.ShopEmployeeVo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 团队编辑页面的联系人列表
 * 开发者：vincent
 * 日期：2015/10/12
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class TeamEditContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ShopEmployeeVo> mList;
    private Context mContext;

    private DisplayImageOptions options;

    private RecyclerItemClickListener mRecyclerItemClickListener;

    public TeamEditContactsAdapter(Context mContext, List<ShopEmployeeVo> list) {

        this.mContext = mContext;
        this.mList    = list;

        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher)
                .showImageForEmptyUri(R.mipmap.ic_launcher)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.ic_launcher)// 设置图片加载或解码过程中发生错误显示的图片
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        ViewHolder holder = null;
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_team_contact, null);
            view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            holder = new ViewHolder(view, mRecyclerItemClickListener);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ShopEmployeeVo employeeVo = mList.get(position);
        int section = getSectionForPosition(position);

        if (position == getPositionForSection(section)) {
            ((ViewHolder)holder).tvLetter.setVisibility(View.VISIBLE);
            ((ViewHolder)holder).tvLetter.setText(employeeVo.getRole_name());
        } else {
            ((ViewHolder)holder).tvLetter.setVisibility(View.GONE);
        }

        //根据url显示图片
        String avatarUrl = ProtocolUtil.getAvatarUrl(employeeVo.getEmpid());
        ImageLoader.getInstance().displayImage(avatarUrl, ((ViewHolder) holder).civContactAvatar, options);

        //显示客户名称
        String clientName = employeeVo.getName();
        if(!TextUtils.isEmpty(clientName)){
            ((ViewHolder)holder).tvContactName.setText(clientName);
        }
        holder.itemView.setTag(employeeVo);
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
        return mList.get(position).getRole_name().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getItemCount(); i++) {
            String sortStr = mList.get(i).getRole_name();
            char firstChar = sortStr.toUpperCase(Locale.CHINESE).charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tvLetter;
        public CircleImageView civContactAvatar;
        public TextView         tvContactName;
        public RelativeLayout rlContactStatus;
        public TextView         tvContactStatus;
        public RelativeLayout   rlContactOnStatus;
        public TextView         tvContactOnLine;

        private RecyclerItemClickListener mItemClickListener;

        public ViewHolder(View view, RecyclerItemClickListener itemClickListener) {
            super(view);
            tvLetter         = (TextView) view.findViewById(R.id.catalog);
            civContactAvatar = (CircleImageView) view.findViewById(R.id.civ_contact_avatar);
            tvContactName    = (TextView) view.findViewById(R.id.tv_contact_name);
            rlContactStatus  = (RelativeLayout) view.findViewById(R.id.rl_contact_status);
            tvContactStatus    = (TextView) view.findViewById(R.id.tv_contact_status);
            rlContactOnStatus  = (RelativeLayout) view.findViewById(R.id.rl_contact_on_status);
            tvContactOnLine    = (TextView) view.findViewById(R.id.tv_contact_on_line);
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

}
