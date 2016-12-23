package com.noahmob.AppLocker.upgrade;

public class CheckVersionModel {
    String autoupdate;
    String code;
    String downloadUrl;
    String foreupdate;
    String md5code;
    String publishdate;
    String size;
    String updatelogs;
    String versioname;
    String versioncode;

    public String getVersioncode() {
        return this.versioncode;
    }

    public void setVersioncode(String versioncode) {
        this.versioncode = versioncode;
    }

    public String getVersioname() {
        return this.versioname;
    }

    public void setVersioname(String versioname) {
        this.versioname = versioname;
    }

    public String getForeupdate() {
        return this.foreupdate;
    }

    public boolean shouldForceupdate() {
//        return this.foreupdate != null && TextUtils.equals(AppEventsConstants.EVENT_PARAM_VALUE_YES, this.foreupdate.trim());
        return false;
    }

    public void setForeupdate(String foreupdate) {
        this.foreupdate = foreupdate;
    }

    public String getAutoupdate() {
        return this.autoupdate;
    }

    public boolean sholdAutoupdate() {
//        return this.autoupdate != null && TextUtils.equals(AppEventsConstants.EVENT_PARAM_VALUE_YES, this.autoupdate.trim());
        return false;
    }

    public void setAutoupdate(String autoupdate) {
        this.autoupdate = autoupdate;
    }

    public String getUpdatelogs() {
        return this.updatelogs;
    }

    public void setUpdatelogs(String updatelogs) {
        this.updatelogs = updatelogs;
    }

    public String getDownloadUrl() {
        return this.downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getPublishdate() {
        return this.publishdate;
    }

    public void setPublishdate(String publishdate) {
        this.publishdate = publishdate;
    }

    public String getSize() {
        return this.size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getMd5code() {
        return this.md5code;
    }

    public void setMd5code(String md5code) {
        this.md5code = md5code;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
