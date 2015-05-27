package org.clina.core.models;

import org.eclipse.jgit.lib.ObjectId;

import java.util.Date;

/**
 * Created by zjh on 15-5-17.
 */
public class FileInfo {
    public ObjectId id;
    public boolean isDirectory;
    public String name;
    public String message;
    public String commitId;
    public Date time;
    public String author;
    public String mailAddress;
    public String[] linkUrl;

    public FileInfo(ObjectId id, boolean isDirectory, String name, String message, String commitId, Date time, String author, String mailAddress, String[] linkUrl) {
        this.id = id;
        this.isDirectory = isDirectory;
        this.name = name;
        this.message = message;
        this.commitId = commitId;
        this.time = time;
        this.author = author;
        this.mailAddress = mailAddress;
        this.linkUrl = linkUrl;
    }
}
