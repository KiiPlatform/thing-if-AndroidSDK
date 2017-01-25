package com.kii.thingif;

import android.test.AndroidTestCase;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kii.thingif.internal.InternalUtils;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;

public abstract class SmallTestBase extends AndroidTestCase {
    @Before
    public void before() throws Exception {
        // GsonRepository will cache the schema to static field.
        // So unit test must clear that cache in order to avoid the side effect.
        InternalUtils.gsonRepositoryClearCache();
    }
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
                assertEquals(e, a);
            }
        }
    }
}
