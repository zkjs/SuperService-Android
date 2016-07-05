package com.zkjinshi.superservice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zkjinshi.superservice.R;

import com.zkjinshi.superservice.vo.DepartmentVo;

import java.util.ArrayList;


/**
 * 开发者：dujiande
 * 日期：2015/10/14
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class DeptAdapter extends BaseAdapter {
    private final static String TAG = DeptAdapter.class.getSimpleName();

    private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局
    private ArrayList<DepartmentVo> dataList = new ArrayList<DepartmentVo>();
    private String selectid = "";
    Context context;

    public DeptAdapter(Context context,ArrayList<DepartmentVo> dataList,String selectid) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.dataList = dataList;
        this.selectid = selectid;
    }

    public ArrayList<DepartmentVo> getdataList() {
        return dataList;
    }

    public void setdataList(ArrayList<DepartmentVo> dataList) {
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_dept, null);
            holder = new ViewHolder();
            holder.dept = (TextView)convertView.findViewById(R.id.text_dept);
            convertView.setTag(holder);//绑定ViewHolder对象
        }else{
            holder = (ViewHolder)convertView.getTag();//取出ViewHolder对象
        }

        DepartmentVo departmentVo = dataList.get(position);
        holder.dept.setText(departmentVo.getDeptname());
        if(departmentVo.getDeptid().equals(selectid) ){
            holder.dept.setTextColor(context.getResources().getColor(R.color.selected_color));
        }else{
            holder.dept.setTextColor(context.getResources().getColor(R.color.unselect_color));
        }
        return convertView;
    }



    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount();
    }

    /*存放控件*/
    public final class ViewHolder{
        public TextView  dept;
    }
}
