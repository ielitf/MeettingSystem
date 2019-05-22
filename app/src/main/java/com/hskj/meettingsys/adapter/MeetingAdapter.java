package com.hskj.meettingsys.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.hskj.meettingsys.R;
import com.hskj.meettingsys.bean.MqttMeetingListBean;

import java.util.List;

public class MeetingAdapter extends MyBaseAdapter<MqttMeetingListBean> {
    private LayoutInflater inflater;
    private Context context;
    public MeetingAdapter(Context context, List<MqttMeetingListBean> mData) {
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
    protected void bindView(Context context, View view, int position, MqttMeetingListBean model) {
        ViewHolder holderView = (ViewHolder) view.getTag();
        holderView.day.setText(model.getStartDate());
        holderView.hour.setText(model.getEndDate());
        holderView.title.setText(model.getName());
        holderView.order.setText(model.getBookPerson());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        private TextView day,hour,title,order;
    }
}
