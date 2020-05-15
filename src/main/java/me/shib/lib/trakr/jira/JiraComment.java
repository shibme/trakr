package me.shib.lib.trakr.jira;

import me.shib.java.lib.jiraclient.Comment;
import me.shib.lib.trakr.TrakrComment;

import java.util.Date;

public class JiraComment implements TrakrComment {

    private final Comment comment;

    JiraComment(Comment comment) {
        this.comment = comment;
    }

    @Override
    public String getBody() {
        return comment.getBody();
    }

    @Override
    public Date getCreatedDate() {
        return comment.getCreatedDate();
    }

    @Override
    public Date getUpdatedDate() {
        return comment.getUpdatedDate();
    }
}
