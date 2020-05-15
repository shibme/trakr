package me.shib.lib.trakr;

import java.util.Date;

public interface TrakrComment {
    String getBody();

    Date getCreatedDate();

    Date getUpdatedDate();
}
