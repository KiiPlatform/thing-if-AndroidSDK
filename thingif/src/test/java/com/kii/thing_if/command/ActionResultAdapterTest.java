package com.kii.thing_if.command;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class ActionResultAdapterTest {

    private String actionResultToJsonString(ActionResult result) {
        JsonObject ret = new JsonObject();
        JsonObject resultObject = new JsonObject();
        resultObject.addProperty("succeeded", result.isSucceeded());
        if (result.getErrorMessage() != null){
            resultObject.addProperty("errorMessage", result.getErrorMessage());
        }
        if (result.getData() != null) {
            resultObject.add("data", new JsonParser().parse(result.getData().toString()));
        }
        ret.add(result.getActionName(), resultObject);
        return new Gson().toJson(ret);
    }

    @Test
    public void serializationTest() throws Exception{
        ActionResult[] acutalResults = {
                new ActionResult(
                        "turnPower",
                        true,
                        null,
                        null),
                new ActionResult(
                        "turnPower",
                        false,
                        "invalid value",
                        null),
                new ActionResult(
                        "turnPower",
                        false,
                        null,
                        new JSONObject().put("k", "value")),
                new ActionResult(
                        "turnPower",
                        false,
                        "invalid value",
                        new JSONObject().put("k1", "value1"))
        };

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(
                        ActionResult.class,
                        new ActionResultAdapter())
                .create();
        for (int i=0; i< acutalResults.length; i++) {
            ActionResult result = acutalResults[i];
            String expectedValue = actionResultToJsonString(result);
            String actualValue = gson.toJson(result);
            Assert.assertEquals("failed on ["+i+"]", expectedValue, actualValue);
        }
    }

    @Test
    public void deserializationTest() throws Exception{
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(
                        ActionResult.class,
                        new ActionResultAdapter())
                .create();

        JSONObject json1 =
                new JSONObject().put("turnPower", new JSONObject().put("succeeded", true));
        ActionResult result1 = gson.fromJson(json1.toString(), ActionResult.class);
        Assert.assertEquals("turnPower", result1.getActionName());
        Assert.assertEquals(true, result1.isSucceeded());
        Assert.assertNull(result1.getErrorMessage());
        Assert.assertNull(result1.getData());


        JSONObject json2 =
                new JSONObject().put("turnPower",
                        new JSONObject()
                                .put("succeeded", false)
                                .put("errorMessage", "invalid value"));
        ActionResult result2 = gson.fromJson(json2.toString(), ActionResult.class);
        Assert.assertEquals("turnPower", result2.getActionName());
        Assert.assertEquals(false, result2.isSucceeded());
        Assert.assertEquals("invalid value", result2.getErrorMessage());
        Assert.assertNull(result2.getData());

        JSONObject json3 =
                new JSONObject().put("turnPower",
                        new JSONObject()
                                .put("succeeded", false)
                                .put("data", new JSONObject().put("k1", "v1")));
        ActionResult result3 = gson.fromJson(json3.toString(), ActionResult.class);
        Assert.assertEquals("turnPower", result3.getActionName());
        Assert.assertEquals(false, result3.isSucceeded());
        Assert.assertNull(result3.getErrorMessage());
        Assert.assertNotNull(result3.getData());
        Assert.assertEquals(
                new JSONObject().put("k1", "v1").toString(),
                result3.getData().toString());

        JSONObject json4 =
                new JSONObject().put("turnPower",
                        new JSONObject()
                                .put("succeeded", false)
                                .put("errorMessage", "invalid value")
                                .put("data", new JSONObject().put("k1", "v2")));
        ActionResult result4 = gson.fromJson(json4.toString(), ActionResult.class);
        Assert.assertEquals("turnPower", result4.getActionName());
        Assert.assertEquals(false, result4.isSucceeded());
        Assert.assertEquals("invalid value", result4.getErrorMessage());
        Assert.assertNotNull(result4.getData());
        Assert.assertEquals(
                new JSONObject().put("k1", "v2").toString(),
                result4.getData().toString());
    }
}
