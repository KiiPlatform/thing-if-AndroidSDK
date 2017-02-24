package com.kii.thingif.trigger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kii.thingif.ServerError;
import com.kii.thingif.SmallTestBase;
import com.kii.thingif.utils.JsonUtil;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class TriggeredServerCodeResultAdapterTest extends SmallTestBase{
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(
                    TriggeredServerCodeResult.class,
                    new TriggeredServerCodeResultAdapter())
            .create();
    @Test
    public void deserializationTest() throws Exception {
        TriggeredServerCodeResult[] expectedResults = {
                new TriggeredServerCodeResult(
                        true,
                        new JSONObject().put("k", "v"),
                        System.currentTimeMillis(),
                        "endpoint1",
                        null),
                new TriggeredServerCodeResult(
                        true,
                        new JSONArray()
                                .put(new JSONObject().put("key1", "value1"))
                                .put(new JSONObject().put("key2", "value2")),
                        System.currentTimeMillis(),
                        "endpoint2",
                        null),
                new TriggeredServerCodeResult(
                        true,
                        "a string",
                        System.currentTimeMillis(),
                        "endpoint3",
                        null),
                new TriggeredServerCodeResult(
                        true,
                        "",
                        System.currentTimeMillis(),
                        "endpoint4",
                        null),
                new TriggeredServerCodeResult(
                        true,
                        34,
                        System.currentTimeMillis(),
                        "endpoint5",
                        null),
                new TriggeredServerCodeResult(
                        true,
                        (long)456,
                        System.currentTimeMillis(),
                        "endpoint6",
                        null),
                new TriggeredServerCodeResult(
                        true,
                        (double)45,
                        System.currentTimeMillis(),
                        "endpoint7",
                        null),
                new TriggeredServerCodeResult(
                        true,
                        true,
                        System.currentTimeMillis(),
                        "endpoint8",
                        null),
                new TriggeredServerCodeResult(
                        true,
                        null,
                        System.currentTimeMillis(),
                        "endpoint9",
                        null),
                new TriggeredServerCodeResult(
                        false,
                        null,
                        System.currentTimeMillis(),
                        "endpoint10",
                        new ServerError("error found", "RUNTIME_ERROR", "faital error"))
        };

        for (int i=0; i<expectedResults.length; i++) {
            TriggeredServerCodeResult expectedResult = expectedResults[i];
            TriggeredServerCodeResult deserializedResult = this.gson.fromJson(
                    JsonUtil.triggeredServerCodeResultToJson(expectedResult).toString(),
                    TriggeredServerCodeResult.class);
            assertSameTriggeredServerCodeResults(
                    "failed on ["+i+"]",
                    expectedResult,
                    deserializedResult);
        }
    }

}
