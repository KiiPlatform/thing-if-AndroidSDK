package com.kii.thingiftrait.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AliasActionResultAdapterTest {

    private String aliasActionResultToJsonString(AliasActionResult aliasResult) {
        JsonArray results = new JsonArray();
        for (ActionResult result : aliasResult.getResults()) {
            JsonObject resultObject = new JsonObject();
            JsonObject subObject = new JsonObject();
            subObject.addProperty("succeeded", result.isSucceeded());
            if (result.getErrorMessage() != null) {
                subObject.addProperty("errorMessage", result.getErrorMessage());
            }
            if (result.getData() != null) {
                JsonObject dataObject = new JsonObject();
                dataObject.addProperty("k", "v");
                subObject.add("data", dataObject);
            }
            resultObject.add(result.getActionName(), subObject);
            results.add(resultObject);
        }
        JsonObject ret = new JsonObject();
        ret.add(aliasResult.getAlias(), results);
        return ret.toString();
    }

    @Test
    public void serializationTest() {

        List<ActionResult> results = new ArrayList<>();
        results.add(new ActionResult("turnPower", true, null, null));
        results.add(new ActionResult("setPresetTemperature", false, "invalid value", null));
        AliasActionResult result =
                new AliasActionResult("alias", results);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(
                        AliasActionResult.class,
                        new AliasActionResultAdapter())
                .create();
        Assert.assertEquals(aliasActionResultToJsonString(result), gson.toJson(result));
    }

    @Test
    public void deserializationTest() throws Exception{
        JSONObject actualJson = new JSONObject().put(
                "alias",
                new JSONArray()
                        .put(new JSONObject().put(
                                "turnPower",
                                new JSONObject().put(
                                        "succeeded",
                                        true)))
                        .put(new JSONObject().put(
                                "setPresetTemperature",
                                new JSONObject()
                                        .put("succeeded", false)
                                        .put("errorMessage", "invalid value"))
                        )
        );

        List<ActionResult> results = new ArrayList<>();
        results.add(new ActionResult("turnPower", true, null, null));
        results.add(new ActionResult("setPresetTemperature", false, "invalid value", null));
        AliasActionResult expectedResult = new AliasActionResult("alias", results);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(
                        AliasActionResult.class,
                        new AliasActionResultAdapter()
                ).create();
        AliasActionResult deserializedResult =
                gson.fromJson(
                        actualJson.toString(),
                        AliasActionResult.class);
        Assert.assertEquals(expectedResult.getAlias(), deserializedResult.getAlias());
        Assert.assertEquals(
                expectedResult.getResults().size(),
                deserializedResult.getResults().size());
        Assert.assertEquals(
                expectedResult.getResults().get(0).getActionName(),
                deserializedResult.getResults().get(0).getActionName());
        Assert.assertEquals(
                expectedResult.getResults().get(0).isSucceeded(),
                deserializedResult.getResults().get(0).isSucceeded());
        Assert.assertNull(deserializedResult.getResults().get(0).getErrorMessage());
        Assert.assertNull(deserializedResult.getResults().get(0).getData());

        Assert.assertEquals(expectedResult.getAlias(), deserializedResult.getAlias());
        Assert.assertEquals(
                expectedResult.getResults().get(1).getActionName(),
                deserializedResult.getResults().get(1).getActionName());
        Assert.assertEquals(
                expectedResult.getResults().get(1).getErrorMessage(),
                deserializedResult.getResults().get(1).getErrorMessage());
        Assert.assertNull(deserializedResult.getResults().get(1).getData());
    }
}
