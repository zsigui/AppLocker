package com.noahapp.accesslib.entity;

public class KeyTable {
    private String keyword;
    private long last_search_time = 0;
    private String link;
    private int search_count = 0;

    public KeyTable(String keyword, String link) {
        this.keyword = keyword;
        this.link = link;
    }

    public String getKeyword() {
        return this.keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getLink() {
        return this.link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getSearch_count() {
        return this.search_count;
    }

    public void setSearch_count(int click_count) {
        this.search_count = click_count;
    }

    public long getLast_search_time() {
        return this.last_search_time;
    }

    public void setLast_search_time(long last_search_time) {
        this.last_search_time = last_search_time;
    }
}
