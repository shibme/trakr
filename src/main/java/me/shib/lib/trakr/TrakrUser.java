package me.shib.lib.trakr;

public abstract class TrakrUser {
    public abstract String getName();

    public abstract String getUsername();

    public abstract String getEmail();

    @Override
    public String toString() {
        return getName() + " [" + getEmail() + "]";
    }
}
