package com.revify.monolith.user.service.phone_messaging;

public class MessageUtil {

    private final static String CODE_MESSAGE_TEMPLATE_EN = """
            Authorization code: %s
            """;

    public static String prepareCodeMessage(String code) {
        return String.format(CODE_MESSAGE_TEMPLATE_EN, code);
    }
}
