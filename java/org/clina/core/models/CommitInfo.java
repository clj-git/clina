package org.clina.core.models;

import org.eclipse.jgit.revwalk.RevCommit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zjh on 15-5-18.
 */
public class CommitInfo {
    //id 就是 commit hash
    public String id;
    public String shortMessage;
    public String fullMessage;
    public List<String> parents;
    public Date authorTime;
    public String authorName;
    public String authorEmailAddress;
    public Date commitTime;
    public String committerName;
    public String committerEmailAddress;
    public String summary;
    public String description;
    public Boolean isDifferentFromAuthor;

    public void createCommitInfo (String id, String shortMessage, String fullMessage, List<String> parents, Date authorTime, String authorName, String authorEmailAddress, Date commitTime, String committerName, String committerEmailAddress) {
        this.id = id;
        this.shortMessage = shortMessage;
        this.fullMessage = fullMessage;
        this.parents = parents;
        this.authorTime = authorTime;
        this.authorName = authorName;
        this.authorEmailAddress = authorEmailAddress;
        this.commitTime = commitTime;
        this.committerName = committerName;
        this.committerEmailAddress = committerEmailAddress;

        summary = getSummaryMessage(fullMessage, shortMessage);
        description = getDescription(fullMessage);
        isDifferentFromAuthor = !authorName.equals(committerName) || !authorEmailAddress.equals(committerEmailAddress);
    }

    public CommitInfo(RevCommit commit) {
        List<String> parents = new ArrayList<String>();
        for (RevCommit parent : commit.getParents()) {
            parents.add(parent.name());
        }
        createCommitInfo(
                commit.getName(),
                commit.getShortMessage(),
                commit.getFullMessage(),
                parents,
                commit.getAuthorIdent().getWhen(),
                commit.getAuthorIdent().getName(),
                commit.getAuthorIdent().getEmailAddress(),
                commit.getCommitterIdent().getWhen(),
                commit.getCommitterIdent().getName(),
                commit.getCommitterIdent().getEmailAddress());
    }

    public String getSummaryMessage(String fullMessage, String shortMessage) {
        int i = fullMessage.trim().indexOf("\n");
        String result;
        if (i >= 0) {
            result = fullMessage.trim().substring(0, i).trim();
        } else {
            result = fullMessage;
        }

        if (result.length() > shortMessage.length()) {
            result = shortMessage;
        }

        return result;
    }

    public String getDescription(String fullMessage) {
        int i = fullMessage.trim().indexOf("\n");
        if (i >= 0) {
            return fullMessage.trim().substring(i).trim();
        }
        return "";
    }

    @Override
    public String toString() {
        return "CommitInfo{" +
                "id='" + id + '\'' +
                ", shortMessage='" + shortMessage + '\'' +
                ", fullMessage='" + fullMessage + '\'' +
                ", parents=" + parents +
                ", authorTime=" + authorTime +
                ", authorName='" + authorName + '\'' +
                ", authorEmailAddress='" + authorEmailAddress + '\'' +
                ", commitTime=" + commitTime +
                ", committerName='" + committerName + '\'' +
                ", committerEmailAddress='" + committerEmailAddress + '\'' +
                ", summary='" + summary + '\'' +
                ", description='" + description + '\'' +
                ", isDifferentFromAuthor=" + isDifferentFromAuthor +
                '}';
    }
}