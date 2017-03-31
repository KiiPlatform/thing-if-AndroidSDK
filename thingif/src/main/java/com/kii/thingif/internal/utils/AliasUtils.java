package com.kii.thingif.internal.utils;

public class AliasUtils {
    public static String aliasActionKey(String alias, String actionName) {
        return alias + ":"+ actionName;
    }

    public static String actionNameFromKey(String key) {
        return key.substring(key.indexOf(":"), key.length());
    }

    public static String aliasFromKey(String key) {
        return key.substring(0, key.indexOf(":"));
    }
}
