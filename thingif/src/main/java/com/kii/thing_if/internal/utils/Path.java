package com.kii.thing_if.internal.utils;

import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Performs operations on String instances that contain URL path information.
 */
public class Path {
    public static String combine(@Nullable String path1, @Nullable String path2) {
        if (TextUtils.isEmpty(path1)) {
            return path2;
        }
        if (TextUtils.isEmpty(path2)) {
            return path1;
        }
        if (path1.endsWith("/") && path2.startsWith("/")) {
            return path1 + path2.substring(1);
        } else if (!path1.endsWith("/") && !path2.startsWith("/")) {
            return path1 + "/" + path2;
        } else {
            return path1 + path2;
        }
    }
}
