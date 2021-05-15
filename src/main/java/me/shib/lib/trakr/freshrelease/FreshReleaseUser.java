package me.shib.lib.trakr.freshrelease;

import me.shib.lib.freshrelease.api.User;
import me.shib.lib.trakr.TrakrUser;

public final class FreshReleaseUser extends TrakrUser {

    private final User user;

    private FreshReleaseUser(User user) {
        this.user = user;
    }

    static FreshReleaseUser getInstance(User user) {
        if (user == null) {
            return null;
        }
        return new FreshReleaseUser(user);
    }

    @Override
    public String getName() {
        return user.getName();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public String getEmail() {
        return user.getEmail();
    }
}
