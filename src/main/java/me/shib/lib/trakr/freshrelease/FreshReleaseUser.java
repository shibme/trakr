package me.shib.lib.trakr.freshrelease;

import com.freshworks.freshrelease.api.User;
import me.shib.lib.trakr.TrakrUser;

public final class FreshReleaseUser implements TrakrUser {

    private final User user;

    FreshReleaseUser(User user) {
        this.user = user;
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
