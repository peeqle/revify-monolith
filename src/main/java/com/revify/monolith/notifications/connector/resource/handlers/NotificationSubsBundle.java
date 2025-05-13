package com.revify.monolith.notifications.connector.resource.handlers;


import com.revify.monolith.notifications.connector.resource.env.Bundle;

import java.util.ResourceBundle;

import static com.revify.monolith.notifications.connector.resource.env.NotificationBundles.NOTIFICATION_SUBS;


public class NotificationSubsBundle extends Bundle {

    private static final ResourceBundle resourceBundleDEFAULT = ResourceBundle.getBundle(NOTIFICATION_SUBS);

    @Override
    public ResourceBundle getBundle() {
        return resourceBundleDEFAULT;
    }
}
