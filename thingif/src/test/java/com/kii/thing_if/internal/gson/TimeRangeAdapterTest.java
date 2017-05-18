package com.kii.thing_if.internal.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.kii.thing_if.query.TimeRange;

import junit.framework.Assert;

import org.junit.Test;

import java.util.Date;

public class TimeRangeAdapterTest {

    @Test
    public void deserializationTest() throws Exception {
        TimeRange expected = new TimeRange(new Date(1), new Date(100));

        JsonObject json = new JsonObject();
        json.addProperty("from", expected.getFrom().getTime());
        json.addProperty("to", expected.getTo().getTime());

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(TimeRange.class, new TimeRangeAdapter())
                .create();

        TimeRange actual = gson.fromJson(json, TimeRange.class);

        Assert.assertNotNull(actual);
        Assert.assertEquals(expected, actual);
    }
}
