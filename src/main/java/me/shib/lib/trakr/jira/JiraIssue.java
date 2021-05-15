package me.shib.lib.trakr.jira;

import me.shib.java.lib.jiraclient.Comment;
import me.shib.java.lib.jiraclient.Issue;
import me.shib.java.lib.jiraclient.JiraException;
import me.shib.java.lib.jiraclient.User;
import me.shib.lib.trakr.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JiraIssue extends TrakrIssue {

    private final transient Issue issue;
    private final transient JiraTracker tracker;
    private transient TrakrPriority priority;

    JiraIssue(JiraTracker tracker, Issue issue) {
        super(tracker);
        this.tracker = tracker;
        this.issue = issue;
    }

    @Override
    public void refresh() throws TrakrException {
        try {
            issue.refresh();
        } catch (JiraException e) {
            throw new TrakrException(e);
        }
    }

    Issue getIssue() {
        return issue;
    }

    @Override
    public String getKey() {
        return issue.getKey();
    }

    @Override
    public String getProjectKey() {
        return issue.getProject().getKey();
    }

    @Override
    public String getTitle() {
        return issue.getSummary();
    }

    @Override
    public String getDescription() {
        return issue.getDescription();
    }

    @Override
    public String getType() {
        return issue.getIssueType().getName();
    }

    @Override
    public String getStatus() {
        return issue.getStatus().getName();
    }

    @Override
    public TrakrPriority getPriority() {
        if (priority == null) {
            priority = tracker.getPriorityForName(issue.getPriority().getName());
        }
        return priority;
    }

    @Override
    public Date getCreatedDate() {
        return issue.getCreatedDate();
    }

    @Override
    public Date getUpdatedDate() {
        return issue.getUpdatedDate();
    }

    @Override
    public Date getDueDate() {
        return issue.getDueDate();
    }

    @Override
    public TrakrUser getReporter() {
        return JiraUser.getInstance(issue.getReporter());
    }

    @Override
    public TrakrUser getAssignee() {
        return JiraUser.getInstance(issue.getAssignee());
    }

    @Override
    public List<TrakrUser> getSubscribers() {
        List<TrakrUser> subscribers = new ArrayList<>();
        for (User user : issue.getWatches().getWatchers()) {
            JiraUser jiraUser = JiraUser.getInstance(user);
            if (jiraUser != null) {
                subscribers.add(jiraUser);
            }
        }
        return subscribers;
    }

    @Override
    public List<String> getLabels() {
        return issue.getLabels();
    }

    @Override
    public Object getCustomField(String identifier) {
        return issue.getField(identifier);
    }

    @Override
    public List<TrakrComment> getComments() {
        List<TrakrComment> comments = new ArrayList<>();
        for (Comment comment : issue.getComments()) {
            JiraComment jiraComment = JiraComment.getInstance(comment);
            if (jiraComment != null) {
                comments.add(jiraComment);
            }
        }
        return comments;
    }

    @Override
    public TrakrComment addComment(TrakrContent comment) throws TrakrException {
        try {
            return JiraComment.getInstance(issue.addComment(comment.getJiraContent()));
        } catch (JiraException e) {
            throw new TrakrException(e.getMessage());
        }
    }
}
