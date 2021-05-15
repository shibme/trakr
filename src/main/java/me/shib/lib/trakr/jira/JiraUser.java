package me.shib.lib.trakr.jira;

import me.shib.java.lib.jiraclient.User;
import me.shib.lib.trakr.TrakrUser;

public class JiraUser extends TrakrUser {

    private final User user;

    private JiraUser(User user) {
        this.user = user;
    }

    static JiraUser getInstance(User user) {
        if (user == null) {
            return null;
        }
        return new JiraUser(user);
    }

    @Override
    public String getName() {
        return user.getDisplayName();
    }

    @Override
    public String getUsername() {
        return user.getName();
    }

    @Override
    public String getEmail() {
        return user.getEmail();
    }
}
