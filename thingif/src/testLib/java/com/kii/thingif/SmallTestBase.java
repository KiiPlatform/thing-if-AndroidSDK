package com.kii.thingif;


import com.google.gson.JsonParser;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SmallTestBase {


    protected void assertJSONObject(
            String errorMessage,
            JSONObject expected,
            JSONObject actual)
    {
        if (expected == null && actual == null) {
            return;
        }
        Assert.assertEquals(errorMessage,
                new JsonParser().parse(expected.toString()),
                new JsonParser().parse(actual.toString()));
    }
    protected void assertJSONObject(JSONObject expected, JSONObject actual) {
        if (expected == null && actual == null) {
            return;
        }
        Assert.assertEquals(new JsonParser().parse(expected.toString()), new JsonParser().parse(actual.toString()));
    }
    protected void assertJSONArray(JSONArray expected, JSONArray actual) throws JSONException {
        if (expected == null && actual == null) {
            return;
        }
        Assert.assertEquals(expected.length(), actual.length());
        for (int i = 0; i< expected.length(); i++) {
            Object e = expected.get(i);
            Object a = actual.get(i);
            if (a instanceof JSONObject) {
                assertJSONObject((JSONObject)e, (JSONObject)a);
            } else if (a instanceof JSONArray) {
                assertJSONArray((JSONArray)e, (JSONArray)a);
            } else {
                Assert.assertEquals(e, a);
            }
        }
    }
}
