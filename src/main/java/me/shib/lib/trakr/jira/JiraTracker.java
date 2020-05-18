package me.shib.lib.trakr.jira;

import me.shib.java.lib.jiraclient.*;
import me.shib.lib.trakr.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class JiraTracker extends Trakr {

    private final JiraClient client;

    public JiraTracker(Connection connection, Map<TrakrPriority, String> priorityMap) throws TrakrException {
        super(connection, priorityMap);
        BasicCredentials credentials = new BasicCredentials(connection.getUsername(), connection.getPassword());
        try {
            this.client = new JiraClient(connection.getEndpoint(), credentials);
        } catch (JiraException e) {
            e.printStackTrace();
            throw new TrakrException(e.getMessage());
        }
    }

    @Override
    public TrakrContent.Type getContentType() {
        return TrakrContent.Type.Jira;
    }

    private String cleanUsername(String username) {
        if (username.contains("@")) {
            return username.split("@")[0];
        }
        return username;
    }

    @Override
    public TrakrIssue createIssue(TrakrIssueBuilder creator) throws TrakrException {
        try {
            Issue.FluentCreate fluentCreate = client.createIssue(creator.getProject(), creator.getIssueType());
            fluentCreate.field(Field.SUMMARY, creator.getTitle());
            fluentCreate.field(Field.DESCRIPTION, creator.getDescription().getJiraContent());
            if (creator.getAssignee() != null) {
                fluentCreate.field(Field.ASSIGNEE, cleanUsername(creator.getAssignee()));
            }
            if (creator.getPriority() != null) {
                fluentCreate.field(Field.PRIORITY, getPriorityName(creator.getPriority()));
            }
            if (creator.getLabels().size() > 0) {
                fluentCreate.field(Field.LABELS, creator.getLabels());
            }
            if (creator.getCustomFields() != null) {
                for (String key : creator.getCustomFields().keySet()) {
                    try {
                        fluentCreate.field(key, Field.valueById((String) creator.getCustomFields().get(key)));
                    } catch (Exception e) {
                        fluentCreate.field(key, creator.getCustomFields().get(key));
                    }
                }
            }
            Issue issue = fluentCreate.execute();
            if (creator.getSubscribers() != null && creator.getSubscribers().size() > 0) {
                for (String watcher : creator.getSubscribers()) {
                    issue.addWatcher(cleanUsername(watcher));
                }
            }
            return new JiraIssue(this, issue);
        } catch (JiraException e) {
            throw new TrakrException(e);
        }
    }

    private void transitionIssue(Issue issue, String status) throws JiraException {
        List<Transition> transitions = issue.getTransitions();
        for (Transition transition : transitions) {
            if (transition.getToStatus().getName().equalsIgnoreCase(status)) {
                issue.transition().execute(transition.getName());
            }
        }
    }

    @Override
    public TrakrIssue updateIssue(TrakrIssue TrakrIssue, TrakrIssueBuilder updater) throws TrakrException {
        JiraIssue jiraIssue = (JiraIssue) TrakrIssue;
        boolean fluentUpdatable = false;
        try {
            Issue issue = jiraIssue.getIssue();
            Issue.FluentUpdate fluentUpdate = issue.update();
            if (null != updater.getIssueType()) {
                fluentUpdate.field(Field.ISSUE_TYPE, updater.getIssueType());
                fluentUpdatable = true;
            }
            if (null != updater.getTitle()) {
                fluentUpdate.field(Field.SUMMARY, updater.getTitle());
                fluentUpdatable = true;
            }
            if (null != updater.getDescription()) {
                fluentUpdate.field(Field.DESCRIPTION, updater.getDescription().getJiraContent());
                fluentUpdatable = true;
            }
            if (null != updater.getAssignee()) {
                fluentUpdate.field(Field.ASSIGNEE, updater.getAssignee());
                fluentUpdatable = true;
            }
            if (null != updater.getStatus()) {
                transitionIssue(issue, updater.getStatus());
            }
            if (null != updater.getPriority()) {
                fluentUpdate.field(Field.PRIORITY, getPriorityName(updater.getPriority()));
                fluentUpdatable = true;
            }
            if (updater.getLabels().size() > 0) {
                fluentUpdate.field(Field.LABELS, updater.getLabels());
                fluentUpdatable = true;
            }
            if (null != updater.getCustomFields()) {
                for (String key : updater.getCustomFields().keySet()) {
                    fluentUpdate.field(key, updater.getCustomFields().get(key));
                    fluentUpdatable = true;
                }
            }
            if (fluentUpdatable) {
                fluentUpdate.execute();
            }
        } catch (Exception e) {
            throw new TrakrException(e);
        }
        return jiraIssue;
    }

    private String getJqlForBatQuery(TrakrQuery query) {
        StringBuilder jql = new StringBuilder();
        for (TrakrQuery.TrakrQueryItem queryItem : query.getQueryItems()) {
            jql.append(" AND ");
            switch (queryItem.getCondition()) {
                case project:
                    jql.append("project");
                    break;
                case status:
                    jql.append("status");
                    break;
                case label:
                    jql.append("labels");
                    break;
                case type:
                    jql.append("issuetype");
            }
            if (queryItem.getOperator() == TrakrQuery.Operator.matching) {
                jql.append(" in ");
            } else if (queryItem.getOperator() == TrakrQuery.Operator.not_matching) {
                jql.append(" not in ");
            }
            jql.append("(");
            jql.append("\"").append(queryItem.getValues().get(0)).append("\"");
            for (int i = 1; i < queryItem.getValues().size(); i++) {
                jql.append(", ").append("\"").append(queryItem.getValues().get(i)).append("\"");
            }
            jql.append(")");
        }
        return jql.toString();
    }

    @Override
    public List<TrakrIssue> searchTrakrIssues(TrakrQuery query) throws TrakrException {
        try {
            List<TrakrIssue> TrakrIssues = new ArrayList<>();
            List<Issue> issues = client.searchIssues(getJqlForBatQuery(query)).issues;
            for (Issue issue : issues) {
                TrakrIssues.add(new JiraIssue(this, issue));
            }
            return TrakrIssues;
        } catch (JiraException e) {
            throw new TrakrException(e);
        }
    }
}
