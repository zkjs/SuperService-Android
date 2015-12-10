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
import android.widget.ImageView;
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
import com.zkjinshi.superservice.activity.set.ClientActivity;
import com.zkjinshi.superservice.activity.set.ClientDetailActivity;
import com.zkjinshi.superservice.listener.RecyclerItemClickListener;
import com.zkjinshi.superservice.sqlite.ClientDBUtil;
import com.zkjinshi.superservice.sqlite.ShopEmployeeDBUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.utils.RandomDrawbleUtil;
import com.zkjinshi.superservice.vo.ContactType;
import com.zkjinshi.superservice.vo.OnlineStatus;
import com.zkjinshi.superservice.vo.SortModel;
import com.zkjinshi.superservice.view.CircleImageView;

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
public class ContactsSortAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
                                  implements SectionIndexer {

    private List<SortModel> mList;
    private Context         mContext;

    private DisplayImageOptions options;

    private RecyclerItemClickListener mRecyclerItemClickListener;

    public ContactsSortAdapter(Context mContext, List<SortModel> list) {
        this.mContext = mContext;
        this.mList    = list;

        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(Color.TRANSPARENT)// 设置图片下载期间显示的图片
                .showImageForEmptyUri(Color.TRANSPARENT)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(Color.TRANSPARENT)// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build();
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     * @param list
     */
    public void updateListView(List<SortModel> list) {
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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_my_cilent, null);
        //设置条目宽度满足屏幕
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                            LinearLayout.LayoutParams.WRAP_CONTENT));
        ClientViewHolder clientHolder = new ClientViewHolder(view, mRecyclerItemClickListener);
        return clientHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        //根据position获取分类的首字母的Char ascii值
        final SortModel sortModel = mList.get(position);
        int section = getSectionForPosition(position);

        //是否显示首字母
        if (position == getPositionForSection(section)) {
            ((ClientViewHolder)holder).tvLetter.setVisibility(View.VISIBLE);
            String sortLetter = sortModel.getSortLetters();
            if("?".equals(sortLetter)){
                ((ClientViewHolder)holder).tvLetter.setText(mContext.getString(R.string.latest_contact));
            }else {
                ((ClientViewHolder)holder).tvLetter.setText(sortLetter);
            }
        } else {
            ((ClientViewHolder)holder).tvLetter.setVisibility(View.GONE);
        }

        String clientID    = sortModel.getClientID();
        int bgDrawableRes =  ClientDBUtil.getInstance().queryBgDrawableResByClientID(sortModel.getClientID());
        if(bgDrawableRes != 0){
            sortModel.setBgDrawableRes(bgDrawableRes);
        } else {
            int bgRes = RandomDrawbleUtil.getRandomDrawable();
            ClientDBUtil.getInstance().updateClientBgDrawableResByClientID(clientID, bgRes);
            sortModel.setBgDrawableRes(bgRes);
        }

        //根据url显示图片
        String avatarUrl = ProtocolUtil.getAvatarUrl(clientID);
        ImageLoader.getInstance().displayImage(avatarUrl, ((ClientViewHolder) holder).civContactAvatar, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                ((ClientViewHolder) holder).tvContactAvatar.setBackgroundResource(sortModel.getBgDrawableRes());
                ((ClientViewHolder) holder).civContactAvatar.setBackgroundColor(Color.TRANSPARENT);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                ((ClientViewHolder) holder).civContactAvatar.setBackgroundColor(Color.TRANSPARENT);
                ((ClientViewHolder) holder).tvContactAvatar.setBackgroundResource(sortModel.getBgDrawableRes());
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                ((ClientViewHolder) holder).civContactAvatar.setBackgroundColor(Color.TRANSPARENT);
                ((ClientViewHolder) holder).tvContactAvatar.setBackgroundResource(sortModel.getBgDrawableRes());
            }
        });

        //显示客户名称
        String      clientName  = sortModel.getName();
        if(!TextUtils.isEmpty(clientName)){
            ((ClientViewHolder) holder).tvContactAvatar.setText(clientName.substring(0, 1));
            //去除问号
            if("?".equals(clientName.trim().substring(0, 1))){
                ((ClientViewHolder)holder).tvContactName.setText(clientName.substring(1));
            } else {
                ((ClientViewHolder)holder).tvContactName.setText(clientName);
            }
        }
        ContactType contactType = sortModel.getContactType();
        if(contactType == ContactType.NORMAL){
            ((ClientViewHolder)holder).ivStar.setVisibility(View.VISIBLE);
            ((ClientViewHolder)holder).ivStar.setBackgroundResource(R.mipmap.ic_star_shouye_pre);
        }else {
            ((ClientViewHolder)holder).ivStar.setVisibility(View.GONE);
        }
        ((ClientViewHolder)holder).tvContactOnLine.setVisibility(View.INVISIBLE);
        ((ClientViewHolder) holder).civContactAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sortModel.getContactType().getValue() == ContactType.NORMAL.getValue()) {
                    String phoneNumber = sortModel.getNumber();
                    Intent clientDetail = new Intent(mContext, ClientDetailActivity.class);
                    clientDetail.putExtra("phone_number", phoneNumber);
                    mContext.startActivity(clientDetail);
                } else {
                    DialogUtil.getInstance().showCustomToast(mContext, "当前客户为本地联系人，无详细信息。", Gravity.CENTER);
                }
            }
        });
    }

    public void setOnItemClickListener(RecyclerItemClickListener listener) {
        this.mRecyclerItemClickListener = listener;
    }

    public static class ClientViewHolder extends RecyclerView.ViewHolder{

        public TextView         tvLetter;
        public CircleImageView  civContactAvatar;
        public TextView         tvContactAvatar;
        public TextView         tvContactName;
        public ImageView        ivStar;
        public TextView         tvContactDes;
        public RelativeLayout   rlContactOnStatus;
        public TextView         tvContactOnLine;
        public TextView         tvContactOnShop;

        private RecyclerItemClickListener mItemClickListener;

        public ClientViewHolder(View view, RecyclerItemClickListener itemClickListener) {
            super(view);
            tvLetter         = (TextView) view.findViewById(R.id.catalog);
            civContactAvatar = (CircleImageView) view.findViewById(R.id.civ_contact_avatar);
            tvContactAvatar  = (TextView) view.findViewById(R.id.tv_contact_avatar);
            tvContactName    = (TextView) view.findViewById(R.id.tv_contact_name);
            ivStar           = (ImageView) view.findViewById(R.id.iv_star);
            tvContactDes     = (TextView) view.findViewById(R.id.tv_contact_des);
            rlContactOnStatus  = (RelativeLayout) view.findViewById(R.id.rl_contact_on_status);
            tvContactOnLine    = (TextView) view.findViewById(R.id.tv_contact_on_line);
            tvContactOnShop    = (TextView) view.findViewById(R.id.tv_contact_on_shop);
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
        return mList.get(position).getSortLetters().charAt(0);
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
            String sortStr = mList.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase(Locale.CHINESE).charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

}
