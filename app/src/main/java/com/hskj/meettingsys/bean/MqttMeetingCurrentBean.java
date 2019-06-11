package com.hskj.meettingsys.bean;

public class MqttMeetingCurrentBean {
    private String department;//部门
    private long endDate;//结束时间
    private String isOpen;//是否公开  字符串1:公开 0:未公开
    private int meetingId;
    private String meetingName;//会议主题名称
    private String roomName;//会议室名称
    private long startDate;//开始时间

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(String isOpen) {
        this.isOpen = isOpen;
    }

    public int getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(int meetingId) {
        this.meetingId = meetingId;
    }

    public String getMeetingName() {
        return meetingName;
    }

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    @Override
    public String toString() {
        return "MqttMeetingCurrentBean{" +
                "department='" + department + '\'' +
                ", endDate=" + endDate +
                ", isOpen='" + isOpen + '\'' +
                ", meetingId=" + meetingId +
                ", meetingName='" + meetingName + '\'' +
                ", roomName='" + roomName + '\'' +
                ", startDate=" + startDate +
                '}';
    }
}
