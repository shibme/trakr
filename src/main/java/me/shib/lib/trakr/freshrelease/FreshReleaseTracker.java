package me.shib.lib.trakr.freshrelease;

import com.freshworks.freshrelease.api.*;
import me.shib.lib.trakr.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class FreshReleaseTracker extends Trakr {

    private static final transient Map<TrakrQuery.Operator, Query.Operator> operatorMap;

    static {
        operatorMap = new HashMap<>();
        operatorMap.put(TrakrQuery.Operator.matching, Query.Operator.is_in);
        operatorMap.put(TrakrQuery.Operator.not_matching, Query.Operator.is_not);
    }

    private final transient FreshRelease fr;
    private transient Map<String, Project> projectMap;
    private transient Map<String, IssueType> issueTypeMap;
    private transient Map<String, Priority> priorityMap;
    private transient Map<String, Status> statusMap;
    private transient Map<String, User> userMap;

    public FreshReleaseTracker(Connection connection, Map<TrakrPriority, String> priorityMap)
            throws IOException, FreshReleaseException {
        super(connection, priorityMap);
        this.fr = FreshRelease.getInstance(connection.getEndpoint(), connection.getApiKey());
    }

    private synchronized Project getProject(String key) throws FreshReleaseException {
        if (projectMap == null) {
            projectMap = new HashMap<>();
            for (Project project : fr.getProjects()) {
                projectMap.put(project.getKey(), project);
            }
        }
        return projectMap.get(key);
    }

    private synchronized Priority getPriority(String priorityName) throws FreshReleaseException {
        if (priorityMap == null) {
            priorityMap = new HashMap<>();
            for (Priority priority : fr.getPriorities()) {
                priorityMap.put(priority.getLabel(), priority);
            }
        }
        return priorityMap.get(priorityName);
    }

    private synchronized Status getStatus(String statusName) throws FreshReleaseException {
        if (statusMap == null) {
            statusMap = new HashMap<>();
            for (Status status : fr.getStatuses()) {
                statusMap.put(status.getLabel(), status);
            }
        }
        return statusMap.get(statusName);
    }

    private synchronized IssueType getIssueType(String label) throws FreshReleaseException {
        if (issueTypeMap == null) {
            issueTypeMap = new HashMap<>();
            for (IssueType issueType : fr.getIssueTypes()) {
                issueTypeMap.put(issueType.getLabel(), issueType);
            }
        }
        return issueTypeMap.get(label);
    }

    private synchronized User getUser(String email) throws FreshReleaseException {
        if (userMap == null) {
            userMap = new HashMap<>();
            for (User user : fr.getUsers()) {
                userMap.put(user.getEmail(), user);
            }
        }
        return userMap.get(email);
    }

    @Override
    public TrakrContent.Type getContentType() {
        return TrakrContent.Type.HTML;
    }

    @Override
    public TrakrIssue createIssue(TrakrIssueBuilder creator) throws TrakrException {
        try {
            Issue issueBuilder = new Issue(creator.getTitle(), getIssueType(creator.getIssueType()).getId());
            User owner = getUser(creator.getAssignee());
            if (null != owner) {
                issueBuilder.setOwnerId(owner.getId());
            }
            issueBuilder.setDescription(creator.getDescription().getHtmlContent());
            issueBuilder.setTags(creator.getLabels());
            issueBuilder.setCustomFields(creator.getCustomFields());
            issueBuilder.setPriorityId(getPriority(getPriorityName(creator.getPriority())).getId());
            Issue issue = fr.createIssue(creator.getProject(), issueBuilder);
            return new FreshReleaseIssue(this, issue);
        } catch (FreshReleaseException e) {
            throw new TrakrException(e);
        }
    }

    @Override
    public TrakrIssue updateIssue(TrakrIssue issue, TrakrIssueBuilder updater) throws TrakrException {
        FreshReleaseIssue frIssue = (FreshReleaseIssue) issue;
        try {
            Issue.IssueUpdate issueUpdate = frIssue.getIssue().update();
            if (null != updater.getIssueType()) {
                IssueType issueType = getIssueType(updater.getIssueType());
                if (null != issueType) {
                    issueUpdate.setIssueTypeId(issueType.getId());
                }
            }
            if (null != updater.getTitle()) {
                issueUpdate.setTitle(updater.getTitle());
            }
            if (null != updater.getDescription()) {
                issueUpdate.setDescription(updater.getDescription().getHtmlContent());
            }
            if (null != updater.getAssignee()) {
                User owner = getUser(updater.getAssignee());
                if (null != owner) {
                    issueUpdate.setOwnerId(owner.getId());
                }
            }
            if (null != updater.getStatus()) {
                Status status = getStatus(updater.getStatus());
                if (null != status) {
                    issueUpdate.setStatusId(status.getId());
                }
            }
            if (null != updater.getPriority()) {
                Priority priority = getPriority(getPriorityName(updater.getPriority()));
                if (null != priority) {
                    issueUpdate.setPriorityId(priority.getId());
                }
            }
            if (updater.getLabels().size() > 0) {
                issueUpdate.setTags(updater.getLabels());
            }
            if (null != updater.getCustomFields()) {
                issueUpdate.setCustomFields(updater.getCustomFields());
            }
            frIssue.setIssue(issueUpdate.execute());
        } catch (FreshReleaseException e) {
            throw new TrakrException(e);
        }
        return frIssue;
    }

    private boolean buildSearchQuery(TrakrQuery.TrakrQueryItem queryItem, Query searchQuery)
            throws FreshReleaseException {
        List<Query.Value> values = new ArrayList<>();
        switch (queryItem.getCondition()) {
            case project:
                for (String val : queryItem.getValues()) {
                    Project project = getProject(val);
                    if (project == null) {
                        return false;
                    }
                    values.add(new Query.Value(project.getId()));
                }
                searchQuery.add(Query.Condition.project_id, operatorMap.get(queryItem.getOperator()), values);
                break;
            case type:
                for (String val : queryItem.getValues()) {
                    IssueType issueType = getIssueType(val);
                    if (issueType == null) {
                        return false;
                    }
                    values.add(new Query.Value(issueType.getId()));
                }
                searchQuery.add(Query.Condition.issue_type_id, operatorMap.get(queryItem.getOperator()), values);
                break;
            case label:
                for (String tag : queryItem.getValues()) {
                    values.add(new Query.Value(tag));
                }
                searchQuery.add(Query.Condition.tags, operatorMap.get(queryItem.getOperator()), values);
                break;
            case status:
                for (String val : queryItem.getValues()) {
                    Status status = getStatus(val);
                    if (status == null) {
                        return false;
                    }
                    values.add(new Query.Value(status.getId()));
                }
                searchQuery.add(Query.Condition.status_id, operatorMap.get(queryItem.getOperator()), values);
        }
        return true;
    }

    private boolean isStringPresentInList(List<String> searchIn, String searchString) {
        for (String s : searchIn) {
            if (s.equalsIgnoreCase(searchString)) {
                return true;
            }
        }
        return false;
    }

    private boolean isListSubsetOfList(List<String> searchIn, List<String> searchWith) {
        for (String s : searchWith) {
            if (!isStringPresentInList(searchIn, s)) {
                return false;
            }
        }
        return true;
    }

    private List<Issue> validateSearch(List<Issue> issues, TrakrQuery query) {
        List<Issue> issueList = new ArrayList<>(issues);
        for (TrakrQuery.TrakrQueryItem queryItem : query.getQueryItems()) {
            switch (queryItem.getCondition()) {
                case label:
                    List<Issue> labelFilterList = new ArrayList<>();
                    List<String> tagNames = queryItem.getValues();
                    for (Issue issue : issueList) {
                        if (isListSubsetOfList(issue.getTags(), tagNames)) {
                            labelFilterList.add(issue);
                        }
                    }
                    issueList = labelFilterList;
                    break;
                case status:
                    List<Issue> statusFilterList = new ArrayList<>();
                    for (Issue issue : issueList) {
                        if (queryItem.getValues().contains(issue.getStatus().getLabel())) {
                            if (queryItem.getOperator() == TrakrQuery.Operator.matching) {
                                statusFilterList.add(issue);
                            }
                        } else {
                            if (queryItem.getOperator() == TrakrQuery.Operator.not_matching) {
                                statusFilterList.add(issue);
                            }
                        }
                    }
                    issueList = statusFilterList;
                    break;
                case type:
                    List<Issue> typeFilterList = new ArrayList<>();
                    for (Issue issue : issueList) {
                        if (queryItem.getValues().contains(issue.getIssueType().getLabel())) {
                            if (queryItem.getOperator() == TrakrQuery.Operator.matching) {
                                typeFilterList.add(issue);
                            }
                        } else {
                            if (queryItem.getOperator() == TrakrQuery.Operator.not_matching) {
                                typeFilterList.add(issue);
                            }
                        }
                    }
                    issueList = typeFilterList;
                    break;
            }
        }
        return issueList;
    }

    @Override
    public List<TrakrIssue> searchTrakrIssues(TrakrQuery query) throws TrakrException {
        try {
            List<TrakrIssue> trakrIssues = new ArrayList<>();
            Query frQuery = new Query();
            frQuery.includeAllFields();
            for (TrakrQuery.TrakrQueryItem queryItem : query.getQueryItems()) {
                if (!buildSearchQuery(queryItem, frQuery)) {
                    return trakrIssues;
                }
            }
            List<Issue> issues = fr.listIssues(frQuery);
            issues = validateSearch(issues, query);
            for (Issue issue : issues) {
                trakrIssues.add(new FreshReleaseIssue(this, issue));
            }
            return trakrIssues;
        } catch (FreshReleaseException e) {
            throw new TrakrException(e);
        }
    }
}
