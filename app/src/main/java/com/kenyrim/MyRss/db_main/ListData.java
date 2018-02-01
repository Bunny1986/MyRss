package com.kenyrim.MyRss.db_main;

import java.io.Serializable;

/**
 * Created by kenyr on 28.01.2018.
 */

public class ListData implements Serializable {
    private long id;
    private String title;
    private String text;
    private String url;

    public ListData(long id, String title, String text, String url) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.url = url;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getUrl() {
        return url;
    }
}
