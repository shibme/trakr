package me.shib.lib.trakr;

public class TrakrException extends Exception {
    public TrakrException(String message) {
        super(message);
    }

    public TrakrException(Exception e) {
        super(e.getMessage(), e.getCause());
    }
}
