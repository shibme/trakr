package me.shib.lib.trakr;

import me.shib.lib.trakr.freshrelease.FreshReleaseTracker;
import me.shib.lib.trakr.jira.JiraTracker;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Trakr {

    private final transient Connection connection;
    private final transient Map<TrakrPriority, String> priorityMap;
    private final transient Map<String, TrakrPriority> reversePriorityMap;

    protected Trakr(Connection connection, Map<TrakrPriority, String> priorityMap) {
        this.connection = connection;
        this.priorityMap = priorityMap;
        this.reversePriorityMap = new HashMap<>();
        for (TrakrPriority priority : priorityMap.keySet()) {
            reversePriorityMap.put(priorityMap.get(priority), priority);
        }
    }

    public static synchronized Trakr getTrakr(Type type, Connection connection,
                                              Map<TrakrPriority, String> priorityMap) {
        if (type != null) {
            try {
                Constructor<? extends Trakr> constructor = type.getTrackerClass()
                        .getConstructor(Trakr.Connection.class, Map.class);
                return constructor.newInstance(connection, priorityMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Connection getConnection() {
        return connection;
    }

    public Map<TrakrPriority, String> getPriorityMap() {
        return priorityMap;
    }

    public boolean areContentsMatching(TrakrContent content, String trakrFormatContent) {
        String source = TrakrContent.simplifyContent(content.getContent(getContentType()), getContentType());
        String dest = TrakrContent.simplifyContent(trakrFormatContent, getContentType());
        return source.contentEquals(dest);
    }

    public String getPriorityName(TrakrPriority priority) {
        return priorityMap.get(priority);
    }

    public TrakrPriority getPriorityForName(String priorityName) {
        return reversePriorityMap.get(priorityName);
    }

    public abstract TrakrContent.Type getContentType();

    public abstract TrakrIssue createIssue(TrakrIssueBuilder creator) throws TrakrException;

    public abstract TrakrIssue updateIssue(TrakrIssue issue, TrakrIssueBuilder updater) throws TrakrException;

    public abstract List<TrakrIssue> searchTrakrIssues(TrakrQuery query) throws TrakrException;

    public enum Type {
        JIRA(JiraTracker.class),
        FRESHRELEASE(FreshReleaseTracker.class);

        private final Class<? extends Trakr> trackerClass;

        Type(Class<? extends Trakr> clazz) {
            this.trackerClass = clazz;
        }

        private Class<? extends Trakr> getTrackerClass() {
            return trackerClass;
        }
    }

    public static final class Connection {

        private final String endpoint;
        private String username;
        private String password;
        private String apiKey;

        public Connection(String endpoint, String apiKey) {
            this.endpoint = endpoint;
            this.apiKey = apiKey;
        }

        public Connection(String endpoint, String username, String password) {
            this.endpoint = endpoint;
            this.username = username;
            this.password = password;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getApiKey() {
            return apiKey;
        }
    }

}
