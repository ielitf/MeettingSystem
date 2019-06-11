package com.hskj.meettingsys.bean;

import java.util.Arrays;
import java.util.List;

public class WeatherBean {
    private String day;
    private String date;
    private String week;
    private String wea;
    private String wea_img;
    private String tem1;
    private String tem2;
    private String tem;
    private String win[];
    private String win_speed;
    private List <Hours> hours;
    private List<Indexv> index;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getWea() {
        return wea;
    }

    public void setWea(String wea) {
        this.wea = wea;
    }

    public String getWea_img() {
        return wea_img;
    }

    public void setWea_img(String wea_img) {
        this.wea_img = wea_img;
    }

    public String getTem1() {
        return tem1;
    }

    public void setTem1(String tem1) {
        this.tem1 = tem1;
    }

    public String getTem2() {
        return tem2;
    }

    public void setTem2(String tem2) {
        this.tem2 = tem2;
    }

    public String getTem() {
        return tem;
    }

    public void setTem(String tem) {
        this.tem = tem;
    }

    public String[] getWin() {
        return win;
    }

    public void setWin(String[] win) {
        this.win = win;
    }

    public String getWin_speed() {
        return win_speed;
    }

    public void setWin_speed(String win_speed) {
        this.win_speed = win_speed;
    }

    public List<Hours> getHours() {
        return hours;
    }

    public void setHours(List<Hours> hours) {
        this.hours = hours;
    }

    public List<Indexv> getIndex() {
        return index;
    }

    public void setIndex(List<Indexv> index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "WeatherBean{" +
                "day='" + day + '\'' +
                ", date='" + date + '\'' +
                ", week='" + week + '\'' +
                ", wea='" + wea + '\'' +
                ", wea_img='" + wea_img + '\'' +
                ", tem1='" + tem1 + '\'' +
                ", tem2='" + tem2 + '\'' +
                ", tem='" + tem + '\'' +
                ", win=" + Arrays.toString(win) +
                ", win_speed='" + win_speed + '\'' +
                ", hours=" + hours +
                ", index=" + index +
                '}';
    }
}
