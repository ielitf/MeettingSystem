package com.hskj.meettingsys.bean;

public class MqttMeetingListBean {
    private String bookPerson;//预订人
    private long endDate;
    private int id;//会议ID
    private String isOpen;//是否公开  字符串1:公开 0:未公开
    private String name;//会议主题名称
    private String roomName;//会议室名称
    private long startDate; //日期
    private int templateId;// 1 代表模板A   2代表模板B

    public String getBookPerson() {
        return bookPerson;
    }

    public void setBookPerson(String bookPerson) {
        this.bookPerson = bookPerson;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(String isOpen) {
        this.isOpen = isOpen;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    @Override
    public String toString() {
        return "MqttMeetingListBean{" +
                "bookPerson='" + bookPerson + '\'' +
                ", endDate=" + endDate +
                ", id=" + id +
                ", isOpen='" + isOpen + '\'' +
                ", name='" + name + '\'' +
                ", roomName='" + roomName + '\'' +
                ", startDate=" + startDate +
                ", templateId=" + templateId +
                '}';
    }
}
