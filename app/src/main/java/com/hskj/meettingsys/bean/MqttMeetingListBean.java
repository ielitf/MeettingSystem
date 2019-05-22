package com.hskj.meettingsys.bean;

public class MqttMeetingListBean {
    private String bookPerson;//预订人
    private String isOpen;//是否公开
    private String endDate;//是否公开
    private String name;//会议主题名称
    private int id;
    private int templateId;//
    private String startDate; //日期

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

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
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

    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    @Override
    public String toString() {
        return "MqttMeetingListBean{" +
                "bookPerson='" + bookPerson + '\'' +
                ", isOpen='" + isOpen + '\'' +
                ", endDate='" + endDate + '\'' +
                ", name='" + name + '\'' +
                ", id=" + id +
                ", templateId=" + templateId +
                ", startDate='" + startDate + '\'' +
                '}';
    }
}
