package com.kenyrim.MyRss.db_result;

import java.io.Serializable;

/**
 * Created by kenyr on 29.01.2018.
 */
public class RssData implements Serializable {

    public long id;
    public String rssName;
    public String title;
    public String description;
    public String link;

    public RssData(long id, String rssName, String title, String description, String link) {
        this.id = id;
        this.rssName = rssName;
        this.title = title;
        this.description = description;
        this.link = link;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRssName() {
        return rssName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }
}
