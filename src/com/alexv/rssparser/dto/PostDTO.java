package com.alexv.rssparser.dto;

import java.util.Date;

public class PostDTO {

    private String title;
    private String url;
    private Date date;
    
    public PostDTO() {}

    public PostDTO(Long id, String title, String url, Date date) {
        super();
        this.title = title;
        this.url = url;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}