package com.hskj.meettingsys.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hskj.meettingsys.R;
import com.hskj.meettingsys.bean.JiaWeatherBean;
import com.hskj.meettingsys.bean.WeatherBean;
import com.hskj.meettingsys.utils.GlideHelper;

import java.util.List;

public class WeatherAdapter extends MyBaseAdapter2<WeatherBean> {
    private LayoutInflater inflater;
    private Context context;

    public WeatherAdapter(Context context, List<WeatherBean> mData) {
        super(mData, context);
        this.context = context;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return 3;
    }

    @Override
    public WeatherBean getItem(int position) {
        // TODO Auto-generated method stub
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holderView;
        if (convertView == null) {
            convertView=inflater.inflate(R.layout.item_weather, null);
            holderView=new ViewHolder();
            holderView.day = convertView.findViewById(R.id.weather_day);
            holderView.tem_h = convertView.findViewById(R.id.weather_tem_h);
            holderView.tem_l = convertView.findViewById(R.id.weather_tem_l);
            holderView.wea = convertView.findViewById(R.id.weather_wea);
            convertView.setTag(holderView);
        }else{
            holderView=(ViewHolder) convertView.getTag();
        }
        WeatherBean item = data.get(position);
        switch (position) {
            case 0:
                holderView.day.setText("今天");
                break;
            case 1:
                holderView.day.setText("明天");
                break;
            case 2:
                holderView.day.setText("后天");
                break;
            default:
                break;
        }
        holderView.tem_h.setText(item.getWeather());
        holderView.tem_l.setText(item.getTemperature());

//        GlideHelper.showImageWithFullUrl(context,item.getWeather_icon(),holderView.wea);
        return convertView;
    }

    class ViewHolder {
        private TextView day, tem_h, tem_l;
        private ImageView wea;
    }
}
