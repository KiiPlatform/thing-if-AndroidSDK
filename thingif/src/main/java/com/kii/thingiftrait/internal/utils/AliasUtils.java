package com.kii.thingiftrait.internal.utils;

public class AliasUtils {
    public static String aliasActionKey(String alias, String actionName) {
        return alias + ":"+ actionName;
    }

    public static String actionNameFromKey(String key) {
        return key.substring(key.indexOf(":")+1, key.length());
    }

    public static String aliasFromKey(String key) {
        return key.substring(0, key.indexOf(":"));
    }
}
