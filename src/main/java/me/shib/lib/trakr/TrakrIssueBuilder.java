package me.shib.lib.trakr;


import java.util.*;

public final class TrakrIssueBuilder {

    private final Set<String> labels;
    private String project;
    private String issueType;
    private String title;
    private TrakrContent description;
    private String assignee;
    private List<String> subscribers;
    private String status;
    private TrakrPriority priority;
    private Map<String, Object> customFields;

    public TrakrIssueBuilder() {
        this.labels = new HashSet<>();
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public List<String> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(List<String> subscribers) {
        this.subscribers = subscribers;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public TrakrContent getDescription() {
        return description;
    }

    public void setDescription(TrakrContent description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public TrakrPriority getPriority() {
        return priority;
    }

    public void setPriority(TrakrPriority priority) {
        this.priority = priority;
    }

    public List<String> getLabels() {
        return new ArrayList<>(labels);
    }

    public void setLabels(List<String> labels) {
        this.labels.addAll(labels);
    }

    public Map<String, Object> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(Map<String, Object> customFields) {
        this.customFields = customFields;
    }
}
