package com.hskj.meettingsys.control;

public class CodeConstants {

    public static String API_HOST = "api.youkes.com";
    public static int API_PORT = 8081;
    public static String URL_Query = "http://" + API_HOST + ":" + API_PORT + "/api/video/query";

    public static  String TOPIC_MEETING_LIST = "002_meetList";//浩浩
    public static  String TOPIC_MEETING_CUR = "002_currtMeet";//浩浩

    public static String MEETING_LIST_DATA = "[{\"bookPerson\":\"zhangsan\",\"endDate\":\"2019-05-24 14:17:31\",\"id\":161,\"isOpen\":\"2\",\"name\":\"慧电科技会议室\",\"startDate\":\"2019-05-24 00:00:00\",\"templateId\":2}]";



    public static String MEETING_CUR_DATA = "[{\"department\":\"123\",\"endDate\":\"2019-05-24 15:32:57\",\"isOpen\":\"99\",\"meetingId\":162,\"meetingName\":\"123\",\"roomName\":\"慧电科技会议室\",\"startDate\":\"2019-05-23 00:00:00\"}]";





    public static String WHEATHER = "{\"success\":\"1\",\"result\":{\"weaid\":\"1\",\"days\":\"2019-05-24\",\"week\":\"星期五\",\"cityno\":\"beijing\",\"citynm\":\"北京\",\"cityid\":\"101010100\",\"temperature\":\"35℃/21℃\",\"temperature_curr\":\"32℃\",\"humidity\":\"22%\",\"aqi\":\"104\",\"weather\":\"多云\",\"weather_curr\":\"多云\",\"weather_icon\":\"http://api.k780.com/upload/weather/d/1.gif\",\"weather_icon1\":\"\",\"wind\":\"西南风\",\"winp\":\"3级\",\"temp_high\":\"35\",\"temp_low\":\"21\",\"temp_curr\":\"32\",\"humi_high\":\"0\",\"humi_low\":\"0\",\"weatid\":\"2\",\"weatid1\":\"\",\"windid\":\"5\",\"winpid\":\"3\",\"weather_iconid\":\"1\"}}";






}
