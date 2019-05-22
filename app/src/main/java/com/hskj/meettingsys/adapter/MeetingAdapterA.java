package com.hskj.meettingsys.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hskj.meettingsys.R;
import com.hskj.meettingsys.bean.MeetingItemBean;

import java.util.List;

public class MeetingAdapterA extends MyBaseAdapter<MeetingItemBean> {
    private LayoutInflater inflater;
    private Context context;
    public MeetingAdapterA(Context context, List<MeetingItemBean> mData) {
        super(context, mData);
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    protected View newView(Context context, int position, ViewGroup parentView) {
        ViewHolder holderView = new ViewHolder();
        View convertView = inflater.inflate(R.layout.item_meetting, null, false);
        holderView.day = convertView.findViewById(R.id.meeting_data_day);
        holderView.hour = convertView.findViewById(R.id.meeting_data_hour);
        holderView.title =  convertView.findViewById(R.id.meeting_title);
        holderView.order = convertView.findViewById(R.id.meeting_order);
        convertView.setTag(holderView);
        return convertView;
    }

    @Override
    protected void bindView(Context context, View view, int position, MeetingItemBean model) {
        ViewHolder holderView = (ViewHolder) view.getTag();
        holderView.day.setText(model.getDay());
        holderView.hour.setText(model.getHour());
        holderView.title.setText(model.getTitle());
        holderView.order.setText(model.getOrder());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        private TextView day,hour,title,order;
    }
}
