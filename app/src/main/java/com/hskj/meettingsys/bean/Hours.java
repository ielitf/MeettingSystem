package com.hskj.meettingsys.bean;

public class Hours {
    private String day;
    private String wea;
    private String tem;
    private String win;
    private String win_speed;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getWea() {
        return wea;
    }

    public void setWea(String wea) {
        this.wea = wea;
    }

    public String getTem() {
        return tem;
    }

    public void setTem(String tem) {
        this.tem = tem;
    }

    public String getWin() {
        return win;
    }

    public void setWin(String win) {
        this.win = win;
    }

    public String getWin_speed() {
        return win_speed;
    }

    public void setWin_speed(String win_speed) {
        this.win_speed = win_speed;
    }

    @Override
    public String toString() {
        return "Hours{" +
                "day='" + day + '\'' +
                ", wea='" + wea + '\'' +
                ", tem='" + tem + '\'' +
                ", win='" + win + '\'' +
                ", win_speed='" + win_speed + '\'' +
                '}';
    }
}
