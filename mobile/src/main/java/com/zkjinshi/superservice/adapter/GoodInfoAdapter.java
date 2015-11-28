package com.zkjinshi.superservice.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.listener.RecyclerItemClickListener;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.GoodInfoVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 联系人适配器
 * 开发者：vincent
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class GoodInfoAdapter extends RecyclerView.Adapter<GoodInfoAdapter.GoodInfoViewHolder>{

    private Activity             mActivity;
    private List<GoodInfoVo>     mDatas;
    private DisplayImageOptions  mOptions;
    private static Map<Integer, GoodInfoViewHolder> mCheckHolderMap;  //初始值未选中值 -1

    private RecyclerItemClickListener mRecyclerItemClickListener;

    public GoodInfoAdapter(List<GoodInfoVo> datas, Activity activity) {

        if (datas == null) {
            this.mDatas = new ArrayList<>();
        } else {
            this.mDatas = datas;
        }
        this.mActivity = activity;

        mCheckHolderMap = new HashMap<>();
        this.mOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_room_pic_default)
                .showImageForEmptyUri(R.mipmap.ic_room_pic_default)
                .showImageOnFail(R.mipmap.ic_room_pic_default)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     * @param datas
     */
    public void updateListView(List<GoodInfoVo> datas) {
        if (datas == null) {
            this.mDatas = new ArrayList<>();
        } else {
            this.mDatas = datas;
        }
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public void setOnItemClickListener(RecyclerItemClickListener listener) {
        this.mRecyclerItemClickListener = listener;
    }

    @Override
    public GoodInfoAdapter.GoodInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_good_info, null);
        view.setLayoutParams(new LinearLayout.LayoutParams(
                                 LinearLayout.LayoutParams.MATCH_PARENT,
                                 LinearLayout.LayoutParams.WRAP_CONTENT));
        GoodInfoViewHolder holder = new GoodInfoViewHolder(view, mRecyclerItemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(final GoodInfoAdapter.GoodInfoViewHolder holder, final int position) {

        GoodInfoVo goodInfoVo = mDatas.get(position);
        //显示房型图片
        String imgUrl = goodInfoVo.getImgurl();
        ImageLoader.getInstance().displayImage(ProtocolUtil.getGoodImgUrl(imgUrl), holder.ivRoomType, mOptions);

        //房间类型描述
        String roomType = goodInfoVo.getRoom();
        if(!TextUtils.isEmpty(roomType)){
            holder.tvRoomType.setText(roomType);
        }

        //设置checkbox选中切换事件
        holder.cbChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    mCheckHolderMap.put(position, holder);
                } else {
                    if(mCheckHolderMap.containsKey(position)){
                        mCheckHolderMap.remove(position);
                    }
                }
            }
        });
    }

    /**
     * data数据清空
     */
    public void clear() {
        this.mDatas.clear();
        notifyDataSetChanged();
    }

    public static class GoodInfoViewHolder extends RecyclerView.ViewHolder{

        public ImageView  ivRoomType;
        public TextView   tvRoomType;
        public CheckBox   cbChecked;

        private RecyclerItemClickListener mItemClickListener;

        public GoodInfoViewHolder(View view, RecyclerItemClickListener itemClickListener) {

            super(view);
            ivRoomType = (ImageView) view.findViewById(R.id.iv_room_type);
            tvRoomType = (TextView)  view.findViewById(R.id.tv_room_type);
            cbChecked  = (CheckBox)  view.findViewById(R.id.cb_check);

            this.mItemClickListener = itemClickListener;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mItemClickListener != null){
                        //取消checkbox选中
                        Iterator<Map.Entry<Integer, GoodInfoViewHolder>> it = mCheckHolderMap.entrySet().iterator();
                        GoodInfoViewHolder viewHolder = null;
                        while (it.hasNext()) {
                            Map.Entry<Integer, GoodInfoViewHolder> entry = it.next();
                            viewHolder = entry.getValue();
                            if(viewHolder.cbChecked.isChecked()){
                                viewHolder.cbChecked.setChecked(false);
                            }
                        }
                        cbChecked.setChecked(!cbChecked.isChecked());
                        //触发条目点击事件
                        mItemClickListener.onItemClick(v, getPosition());
                    }
                }
            });
        }
    }

    /**
     * 获取选中房型
     * @return
     */
    public int getCheckedPosition(){
        int checkedPostion = -1;
        Iterator<Map.Entry<Integer, GoodInfoViewHolder>> it = mCheckHolderMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, GoodInfoViewHolder> entry = it.next();
            checkedPostion = entry.getKey();
        }
        return checkedPostion;
    }
}
