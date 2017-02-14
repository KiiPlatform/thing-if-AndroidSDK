package com.kii.thingif.internal.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.Test;

public class JSONObjectAdapterTest {
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(JSONObject.class, new JSONObjectAdapter())
            .create();
    @Test
    public void serializationTest() throws Exception{
        JSONObject json = new JSONObject().put("k", "v");
        JsonObject expectedJson = new JsonObject();
        expectedJson.addProperty("k", "v");
        Assert.assertEquals(expectedJson.toString(), gson.toJson(json));
    }

    @Test
    public void deserializationTest() throws Exception{
        JSONObject expectedJson = new JSONObject().put("k", "v");
        JsonObject json = new JsonObject();
        json.addProperty("k", "v");
        Assert.assertEquals(
                expectedJson.toString(),
                gson.fromJson(json, JSONObject.class).toString());
    }
}
