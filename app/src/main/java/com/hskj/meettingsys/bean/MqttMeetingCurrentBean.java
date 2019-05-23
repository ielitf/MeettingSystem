package com.hskj.meettingsys.bean;

public class MqttMeetingCurrentBean {
    private String name;//会议主题名称
    private String department;//部门
    private String startTime;//开始时间
    private String endTime;//结束时间
    private String template;//模板类型
    private String isOpen;//是否公开

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(String isOpen) {
        this.isOpen = isOpen;
    }

    @Override
    public String toString() {
        return "MqttMeetingCurrentBean{" +
                "name='" + name + '\'' +
                ", department='" + department + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", template='" + template + '\'' +
                ", isOpen='" + isOpen + '\'' +
                '}';
    }
}
