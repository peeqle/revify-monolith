package com.revify.monolith.notifications.connector.resource.env;

import java.util.ResourceBundle;

public abstract class Bundle {

    public String getFrom(String bundleKey) {
        return getBundle().getString(bundleKey);
    }

    public abstract ResourceBundle getBundle();
}
