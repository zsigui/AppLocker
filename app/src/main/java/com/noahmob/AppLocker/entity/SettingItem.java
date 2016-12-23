package com.noahmob.AppLocker.entity;

public class SettingItem {
    private int action;
    private String title;
    private int type;

    public SettingItem(String title, int type, int action) {
        this.title = title;
        this.type = type;
        this.action = action;
    }

    public int getAction() {
        return this.action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
