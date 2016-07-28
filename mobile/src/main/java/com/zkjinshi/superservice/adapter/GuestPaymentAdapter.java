package com.zkjinshi.superservice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.ext.util.MathUtil;
import com.zkjinshi.superservice.vo.ClientArrivingVo;
import com.zkjinshi.superservice.vo.ClientPaymentVo;

import java.util.List;

/**
 * Created by qinyejun on 7/26/16.
 */
public class GuestPaymentAdapter extends ArrayAdapter<ClientPaymentVo> {
    private List<ClientPaymentVo> objects;
    private Context mContext;
    private int resource;

    public GuestPaymentAdapter(Context context, int resource,
                               List<ClientPaymentVo> objects) {
        super(context, resource, objects);
        this.objects = objects;
        this.mContext = context;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return super.getCount();
    }

    @Override
    public ClientPaymentVo getItem(int position) {
        // TODO Auto-generated method stub
        return super.getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(resource, null);
            holder.dateTime = (TextView) convertView.findViewById(R.id.tv_date_time);
            holder.amount = (TextView) convertView.findViewById(R.id.tv_amount);
            holder.remark = (TextView) convertView.findViewById(R.id.tv_remark);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ClientPaymentVo item = this.getItem(position);
        holder.dateTime.setText(item.getCreatetime());
        holder.amount.setText("¥"+ MathUtil.convertStr(item.getAmount()));
        holder.remark.setText(item.getRemark());

        return convertView;
    }

    public void setData(List<ClientPaymentVo> data) {
        this.objects = data;
    }

    final class ViewHolder {
        TextView dateTime;
        TextView amount;
        TextView remark;
    }

}
