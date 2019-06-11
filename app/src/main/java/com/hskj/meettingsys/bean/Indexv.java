package com.hskj.meettingsys.bean;

public class Indexv {
    private String title;
    private String level;
    private String desc;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "Indexv{" +
                "title='" + title + '\'' +
                ", level='" + level + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
