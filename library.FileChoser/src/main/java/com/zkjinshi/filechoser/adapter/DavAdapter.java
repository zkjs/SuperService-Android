package com.zkjinshi.filechoser.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zkjinshi.filechoser.R;
import com.zkjinshi.filechoser.listener.RecyclerItemClickListener;
import com.zkjinshi.filechoser.util.DisplayUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * 文件目录导航适配器
 * 开发者：JimmyZhang
 * 日期：2015/10/10
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class DavAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<File> fileDirList;
    private Context context;
    private LayoutInflater inflater;
    private RecyclerItemClickListener itemClickListener;

    public void setOnItemClickListener(RecyclerItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public DavAdapter(Context context,ArrayList<File> dirFileList){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.setFileDirList(dirFileList);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.browser_item_dirs_nav, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        File file = fileDirList.get(position);
        String departName = file.getName();
        if(position == 0){
            ((ViewHolder)holder).fileDirTv.setText("存储卡");
            ((ViewHolder)holder).fileDirsNavLayout.setPadding(DisplayUtil.dip2px(context,10), 0, DisplayUtil.dip2px(context, 5), 0);
        }else if(!TextUtils.isEmpty(departName)){
            ((ViewHolder)holder).fileDirTv.setText(departName);
        }
        if(position == fileDirList.size()-1){
            ((ViewHolder)holder).fileDirIv.setVisibility(View.GONE);
            ((ViewHolder)holder).fileDirTv.setTextColor(context.getResources().getColor(R.color.browser_file_dir_select_text_color));
            if(fileDirList.size() > 1){
                ((ViewHolder)holder).fileDirsNavLayout.setPadding(DisplayUtil.dip2px(context,5), 0, DisplayUtil.dip2px(context,10), 0);
            }
        }else{
            ((ViewHolder)holder).fileDirTv.setTextColor(context.getResources().getColor(R.color.browser_file_dir_normal_text_color));
            ((ViewHolder)holder).fileDirIv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return fileDirList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView fileDirTv;
        ImageView fileDirIv;
        LinearLayout fileDirsNavLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            fileDirTv = (TextView) itemView.findViewById(R.id.select_file_dir_tv);
            fileDirIv = (ImageView)itemView.findViewById(R.id.select_file_dir_iv);
            fileDirsNavLayout = (LinearLayout)itemView.findViewById(R.id.browser_item_dirs_nav_layout);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(null != itemClickListener){
                itemClickListener.onItemClick(v,getAdapterPosition());
            }
        }
    }

    public void setFileDirList(ArrayList<File> fileDirList) {
        if(null == fileDirList){
            this.fileDirList = new ArrayList<File>();
        }else{
            this.fileDirList = fileDirList;
        }
        notifyDataSetChanged();
    }
}
