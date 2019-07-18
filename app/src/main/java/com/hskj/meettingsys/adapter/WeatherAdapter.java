package com.hskj.meettingsys.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hskj.meettingsys.R;
import com.hskj.meettingsys.bean.WeatherBean;

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
        if(data == null){
            return 0;
        }else if(data.size() < 3){
            return data.size();
        }else
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

        String weather=item.getWeather();


        if((weather.indexOf("雷阵雨"))!=-1){
            holderView.wea.setImageResource(R.mipmap.leizhenyu);
        }
        if((weather.indexOf("暴雨"))!=-1){
            holderView.wea.setImageResource(R.mipmap.baoyu);
        }
        if((weather.indexOf("小雨"))!=-1){
            holderView.wea.setImageResource(R.mipmap.xiaoyu);
        }
        if((weather.indexOf("中雨"))!=-1){
            holderView.wea.setImageResource(R.mipmap.zhongyu);
        }
        if((weather.indexOf("大雨"))!=-1){
            holderView.wea.setImageResource(R.mipmap.dayu);
        }
        if((weather.indexOf("阵雨"))!=-1){
            holderView.wea.setImageResource(R.mipmap.dayu);
        }
        if((weather.indexOf("转晴"))!=-1){
            holderView.wea.setImageResource(R.mipmap.sunny);
        }
        if((weather.indexOf("转雾"))!=-1){
            holderView.wea.setImageResource(R.mipmap.wu);
        }
        if((weather.indexOf("雨夹雪"))!=-1){
            holderView.wea.setImageResource(R.mipmap.yujiaxue);
        }
        if((weather.indexOf("小雪"))!=-1){
            holderView.wea.setImageResource(R.mipmap.xiaoxue);
        }
        if((weather.indexOf("中雪"))!=-1){
            holderView.wea.setImageResource(R.mipmap.zhongxue);
        }
        if((weather.indexOf("大雪"))!=-1){
            holderView.wea.setImageResource(R.mipmap.daxue);
        }

        if(weather.equals("晴转多云")){
            holderView.wea.setImageResource(R.mipmap.sunntclude);
        }else if(weather.equals("晴")){
            holderView.wea.setImageResource(R.mipmap.sunny);
        }else if(weather.equals("阴")){
            holderView.wea.setImageResource(R.mipmap.yin);
        }else if(weather.equals("多云")){
            holderView.wea.setImageResource(R.mipmap.yin);
        }else if(weather.equals("小雪")){
            holderView.wea.setImageResource(R.mipmap.xiaoxue);
        }else if(weather.equals("中雪")){
            holderView.wea.setImageResource(R.mipmap.zhongxue);
        }else if(weather.equals("大雪")){
            holderView.wea.setImageResource(R.mipmap.daxue);
        }else if(weather.equals("雾")){
            holderView.wea.setImageResource(R.mipmap.wu);
        }else if(weather.equals("扬沙")){
            holderView.wea.setImageResource(R.mipmap.yangsha);
        }else if(weather.equals("小雨")){
            holderView.wea.setImageResource(R.mipmap.xiaoyu);
        }else if(weather.equals("中雨")){
            holderView.wea.setImageResource(R.mipmap.zhongyu);
        }else if(weather.equals("大雨")){
            holderView.wea.setImageResource(R.mipmap.dayu);
        }else if(weather.equals("暴雨")){
            holderView.wea.setImageResource(R.mipmap.baoyu);
        }else if(weather.equals("雨夹雪")){
            holderView.wea.setImageResource(R.mipmap.yujiaxue);
        }else if(weather.equals("沙尘暴")){
            holderView.wea.setImageResource(R.mipmap.shachenbao);
        }else if(weather.equals("雷阵雨")){
            holderView.wea.setImageResource(R.mipmap.leizhenyu);
        } else if(weather.equals("多云转晴")){
            holderView.wea.setImageResource(R.mipmap.sunntclude);
        }



        return convertView;
    }

    class ViewHolder {
        private TextView day, tem_h, tem_l;
        private ImageView wea;
    }
}
