package me.shib.lib.trakr.freshrelease;


import com.freshworks.freshrelease.api.Comment;
import me.shib.lib.trakr.TrakrComment;

import java.util.Date;

public final class FreshReleaseComment implements TrakrComment {

    private final Comment comment;

    FreshReleaseComment(Comment comment) {
        this.comment = comment;
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
