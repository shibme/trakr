package me.shib.lib.trakr.freshrelease;


import me.shib.lib.freshrelease.api.Comment;
import me.shib.lib.trakr.TrakrComment;

import java.util.Date;

public final class FreshReleaseComment implements TrakrComment {

    private final Comment comment;

    private FreshReleaseComment(Comment comment) {
        this.comment = comment;
    }

    static FreshReleaseComment getInstance(Comment comment) {
        if (comment == null) {
            return null;
        }
        return new FreshReleaseComment(comment);
    }

    @Override
    public String getBody() {
        return comment.getContent();
    }

    @Override
    public Date getCreatedDate() {
        return comment.getCreatedAt();
    }

    @Override
    public Date getUpdatedDate() {
        return comment.getCreatedAt();
    }
}
