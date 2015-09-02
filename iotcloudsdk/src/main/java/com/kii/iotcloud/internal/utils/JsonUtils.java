package com.kii.iotcloud.internal.utils;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Provides useful methods for JSON.
 */
public class JsonUtils {
    public static JSONObject newJson(@NonNull String json) {
        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            throw new AssertionError(e);
        }
    }
}
