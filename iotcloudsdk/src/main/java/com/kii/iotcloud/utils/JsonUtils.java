package com.kii.iotcloud.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {
    public static JSONObject newJson(String json) {
        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            throw new AssertionError(e);
        }
    }
}
