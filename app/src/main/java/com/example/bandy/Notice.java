package com.example.bandy;

import java.util.ArrayList;

public class Notice {
    private int notiId;
    private String notiName;
    private String nodeId;
    private String nodeName;
    private int notiTime;
    private String[] routeIds = new String[2];
    private String[] routeNames = new String[2];
    private String[] arrTimes = new String[2];
    private String startAt;
    private String endAt;
    private int days;
    private boolean isOn;
    private boolean flag = false;

    public Notice(int notiId, String notiName, String nodeId, String nodeName, int notiTime, String startAt, String endAt, int days, boolean isOn) {
        this.notiId = notiId;
        this.notiName = notiName;
        this.nodeId = nodeId;
        this.nodeName = nodeName;
        this.notiTime = notiTime;
        this.startAt = startAt;
        this.endAt = endAt;
        this.days = days;
        this.isOn = isOn;
    }

    public int getNotiId() {
        return notiId;
    }

    public void setNotiId(int notiId) {
        this.notiId = notiId;
    }

    public String getNotiName() {
        return notiName;
    }

    public void setNotiName(String notiName) {
        this.notiName = notiName;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String NodeName) {
        this.nodeName = nodeName;
    }

    public int getNotiTime() {
        return notiTime;
    }

    public void setNotiTime(int notiTime) {
        this.notiTime = notiTime;
    }

    public String[] getRouteIds() {
        return routeIds;
    }

    public String getRouteIds(int position) {
        return routeIds[position];
    }

    public void setRouteIds(String[] routeIds) {
        this.routeIds = routeIds;
    }

    public String getRouteName(int position) {
        return routeNames[position];
    }

    public void setRouteNames(String[] routeNames) {
        this.routeNames = routeNames;
    }

    public String getArrTimes(int position) {
        return arrTimes[position];
    }

    public void setArrTimes(int position, String arrTimes) {
        this.arrTimes[position] = arrTimes;
    }

    public String getStartAt() {
        return startAt;
    }

    public void setStartAt(String startAt) {
        this.startAt = startAt;
    }

    public String getEndAt() {
        return endAt;
    }

    public void setEndAt(String endAt) {
        this.endAt = endAt;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setIsOn(boolean isOn) {
        isOn = isOn;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}