package com.zkjinshi.superservice.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.deque.LIFOLinkedBlockingDeque;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.listener.RecyclerItemClickListener;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.utils.RandomDrawbleUtil;
import com.zkjinshi.superservice.utils.StringUtil;
import com.zkjinshi.superservice.view.CircleImageView;
import com.zkjinshi.superservice.view.CircleTextView;
import com.zkjinshi.superservice.vo.OnlineStatus;
import com.zkjinshi.superservice.vo.ShopEmployeeVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 团队编辑页面的联系人列表
 * 开发者：vincent
 * 日期：2015/10/12
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class TeamEditContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SectionIndexer {

    private List<ShopEmployeeVo>  mList;
    private Context               mContext;
    private DisplayImageOptions   options;
    private Map<Integer, Boolean> mCheckedMap;
    private RecyclerItemClickListener mRecyclerItemClickListener;

    public TeamEditContactsAdapter(Context mContext, List<ShopEmployeeVo> list) {

        this.mContext = mContext;
        this.mList    = list;
        this.mCheckedMap = new HashMap<>();
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
        if(mCheckedMap != null){
            mCheckedMap.clear();
        }
        this.notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_team_edit_contact, null);
        view.setLayoutParams(new LinearLayout.LayoutParams(
                                 LinearLayout.LayoutParams.MATCH_PARENT,
                                 LinearLayout.LayoutParams.WRAP_CONTENT));
        ContactViewHolder holder = new ContactViewHolder(view, mRecyclerItemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        ShopEmployeeVo employeeVo = mList.get(position);
        int section = getSectionForPosition(position);

        if (position == getPositionForSection(section)) {
            ((ContactViewHolder)holder).tvLetter.setVisibility(View.VISIBLE);
            String deptName = employeeVo.getDept_name();
            if(!TextUtils.isEmpty(deptName)){
                ((ContactViewHolder)holder).tvLetter.setText(deptName);
            }
        } else {
            ((ContactViewHolder)holder).tvLetter.setVisibility(View.GONE);
        }

        //显示客户名称
        final String employeeName = employeeVo.getName();
        if(!TextUtils.isEmpty(employeeName)) {
            final String firstName = employeeName.substring(0, 1);
            ((ContactViewHolder) holder).tvContactAvatar.setText(firstName);
            ((ContactViewHolder) holder).tvContactName.setText(employeeName);
        }

        //根据url显示图片
        String avatarUrl = ProtocolUtil.getAvatarUrl(employeeVo.getEmpid());
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

        //设置显示在线状态时间
        if(employeeVo.getOnline_status() == OnlineStatus.ONLINE) {
            ((ContactViewHolder)holder).tvContactOnLine.setTextColor(Color.BLUE);
            ((ContactViewHolder)holder).tvContactOnLine.setText(mContext.getString(R.string.online));
        } else {
            ((ContactViewHolder)holder).tvContactOnLine.setTextColor(Color.GRAY);
            String strLastOnline  = mContext.getString(R.string.offline);
            Long   lastOnlineTime = employeeVo.getLastOnLineTime();
            if(lastOnlineTime != 0){
                strLastOnline +=  mContext.getString(R.string.last_online_time )
                        + " : "
                        + TimeUtil.getChatTime(lastOnlineTime);
            }
            ((ContactViewHolder)holder).tvContactOnLine.setText(strLastOnline);
        }

        //set the checkbox
        ((ContactViewHolder) holder).cbCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCheckedMap.put(position, isChecked);
            }
        });

        if(mCheckedMap.containsKey(position) && mCheckedMap.get(position)){
            ((ContactViewHolder) holder).cbCheck.setChecked(true);
        } else {
            ((ContactViewHolder) holder).cbCheck.setChecked(false);
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
        return mList.get(position).getDept_name().charAt(0);
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

    static class ContactViewHolder extends RecyclerView.ViewHolder{

        public TextView         tvLetter;
        public CircleImageView  civContactAvatar;
        public TextView         tvContactAvatar;
        public TextView         tvContactName;
        public RelativeLayout   rlContactStatus;
        public TextView         tvContactStatus;
        public RelativeLayout   rlContactOnStatus;
        public TextView         tvContactOnLine;
        public CheckBox         cbCheck;

        private RecyclerItemClickListener mItemClickListener;

        public ContactViewHolder(View view, RecyclerItemClickListener itemClickListener) {
            super(view);
            tvLetter         = (TextView) view.findViewById(R.id.catalog);
            civContactAvatar = (CircleImageView) view.findViewById(R.id.civ_contact_avatar);
            tvContactAvatar  = (TextView) view.findViewById(R.id.tv_contact_avatar);
            tvContactName    = (TextView) view.findViewById(R.id.tv_contact_name);
            rlContactStatus  = (RelativeLayout) view.findViewById(R.id.rl_contact_status);
            tvContactStatus    = (TextView) view.findViewById(R.id.tv_contact_status);
            rlContactOnStatus  = (RelativeLayout) view.findViewById(R.id.rl_contact_on_status);
            tvContactOnLine    = (TextView) view.findViewById(R.id.tv_contact_on_line);
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
