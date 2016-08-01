package com.zkjinshi.superservice.pad.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.zkjinshi.superservice.pad.R;
import com.zkjinshi.superservice.pad.view.RoundProgressBar;
import com.zkjinshi.superservice.pad.vo.ClientArrivingVo;
import com.zkjinshi.superservice.pad.vo.ItemTagVo;

import java.util.List;

/**
 * Created by qinyejun on 7/26/16.
 */
public class GuestArrivingAdapter extends ArrayAdapter<ClientArrivingVo> {
    private List<ClientArrivingVo> objects;
    private Context mContext;
    private int resource;

    public GuestArrivingAdapter(Context context, int resource,
                                List<ClientArrivingVo> objects) {
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
    public ClientArrivingVo getItem(int position) {
        // TODO Auto-generated method stub
        return super.getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(resource, null);
            holder.date = (TextView) convertView.findViewById(R.id.tv_date);
            holder.time = (TextView) convertView.findViewById(R.id.tv_time);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ClientArrivingVo item = this.getItem(position);
        holder.date.setText(item.getFirstDate());
        holder.time.setText(item.getFirstTime());

        return convertView;
    }

    public void setData(List<ClientArrivingVo> data) {
        this.objects = data;
    }

    final class ViewHolder {
        TextView date;
        TextView time;
    }

}
