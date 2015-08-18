package com.kii.iotcloud.utils;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {
    public static JSONObject newJson(@NonNull String json) {
        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            throw new AssertionError(e);
        }
    }
}
