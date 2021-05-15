package me.shib.lib.trakr.freshrelease;

import me.shib.lib.freshrelease.api.Comment;
import me.shib.lib.freshrelease.api.FreshReleaseException;
import me.shib.lib.freshrelease.api.Issue;
import me.shib.lib.trakr.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class FreshReleaseIssue extends TrakrIssue {

    private final transient FreshReleaseTracker tracker;
    private transient Issue issue;
    private transient TrakrPriority priority;
    private transient TrakrUser reporter;
    private transient TrakrUser assignee;
    private transient List<TrakrUser> subscribers;
    private transient List<TrakrComment> comments;

    FreshReleaseIssue(FreshReleaseTracker tracker, Issue issue) {
        super(tracker);
        this.tracker = tracker;
        this.issue = issue;
    }

    Issue getIssue() {
        return issue;
    }

    void setIssue(Issue issue) {
        this.issue = issue;
    }

    @Override
    public void refresh() {
        try {
            issue.refresh();
            priority = null;
            reporter = null;
            assignee = null;
            subscribers = null;
            comments = null;
        } catch (FreshReleaseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getKey() {
        return issue.getKey();
    }

    @Override
    public String getProjectKey() {
        return issue.getProjectKey();
    }

    @Override
    public String getTitle() {
        return issue.getTitle();
    }

    @Override
    public String getDescription() {
        return issue.getDescription();
    }

    @Override
    public String getType() {
        return issue.getIssueType().getLabel();
    }

    @Override
    public String getStatus() {
        return issue.getStatus().getLabel();
    }

    @Override
    public TrakrPriority getPriority() {
        if (priority == null) {
            priority = tracker.getPriorityForName(issue.getPriority().getLabel());
        }
        return priority;
    }

    @Override
    public Date getCreatedDate() {
        return issue.getCreateAt();
    }

    @Override
    public Date getUpdatedDate() {
        return issue.getUpdatedAt();
    }

    @Override
    public Date getDueDate() {
        return issue.getDueBy();
    }

    @Override
    public TrakrUser getReporter() {
        if (reporter == null) {
            reporter = FreshReleaseUser.getInstance(issue.getReporter());
        }
        return reporter;
    }

    @Override
    public TrakrUser getAssignee() {
        if (assignee == null) {
            assignee = FreshReleaseUser.getInstance(issue.getOwner());
        }
        return this.assignee;
    }

    @Override
    public List<TrakrUser> getSubscribers() {
        if (null == this.subscribers) {
            this.subscribers = new ArrayList<>();
            TrakrUser assignee = getAssignee();
            TrakrUser reporter = getReporter();
            if (null != assignee) {
                this.subscribers.add(assignee);
            }
            if (null != reporter) {
                this.subscribers.add(reporter);
            }
        }
        return this.subscribers;
    }

    @Override
    public List<String> getLabels() {
        return issue.getTags();
    }

    @Override
    public Object getCustomField(String identifier) {
        return issue.getCustomFields().get(identifier);
    }

    @Override
    public List<TrakrComment> getComments() throws TrakrException {
        if (null == this.comments) {
            this.comments = new ArrayList<>();
            try {
                List<Comment> comments = issue.getComments();
                if (null != comments) {
                    for (Comment comment : comments) {
                        FreshReleaseComment freshReleaseComment = FreshReleaseComment.getInstance(comment);
                        if (freshReleaseComment != null) {
                            this.comments.add(freshReleaseComment);
                        }
                    }
                }
            } catch (FreshReleaseException e) {
                throw new TrakrException(e);
            }
        }
        return this.comments;
    }

    @Override
    public TrakrComment addComment(TrakrContent comment) throws TrakrException {
        try {
            return FreshReleaseComment.getInstance(issue.addComment(comment.getHtmlContent()));
        } catch (FreshReleaseException e) {
            throw new TrakrException(e);
        }
    }
}
