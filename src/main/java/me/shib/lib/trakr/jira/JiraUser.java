package me.shib.lib.trakr.jira;

import me.shib.java.lib.jiraclient.User;
import me.shib.lib.trakr.TrakrUser;

public class JiraUser implements TrakrUser {

    private final User user;

    JiraUser(User user) {
        this.user = user;
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
