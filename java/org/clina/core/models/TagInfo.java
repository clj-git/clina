package org.clina.core.models;

import java.util.Date;

/**
 * Created by zjh on 15-5-25.
 */
public class TagInfo {
    public String name;
    public Date time;
    public String id;

    public TagInfo(String name, Date time, String id) {
        this.name = name;
        this.time = time;
        this.id = id;
    }

    @Override
    public String toString() {
        return "TagInfo{" +
                "name='" + name + '\'' +
                ", time=" + time +
                ", id='" + id + '\'' +
                '}';
    }
}
