package me.shib.lib.trakr;


import java.util.Date;
import java.util.List;

public abstract class TrakrIssue {

    protected transient Trakr trakr;

    protected TrakrIssue(Trakr trakr) {
        this.trakr = trakr;
    }

    public abstract void refresh() throws TrakrException;

    public abstract String getKey();

    public abstract String getProjectKey();

    public abstract String getTitle();

    public abstract String getDescription();

    public abstract String getType();

    public abstract String getStatus();

    public abstract TrakrPriority getPriority();

    public abstract Date getCreatedDate();

    public abstract Date getUpdatedDate();

    public abstract Date getDueDate();

    public abstract TrakrUser getReporter();

    public abstract TrakrUser getAssignee();

    public abstract List<TrakrUser> getSubscribers();

    public abstract List<String> getLabels();

    public abstract Object getCustomField(String identifier);

    public abstract List<TrakrComment> getComments() throws TrakrException;

    public abstract TrakrComment addComment(TrakrContent comment) throws TrakrException;

    @Override
    public String toString() {
        return getKey();
    }
}
