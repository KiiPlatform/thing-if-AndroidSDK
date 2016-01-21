package com.kii.thingif;

import android.test.AndroidTestCase;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kii.thingif.internal.InternalUtils;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.Before;

public abstract class SmallTestBase extends AndroidTestCase {
    @Before
    public void before() throws Exception {
        // GsonRepository will cache the schema to static field.
        // So unit test must clear that cache in order to avoid the side effect.
        InternalUtils.gsonRepositoryClearCache();
    }
    protected void assertJSONObject(JSONObject expected, JSONObject actual) {
        if (expected == null && actual == null) {
            return;
        }
        Assert.assertEquals(new JsonParser().parse(expected.toString()), new JsonParser().parse(actual.toString()));
    }
}
