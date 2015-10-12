package com.zkjinshi.superservice.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.bean.OrderBean;
import com.zkjinshi.superservice.view.CircleImageView;
import com.zkjinshi.superservice.vo.DepartmentVo;

import java.util.ArrayList;

/**
 * 开发者：dujiande
 * 日期：2015/10/12
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class DepartmentAdapter  extends RecyclerView.Adapter {

    private int selectId;
    private DepartmentVo selectDepartmentVo;
    private ArrayList<DepartmentVo> dataList = new ArrayList<DepartmentVo>();
    private Context context;
    public ClickRadioButtonInterface clickRadioButtonInterface = null;

    public interface ClickRadioButtonInterface{
        public void clickItem(DepartmentVo selectDepartmentVo);
    }

    public void setClickRadioButtonInterface(ClickRadioButtonInterface clickRadioButtonInterface) {
        this.clickRadioButtonInterface = clickRadioButtonInterface;
    }

    public DepartmentVo getSelectDepartmentVo() {
        return selectDepartmentVo;
    }

    public void setSelectDepartmentVo(DepartmentVo selectDepartmentVo) {
        this.selectDepartmentVo = selectDepartmentVo;
    }

    public DepartmentAdapter(ArrayList<DepartmentVo> dataList,int selectId) {
        this.dataList = dataList;
        this.selectId = selectId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.radio_button_item, null);
        return new DepartmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        DepartmentViewHolder holder = (DepartmentViewHolder)viewHolder;
        holder.position = position;
        DepartmentVo departmentVo = dataList.get(position);
        holder.radioButton.setText(departmentVo.getName());


        if(departmentVo.getId() == selectId){
            holder.radioButton.setChecked(true);
        }else{
            holder.radioButton.setChecked(false);
        }

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class DepartmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public int position;
        private RadioButton radioButton;

        public DepartmentViewHolder(View itemView) {
            super(itemView);
            radioButton = (RadioButton) itemView.findViewById(R.id.rbtn_item);
            radioButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            selectDepartmentVo = dataList.get(position);
            selectId = selectDepartmentVo.getId();
            notifyDataSetChanged();

            if(clickRadioButtonInterface != null){
                clickRadioButtonInterface.clickItem(selectDepartmentVo);
            }
        }
    }
}
