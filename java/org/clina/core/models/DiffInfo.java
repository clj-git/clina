package org.clina.core.models;

import org.eclipse.jgit.diff.DiffEntry.ChangeType;

/**
 * Created by zjh on 15-5-21.
 */
public class DiffInfo {
    public ChangeType changeType;
    public String oldPath;
    public String newPath;
    public String oldContent;
    public String newContent;

    public DiffInfo(ChangeType changeType, String oldPath, String newPath, String oldContent, String newContent) {
        this.changeType = changeType;
        this.oldPath = oldPath;
        this.newPath = newPath;
        this.oldContent = oldContent;
        this.newContent = newContent;
    }

    @Override
    public String toString() {
        return "DiffInfo{" +
                "changeType=" + changeType +
                ", oldPath='" + oldPath + '\'' +
                ", newPath='" + newPath + '\'' +
                ", oldContent='" + oldContent + '\'' +
                ", newContent='" + newContent + '\'' +
                '}';
    }
}
