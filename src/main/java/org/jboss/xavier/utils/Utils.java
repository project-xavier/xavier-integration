package org.jboss.xavier.utils;

import java.util.Date;
import java.util.UUID;

public class Utils {

    public Date getUnixEpochDate() {
        return new Date(0L);
    }

    public String generateUUID() {
        return UUID.randomUUID().toString();
    }

}
