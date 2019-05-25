package com.hskj.meettingsys.bean;

public class MqttMeetingListBean {
    private String bookPerson;//预订人
    private String isOpen;//是否公开  字符串true:公开 false:未公开
    private long endDate;
    private String name;//会议主题名称
    private int id;//会议ID
    private String templateId;// 0 代表模板A   1代表模板2
    private long startDate; //日期

    public String getBookPerson() {
        return bookPerson;
    }

    public void setBookPerson(String bookPerson) {
        this.bookPerson = bookPerson;
    }

    public String getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(String isOpen) {
        this.isOpen = isOpen;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    @Override
    public String toString() {
        return "MqttMeetingListBean{" +
                "bookPerson='" + bookPerson + '\'' +
                ", isOpen='" + isOpen + '\'' +
                ", endDate=" + endDate +
                ", name='" + name + '\'' +
                ", id=" + id +
                ", templateId='" + templateId + '\'' +
                ", startDate=" + startDate +
                '}';
    }
}
